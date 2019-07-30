package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.HasHashCodeQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.MoveOutMethodCallExpressionQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.RemoveActualOutmostMethodCallQuickFix

class AssertThatObjectExpressionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting equals(), toString(), or hashCode()"
        private const val HASHCODE_MESSAGE_TEMPLATE = "Replacing hashCode() expressions by hasSameHashCode() would be more concise"

        private val OBJECT_TO_STRING = CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_OBJECT, "toString").parameterCount(0)
        private val OBJECT_HASHCODE = CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_OBJECT, "hashCode").parameterCount(0)
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                if (!statement.hasAssertThat()) return
                val staticMethodCall = statement.findStaticMethodCall() ?: return

                val assertThatArgument = staticMethodCall.firstArg as? PsiMethodCallExpression ?: return
                val expectedCallExpression = statement.findOutmostMethodCall() ?: return
                when {
                    OBJECT_EQUALS.test(assertThatArgument) -> {
                        val expectedResult = expectedCallExpression.getAllTheSameExpectedBooleanConstants() ?: return
                        val replacementMethod = expectedResult.map(MethodNames.IS_EQUAL_TO, MethodNames.IS_NOT_EQUAL_TO)
                        registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, replacementMethod) { desc, method ->
                            MoveOutMethodCallExpressionQuickFix(desc, method)
                        }
                    }
                    OBJECT_TO_STRING.test(assertThatArgument) -> {
                        staticMethodCall.findFluentCallTo(IS_EQUAL_TO_OBJECT) ?: staticMethodCall.findFluentCallTo(IS_EQUAL_TO_STRING) ?: return
                        registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, MethodNames.HAS_TO_STRING) { desc, method ->
                            RemoveActualOutmostMethodCallQuickFix(desc, method)
                        }
                    }
                    OBJECT_HASHCODE.test(assertThatArgument) -> {
                        val isEqualTo = staticMethodCall.findFluentCallTo(IS_EQUAL_TO_INT) ?: return
                        val expectedExpression = isEqualTo.firstArg as? PsiMethodCallExpression ?: return
                        if (OBJECT_HASHCODE.test(expectedExpression)) {
                            holder.registerProblem(expectedCallExpression, HASHCODE_MESSAGE_TEMPLATE, HasHashCodeQuickFix())
                        }
                    }
                }
            }
        }
    }
}
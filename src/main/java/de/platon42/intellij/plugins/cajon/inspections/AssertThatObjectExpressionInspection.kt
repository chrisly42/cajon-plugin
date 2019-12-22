package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.HasHashCodeQuickFix

class AssertThatObjectExpressionInspection : AbstractMoveOutInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting equals(), toString(), or hashCode()"
        private const val HASHCODE_MESSAGE_TEMPLATE = "Replacing hashCode() expressions by hasSameHashCode() would be more concise"

        private val OBJECT_TO_STRING = CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_OBJECT, "toString").parameterCount(0)
        private val OBJECT_HASHCODE = CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_OBJECT, "hashCode").parameterCount(0)

        private val MAPPINGS = listOf(
            MoveOutMapping(
                OBJECT_EQUALS,
                MethodNames.IS_EQUAL_TO, MethodNames.IS_NOT_EQUAL_TO, expectBoolean = true
            ),
            MoveOutMapping(
                OBJECT_TO_STRING,
                MethodNames.HAS_TO_STRING, expectedMatcher = CallMatcher.anyOf(IS_EQUAL_TO_OBJECT, IS_EQUAL_TO_STRING)
            )
        )
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                if (!statement.hasAssertThat()) return

                val staticMethodCall = statement.findStaticMethodCall() ?: return
                val assertThatArgument = staticMethodCall.getArgOrNull(0) as? PsiMethodCallExpression ?: return
                if (OBJECT_HASHCODE.test(assertThatArgument)) {
                    val expectedCallExpression = statement.findOutmostMethodCall() ?: return
                    val isEqualTo = staticMethodCall.findFluentCallTo(IS_EQUAL_TO_INT) ?: return
                    val expectedExpression = isEqualTo.firstArg as? PsiMethodCallExpression ?: return
                    if (OBJECT_HASHCODE.test(expectedExpression)) {
                        holder.registerProblem(expectedCallExpression, HASHCODE_MESSAGE_TEMPLATE, HasHashCodeQuickFix())
                    }
                } else {
                    createInspectionsForMappings(statement, holder, MAPPINGS)
                }
            }
        }
    }
}
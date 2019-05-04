package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.MoveOutMethodCallExpressionQuickFix

class AssertThatStringExpressionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a string specific expression"

        private val MAPPINGS = listOf(
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "isEmpty").parameterCount(0)!!,
                MethodNames.IS_EMPTY, MethodNames.IS_NOT_EMPTY
            ),
            Mapping(
                CallMatcher.anyOf(
                    CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "equals").parameterCount(1)!!,
                    CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "contentEquals").parameterCount(1)!!
                ),
                MethodNames.IS_EQUAL_TO, MethodNames.IS_NOT_EQUAL_TO
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "equalsIgnoreCase").parameterTypes(CommonClassNames.JAVA_LANG_STRING)!!,
                MethodNames.IS_EQUAL_TO_IC, MethodNames.IS_NOT_EQUAL_TO_IC
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "contains").parameterCount(1)!!,
                MethodNames.CONTAINS, MethodNames.DOES_NOT_CONTAIN
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "startsWith").parameterTypes(CommonClassNames.JAVA_LANG_STRING)!!,
                MethodNames.STARTS_WITH, MethodNames.DOES_NOT_START_WITH
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "endsWith").parameterTypes(CommonClassNames.JAVA_LANG_STRING)!!,
                MethodNames.ENDS_WITH, MethodNames.DOES_NOT_END_WITH
            )
        )
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                if (!statement.hasAssertThat()) {
                    return
                }
                val staticMethodCall = statement.findStaticMethodCall() ?: return
                if (!ASSERT_THAT_BOOLEAN.test(staticMethodCall)) {
                    return
                }
                val assertThatArgument = staticMethodCall.firstArg as? PsiMethodCallExpression ?: return
                val mapping = MAPPINGS.firstOrNull { it.callMatcher.test(assertThatArgument) } ?: return

                val expectedCallExpression = statement.findOutmostMethodCall() ?: return
                val expectedResult = expectedCallExpression.getAllTheSameExpectedBooleanConstants() ?: return

                val replacementMethod = if (expectedResult) mapping.replacementForTrue else mapping.replacementForFalse
                registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, replacementMethod, ::MoveOutMethodCallExpressionQuickFix)
            }
        }
    }

    private class Mapping(
        val callMatcher: CallMatcher,
        val replacementForTrue: String,
        val replacementForFalse: String
    )
}
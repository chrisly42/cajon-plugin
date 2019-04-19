package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.CommonClassNames
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.findOutmostMethodCall
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.quickfixes.MoveActualOuterExpressionMethodCallQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.RemoveActualOutmostMethodCallQuickFix

class AssertThatStringExpressionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a string specific expression"
        private const val MOVE_EXPECTED_EXPRESSION_DESCRIPTION_TEMPLATE = "Remove %s() of expected expression and use assertThat().%s() instead"
        private const val MOVING_OUT_MESSAGE_TEMPLATE = "Moving %s() expression out of assertThat() would be more concise"

        private val MAPPINGS = listOf(
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "isEmpty").parameterCount(0)!!,
                MethodNames.IS_EMPTY, MethodNames.IS_NOT_EMPTY, hasExpected = false
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
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!ASSERT_THAT_BOOLEAN.test(expression)) {
                    return
                }
                val assertThatArgument = expression.firstArg as? PsiMethodCallExpression ?: return

                val mapping = MAPPINGS.firstOrNull { it.callMatcher.test(assertThatArgument) } ?: return

                val expectedCallExpression = expression.findOutmostMethodCall() ?: return
                val expectedResult = getExpectedBooleanResult(expectedCallExpression) ?: return

                val replacementMethod = if (expectedResult) mapping.replacementForTrue else mapping.replacementForFalse
                if (mapping.hasExpected) {
                    registerMoveOutMethod(holder, expression, assertThatArgument, replacementMethod, ::MoveActualOuterExpressionMethodCallQuickFix)
                } else {
                    registerMoveOutMethod(holder, expression, assertThatArgument, replacementMethod) { desc, method ->
                        RemoveActualOutmostMethodCallQuickFix(desc, method, noExpectedExpression = true)
                    }
                }
            }

            private fun registerMoveOutMethod(
                holder: ProblemsHolder,
                expression: PsiMethodCallExpression,
                oldActualExpression: PsiMethodCallExpression,
                replacementMethod: String,
                quickFixSupplier: (String, String) -> LocalQuickFix
            ) {
                val originalMethod = getOriginalMethodName(oldActualExpression) ?: return
                val description = MOVE_EXPECTED_EXPRESSION_DESCRIPTION_TEMPLATE.format(originalMethod, replacementMethod)
                val message = MOVING_OUT_MESSAGE_TEMPLATE.format(originalMethod)
                val quickfix = quickFixSupplier(description, replacementMethod)
                holder.registerProblem(expression, message, quickfix)
            }
        }
    }

    private class Mapping(
        val callMatcher: CallMatcher,
        val replacementForTrue: String,
        val replacementForFalse: String,
        val hasExpected: Boolean = true
    )
}
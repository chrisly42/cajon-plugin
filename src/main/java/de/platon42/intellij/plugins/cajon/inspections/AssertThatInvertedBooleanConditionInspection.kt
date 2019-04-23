package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.findOutmostMethodCall
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.map
import de.platon42.intellij.plugins.cajon.quickfixes.RemoveUnaryExpressionQuickFix

class AssertThatInvertedBooleanConditionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting an inverted boolean condition"
        private const val INVERT_CONDITION_DESCRIPTION = "Invert condition in assertThat()"
        private const val INVERT_CONDITION_MESSAGE = "Condition inside assertThat() could be inverted"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!ASSERT_THAT_BOOLEAN.test(expression)) {
                    return
                }

                val expectedCallExpression = expression.findOutmostMethodCall() ?: return
                val expectedResult = getExpectedBooleanResult(expectedCallExpression) ?: return

                val prefixExpression = expression.firstArg as? PsiPrefixExpression ?: return
                if (prefixExpression.operationTokenType == JavaTokenType.EXCL) {
                    val replacementMethod = expectedResult.map(MethodNames.IS_FALSE, MethodNames.IS_TRUE)
                    registerInvertMethod(holder, expression, replacementMethod, ::RemoveUnaryExpressionQuickFix)
                }
            }
        }
    }

    private fun registerInvertMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        replacementMethod: String,
        quickFixSupplier: (String, String) -> LocalQuickFix
    ) {
        val quickfix = quickFixSupplier(INVERT_CONDITION_DESCRIPTION, replacementMethod)
        holder.registerProblem(expression, INVERT_CONDITION_MESSAGE, quickfix)
    }
}
package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import de.platon42.intellij.plugins.cajon.findOutmostMethodCall
import de.platon42.intellij.plugins.cajon.findStaticMethodCall
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.getExpectedBooleanResult
import de.platon42.intellij.plugins.cajon.quickfixes.InvertUnaryStatementQuickFix

class AssertThatInvertedBooleanConditionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting an inverted boolean condition"
        private const val INVERT_CONDITION_MESSAGE = "Condition inside assertThat() could be inverted"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                val staticMethodCall = expression.findStaticMethodCall() ?: return
                if (!ASSERT_THAT_BOOLEAN.test(staticMethodCall)) {
                    return
                }
                expression.getExpectedBooleanResult() ?: return

                val prefixExpression = staticMethodCall.firstArg as? PsiPrefixExpression ?: return
                if (prefixExpression.operationTokenType == JavaTokenType.EXCL) {
                    val outmostMethodCall = expression.findOutmostMethodCall() ?: return
                    holder.registerProblem(outmostMethodCall, INVERT_CONDITION_MESSAGE, InvertUnaryStatementQuickFix())
                }
            }
        }
    }
}
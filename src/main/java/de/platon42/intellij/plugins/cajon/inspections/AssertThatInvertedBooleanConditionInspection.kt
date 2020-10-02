package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.util.TypeConversionUtil
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.InvertUnaryExpressionQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.InvertUnaryStatementQuickFix

class AssertThatInvertedBooleanConditionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting an inverted boolean condition"
        private const val INVERT_CONDITION_MESSAGE = "Condition inside assertThat() could be inverted"
        private const val REMOVE_INSTANCEOF_DESCRIPTION_TEMPLATE = "Invert condition in %s() and use %s() instead"
        private const val INVERT_OUTSIDE_CONDITION_MESSAGE = "Condition outside assertThat() could be inverted"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!expression.hasAssertThat()) return
                val staticMethodCall = expression.findStaticMethodCall() ?: return
                if (expression.getExpectedBooleanResult() != null) {
                    if (!ASSERT_THAT_BOOLEAN.test(staticMethodCall)) return
                    val prefixExpression = staticMethodCall.firstArg as? PsiPrefixExpression ?: return
                    if (prefixExpression.operationTokenType == JavaTokenType.EXCL) {
                        val outmostMethodCall = expression.findOutmostMethodCall() ?: return
                        holder.registerProblem(outmostMethodCall, INVERT_CONDITION_MESSAGE, InvertUnaryStatementQuickFix())
                    }
                } else {
                    if (!CallMatcher.anyOf(ASSERT_THAT_BOOLEAN, ASSERT_THAT_BOOLEAN_OBJ).test(staticMethodCall)) return
                    val isEqualTo = CallMatcher.anyOf(IS_EQUAL_TO_BOOLEAN, IS_EQUAL_TO_OBJECT).test(expression)
                    val isNotEqualTo = CallMatcher.anyOf(IS_NOT_EQUAL_TO_BOOLEAN, IS_NOT_EQUAL_TO_OBJECT).test(expression)
                    if (!(isEqualTo || isNotEqualTo)) return
                    val prefixExpression = expression.firstArg as? PsiPrefixExpression ?: return
                    val operand = PsiUtil.skipParenthesizedExprDown(prefixExpression.operand) ?: return
                    if (!TypeConversionUtil.isPrimitiveAndNotNull(operand.type)) return
                    val originalMethod = getOriginalMethodName(expression) ?: return

                    val replacementMethod = isEqualTo.map(MethodNames.IS_NOT_EQUAL_TO, MethodNames.IS_EQUAL_TO)
                    val description = REMOVE_INSTANCEOF_DESCRIPTION_TEMPLATE.format(originalMethod, replacementMethod)
                    val textRange = TextRange(staticMethodCall.textLength, expression.textLength)
                    holder.registerProblem(expression, textRange, INVERT_OUTSIDE_CONDITION_MESSAGE, InvertUnaryExpressionQuickFix(description, replacementMethod))
                }
            }
        }
    }
}
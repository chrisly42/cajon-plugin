package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.TypeConversionUtil
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.quickfixes.SplitBinaryExpressionMethodCallQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.SplitEqualsExpressionMethodCallQuickFix

class AssertThatBinaryExpressionIsTrueOrFalseInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a binary expression"
        private const val SPLIT_BINARY_EXPRESSION_DESCRIPTION = "Split binary expression out of assertThat()"
        private const val SPLIT_EQUALS_EXPRESSION_DESCRIPTION = "Split equals() expression out of assertThat()"
        private const val BINARY_MORE_MEANINGFUL_MESSAGE = "Moving binary expression out of assertThat() would be more meaningful"
        private const val EQUALS_MORE_MEANINGFUL_MESSAGE = "Moving equals() expression out of assertThat() would be more meaningful"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!ASSERT_THAT_BOOLEAN.test(expression)) {
                    return
                }

                val statement = PsiTreeUtil.getParentOfType(expression, PsiStatement::class.java) ?: return
                val expectedCallExpression = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java) ?: return
                val expectedResult = getExpectedBooleanResult(expectedCallExpression) ?: return

                val assertThatArgument = expression.firstArg
                if (assertThatArgument is PsiMethodCallExpression && OBJECT_EQUALS.test(assertThatArgument)) {
                    val replacementMethod = if (expectedResult) "isEqualTo()" else "isNotEqualTo()"
                    val quickFix = SplitEqualsExpressionMethodCallQuickFix(SPLIT_EQUALS_EXPRESSION_DESCRIPTION, replacementMethod)
                    holder.registerProblem(expression, EQUALS_MORE_MEANINGFUL_MESSAGE, quickFix)
                    return
                }

                val binaryExpression = assertThatArgument as? PsiBinaryExpression ?: return

                val leftType = binaryExpression.lOperand.type ?: return
                val rightType = binaryExpression.rOperand?.type ?: return

                val bothTypes = listOf(leftType, rightType)
                val (isLeftNull, isRightNull) = bothTypes.map(TypeConversionUtil::isNullType)
                if (isLeftNull && isRightNull) {
                    return
                } else if (isLeftNull || isRightNull) {
                    val replacementMethod = if (expectedResult) "isNull()" else "isNotNull()"
                    registerSplitBinaryExpressionMethod(holder, expression, replacementMethod, pickRightOperand = isLeftNull, noExpectedExpression = true)
                    return
                }

                val isPrimitive = bothTypes.all(TypeConversionUtil::isPrimitiveAndNotNull)
                val isNumericType = bothTypes.all(TypeConversionUtil::isNumericType)
                val constantEvaluationHelper = JavaPsiFacade.getInstance(expression.project).constantEvaluationHelper
                val swapExpectedAndActual = constantEvaluationHelper.computeConstantExpression(binaryExpression.lOperand) != null

                val tokenType = binaryExpression.operationSign.tokenType
                    .let {
                        if (swapExpectedAndActual) SWAP_SIDE_OF_BINARY_OPERATOR.getOrDefault(it, it) else it
                    }
                    .let {
                        if (expectedResult) it else INVERT_BINARY_OPERATOR.getOrDefault(it, it)
                    } ?: return
                val mappingToUse =
                    if (isPrimitive || isNumericType) {
                        TOKEN_TO_ASSERTJ_FOR_PRIMITIVE_MAP
                    } else {
                        TOKEN_TO_ASSERTJ_FOR_OBJECT_MAPPINGS
                    }
                val replacementMethod = mappingToUse[tokenType] ?: return

                registerSplitBinaryExpressionMethod(holder, expression, "$replacementMethod()", pickRightOperand = swapExpectedAndActual)
            }

            private fun registerSplitBinaryExpressionMethod(
                holder: ProblemsHolder,
                expression: PsiMethodCallExpression,
                replacementMethod: String,
                pickRightOperand: Boolean = false,
                noExpectedExpression: Boolean = false
            ) {
                val quickFix = SplitBinaryExpressionMethodCallQuickFix(SPLIT_BINARY_EXPRESSION_DESCRIPTION, replacementMethod, pickRightOperand, noExpectedExpression)
                holder.registerProblem(expression, BINARY_MORE_MEANINGFUL_MESSAGE, quickFix)
            }
        }
    }
}
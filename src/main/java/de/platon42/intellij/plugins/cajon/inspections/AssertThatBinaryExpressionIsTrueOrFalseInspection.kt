package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.TypeConversionUtil
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.MethodNames.Companion.IS_NOT_NULL
import de.platon42.intellij.plugins.cajon.MethodNames.Companion.IS_NULL
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.map
import de.platon42.intellij.plugins.cajon.quickfixes.SplitBinaryExpressionMethodCallQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.SplitEqualsExpressionMethodCallQuickFix

class AssertThatBinaryExpressionIsTrueOrFalseInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a binary expression"
        private const val SPLIT_EXPRESSION_DESCRIPTION_TEMPLATE = "Split %s expression out of assertThat()"
        private const val MORE_MEANINGFUL_MESSAGE_TEMPLATE = "Moving %s expression out of assertThat() would be more meaningful"
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
                    val replacementMethod = if (expectedResult) MethodNames.IS_EQUAL_TO else MethodNames.IS_NOT_EQUAL_TO
                    val type = "${MethodNames.EQUALS}()"
                    val description = SPLIT_EXPRESSION_DESCRIPTION_TEMPLATE.format(type)
                    val message = MORE_MEANINGFUL_MESSAGE_TEMPLATE.format(type)
                    val quickFix = SplitEqualsExpressionMethodCallQuickFix(description, replacementMethod)
                    holder.registerProblem(expression, message, quickFix)
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
                    val replacementMethod = if (expectedResult) IS_NULL else IS_NOT_NULL
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
                    (isPrimitive || isNumericType).map(TOKEN_TO_ASSERTJ_FOR_PRIMITIVE_MAP, TOKEN_TO_ASSERTJ_FOR_OBJECT_MAPPINGS)
                val replacementMethod = mappingToUse[tokenType] ?: return

                registerSplitBinaryExpressionMethod(holder, expression, replacementMethod, pickRightOperand = swapExpectedAndActual)
            }

            private fun registerSplitBinaryExpressionMethod(
                holder: ProblemsHolder,
                expression: PsiMethodCallExpression,
                replacementMethod: String,
                pickRightOperand: Boolean = false,
                noExpectedExpression: Boolean = false
            ) {
                val type = "binary"
                val description = SPLIT_EXPRESSION_DESCRIPTION_TEMPLATE.format(type)
                val message = MORE_MEANINGFUL_MESSAGE_TEMPLATE.format(type)
                val quickFix = SplitBinaryExpressionMethodCallQuickFix(description, replacementMethod, pickRightOperand, noExpectedExpression)
                holder.registerProblem(expression, message, quickFix)
            }
        }
    }
}
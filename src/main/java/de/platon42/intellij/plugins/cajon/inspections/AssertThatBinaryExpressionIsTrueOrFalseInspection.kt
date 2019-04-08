package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
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

        private val PRIMITIVE_MAPPINGS = listOf(
            Mapping(JavaTokenType.EQEQ, "isEqualTo()", "isNotEqualTo()"),
            Mapping(JavaTokenType.NE, "isNotEqualTo()", "isEqualTo()"),
            Mapping(JavaTokenType.GT, "isGreaterThan()", "isLessThanOrEqualTo()"),
            Mapping(JavaTokenType.GE, "isGreaterThanOrEqualTo()", "isLessThan()"),
            Mapping(JavaTokenType.LT, "isLessThan()", "isGreaterThanOrEqualTo()"),
            Mapping(JavaTokenType.LE, "isLessThanOrEqualTo()", "isGreaterThan()")
        )

        private val OBJECT_MAPPINGS = listOf(
            Mapping(JavaTokenType.EQEQ, "isSameAs()", "isNotSameAs()"),
            Mapping(JavaTokenType.NE, "isNotSameAs()", "isSameAs()")
        )

        private val SWAP_BINARY_OPERATOR = mapOf<IElementType, IElementType>(
            JavaTokenType.GT to JavaTokenType.LT,
            JavaTokenType.GE to JavaTokenType.LE,
            JavaTokenType.LT to JavaTokenType.GT,
            JavaTokenType.LE to JavaTokenType.GE
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

                val statement = PsiTreeUtil.getParentOfType(expression, PsiStatement::class.java) ?: return
                val expectedCallExpression = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java) ?: return
                val isInverted = getExpectedResult(expectedCallExpression) ?: return

                val assertThatArgument = expression.firstArg
                if (assertThatArgument is PsiMethodCallExpression && OBJECT_EQUALS.test(assertThatArgument)) {
                    val replacementMethod = if (isInverted) "isNotEqualTo()" else "isEqualTo()"
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
                    registerSplitBinaryExpressionMethod(
                        holder,
                        expression,
                        if (isInverted) "isNotNull()" else "isNull()",
                        pickRightOperand = isLeftNull,
                        noExpectedExpression = true
                    )
                    return
                }

                val isPrimitive = bothTypes.all(TypeConversionUtil::isPrimitiveAndNotNull)
                val isNumericType = bothTypes.all(TypeConversionUtil::isNumericType)
                val constantEvaluationHelper = JavaPsiFacade.getInstance(expression.project).constantEvaluationHelper
                val swapExpectedAndActual = constantEvaluationHelper.computeConstantExpression(binaryExpression.lOperand) != null

                val tokenType = binaryExpression.operationSign.tokenType.let {
                    if (swapExpectedAndActual) SWAP_BINARY_OPERATOR.getOrDefault(it, it) else it
                } ?: return
                val mappingToUse =
                    if (isPrimitive || isNumericType) {
                        PRIMITIVE_MAPPINGS
                    } else {
                        OBJECT_MAPPINGS
                    }
                val mapping = mappingToUse.find { it.tokenType == tokenType } ?: return
                val replacementMethod = if (isInverted) mapping.replacementInverted else mapping.replacement

                registerSplitBinaryExpressionMethod(holder, expression, replacementMethod, pickRightOperand = swapExpectedAndActual)
            }

            private fun getExpectedResult(expectedCallExpression: PsiMethodCallExpression): Boolean? {
                val isTrue = IS_TRUE.test(expectedCallExpression)
                val isFalse = IS_FALSE.test(expectedCallExpression)
                if (isTrue || isFalse) {
                    return isFalse
                } else {
                    val isEqualTo = IS_EQUAL_TO_BOOLEAN.test(expectedCallExpression)
                    val isNotEqualTo = IS_NOT_EQUAL_TO_BOOLEAN.test(expectedCallExpression)
                    if (isEqualTo || isNotEqualTo) {
                        val constValue = calculateConstantParameterValue(expectedCallExpression, 0) as? Boolean ?: return null
                        return isEqualTo xor constValue
                    }
                }
                return null
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

    private class Mapping(
        val tokenType: IElementType,
        val replacement: String,
        val replacementInverted: String
    )
}
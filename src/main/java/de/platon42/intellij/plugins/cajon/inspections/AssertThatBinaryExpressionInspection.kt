package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.util.TypeConversionUtil
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.MoveOutMethodCallExpressionQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.SplitBinaryExpressionMethodCallQuickFix

class AssertThatBinaryExpressionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a binary expression"
        private const val SPLIT_EXPRESSION_DESCRIPTION_TEMPLATE = "Split %s expression out of assertThat()"
        private const val MORE_MEANINGFUL_MESSAGE_TEMPLATE = "Moving %s expression out of assertThat() would be more meaningful"
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

                val expectedCallExpression = statement.findOutmostMethodCall() ?: return
                val expectedResult = expectedCallExpression.getAllTheSameExpectedBooleanConstants() ?: return

                val assertThatArgument = staticMethodCall.firstArg
                if (assertThatArgument is PsiMethodCallExpression && OBJECT_EQUALS.test(assertThatArgument)) {
                    val replacementMethod = expectedResult.map(MethodNames.IS_EQUAL_TO, MethodNames.IS_NOT_EQUAL_TO)
                    registerSplitMethod(holder, expectedCallExpression, "${MethodNames.EQUALS}()", replacementMethod) { desc, method ->
                        MoveOutMethodCallExpressionQuickFix(desc, method)
                    }
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
                    val replacementMethod = expectedResult.map(MethodNames.IS_NULL, MethodNames.IS_NOT_NULL)
                    registerSplitMethod(holder, expectedCallExpression, "binary", replacementMethod) { desc, method ->
                        SplitBinaryExpressionMethodCallQuickFix(desc, method, pickRightOperand = isLeftNull, noExpectedExpression = true)
                    }
                    return
                }

                val isPrimitive = bothTypes.all(TypeConversionUtil::isPrimitiveAndNotNull)
                val isNumericType = bothTypes.all(TypeConversionUtil::isNumericType)
                val constantEvaluationHelper = JavaPsiFacade.getInstance(statement.project).constantEvaluationHelper
                val swapExpectedAndActual = constantEvaluationHelper.computeConstantExpression(binaryExpression.lOperand) != null

                val tokenType = binaryExpression.operationTokenType
                    .let {
                        if (swapExpectedAndActual) SWAP_SIDE_OF_BINARY_OPERATOR.getOrDefault(it, it) else it
                    }
                    .let {
                        if (expectedResult) it else INVERT_BINARY_OPERATOR.getOrDefault(it, it)
                    }
                val mappingToUse =
                    (isPrimitive || isNumericType).map(TOKEN_TO_ASSERTJ_FOR_PRIMITIVE_MAP, TOKEN_TO_ASSERTJ_FOR_OBJECT_MAPPINGS)
                val replacementMethod = mappingToUse[tokenType] ?: return

                registerSplitMethod(holder, expectedCallExpression, "binary", replacementMethod) { desc, method ->
                    SplitBinaryExpressionMethodCallQuickFix(desc, method, pickRightOperand = swapExpectedAndActual)
                }
            }
        }
    }

    private fun registerSplitMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        type: String,
        replacementMethod: String,
        quickFixSupplier: (String, String) -> LocalQuickFix
    ) {
        val description = SPLIT_EXPRESSION_DESCRIPTION_TEMPLATE.format(type)
        val message = MORE_MEANINGFUL_MESSAGE_TEMPLATE.format(type)
        val quickfix = quickFixSupplier(description, replacementMethod)
        holder.registerProblem(expression, message, quickfix)
    }
}
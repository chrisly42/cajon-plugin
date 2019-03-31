package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSizeMethodCallQuickFix

class AssertThatSizeInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting the size of an collection or array"
        private const val CONCISER_MESSAGE_TEMPLATE = "%s would be conciser than %s"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!ASSERT_THAT_INT.test(expression)) {
                    return
                }
                val actualExpression = expression.argumentList.expressions[0] ?: return

                if (isArrayLength(actualExpression) || isCollectionSize(actualExpression)) {
                    val statement = PsiTreeUtil.getParentOfType(expression, PsiStatement::class.java) ?: return
                    val expectedCallExpression = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java) ?: return
                    val constValue = calculateConstantParameterValue(expectedCallExpression, 0)
                    if (IS_EQUAL_TO_INT.test(expectedCallExpression)) {
                        if (constValue == 0) {
                            registerSizeMethod(holder, expression, "isEmpty()", noExpectedExpression = true)
                            return
                        }
                        val equalToExpression = expectedCallExpression.argumentList.expressions[0]
                        if (isCollectionSize(equalToExpression) || isArrayLength(equalToExpression)) {
                            registerSizeMethod(holder, expression, "hasSameSizeAs()", expectedIsCollection = true)
                            return
                        }
                        registerSizeMethod(holder, expression, "hasSize()")
                    } else {
                        if ((IS_LESS_THAN_OR_EQUAL_TO_INT.test(expectedCallExpression) && (constValue == 0))
                            || (IS_LESS_THAN_INT.test(expectedCallExpression) && (constValue == 1))
                            || IS_ZERO.test(expectedCallExpression)
                        ) {
                            registerSizeMethod(holder, expression, "isEmpty()", noExpectedExpression = true)
                            return
                        }
                        if ((IS_GREATER_THAN_INT.test(expectedCallExpression) && (constValue == 0))
                            || (IS_GREATER_THAN_OR_EQUAL_TO_INT.test(expectedCallExpression) && (constValue == 1))
                            || IS_NOT_ZERO.test(expectedCallExpression)
                        ) {
                            registerSizeMethod(holder, expression, "isNotEmpty()", noExpectedExpression = true)
                            return
                        }
                    }
                }
            }

            private fun isCollectionSize(expression: PsiExpression) = (expression is PsiMethodCallExpression) && COLLECTION_SIZE.test(expression)

            private fun isArrayLength(expression: PsiExpression): Boolean {
                val psiReferenceExpression = expression as? PsiReferenceExpression ?: return false
                return ((psiReferenceExpression.qualifierExpression?.type is PsiArrayType)
                        && ((psiReferenceExpression.resolve() as? PsiField)?.name == "length"))
            }

            private fun registerSizeMethod(
                holder: ProblemsHolder,
                expression: PsiMethodCallExpression,
                replacementMethod: String,
                noExpectedExpression: Boolean = false,
                expectedIsCollection: Boolean = false
            ) {
                val originalMethod = getOriginalMethodName(expression) ?: return
                val description = REPLACE_DESCRIPTION_TEMPLATE.format(replacementMethod, originalMethod)
                val message = CONCISER_MESSAGE_TEMPLATE.format(originalMethod, replacementMethod)
                holder.registerProblem(
                    expression,
                    message,
                    ProblemHighlightType.INFORMATION,
                    null as TextRange?,
                    ReplaceSizeMethodCallQuickFix(description, replacementMethod, noExpectedExpression, expectedIsCollection)
                )
            }
        }
    }
}
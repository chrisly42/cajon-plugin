package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSizeMethodCallQuickFix

class AssertThatSizeInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting the size of an collection or array"
        private const val MORE_CONCISE_MESSAGE_TEMPLATE = "%s would be more concise than %s"
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
                            registerSizeMethod(holder, expression, expectedCallExpression, "isEmpty()", noExpectedExpression = true)
                            return
                        }
                        val equalToExpression = expectedCallExpression.argumentList.expressions[0]
                        if (isCollectionSize(equalToExpression) || isArrayLength(equalToExpression)) {
                            registerSizeMethod(holder, expression, expectedCallExpression, "hasSameSizeAs()", expectedIsCollection = true)
                            return
                        }
                        registerSizeMethod(holder, expression, expectedCallExpression, "hasSize()")
                    } else {
                        if ((IS_LESS_THAN_OR_EQUAL_TO_INT.test(expectedCallExpression) && (constValue == 0))
                            || (IS_LESS_THAN_INT.test(expectedCallExpression) && (constValue == 1))
                            || IS_ZERO.test(expectedCallExpression)
                        ) {
                            registerSizeMethod(holder, expression, expectedCallExpression, "isEmpty()", noExpectedExpression = true)
                            return
                        }
                        if ((IS_GREATER_THAN_INT.test(expectedCallExpression) && (constValue == 0))
                            || (IS_GREATER_THAN_OR_EQUAL_TO_INT.test(expectedCallExpression) && (constValue == 1))
                            || IS_NOT_ZERO.test(expectedCallExpression)
                        ) {
                            registerSizeMethod(holder, expression, expectedCallExpression, "isNotEmpty()", noExpectedExpression = true)
                            return
                        }
                        // new stuff in AssertJ 13.2.0
                        if (hasAssertJMethod(expression, "AbstractIterableAssert.hasSizeLessThan")) {
                            val matchedMethod = listOf(
                                Pair(IS_GREATER_THAN_INT, "hasSizeGreaterThan()"),
                                Pair(IS_GREATER_THAN_OR_EQUAL_TO_INT, "hasSizeGreaterThanOrEqualTo()"),
                                Pair(IS_LESS_THAN_OR_EQUAL_TO_INT, "hasSizeLessThanOrEqualTo()"),
                                Pair(IS_LESS_THAN_INT, "hasSizeLessThan()")
                            ).find { it.first.test(expectedCallExpression) }?.second
                            if (matchedMethod != null) {
                                registerSizeMethod(holder, expression, expectedCallExpression, matchedMethod)
                                return
                            }
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
                expectedCallExpression: PsiMethodCallExpression,
                replacementMethod: String,
                noExpectedExpression: Boolean = false,
                expectedIsCollection: Boolean = false
            ) {
                val originalMethod = getOriginalMethodName(expectedCallExpression) ?: return
                val description = REPLACE_DESCRIPTION_TEMPLATE.format(originalMethod, replacementMethod)
                val message = MORE_CONCISE_MESSAGE_TEMPLATE.format(replacementMethod, originalMethod)
                holder.registerProblem(
                    expression,
                    message,
                    ReplaceSizeMethodCallQuickFix(description, replacementMethod, noExpectedExpression, expectedIsCollection)
                )
            }
        }
    }
}
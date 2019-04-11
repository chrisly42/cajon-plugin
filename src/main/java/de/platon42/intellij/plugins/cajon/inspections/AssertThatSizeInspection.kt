package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSizeMethodCallQuickFix

class AssertThatSizeInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting the size of an collection or array"
        private val BONUS_EXPRESSIONS_CALL_MATCHER_MAP = listOf(
            IS_GREATER_THAN_INT to "hasSizeGreaterThan()",
            IS_GREATER_THAN_OR_EQUAL_TO_INT to "hasSizeGreaterThanOrEqualTo()",
            IS_LESS_THAN_OR_EQUAL_TO_INT to "hasSizeLessThanOrEqualTo()",
            IS_LESS_THAN_INT to "hasSizeLessThan()"
        )
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!ASSERT_THAT_INT.test(expression)) {
                    return
                }
                val actualExpression = expression.firstArg

                if (isArrayLength(actualExpression) || isCollectionSize(actualExpression)) {
                    val statement = PsiTreeUtil.getParentOfType(expression, PsiStatement::class.java) ?: return
                    val expectedCallExpression = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java) ?: return
                    val constValue = calculateConstantParameterValue(expectedCallExpression, 0)
                    if (IS_EQUAL_TO_INT.test(expectedCallExpression)) {
                        if (constValue == 0) {
                            registerReplaceSizeMethod(holder, expression, expectedCallExpression, "isEmpty()", noExpectedExpression = true)
                        } else {
                            val equalToExpression = expectedCallExpression.firstArg
                            if (isCollectionSize(equalToExpression) || isArrayLength(equalToExpression)) {
                                registerReplaceSizeMethod(holder, expression, expectedCallExpression, "hasSameSizeAs()", expectedIsCollection = true)
                            } else {
                                registerReplaceSizeMethod(holder, expression, expectedCallExpression, "hasSize()")
                            }
                        }
                    } else {
                        val isTestForEmpty = ((IS_LESS_THAN_OR_EQUAL_TO_INT.test(expectedCallExpression) && (constValue == 0))
                                || (IS_LESS_THAN_INT.test(expectedCallExpression) && (constValue == 1))
                                || IS_ZERO.test(expectedCallExpression))
                        val isTestForNotEmpty = ((IS_GREATER_THAN_INT.test(expectedCallExpression) && (constValue == 0))
                                || (IS_GREATER_THAN_OR_EQUAL_TO_INT.test(expectedCallExpression) && (constValue == 1))
                                || IS_NOT_ZERO.test(expectedCallExpression))
                        when {
                            isTestForEmpty -> registerReplaceSizeMethod(holder, expression, expectedCallExpression, "isEmpty()", noExpectedExpression = true)
                            isTestForNotEmpty -> registerReplaceSizeMethod(holder, expression, expectedCallExpression, "isNotEmpty()", noExpectedExpression = true)
                            // new stuff in AssertJ 13.2.0
                            hasAssertJMethod(expression, "AbstractIterableAssert.hasSizeLessThan") -> {
                                val matchedMethod = BONUS_EXPRESSIONS_CALL_MATCHER_MAP.find { it.first.test(expectedCallExpression) }?.second ?: return
                                registerReplaceSizeMethod(holder, expression, expectedCallExpression, matchedMethod)
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

            private fun registerReplaceSizeMethod(
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
                val quickfix = ReplaceSizeMethodCallQuickFix(description, replacementMethod, noExpectedExpression, expectedIsCollection)
                holder.registerProblem(expression, message, quickfix)
            }
        }
    }
}
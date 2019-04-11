package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_ITERABLE_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.map
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSizeMethodCallQuickFix

class AssertThatSizeInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting the size of an collection or array"

        private val BONUS_EXPRESSIONS_CALL_MATCHER_MAP = listOf(
            IS_LESS_THAN_INT to MethodNames.HAS_SIZE_LESS_THAN,
            IS_LESS_THAN_OR_EQUAL_TO_INT to MethodNames.HAS_SIZE_LESS_THAN_OR_EQUAL_TO,
            IS_GREATER_THAN_INT to MethodNames.HAS_SIZE_GREATER_THAN,
            IS_GREATER_THAN_OR_EQUAL_TO_INT to MethodNames.HAS_SIZE_GREATER_THAN_OR_EQUAL_TO
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
                            registerReplaceSizeMethod(holder, expression, expectedCallExpression, MethodNames.IS_EMPTY, noExpectedExpression = true)
                        } else {
                            val equalToExpression = expectedCallExpression.firstArg
                            if (isCollectionSize(equalToExpression) || isArrayLength(equalToExpression)) {
                                registerReplaceSizeMethod(holder, expression, expectedCallExpression, MethodNames.HAS_SAME_SIZE_AS, expectedIsCollection = true)
                            } else {
                                registerReplaceSizeMethod(holder, expression, expectedCallExpression, MethodNames.HAS_SIZE)
                            }
                        }
                    } else {
                        val isTestForEmpty = ((IS_LESS_THAN_OR_EQUAL_TO_INT.test(expectedCallExpression) && (constValue == 0))
                                || (IS_LESS_THAN_INT.test(expectedCallExpression) && (constValue == 1))
                                || IS_ZERO.test(expectedCallExpression))
                        val isTestForNotEmpty = ((IS_GREATER_THAN_INT.test(expectedCallExpression) && (constValue == 0))
                                || (IS_GREATER_THAN_OR_EQUAL_TO_INT.test(expectedCallExpression) && (constValue == 1))
                                || IS_NOT_ZERO.test(expectedCallExpression))
                        if (isTestForEmpty || isTestForNotEmpty) {
                            val replacementMethod = isTestForEmpty.map(MethodNames.IS_EMPTY, MethodNames.IS_NOT_EMPTY)
                            registerReplaceSizeMethod(holder, expression, expectedCallExpression, replacementMethod, noExpectedExpression = true)
                        } else if (hasAssertJMethod(expression, ABSTRACT_ITERABLE_ASSERT_CLASSNAME, MethodNames.HAS_SIZE_LESS_THAN)) {
                            // new stuff in AssertJ 13.2.0
                            val matchedMethod = BONUS_EXPRESSIONS_CALL_MATCHER_MAP.find { it.first.test(expectedCallExpression) }?.second ?: return
                            registerReplaceSizeMethod(holder, expression, expectedCallExpression, matchedMethod)
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
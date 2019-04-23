package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_ITERABLE_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSizeMethodCallQuickFix

class AssertThatSizeInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting the size of an collection, array or string"
        private const val REMOVE_SIZE_DESCRIPTION_TEMPLATE = "Remove size determination of expected expression and replace %s() with %s()"

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
                val isAssertThatWithInt = ASSERT_THAT_INT.test(expression)
                val isHasSize = HAS_SIZE.test(expression)
                if (!(isAssertThatWithInt || isHasSize)) {
                    return
                }
                val actualExpression = expression.firstArg

                val isForArrayOrCollection = isArrayLength(actualExpression) || isCollectionSize(actualExpression)
                val isForString = isCharSequenceLength(actualExpression)
                if (isHasSize && (isForArrayOrCollection
                            || (isForString && checkAssertedType(expression, ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME)))
                ) {
                    val assertThatCall = PsiTreeUtil.findChildrenOfType(expression, PsiMethodCallExpression::class.java).find { CORE_ASSERT_THAT_MATCHER.test(it) } ?: return
                    registerConciseMethod(
                        REMOVE_SIZE_DESCRIPTION_TEMPLATE,
                        holder,
                        assertThatCall,
                        expression,
                        MethodNames.HAS_SAME_SIZE_AS
                    ) { desc, method ->
                        ReplaceSizeMethodCallQuickFix(desc, method, expectedIsCollection = true, keepActualAsIs = true)
                    }
                } else if (isForArrayOrCollection || isForString) {
                    val expectedCallExpression = expression.findOutmostMethodCall() ?: return
                    val constValue = calculateConstantParameterValue(expectedCallExpression, 0)
                    if (IS_EQUAL_TO_INT.test(expectedCallExpression)) {
                        if (constValue == 0) {
                            registerReplaceMethod(holder, expression, expectedCallExpression, MethodNames.IS_EMPTY) { desc, method ->
                                ReplaceSizeMethodCallQuickFix(desc, method, noExpectedExpression = true)
                            }
                        } else {
                            val equalToExpression = expectedCallExpression.firstArg
                            if (isForArrayOrCollection && (isCollectionSize(equalToExpression) || isArrayLength(equalToExpression)) ||
                                isForString && (isCollectionSize(equalToExpression) || isArrayLength(equalToExpression) || isCharSequenceLength(equalToExpression))
                            ) {
                                registerReplaceMethod(holder, expression, expectedCallExpression, MethodNames.HAS_SAME_SIZE_AS) { desc, method ->
                                    ReplaceSizeMethodCallQuickFix(desc, method, expectedIsCollection = true)
                                }
                            } else {
                                registerReplaceMethod(holder, expression, expectedCallExpression, MethodNames.HAS_SIZE) { desc, method ->
                                    ReplaceSizeMethodCallQuickFix(desc, method)
                                }
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
                            registerReplaceMethod(holder, expression, expectedCallExpression, replacementMethod) { desc, method ->
                                ReplaceSizeMethodCallQuickFix(desc, method, noExpectedExpression = true)
                            }
                        } else if (hasAssertJMethod(expression, ABSTRACT_ITERABLE_ASSERT_CLASSNAME, MethodNames.HAS_SIZE_LESS_THAN)) {
                            // new stuff in AssertJ 13.2.0
                            val matchedMethod = BONUS_EXPRESSIONS_CALL_MATCHER_MAP.find { it.first.test(expectedCallExpression) }?.second ?: return
                            registerReplaceMethod(holder, expression, expectedCallExpression, matchedMethod) { desc, method ->
                                ReplaceSizeMethodCallQuickFix(desc, method)
                            }
                        }
                    }
                }
            }

            private fun isCharSequenceLength(expression: PsiExpression) = (expression is PsiMethodCallExpression) && CHAR_SEQUENCE_LENGTH.test(expression)

            private fun isCollectionSize(expression: PsiExpression) = (expression is PsiMethodCallExpression) && COLLECTION_SIZE.test(expression)

            private fun isArrayLength(expression: PsiExpression): Boolean {
                val psiReferenceExpression = expression as? PsiReferenceExpression ?: return false
                return ((psiReferenceExpression.qualifierExpression?.type is PsiArrayType)
                        && ((psiReferenceExpression.resolve() as? PsiField)?.name == "length"))
            }
        }
    }
}
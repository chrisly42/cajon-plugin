package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_ITERABLE_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceHasSizeMethodCallQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSizeMethodCallQuickFix

class AssertThatSizeInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting the size of an collection, map, array or string"
        private const val REMOVE_SIZE_DESCRIPTION_TEMPLATE = "Remove size determination of expected expression and replace %s() with %s()"
        private const val REMOVE_ALL_MESSAGE = "Try to operate on the iterable itself rather than its size"

        private val BONUS_EXPRESSIONS_CALL_MATCHER_MAP = listOf(
            IS_LESS_THAN_INT to MethodNames.HAS_SIZE_LESS_THAN,
            IS_LESS_THAN_OR_EQUAL_TO_INT to MethodNames.HAS_SIZE_LESS_THAN_OR_EQUAL_TO,
            IS_GREATER_THAN_INT to MethodNames.HAS_SIZE_GREATER_THAN,
            IS_GREATER_THAN_OR_EQUAL_TO_INT to MethodNames.HAS_SIZE_GREATER_THAN_OR_EQUAL_TO
        )

        private fun isCharSequenceLength(expression: PsiExpression) = (expression is PsiMethodCallExpression) && CHAR_SEQUENCE_LENGTH.test(expression)

        private fun isCollectionSize(expression: PsiExpression) = (expression is PsiMethodCallExpression) && COLLECTION_SIZE.test(expression)

        private fun isMapSize(expression: PsiExpression) = (expression is PsiMethodCallExpression) && MAP_SIZE.test(expression)

        private fun isArrayLength(expression: PsiExpression): Boolean {
            val psiReferenceExpression = expression as? PsiReferenceExpression ?: return false
            return ((psiReferenceExpression.qualifierExpression?.type is PsiArrayType)
                    && ((psiReferenceExpression.resolve() as? PsiField)?.name == "length"))
        }

        fun getMatch(expression: PsiMethodCallExpression, isForArrayCollectionOrMap: Boolean, isForString: Boolean): Match? {
            val isLastExpression = expression.parent is PsiStatement
            val constValue = expression.calculateConstantParameterValue(0)
            if (IS_EQUAL_TO_INT.test(expression)) {
                return if ((constValue == 0) && isLastExpression) {
                    Match(expression, MethodNames.IS_EMPTY, noExpectedExpression = true)
                } else {
                    val equalToExpression = expression.firstArg
                    val equalsArrayCollectionOrMapSize = isArrayLength(equalToExpression) ||
                            isCollectionSize(equalToExpression) || isMapSize(equalToExpression)
                    if (isForArrayCollectionOrMap && equalsArrayCollectionOrMapSize ||
                        isForString && (equalsArrayCollectionOrMapSize || isCharSequenceLength(equalToExpression))
                    ) {
                        Match(expression, MethodNames.HAS_SAME_SIZE_AS, expectedIsCollection = true)
                    } else {
                        Match(expression, MethodNames.HAS_SIZE)
                    }
                }
            } else {
                val isTestForEmpty = ((IS_LESS_THAN_OR_EQUAL_TO_INT.test(expression) && (constValue == 0))
                        || (IS_LESS_THAN_INT.test(expression) && (constValue == 1))
                        || IS_ZERO.test(expression))
                val isTestForNotEmpty = ((IS_GREATER_THAN_INT.test(expression) && (constValue == 0))
                        || (IS_GREATER_THAN_OR_EQUAL_TO_INT.test(expression) && (constValue == 1))
                        || IS_NOT_ZERO.test(expression))
                if ((isTestForEmpty && isLastExpression) || isTestForNotEmpty) {
                    val replacementMethod = isTestForEmpty.map(MethodNames.IS_EMPTY, MethodNames.IS_NOT_EMPTY)
                    return Match(expression, replacementMethod, noExpectedExpression = true)
                } else if (hasAssertJMethod(expression, ABSTRACT_ITERABLE_ASSERT_CLASSNAME, MethodNames.HAS_SIZE_LESS_THAN)) {
                    // new stuff in AssertJ 3.12.0
                    val replacementMethod = BONUS_EXPRESSIONS_CALL_MATCHER_MAP.find { it.first.test(expression) }?.second ?: return null
                    return Match(expression, replacementMethod)
                }
                return null
            }
        }
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                if (!statement.hasAssertThat()) return
                val staticMethodCall = statement.findStaticMethodCall() ?: return
                if (!ASSERT_THAT_INT.test(staticMethodCall)) return

                val actualExpression = staticMethodCall.firstArg
                val isForArrayCollectionOrMap = isArrayLength(actualExpression) || isCollectionSize(actualExpression) || isMapSize(actualExpression)
                val isForString = isCharSequenceLength(actualExpression)
                if (!(isForArrayCollectionOrMap || isForString)) return

                val matches = staticMethodCall.collectMethodCallsUpToStatement()
                    .mapNotNull { getMatch(it, isForArrayCollectionOrMap, isForString) }
                    .toList()
                if (matches.isNotEmpty()) {
                    if (matches.size == 1) {
                        val match = matches.single()
                        val expression = match.methodCall
                        registerReplaceMethod(
                            holder,
                            expression,
                            expression,
                            match.replacementMethod
                        )
                        { desc, method ->
                            ReplaceSizeMethodCallQuickFix(desc, method, noExpectedExpression = match.noExpectedExpression, expectedIsCollection = match.expectedIsCollection)
                        }
                    } else {
                        // I could try to create a quickfix for this, too, but it's probably not worth the effort
                        holder.registerProblem(statement, REMOVE_ALL_MESSAGE)
                    }
                }
            }

            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!expression.hasAssertThat()) return
                if (!HAS_SIZE.test(expression)) return
                val actualExpression = expression.firstArg

                val isForArrayCollectionOrMap = isArrayLength(actualExpression) || isCollectionSize(actualExpression) || isMapSize(actualExpression)
                val isForString = isCharSequenceLength(actualExpression)
                if (!(isForArrayCollectionOrMap
                            || (isForString && checkAssertedType(expression, ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME)))
                ) return

                registerConciseMethod(
                    REMOVE_SIZE_DESCRIPTION_TEMPLATE,
                    holder,
                    expression,
                    expression,
                    MethodNames.HAS_SAME_SIZE_AS,
                    ::ReplaceHasSizeMethodCallQuickFix
                )
            }
        }
    }

    class Match(
        val methodCall: PsiMethodCallExpression,
        val replacementMethod: String,
        val noExpectedExpression: Boolean = false,
        val expectedIsCollection: Boolean = false
    )
}
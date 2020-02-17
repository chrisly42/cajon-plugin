package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.SwapActualAndExpectedExpressionMethodCallQuickFix

class TwistedAssertionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Twisted or suspicious actual and expected expressions"
        private const val TWISTED_ACTUAL_AND_EXPECTED_MESSAGE = "Twisted actual and expected expressions in assertion"
        private const val SWAP_ACTUAL_AND_EXPECTED_DESCRIPTION = "Swap actual and expected expressions in assertion"
        private const val SWAP_ACTUAL_AND_EXPECTED_AND_REPLACE_DESCRIPTION_TEMPLATE = "Replace %s() by %s() and swap actual and expected expressions"
        private const val ACTUAL_IS_A_CONSTANT_MESSAGE = "Actual expression in assertThat() is a constant"

        private val GENERIC_IS_EQUAL_TO = CallMatcher.instanceCall(AssertJClassNames.ASSERT_INTERFACE, MethodNames.IS_EQUAL_TO).parameterCount(1)
        private val GENERIC_IS_NOT_EQUAL_TO = CallMatcher.instanceCall(AssertJClassNames.ASSERT_INTERFACE, MethodNames.IS_NOT_EQUAL_TO).parameterCount(1)
        private val GENERIC_IS_SAME_AS = CallMatcher.instanceCall(AssertJClassNames.ASSERT_INTERFACE, MethodNames.IS_SAME_AS).parameterCount(1)
        private val GENERIC_IS_NOT_SAME_AS = CallMatcher.instanceCall(AssertJClassNames.ASSERT_INTERFACE, MethodNames.IS_NOT_SAME_AS).parameterCount(1)
        private val GENERIC_IS_GREATER_THAN = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_COMPARABLE_ASSERT_CLASSNAME, MethodNames.IS_GREATER_THAN).parameterCount(1)
        private val GENERIC_IS_GREATER_THAN_OR_EQUAL_TO =
            CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_COMPARABLE_ASSERT_CLASSNAME, MethodNames.IS_GREATER_THAN_OR_EQUAL_TO).parameterCount(1)
        private val GENERIC_IS_LESS_THAN = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_COMPARABLE_ASSERT_CLASSNAME, MethodNames.IS_LESS_THAN).parameterCount(1)
        private val GENERIC_IS_LESS_THAN_OR_EQUAL_TO =
            CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_COMPARABLE_ASSERT_CLASSNAME, MethodNames.IS_LESS_THAN_OR_EQUAL_TO).parameterCount(1)

        private val STRING_IS_EQUAL_TO_IC = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME, MethodNames.IS_EQUAL_TO_IC).parameterCount(1)

        private val STRING_REGEX_MATCHING = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME, "matches", "doesNotMatch").parameterCount(1)

        private val CALL_MATCHER_TO_REPLACEMENT_MAP = mapOf(
            GENERIC_IS_EQUAL_TO to MethodNames.IS_EQUAL_TO,
            GENERIC_IS_NOT_EQUAL_TO to MethodNames.IS_NOT_EQUAL_TO,
            GENERIC_IS_SAME_AS to MethodNames.IS_SAME_AS,
            GENERIC_IS_NOT_SAME_AS to MethodNames.IS_NOT_SAME_AS,
            GENERIC_IS_GREATER_THAN to MethodNames.IS_LESS_THAN_OR_EQUAL_TO,
            GENERIC_IS_GREATER_THAN_OR_EQUAL_TO to MethodNames.IS_LESS_THAN,
            GENERIC_IS_LESS_THAN to MethodNames.IS_GREATER_THAN_OR_EQUAL_TO,
            GENERIC_IS_LESS_THAN_OR_EQUAL_TO to MethodNames.IS_GREATER_THAN,

            STRING_IS_EQUAL_TO_IC to MethodNames.IS_EQUAL_TO_IC,
            CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME, MethodNames.IS_NOT_EQUAL_TO_IC).parameterCount(1)
                    to MethodNames.IS_NOT_EQUAL_TO_IC
        )
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                if (!statement.hasAssertThat()) return
                val assertThatCall = PsiTreeUtil.findChildrenOfType(statement, PsiMethodCallExpression::class.java).find { ALL_ASSERT_THAT_MATCHERS.test(it) } ?: return
                val actualExpression = assertThatCall.firstArg
                actualExpression.calculateConstantValue() ?: return
                val allCalls = assertThatCall.collectMethodCallsUpToStatement().toList()
                val tooComplex = allCalls.find(USING_COMPARATOR::test) != null
                var severity = ProblemHighlightType.GENERIC_ERROR_OR_WARNING
                if (actualExpression.type is PsiClassType) {
                    val psiManager = PsiManager.getInstance(statement.project)
                    val javaLangClass = PsiType.getJavaLangClass(psiManager, GlobalSearchScope.allScope(statement.project))
                    if (actualExpression.type!!.isAssignableFrom(javaLangClass)) {
                        return
                    }
                }

                if (!tooComplex) {
                    val onlyAssertionCalls = allCalls
                        .filterNot(NOT_ACTUAL_ASSERTIONS::test)
                        .toList()
                    if (onlyAssertionCalls.size == 1) {
                        val expectedMethodCall = onlyAssertionCalls.first()
                        if (STRING_REGEX_MATCHING.test(expectedMethodCall)) {
                            return
                        }
                        if (expectedMethodCall.getArgOrNull(0)?.calculateConstantValue() == null) {
                            val matchedMethod = CALL_MATCHER_TO_REPLACEMENT_MAP.asSequence().firstOrNull { it.key.test(expectedMethodCall) }
                            if (matchedMethod != null) {
                                val originalMethodName = getOriginalMethodName(expectedMethodCall)
                                val replacementMethod = matchedMethod.value
                                val description = if (originalMethodName == replacementMethod) {
                                    SWAP_ACTUAL_AND_EXPECTED_DESCRIPTION
                                } else {
                                    SWAP_ACTUAL_AND_EXPECTED_AND_REPLACE_DESCRIPTION_TEMPLATE.format(originalMethodName, replacementMethod)
                                }
                                holder.registerProblem(
                                    statement,
                                    TWISTED_ACTUAL_AND_EXPECTED_MESSAGE,
                                    SwapActualAndExpectedExpressionMethodCallQuickFix(description, replacementMethod)
                                )
                                return
                            }
                        } else {
                            severity = ProblemHighlightType.WEAK_WARNING
                        }
                    }
                }
                holder.registerProblem(statement, ACTUAL_IS_A_CONSTANT_MESSAGE, severity)
            }
        }
    }
}
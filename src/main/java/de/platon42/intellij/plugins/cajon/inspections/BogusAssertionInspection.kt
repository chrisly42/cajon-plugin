package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiExpressionStatement
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.util.PsiTreeUtil
import com.siyeh.ig.callMatcher.CallMatcher
import com.siyeh.ig.psiutils.EquivalenceChecker
import de.platon42.intellij.plugins.cajon.*

class BogusAssertionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Bogus assertion due to same actual and expected expressions"
        private const val ACTUAL_IS_EQUAL_TO_EXPECTED_MESSAGE = "Actual expression in assertThat() is the same as expected"

        private val SAME_OBJECT =
            CallMatcher.instanceCall(
                AssertJClassNames.ASSERT_INTERFACE,
                MethodNames.IS_EQUAL_TO,
                MethodNames.IS_SAME_AS,
                "hasSameClassAs",
                "hasSameHashCodeAs"
            ).parameterCount(1)

        private val ARRAY_METHODS = arrayOf(
            "hasSameSizeAs",
            MethodNames.CONTAINS,
            "containsAnyOf",
            "containsExactly",
            "containsExactlyInAnyOrder",
            "containsOnly",
            "containsSequence",
            "containsSubsequence",
            "startsWith",
            "endsWith"
        )

        private val SAME_BOOLEAN_ARRAY_CONTENTS =
            CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_BOOLEAN_ARRAY_ASSERT_CLASSNAME, *ARRAY_METHODS).parameterCount(1)
        private val SAME_BYTE_ARRAY_CONTENTS =
            CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_BYTE_ARRAY_ASSERT_CLASSNAME, *ARRAY_METHODS).parameterCount(1)
        private val SAME_SHORT_ARRAY_CONTENTS =
            CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_SHORT_ARRAY_ASSERT_CLASSNAME, *ARRAY_METHODS).parameterCount(1)
        private val SAME_INT_ARRAY_CONTENTS =
            CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_INT_ARRAY_ASSERT_CLASSNAME, *ARRAY_METHODS).parameterCount(1)
        private val SAME_LONG_ARRAY_CONTENTS =
            CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_LONG_ARRAY_ASSERT_CLASSNAME, *ARRAY_METHODS).parameterCount(1)
        private val SAME_FLOAT_ARRAY_CONTENTS =
            CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_FLOAT_ARRAY_ASSERT_CLASSNAME, *ARRAY_METHODS).parameterCount(1)
        private val SAME_DOUBLE_ARRAY_CONTENTS =
            CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_DOUBLE_ARRAY_ASSERT_CLASSNAME, *ARRAY_METHODS).parameterCount(1)
        private val SAME_CHAR_ARRAY_CONTENTS =
            CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_CHAR_ARRAY_ASSERT_CLASSNAME, *ARRAY_METHODS).parameterCount(1)
        private val SAME_OBJECT_ARRAY_CONTENTS =
            CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_OBJECT_ARRAY_ASSERT_CLASSNAME, *ARRAY_METHODS).parameterCount(1)

        private val SAME_ENUMERABLE_CONTENTS =
            CallMatcher.instanceCall(
                AssertJClassNames.ENUMERABLE_ASSERT_INTERFACE,
                MethodNames.HAS_SAME_SIZE_AS
            ).parameterCount(1)

        private val SAME_ITERABLE_CONTENTS =
            CallMatcher.instanceCall(
                AssertJClassNames.ABSTRACT_ITERABLE_ASSERT_CLASSNAME,
                "hasSameElementsAs",
                MethodNames.CONTAINS_ALL,
                "containsAnyElementsOf",
                "containsOnlyElementsOf",
                "containsExactlyElementsOf",
                "containsSequence",
                "containsSubsequence"
            ).parameterCount(1)

        private val SAME_MAP_CONTENTS =
            CallMatcher.instanceCall(
                AssertJClassNames.ABSTRACT_MAP_ASSERT_CLASSNAME,
                "containsAllEntriesOf",
                "containsExactlyEntriesOf",
                "containsExactlyInAnyOrderEntriesOf",
                MethodNames.HAS_SAME_SIZE_AS
            ).parameterCount(1)

        private val SAME_CHAR_SEQUENCE_CONTENTS =
            CallMatcher.instanceCall(
                AssertJClassNames.ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME,
                MethodNames.IS_EQUAL_TO,
                MethodNames.IS_EQUAL_TO_IC,
                MethodNames.STARTS_WITH,
                MethodNames.ENDS_WITH,
                "containsSequence",
                "containsSubsequence"
            ).parameterCount(1)

        private val SAME_ACTUAL_AND_EXPECTED_MATCHERS = CallMatcher.anyOf(
            SAME_OBJECT,
            SAME_ENUMERABLE_CONTENTS,
            SAME_ITERABLE_CONTENTS,
            SAME_MAP_CONTENTS,
            SAME_CHAR_SEQUENCE_CONTENTS,

            SAME_BOOLEAN_ARRAY_CONTENTS,
            SAME_BYTE_ARRAY_CONTENTS,
            SAME_SHORT_ARRAY_CONTENTS,
            SAME_INT_ARRAY_CONTENTS,
            SAME_LONG_ARRAY_CONTENTS,
            SAME_FLOAT_ARRAY_CONTENTS,
            SAME_DOUBLE_ARRAY_CONTENTS,
            SAME_CHAR_ARRAY_CONTENTS,
            SAME_OBJECT_ARRAY_CONTENTS
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
                val allCalls = assertThatCall.collectMethodCallsUpToStatement().toList()
                // Note: replace with TrackingEquivalenceChecker() for IDEA >= 2019.1
                val equivalenceChecker = EquivalenceChecker.getCanonicalPsiEquivalence()!!
                val isSameExpression = allCalls
                    .filter(SAME_ACTUAL_AND_EXPECTED_MATCHERS::test)
                    .any { equivalenceChecker.expressionsAreEquivalent(actualExpression, it.firstArg) }
                if (isSameExpression) {
                    holder.registerProblem(statement, ACTUAL_IS_EQUAL_TO_EXPECTED_MESSAGE)
                }
            }
        }
    }
}
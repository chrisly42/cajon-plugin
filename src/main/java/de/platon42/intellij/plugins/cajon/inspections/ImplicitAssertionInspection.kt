package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.DeleteMethodCallQuickFix

class ImplicitAssertionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting implicitly covered conditions"
        private const val DELETE_IMPLICIT_DESCRIPTION_TEMPLATE = "Delete implicit %s() covered by %s()"
        private const val SURPLUS_ASSERTION_MESSAGE = "Implicit %s() assertion is covered by %s()"

        private val IS_PRESENT = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_OPTIONAL_ASSERT_CLASSNAME, MethodNames.IS_PRESENT, MethodNames.IS_NOT_EMPTY)
            .parameterCount(0)!!
        private val IS_NOT_PRESENT = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_OPTIONAL_ASSERT_CLASSNAME, MethodNames.IS_NOT_PRESENT, MethodNames.IS_EMPTY)
            .parameterCount(0)!!
        private val OPTIONAL_CONTAINS =
            CallMatcher.instanceCall(
                AssertJClassNames.ABSTRACT_OPTIONAL_ASSERT_CLASSNAME,
                MethodNames.CONTAINS, MethodNames.CONTAINS_SAME,
                "hasValue", "hasValueSatisfying", "containsInstanceOf"
            ).parameterCount(1)!!

        private val OBJECT_ENUMERABLE_ANY_CONTENT_ASSERTIONS = CallMatcher.instanceCall(
            AssertJClassNames.OBJECT_ENUMERABLE_ASSERT_INTERFACE,
            MethodNames.CONTAINS, "containsOnly", "containsOnlyNulls", MethodNames.CONTAINS_ONLY_ONCE,
            MethodNames.CONTAINS_EXACTLY, "containsExactlyInAnyOrder", "containsExactlyInAnyOrderElementsOf",
            MethodNames.CONTAINS_ALL, "containsAnyOf",
            "containsAnyElementsOf", "containsExactlyElementsOf", "containsOnlyElementsOf",
            "isSubsetOf", "containsSequence", "containsSubsequence",
            "doesNotContainSequence", "doesNotContainSubsequence", MethodNames.DOES_NOT_CONTAIN,
            "doesNotContainAnyElementsOf", "doesNotHaveDuplicates",
            MethodNames.STARTS_WITH, MethodNames.ENDS_WITH, "containsNull", "doesNotContainNull",
            "are", "areNot", "have", "doNotHave", "areAtLeastOne", "areAtLeast", "areAtMost", "areExactly",
            "haveAtLeastOne", "haveAtLeast", "haveAtMost", "haveExactly",
            "hasAtLeastOneElementOfType", "hasOnlyElementsOfType", "hasOnlyElementsOfTypes",
            "doesNotHaveAnyElementsOfTypes",
            "has", "doesNotHave",
            "singleElement", "hasOnlyOneElementSatisfying", "hasSameElementsAs",
            "allMatch", "allSatisfy", "anyMatch", "anySatisfy", "noneMatch", "noneSatisfy"
        )!!

        private val OBJECT_ENUMERABLE_AT_LEAST_ONE_CONTENT_ASSERTIONS = CallMatcher.instanceCall(
            AssertJClassNames.OBJECT_ENUMERABLE_ASSERT_INTERFACE,
            "containsOnlyNulls",
            MethodNames.STARTS_WITH, MethodNames.ENDS_WITH, "containsNull",
            "areAtLeastOne",
            "haveAtLeastOne",
            "hasAtLeastOneElementOfType",
            "anyMatch", "anySatisfy"
        )!!

        private val ENUMERABLE_NON_NULL_ASSERTIONS = CallMatcher.instanceCall(
            AssertJClassNames.ENUMERABLE_ASSERT_INTERFACE,
            MethodNames.IS_EMPTY, MethodNames.IS_NOT_EMPTY,
            MethodNames.HAS_SIZE, MethodNames.HAS_SIZE_GREATER_THAN, MethodNames.HAS_SIZE_GREATER_THAN_OR_EQUAL_TO,
            MethodNames.HAS_SIZE_LESS_THAN, MethodNames.HAS_SIZE_LESS_THAN_OR_EQUAL_TO,
            "hasSizeBetween", MethodNames.HAS_SAME_SIZE_AS
        )!!

        private val ENUMERABLE_AT_LEAST_ONE_CONTENT_ASSERTIONS = CallMatcher.instanceCall(
            AssertJClassNames.ENUMERABLE_ASSERT_INTERFACE,
            MethodNames.HAS_SIZE, MethodNames.HAS_SIZE_GREATER_THAN,
            MethodNames.HAS_SAME_SIZE_AS
        )!!

        private val NON_NULL_CORE_ASSERTIONS = CallMatcher.instanceCall(
            AssertJClassNames.ASSERT_INTERFACE,
            MethodNames.IS_INSTANCE_OF, "isInstanceOfSatisfying", "isInstanceOfAny", "isExactlyInstanceOf", "isOfAnyClassIn",
            MethodNames.IS_NOT_INSTANCE_OF, "isNotInstanceOfAny", "isNotExactlyInstanceOf", "isNotOfAnyClassIn",
            "hasSameClassAs", "doesNotHaveSameClassAs",
            MethodNames.HAS_TO_STRING, "hasSameHashCodeAs"
        )!!

        private val GUAVA_IS_PRESENT = CallMatcher.instanceCall(AssertJClassNames.GUAVA_OPTIONAL_ASSERTIONS_CLASSNAME, MethodNames.IS_PRESENT)
            .parameterCount(0)!!
        private val GUAVA_IS_ABSENT = CallMatcher.instanceCall(AssertJClassNames.GUAVA_OPTIONAL_ASSERTIONS_CLASSNAME, MethodNames.IS_ABSENT)
            .parameterCount(0)!!
        private val GUAVA_OPTIONAL_CONTAINS = CallMatcher.instanceCall(
            AssertJClassNames.GUAVA_OPTIONAL_ASSERTIONS_CLASSNAME,
            MethodNames.CONTAINS, "extractingValue", "extractingCharSequence"
        )!!

        private val MAPPINGS = listOf(
            IS_NOT_NULL to CallMatcher.anyOf(
                NON_NULL_CORE_ASSERTIONS,
                ENUMERABLE_NON_NULL_ASSERTIONS,
                OBJECT_ENUMERABLE_ANY_CONTENT_ASSERTIONS,
                IS_PRESENT, IS_NOT_PRESENT, OPTIONAL_CONTAINS,
                GUAVA_IS_PRESENT, GUAVA_IS_ABSENT, GUAVA_OPTIONAL_CONTAINS
            )!!,

            IS_NOT_EMPTY to CallMatcher.anyOf(
                ENUMERABLE_AT_LEAST_ONE_CONTENT_ASSERTIONS,
                OBJECT_ENUMERABLE_AT_LEAST_ONE_CONTENT_ASSERTIONS
            )!!,

            IS_PRESENT to OPTIONAL_CONTAINS,
            GUAVA_IS_PRESENT to GUAVA_OPTIONAL_CONTAINS
        )
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!expression.hasAssertThat()) return

                val mapping = MAPPINGS.firstOrNull { it.first.test(expression) } ?: return
                val followupExpression = expression.findFluentCallTo(mapping.second) ?: return
                val redundantName = getOriginalMethodName(expression) ?: return
                val followupName = getOriginalMethodName(followupExpression) ?: return
                val description = DELETE_IMPLICIT_DESCRIPTION_TEMPLATE.format(redundantName, followupName)
                val quickFix = DeleteMethodCallQuickFix(description)

                val textRange = TextRange(expression.qualifierExpression.textLength, expression.textLength)
                holder.registerProblem(expression, textRange, SURPLUS_ASSERTION_MESSAGE.format(redundantName, followupName), quickFix)
            }
        }
    }
}
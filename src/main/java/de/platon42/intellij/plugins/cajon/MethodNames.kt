package de.platon42.intellij.plugins.cajon

import org.jetbrains.annotations.NonNls

class MethodNames {

    companion object {

        @NonNls
        const val EQUALS = "equals"

        @NonNls
        const val ASSERT_THAT = "assertThat"

        @NonNls
        const val ASSUME_THAT = "assumeThat"

        @NonNls
        const val AS = "as"
        @NonNls
        const val DESCRIBED_AS = "describedAs"
        @NonNls
        const val IN_HEXADECIMAL = "inHexadecimal"
        @NonNls
        const val IN_BINARY = "inBinary"

        @NonNls
        const val IS_EQUAL_TO = "isEqualTo"
        @NonNls
        const val IS_NOT_EQUAL_TO = "isNotEqualTo"
        @NonNls
        const val IS_SAME_AS = "isSameAs"
        @NonNls
        const val IS_NOT_SAME_AS = "isNotSameAs"

        @NonNls
        const val HAS_TO_STRING = "hasToString"

        @NonNls
        const val IS_GREATER_THAN = "isGreaterThan"
        @NonNls
        const val IS_GREATER_THAN_OR_EQUAL_TO = "isGreaterThanOrEqualTo"
        @NonNls
        const val IS_LESS_THAN = "isLessThan"
        @NonNls
        const val IS_LESS_THAN_OR_EQUAL_TO = "isLessThanOrEqualTo"
        @NonNls
        const val IS_ZERO = "isZero"
        @NonNls
        const val IS_NOT_ZERO = "isNotZero"
        @NonNls
        const val IS_TRUE = "isTrue"
        @NonNls
        const val IS_FALSE = "isFalse"
        @NonNls
        const val IS_NULL = "isNull" // terminal, returns void
        @NonNls
        const val IS_NOT_NULL = "isNotNull"
        @NonNls
        const val IS_CLOSE_TO = "isCloseTo"
        @NonNls
        const val IS_NOT_CLOSE_TO = "isNotCloseTo"
        @NonNls
        const val IS_INSTANCE_OF = "isInstanceOf"
        @NonNls
        const val IS_NOT_INSTANCE_OF = "isNotInstanceOf"

        @NonNls
        const val IS_NULL_OR_EMPTY = "isNullOrEmpty" // terminal, returns void
        @NonNls
        const val IS_EMPTY = "isEmpty" // terminal, returns void
        @NonNls
        const val IS_NOT_EMPTY = "isNotEmpty"
        @NonNls
        const val HAS_SIZE = "hasSize"
        @NonNls
        const val HAS_SIZE_LESS_THAN = "hasSizeLessThan"
        @NonNls
        const val HAS_SIZE_LESS_THAN_OR_EQUAL_TO = "hasSizeLessThanOrEqualTo"
        @NonNls
        const val HAS_SIZE_GREATER_THAN = "hasSizeGreaterThan"
        @NonNls
        const val HAS_SIZE_GREATER_THAN_OR_EQUAL_TO = "hasSizeGreaterThanOrEqualTo"
        @NonNls
        const val HAS_SAME_SIZE_AS = "hasSameSizeAs"
        @NonNls
        const val CONTAINS = "contains"
        @NonNls
        const val CONTAINS_ONLY_ONCE = "containsOnlyOnce"
        @NonNls
        const val DOES_NOT_CONTAIN = "doesNotContain"
        @NonNls
        const val CONTAINS_EXACTLY = "containsExactly"
        @NonNls
        const val CONTAINS_ALL = "containsAll"
        @NonNls
        const val CONTAINS_KEY = "containsKey"
        @NonNls
        const val DOES_NOT_CONTAIN_KEY = "doesNotContainKey"
        @NonNls
        const val CONTAINS_VALUE = "containsValue"
        @NonNls
        const val DOES_NOT_CONTAIN_VALUE = "doesNotContainValue"
        @NonNls
        const val IS_EQUAL_TO_IC = "isEqualToIgnoringCase"
        @NonNls
        const val IS_NOT_EQUAL_TO_IC = "isNotEqualToIgnoringCase"
        @NonNls
        const val STARTS_WITH = "startsWith"
        @NonNls
        const val ENDS_WITH = "endsWith"
        @NonNls
        const val DOES_NOT_START_WITH = "doesNotStartWith"
        @NonNls
        const val DOES_NOT_END_WITH = "doesNotEndWith"
        @NonNls
        const val CONTAINS_SAME = "containsSame"
        @NonNls
        const val IS_PRESENT = "isPresent"
        @NonNls
        const val IS_NOT_PRESENT = "isNotPresent"

        @NonNls
        const val IS_ABSENT = "isAbsent"
    }
}
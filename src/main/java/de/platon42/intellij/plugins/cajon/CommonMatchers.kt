package de.platon42.intellij.plugins.cajon

import com.intellij.psi.CommonClassNames
import com.siyeh.ig.callMatcher.CallMatcher

val CORE_ASSERT_THAT_MATCHER = CallMatcher.staticCall(AssertJClassNames.ASSERTIONS_CLASSNAME, MethodNames.ASSERT_THAT)!!
val GUAVA_ASSERT_THAT_MATCHER = CallMatcher.staticCall(AssertJClassNames.GUAVA_ASSERTIONS_CLASSNAME, MethodNames.ASSERT_THAT)!!
val ALL_ASSERT_THAT_MATCHERS = CallMatcher.anyOf(CORE_ASSERT_THAT_MATCHER, GUAVA_ASSERT_THAT_MATCHER)!!

val EXTRACTING_FROM_OBJECT = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_OBJECT_ASSERT_CLASSNAME, "extracting")!!
val EXTRACTING_FROM_ITERABLE = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_ITERABLE_ASSERT_CLASSNAME, "extracting")!!
val FLAT_EXTRACTING_FROM_ITERABLE = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_ITERABLE_ASSERT_CLASSNAME, "flatExtracting")!!
val EXTRACTING_RESULT_OF_FROM_ITERABLE = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_ITERABLE_ASSERT_CLASSNAME, "extractingResultOf")!!

val EXTRACTING_CALL_MATCHERS = CallMatcher.anyOf(
    EXTRACTING_FROM_OBJECT,
    EXTRACTING_FROM_ITERABLE,
    FLAT_EXTRACTING_FROM_ITERABLE,
    EXTRACTING_RESULT_OF_FROM_ITERABLE
)!!

val DESCRIBED_AS = CallMatcher.instanceCall(AssertJClassNames.DESCRIPTABLE_INTERFACE, MethodNames.DESCRIBED_AS, MethodNames.AS)!!
val WITH_REPRESENTATION_AND_SUCH = CallMatcher.instanceCall(AssertJClassNames.ASSERT_INTERFACE, "withRepresentation", "withThreadDumpOnError")!!
val USING_COMPARATOR = CallMatcher.instanceCall(AssertJClassNames.ASSERT_INTERFACE, "usingComparator", "usingDefaultComparator")!!
val IN_HEXADECIMAL_OR_BINARY = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_ASSERT_CLASSNAME, MethodNames.IN_HEXADECIMAL, MethodNames.IN_BINARY)!!
val EXTENSION_POINTS = CallMatcher.instanceCall(AssertJClassNames.EXTENSION_POINTS_INTERFACE, "is", "isNot", "has", "doesNotHave", "satisfies")!!

val NOT_ACTUAL_ASSERTIONS = CallMatcher.anyOf(
    ALL_ASSERT_THAT_MATCHERS,
    DESCRIBED_AS,
    WITH_REPRESENTATION_AND_SUCH,
    USING_COMPARATOR,
    IN_HEXADECIMAL_OR_BINARY
)!!

val KNOWN_METHODS_WITH_SIDE_EFFECTS = CallMatcher.anyOf(
    CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_ITERATOR, "next")
)!!
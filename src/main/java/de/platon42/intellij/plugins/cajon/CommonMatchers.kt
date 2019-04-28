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

val KNOWN_METHODS_WITH_SIDE_EFFECTS = CallMatcher.anyOf(
    CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_ITERATOR, "next")
)!!
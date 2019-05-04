package org.assertj.core.api

import org.assertj.core.groups.FieldsOrPropertiesExtractor
import java.util.*

// Workaround for ambiguous method signature of .extracting() see https://github.com/joel-costigliola/assertj-core/issues/1499
fun <SELF : AbstractIterableAssert<SELF, ACTUAL, ELEMENT, ELEMENT_ASSERT>,
        ACTUAL : Iterable<ELEMENT>,
        ELEMENT,
        ELEMENT_ASSERT : AbstractAssert<ELEMENT_ASSERT, ELEMENT>,
        V>
        AbstractIterableAssert<SELF, ACTUAL, ELEMENT, ELEMENT_ASSERT>.extrakting(extractor: (ELEMENT) -> V): AbstractListAssert<*, List<V>, V, ObjectAssert<V>> {
    val values = FieldsOrPropertiesExtractor.extract(actual, extractor)
    if (actual is SortedSet<*>) {
        usingDefaultElementComparator()
    }
    @Suppress("UNCHECKED_CAST")
    return newListAssertInstance(values).withAssertionState(myself) as AbstractListAssert<*, List<V>, V, ObjectAssert<V>>
}
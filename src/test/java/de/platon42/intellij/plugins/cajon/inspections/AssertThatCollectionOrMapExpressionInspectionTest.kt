package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@TestDataSubPath("inspections/CollectionMapExpression")
internal class AssertThatCollectionOrMapExpressionInspectionTest : AbstractCajonTest() {

    @Test
    internal fun assertThat_with_certain_Collection_and_Map_methods(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(AssertThatCollectionOrMapExpressionInspection::class.java)
        myFixture.configureByFile("CollectionMapExpressionBefore.java")
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove isEmpty() of actual expression and use assertThat().isEmpty() instead"), 4)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove contains() of actual expression and use assertThat().contains() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove containsAll() of actual expression and use assertThat().containsAll() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove containsKey() of actual expression and use assertThat().containsKey() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove containsValue() of actual expression and use assertThat().containsValue() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove isEmpty() of actual expression and use assertThat().isNotEmpty() instead"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove contains() of actual expression and use assertThat().doesNotContain() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove containsKey() of actual expression and use assertThat().doesNotContainKey() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove containsValue() of actual expression and use assertThat().doesNotContainValue() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().containsEntry() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().doesNotContainEntry() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().containsKey() instead"), 4)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().doesNotContainKey() instead"), 4)
        myFixture.checkResultByFile("CollectionMapExpressionAfter.java")
    }

    @Test
    internal fun assertThat_with_certain_Collection_and_Map_methods_with_Null_values(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        val inspection = AssertThatCollectionOrMapExpressionInspection()
        inspection.behaviorForMapValueEqualsNull = 3
        myFixture.enableInspections(inspection)
        myFixture.configureByFile("CollectionMapExpressionBefore.java")
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove isEmpty() of actual expression and use assertThat().isEmpty() instead"), 4)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove contains() of actual expression and use assertThat().contains() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove containsAll() of actual expression and use assertThat().containsAll() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove containsKey() of actual expression and use assertThat().containsKey() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove containsValue() of actual expression and use assertThat().containsValue() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove isEmpty() of actual expression and use assertThat().isNotEmpty() instead"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove contains() of actual expression and use assertThat().doesNotContain() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove containsKey() of actual expression and use assertThat().doesNotContainKey() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove containsValue() of actual expression and use assertThat().doesNotContainValue() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().containsEntry() instead"), 6)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().doesNotContainEntry() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().containsKey() instead"), 4)
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().doesNotContainKey() instead"), 0)
        myFixture.checkResultByFile("CollectionMapExpressionWithNullValuesAfter.java")
    }

    @Test
    internal fun assertThat_with_certain_Collection_and_Map_methods_with_no_quickfixes_for_get_equals_null(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        val inspection = AssertThatCollectionOrMapExpressionInspection()
        inspection.behaviorForMapValueEqualsNull = 0

        myFixture.enableInspections(inspection)
        myFixture.configureByFile("CollectionMapExpressionBefore.java")
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().containsEntry() instead"), 2)
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().doesNotContainEntry() instead"), 2)
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().containsKey() instead"), 4)
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().doesNotContainKey() instead"), 0)
    }

    @Test
    internal fun assertThat_with_certain_Collection_and_Map_methods_with_only_warnings_for_get_equals_null(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        val inspection = AssertThatCollectionOrMapExpressionInspection()
        inspection.behaviorForMapValueEqualsNull = 1

        myFixture.enableInspections(inspection)
        myFixture.configureByFile("CollectionMapExpressionBefore.java")
        val highlights = myFixture.doHighlighting()
            .asSequence()
            .filter { it.description == "Moving get() expression out of assertThat() would be more concise" }
            .filter {
                it.quickFixActionRanges?.any { innerit -> innerit.first.action.text.contains("Inspection 'Asserting a collection or map specific expression") } ?: true
            }
            .toList()
        assertThat(highlights).hasSize(4)

        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().containsEntry() instead"), 2)
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().doesNotContainEntry() instead"), 2)
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().containsKey() instead"), 4)
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().doesNotContainKey() instead"), 0)
    }

    @Test
    internal fun assertThat_with_certain_Collection_and_Map_methods_with_both_quickfixes_for_get_equals_null(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        val inspection = AssertThatCollectionOrMapExpressionInspection()
        inspection.behaviorForMapValueEqualsNull = 4

        myFixture.enableInspections(inspection)
        myFixture.configureByFile("CollectionMapExpressionBefore.java")
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().containsEntry() instead"), 2)
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().doesNotContainKey() instead (regular map)"), 4)
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().containsEntry(key, null) instead (degenerated map)"), 4)
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().doesNotContainEntry() instead"), 2)
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().containsKey() instead"), 4)
        getQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().doesNotContainKey() instead"), 0)
    }
}
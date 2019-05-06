package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatCollectionOrMapExpressionInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/CollectionMapExpression")
    internal fun assertThat_with_certain_Collection_and_Map_methods(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
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
            myFixture.checkResultByFile("CollectionMapExpressionAfter.java")
        }
    }
}
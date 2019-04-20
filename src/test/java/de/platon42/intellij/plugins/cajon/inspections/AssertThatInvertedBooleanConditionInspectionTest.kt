package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatInvertedBooleanConditionInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/InvertedBooleanCondition")
    internal fun assertThat_with_inverted_boolean_condition_can_be_inverted(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatInvertedBooleanConditionInspection::class.java)
            myFixture.configureByFile("InvertedBooleanConditionBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Invert condition in assertThat()"), 21)
            myFixture.checkResultByFile("InvertedBooleanConditionAfter.java")
        }
    }
}
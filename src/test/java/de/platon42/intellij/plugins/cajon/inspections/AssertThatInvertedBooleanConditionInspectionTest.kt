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
        myFixture.enableInspections(AssertThatInvertedBooleanConditionInspection::class.java)
        myFixture.configureByFile("InvertedBooleanConditionBefore.java")
        executeQuickFixes(myFixture, Regex.fromLiteral("Invert condition in assertThat()"), 25)
        executeQuickFixes(myFixture, Regex.fromLiteral("Invert condition in isEqualTo() and use isNotEqualTo() instead"), 4)
        executeQuickFixes(myFixture, Regex.fromLiteral("Invert condition in isNotEqualTo() and use isEqualTo() instead"), 2)
        myFixture.checkResultByFile("InvertedBooleanConditionAfter.java")
    }
}
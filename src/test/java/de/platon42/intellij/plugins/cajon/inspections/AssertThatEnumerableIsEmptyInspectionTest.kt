package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatEnumerableIsEmptyInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/EnumerableIsEmpty")
    internal fun assertThat_with_hasSize_zero_and_similar_can_use_isEmpty(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(AssertThatEnumerableIsEmptyInspection::class.java)
        myFixture.configureByFile("EnumerableIsEmptyBefore.java")
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace hasSize() with isEmpty()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace hasSizeLessThan() with isEmpty()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace hasSizeLessThanOrEqualTo() with isEmpty()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace hasSizeGreaterThan() with isNotEmpty()"), 6)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace hasSizeGreaterThanOrEqualTo() with isNotEmpty()"), 6)
        myFixture.checkResultByFile("EnumerableIsEmptyAfter.java")
    }
}
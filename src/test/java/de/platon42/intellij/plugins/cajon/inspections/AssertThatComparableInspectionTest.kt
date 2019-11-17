package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatComparableInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/Comparable")
    internal fun assertThat_with_compareTo_method(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(AssertThatComparableInspection::class.java)
        myFixture.configureByFile("ComparableBefore.java")
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove compareTo() of actual expression and use assertThat().isEqualByComparingTo() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove compareTo() of actual expression and use assertThat().isNotEqualByComparingTo() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove compareTo() of actual expression and use assertThat().isGreaterThanOrEqualTo() instead"), 4)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove compareTo() of actual expression and use assertThat().isGreaterThan() instead"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove compareTo() of actual expression and use assertThat().isLessThanOrEqualTo() instead"), 4)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove compareTo() of actual expression and use assertThat().isLessThan() instead"), 4)
        myFixture.checkResultByFile("ComparableAfter.java")
    }
}
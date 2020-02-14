package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class BogusAssertionInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/BogusAssertion")
    internal fun reports_bogus_assertions(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(BogusAssertionInspection::class.java)
        myFixture.configureByFile("BogusAssertionBefore.java")
        assertHighlightings(myFixture, 14 * 9 + 10 + 12 + 8, "Actual expression in assertThat() is the same as expected")
    }
}
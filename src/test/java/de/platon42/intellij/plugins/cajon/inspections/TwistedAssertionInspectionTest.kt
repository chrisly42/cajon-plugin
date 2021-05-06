package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class TwistedAssertionInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/TwistedAssertion")
    internal fun hint_twisted_actual_and_expected_and_provide_quickfix_where_possible(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(TwistedAssertionInspection::class.java)
        myFixture.configureByFile("TwistedAssertionBefore.java")
        assertHighlightings(myFixture, 9, "Actual expression in assertThat() is a constant")
        assertHighlightings(myFixture, 10, "Twisted actual and expected expressions")

        executeQuickFixes(myFixture, Regex.fromLiteral("Swap actual and expected expressions in assertion"), 6)
        executeQuickFixesNoFamilyNameCheck(myFixture, Regex("Replace .* by .* and swap actual and expected expressions"), 4)
        myFixture.checkResultByFile("TwistedAssertionAfter.java")
    }
}
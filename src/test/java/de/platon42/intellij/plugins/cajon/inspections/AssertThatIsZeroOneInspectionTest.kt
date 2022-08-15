package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatIsZeroOneInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/IsZeroOne")
    internal fun assertThat_with_isEqualTo_zero_or_one_can_use_isZero_or_isOne_plus_isNotZero(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(AssertThatIsZeroOneInspection::class.java)
        myFixture.configureByFile("IsZeroOneBefore.java")
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isZero()"), 10)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isOne()"), 10)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo() with isNotZero()"), 10)
        myFixture.checkResultByFile("IsZeroOneAfter.java")
    }
}
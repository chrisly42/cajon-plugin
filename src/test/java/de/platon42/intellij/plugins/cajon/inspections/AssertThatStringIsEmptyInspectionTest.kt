package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatStringIsEmptyInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/StringIsEmpty")
    internal fun assertThat_with_isEqualTo_emptyString_can_use_isEmpty(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatStringIsEmptyInspection::class.java)
            myFixture.configureByFile("StringIsEmptyBefore.java")
            executeQuickFixes(myFixture, Regex("Replace is.*"), 1)
            myFixture.checkResultByFile("StringIsEmptyAfter.java")
        }
    }
}
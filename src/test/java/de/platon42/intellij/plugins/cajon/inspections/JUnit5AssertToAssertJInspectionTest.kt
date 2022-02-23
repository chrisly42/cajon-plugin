package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.AddLocalJarToModule
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

@AddLocalJarToModule(org.junit.jupiter.api.Assertions::class, Assertions::class)
internal class JUnit5AssertToAssertJInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/JUnit5AssertToAssertJ")
    internal fun junit5_Assertions_can_be_converted_into_AssertJ(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(JUnitAssertToAssertJInspection::class.java)
        myFixture.configureByFile("JUnit5AssertToAssertJInspectionBefore.java")
        executeQuickFixesNoFamilyNameCheck(myFixture, Regex("Convert assert.*\\(\\) to assertThat\\(\\).*"), 48)
        executeQuickFixesNoFamilyNameCheck(myFixture, Regex("Convert assume.*\\(\\) to assumeThat\\(\\).*"), 4)
        myFixture.checkResultByFile("JUnit5AssertToAssertJInspectionAfter.java")
    }
}
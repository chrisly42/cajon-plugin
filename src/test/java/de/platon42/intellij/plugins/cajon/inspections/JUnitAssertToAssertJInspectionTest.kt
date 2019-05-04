package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.AddLocalJarToModule
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.jupiter.api.Test

@AddLocalJarToModule(Assert::class, Assertions::class)
internal class JUnitAssertToAssertJInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/JUnitAssertToAssertJ")
    internal fun junit_Assertions_can_be_converted_into_AssertJ(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(JUnitAssertToAssertJInspection::class.java)
            myFixture.configureByFile("JUnitAssertToAssertJInspectionBefore.java")
            executeQuickFixes(myFixture, Regex("Convert assert.*\\(\\) to assertThat\\(\\).*"), 38)
            myFixture.checkResultByFile("JUnitAssertToAssertJInspectionAfter.java")
        }
    }
}
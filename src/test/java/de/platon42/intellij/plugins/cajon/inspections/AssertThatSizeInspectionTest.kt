package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatSizeInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/AssertThatSize")
    internal fun assertThat_size_of_array_or_collection_can_be_simplified(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatSizeInspection::class.java)
            myFixture.configureByFile("AssertThatSizeBefore.java")
            executeQuickFixes(myFixture, Regex("Replace .*"), 28)
            myFixture.checkResultByFile("AssertThatSizeAfter.java")
        }
    }
}
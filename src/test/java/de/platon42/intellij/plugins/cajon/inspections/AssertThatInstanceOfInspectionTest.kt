package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatInstanceOfInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/InstanceOf")
    internal fun assertThat_with_instanceof_can_be_moved_out(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatInstanceOfInspection::class.java)
            myFixture.configureByFile("InstanceOfBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace instanceof expression by assertThat().isInstanceOf()"), 5)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace instanceof expression by assertThat().isNotInstanceOf()"), 6)
            myFixture.checkResultByFile("InstanceOfAfter.java")
        }
    }
}
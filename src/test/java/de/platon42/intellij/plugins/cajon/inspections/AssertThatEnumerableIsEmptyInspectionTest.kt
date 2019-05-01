package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatEnumerableIsEmptyInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/EnumerableIsEmpty")
    internal fun assertThat_with_hasSize_zero_can_use_isEmpty(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatEnumerableIsEmptyInspection::class.java)
            myFixture.configureByFile("EnumerableIsEmptyBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace hasSize() with isEmpty()"), 5)
            myFixture.checkResultByFile("EnumerableIsEmptyAfter.java")
        }
    }
}
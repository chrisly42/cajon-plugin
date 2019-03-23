package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatBooleanIsTrueOrFalseInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/BooleanIsTrueOrFalse")
    internal fun assertThat_with_isEqualTo_true_or_false_can_use_isTrue_or_isFalse(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatBooleanIsTrueOrFalseInspection::class.java)
            myFixture.configureByFile("BooleanIsTrueOrFalseBefore.java")
            executeQuickFixes(myFixture, Regex("Replace is.*"), 17)
            myFixture.checkResultByFile("BooleanIsTrueOrFalseAfter.java")
        }
    }
}
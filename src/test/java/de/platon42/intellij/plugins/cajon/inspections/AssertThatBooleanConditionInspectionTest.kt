package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatBooleanConditionInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/BooleanCondition")
    internal fun assertThat_with_isEqualTo_true_or_false_can_use_isTrue_or_isFalse(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatBooleanConditionInspection::class.java)
            myFixture.configureByFile("BooleanConditionBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isTrue()"), 6)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isFalse()"), 5)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo() with isTrue()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo() with isFalse()"), 4)
            myFixture.checkResultByFile("BooleanConditionAfter.java")
        }
    }
}
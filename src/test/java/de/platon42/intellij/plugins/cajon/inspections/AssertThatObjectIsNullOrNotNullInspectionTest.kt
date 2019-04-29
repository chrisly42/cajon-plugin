package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatObjectIsNullOrNotNullInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/ObjectIsNullOrNotNull")
    internal fun assertThat_with_isEqualTo_null_can_use_isNull(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatObjectIsNullOrNotNullInspection::class.java)
            myFixture.configureByFile("ObjectIsNullOrNotNullBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isNull()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo() with isNotNull()"), 4)
            myFixture.checkResultByFile("ObjectIsNullOrNotNullAfter.java")
        }
    }
}
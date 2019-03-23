package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatObjectIsNotNullInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/ObjectIsNotNull")
    internal fun assertThat_with_isNotEqualTo_null_can_use_isNotNull(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatObjectIsNotNullInspection::class.java)
            myFixture.configureByFile("ObjectIsNotNullBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo(null) with isNotNull()"), 3)
            myFixture.checkResultByFile("ObjectIsNotNullAfter.java")
        }
    }
}
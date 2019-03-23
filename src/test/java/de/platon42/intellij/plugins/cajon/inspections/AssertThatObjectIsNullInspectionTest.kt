package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatObjectIsNullInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/ObjectIsNull")
    internal fun assertThat_with_isEqualTo_null_can_use_isNull(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatObjectIsNullInspection::class.java)
            myFixture.configureByFile("ObjectIsNullBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo(null) with isNull()"), 3)
            myFixture.checkResultByFile("ObjectIsNullAfter.java")
        }
    }
}
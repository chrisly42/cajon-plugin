package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.AddLocalJarToModule
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

@AddLocalJarToModule(com.google.common.base.Optional::class, org.assertj.guava.api.Assertions::class, Assertions::class)
internal class AssertThatGuavaOptionalInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/AssertThatGuavaOptional")
    internal fun assertThat_get_or_isPresent_for_Guava_Optional_can_be_simplified(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatGuavaOptionalInspection::class.java)
            myFixture.configureByFile("AssertThatGuavaOptionalBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isPresent()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo() with isPresent()"), 5)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isAbsent()"), 5)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo() with isAbsent()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isTrue() with isPresent()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isFalse() with isAbsent()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with contains()"), 7)
            myFixture.checkResultByFile("AssertThatGuavaOptionalAfter.java")
        }
    }

    @Test
    @TestDataSubPath("inspections/AssertThatGuavaOptional")
    internal fun adds_missing_Guava_import_any_order(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatGuavaOptionalInspection::class.java)
            myFixture.configureByFile("WithoutPriorGuavaImportBefore.java")
            executeQuickFixes(myFixture, Regex("Replace .* with .*"), 7)
            myFixture.checkResultByFile("WithoutPriorGuavaImportAfter.java")
        }
    }

    @Test
    @TestDataSubPath("inspections/AssertThatGuavaOptional")
    internal fun adds_missing_Guava_import_isAbsent_first(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatGuavaOptionalInspection::class.java)
            myFixture.configureByFile("WithoutPriorGuavaImportBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isAbsent()"), 1)
            executeQuickFixes(myFixture, Regex("Replace .* with .*"), 6)
            myFixture.checkResultByFile("WithoutPriorGuavaImportAfter.java")
        }
    }
}
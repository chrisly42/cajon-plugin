package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.AddLocalJarToModule
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

@AddLocalJarToModule(com.google.common.base.Optional::class, org.assertj.guava.api.Assertions::class, Assertions::class)
@TestDataSubPath("inspections/GuavaOptional")
internal class AssertThatGuavaOptionalInspectionTest : AbstractCajonTest() {

    @Test
    internal fun assertThat_get_or_isPresent_for_Guava_Optional_can_be_simplified(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatGuavaOptionalInspection::class.java)
            myFixture.configureByFile("GuavaOptionalBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isEqualTo() with isPresent()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isNotEqualTo() with isPresent()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo() with isPresent()"), 3)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isEqualTo() with isAbsent()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isAbsent()"), 3)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isNotEqualTo() with isAbsent()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isTrue() with isPresent()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isFalse() with isAbsent()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isEqualTo() with contains()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Remove unwrapping of expected expression and replace isEqualTo() with contains()"), 6)
            myFixture.checkResultByFile("GuavaOptionalAfter.java")
        }
    }

    @Test
    internal fun adds_missing_Guava_import_any_order(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatGuavaOptionalInspection::class.java)
            myFixture.configureByFile("WithoutPriorGuavaImportBefore.java")
            executeQuickFixes(myFixture, Regex(".*eplace .* with .*"), 7)
            myFixture.checkResultByFile("WithoutPriorGuavaImportAfter.java")
        }
    }

    @Test
    internal fun adds_missing_Guava_import_isAbsent_first(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatGuavaOptionalInspection::class.java)
            myFixture.configureByFile("WithoutPriorGuavaImportBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isAbsent()"), 1)
            executeQuickFixes(myFixture, Regex(".*eplace .* with .*"), 6)
            myFixture.checkResultByFile("WithoutPriorGuavaImportAfter.java")
        }
    }
}
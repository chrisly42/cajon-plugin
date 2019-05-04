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
            executeQuickFixes(myFixture, Regex.fromLiteral("Remove isPresent() of actual expression and use assertThat().isPresent() instead"), 6)
            executeQuickFixes(myFixture, Regex.fromLiteral("Remove isPresent() of actual expression and use assertThat().isAbsent() instead"), 5)
            executeQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().contains() instead"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap expected expression and replace isEqualTo() with contains()"), 6)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with Guava assertThat().isAbsent()"), 3)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo() with Guava assertThat().isPresent()"), 3)
            myFixture.checkResultByFile("GuavaOptionalAfter.java")
        }
    }

    @Test
    internal fun adds_missing_Guava_import_any_order(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatGuavaOptionalInspection::class.java)
            myFixture.configureByFile("WithoutPriorGuavaImportBefore.java")
            executeQuickFixes(myFixture, Regex(".*eplace .* with .*"), 4)
            executeQuickFixes(myFixture, Regex("Remove .*"), 3)
            myFixture.checkResultByFile("WithoutPriorGuavaImportAfter.java")
        }
    }

    @Test
    internal fun adds_missing_Guava_import_isAbsent_first(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatGuavaOptionalInspection::class.java)
            myFixture.configureByFile("WithoutPriorGuavaImportBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with Guava assertThat().isAbsent()"), 1)
            executeQuickFixes(myFixture, Regex(".*eplace .* with .*"), 3)
            executeQuickFixes(myFixture, Regex("Remove .*"), 3)
            myFixture.checkResultByFile("WithoutPriorGuavaImportAfter.java")
        }
    }
}
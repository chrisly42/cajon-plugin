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
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo() with isPresent()"), 3)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isAbsent()"), 3)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo() with isAbsent()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isTrue() with isPresent()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isFalse() with isAbsent()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with contains()"), 3)
            myFixture.checkResultByFile("AssertThatGuavaOptionalAfter.java")
        }
    }
}
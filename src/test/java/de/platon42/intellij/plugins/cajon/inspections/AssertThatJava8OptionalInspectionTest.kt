package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatJava8OptionalInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/AssertThatJava8Optional")
    internal fun assertThat_get_or_isPresent_for_Java8_Optional_can_be_simplified(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatJava8OptionalInspection::class.java)
            myFixture.configureByFile("AssertThatJava8OptionalBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isEqualTo() with isPresent()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isNotEqualTo() with isPresent()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isEqualTo() with isNotPresent()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isNotEqualTo() with isNotPresent()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isTrue() with isPresent()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isNotPresent()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo() with isPresent()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isFalse() with isNotPresent()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Remove unwrapping of expected expression and replace isEqualTo() with contains()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isEqualTo() with contains()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap actual expression and replace isSameAs() with containsSame()"), 1)
            myFixture.checkResultByFile("AssertThatJava8OptionalAfter.java")
        }
    }
}
package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatJava8OptionalInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/Java8Optional")
    internal fun assertThat_get_or_isPresent_for_Java8_Optional_can_be_simplified(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatJava8OptionalInspection::class.java)
            myFixture.configureByFile("Java8OptionalBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Remove isPresent() of actual expression and use assertThat().isPresent() instead"), 6)
            executeQuickFixes(myFixture, Regex.fromLiteral("Remove isPresent() of actual expression and use assertThat().isNotPresent() instead"), 5)
            executeQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().contains() instead"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Remove get() of actual expression and use assertThat().containsSame() instead"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isNotPresent()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotEqualTo() with isPresent()"), 1)
            executeQuickFixes(myFixture, Regex.fromLiteral("Unwrap expected expression and replace isEqualTo() with contains()"), 2)
            myFixture.checkResultByFile("Java8OptionalAfter.java")
        }
    }
}
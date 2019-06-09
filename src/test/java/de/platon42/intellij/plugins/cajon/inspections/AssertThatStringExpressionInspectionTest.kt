package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatStringExpressionInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/StringExpression")
    internal fun assertThat_with_certain_String_methods(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(AssertThatStringExpressionInspection::class.java)
        myFixture.configureByFile("StringExpressionBefore.java")
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove isEmpty() of actual expression and use assertThat().isEmpty() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove equals() of actual expression and use assertThat().isEqualTo() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove equalsIgnoreCase() of actual expression and use assertThat().isEqualToIgnoringCase() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove contentEquals() of actual expression and use assertThat().isEqualTo() instead"), 4)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove contains() of actual expression and use assertThat().contains() instead"), 4)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove startsWith() of actual expression and use assertThat().startsWith() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove endsWith() of actual expression and use assertThat().endsWith() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove isEmpty() of actual expression and use assertThat().isNotEmpty() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove equals() of actual expression and use assertThat().isNotEqualTo() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove equalsIgnoreCase() of actual expression and use assertThat().isNotEqualToIgnoringCase() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove contentEquals() of actual expression and use assertThat().isNotEqualTo() instead"), 4)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove contains() of actual expression and use assertThat().doesNotContain() instead"), 4)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove startsWith() of actual expression and use assertThat().doesNotStartWith() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove endsWith() of actual expression and use assertThat().doesNotEndWith() instead"), 3)
        myFixture.checkResultByFile("StringExpressionAfter.java")
    }
}
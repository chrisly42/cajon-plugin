package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatObjectExpressionInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/ObjectExpression")
    internal fun assertThat_with_certain_Object_methods(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(AssertThatObjectExpressionInspection::class.java)
        myFixture.configureByFile("ObjectExpressionBefore.java")
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove equals() of actual expression and use assertThat().isEqualTo() instead"), 4)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove equals() of actual expression and use assertThat().isNotEqualTo() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace calls to hashCode() with hasSameHashCodeAs()"), 1)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove toString() of actual expression and use assertThat().hasToString() instead"), 1)
        myFixture.checkResultByFile("ObjectExpressionAfter.java")
    }
}
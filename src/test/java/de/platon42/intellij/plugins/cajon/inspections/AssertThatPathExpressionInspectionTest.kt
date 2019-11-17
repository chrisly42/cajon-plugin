package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatPathExpressionInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/PathExpression")
    internal fun assertThat_with_certain_Path_methods(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(AssertThatPathExpressionInspection::class.java)
        myFixture.configureByFile("PathExpressionBefore.java")
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove isAbsolute() of actual expression and use assertThat().isAbsolute() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove isAbsolute() of actual expression and use assertThat().isRelative() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove startsWith() of actual expression and use assertThat().startsWithRaw() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove endsWith() of actual expression and use assertThat().endsWithRaw() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove getParent() of actual expression and use assertThat().hasNoParentRaw() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove getParent() of actual expression and use assertThat().hasParentRaw() instead"), 1)
        myFixture.checkResultByFile("PathExpressionAfter.java")
    }
}
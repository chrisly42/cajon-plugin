package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatBinaryExpressionInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/BinaryExpression")
    internal fun assertThat_of_binary_expression_can_be_moved_out(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatBinaryExpressionInspection::class.java)
            myFixture.configureByFile("BinaryExpressionBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Split binary expression out of assertThat()"), 148)
            executeQuickFixes(myFixture, Regex.fromLiteral("Split equals() expression out of assertThat()"), 12)
            myFixture.checkResultByFile("BinaryExpressionAfter.java")
        }
    }
}
package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatSizeInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/Size")
    internal fun assertThat_size_of_array_or_collection_can_be_simplified(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatSizeInspection::class.java)
            myFixture.configureByFile("SizeBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isEmpty()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isZero() with isEmpty()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotZero() with isNotEmpty()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isGreaterThan() with isNotEmpty()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isGreaterThanOrEqualTo() with isNotEmpty()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isLessThan() with isEmpty()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isLessThanOrEqualTo() with isEmpty()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with hasSameSizeAs()"), 12)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with hasSize()"), 8)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isGreaterThan() with hasSizeGreaterThan()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isGreaterThanOrEqualTo() with hasSizeGreaterThanOrEqualTo()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isLessThan() with hasSizeLessThan()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isLessThanOrEqualTo() with hasSizeLessThanOrEqualTo()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Remove size determination of expected expression and replace hasSize() with hasSameSizeAs()"), 12)
            myFixture.checkResultByFile("SizeAfter.java")
        }
    }
}
package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatSizeInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/AssertThatSize")
    internal fun assertThat_size_of_array_or_collection_can_be_simplified(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssertThatSizeInspection::class.java)
            myFixture.configureByFile("AssertThatSizeBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isEmpty()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isZero() with isEmpty()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotZero() with isNotEmpty()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isGreaterThan() with isNotEmpty()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isGreaterThanOrEqualTo() with isNotEmpty()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isLessThan() with isEmpty()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isLessThanOrEqualTo() with isEmpty()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with hasSameSizeAs()"), 4)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with hasSize()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isGreaterThan() with hasSizeGreaterThan()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isGreaterThanOrEqualTo() with hasSizeGreaterThanOrEqualTo()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isLessThan() with hasSizeLessThan()"), 2)
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace isLessThanOrEqualTo() with hasSizeLessThanOrEqualTo()"), 2)
            myFixture.checkResultByFile("AssertThatSizeAfter.java")
        }
    }
}
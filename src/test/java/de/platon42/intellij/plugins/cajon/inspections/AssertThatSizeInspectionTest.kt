package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.extrakting
import org.junit.jupiter.api.Test

internal class AssertThatSizeInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/Size")
    internal fun assertThat_size_of_array_collection_or_map_can_be_simplified(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(AssertThatSizeInspection::class.java)
        myFixture.configureByFile("SizeBefore.java")
        assertThat(myFixture.doHighlighting()).extrakting { it.description }.containsOnlyOnce("Try to operate on the iterable itself rather than its size")

        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isEmpty()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isZero() with isEmpty()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isNotZero() with isNotEmpty()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isGreaterThan() with isNotEmpty()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isGreaterThanOrEqualTo() with isNotEmpty()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isLessThan() with isEmpty()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isLessThanOrEqualTo() with isEmpty()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with hasSameSizeAs()"), 19)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with hasSize()"), 11)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isGreaterThan() with hasSizeGreaterThan()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isGreaterThanOrEqualTo() with hasSizeGreaterThanOrEqualTo()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isLessThan() with hasSizeLessThan()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isLessThanOrEqualTo() with hasSizeLessThanOrEqualTo()"), 5)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove size determination of expected expression and replace hasSize() with hasSameSizeAs()"), 21)
        myFixture.checkResultByFile("SizeAfter.java")
    }
}
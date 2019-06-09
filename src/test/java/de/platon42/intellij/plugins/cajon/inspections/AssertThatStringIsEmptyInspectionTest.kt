package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.extrakting
import org.junit.jupiter.api.Test

internal class AssertThatStringIsEmptyInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/StringIsEmpty")
    internal fun assertThat_with_isEqualTo_emptyString_can_use_isEmpty(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(AssertThatStringIsEmptyInspection::class.java)
        myFixture.configureByFile("StringIsEmptyBefore.java")
        val highlights = myFixture.doHighlighting()
            .asSequence()
            .filter { it.description?.contains(" can be simplified to") ?: false }
            .toList()
        assertThat(highlights).hasSize(6).extrakting { it.text }.doesNotContain("assertThat")
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace isEqualTo() with isEmpty()"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Replace hasSize() with isEmpty()"), 3)
        myFixture.checkResultByFile("StringIsEmptyAfter.java")
    }
}
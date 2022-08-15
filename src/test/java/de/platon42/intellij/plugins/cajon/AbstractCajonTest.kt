package de.platon42.intellij.plugins.cajon

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.pom.java.LanguageLevel
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.AddMavenDependencyToModule
import de.platon42.intellij.jupiter.LightCodeInsightExtension
import de.platon42.intellij.jupiter.TestDataPath
import de.platon42.intellij.jupiter.TestJdk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Condition
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.reflect.Method

@ExtendWith(LightCodeInsightExtension::class)
@TestDataPath("src/test/resources")
@TestJdk(LanguageLevel.JDK_1_8, annotations = true, useInternal = true)
//@AddLocalJarToModule(Assertions::class)
@AddMavenDependencyToModule("org.assertj:assertj-core:3.15.0")
@DisplayNameGeneration(AbstractCajonTest.CutOffFixtureDisplayNameGenerator::class)
abstract class AbstractCajonTest {

    protected fun executeQuickFixes(myFixture: JavaCodeInsightTestFixture, regex: Regex, expectedFixes: Int) {
        val quickfixes = getQuickFixes(myFixture, regex, expectedFixes)
        assertThat(quickfixes.groupBy { it.familyName }).hasSize(1)
        quickfixes.forEach(myFixture::launchAction)
    }

    protected fun executeQuickFixesNoFamilyNameCheck(myFixture: JavaCodeInsightTestFixture, regex: Regex, expectedFixes: Int) {
        val quickfixes = getQuickFixes(myFixture, regex, expectedFixes)
        quickfixes.forEach(myFixture::launchAction)
    }

    protected fun getQuickFixes(myFixture: JavaCodeInsightTestFixture, regex: Regex, expectedFixes: Int): List<IntentionAction> {
        val quickfixes = myFixture.getAllQuickFixes().filter { it.text.matches(regex) }
        assertThat(quickfixes).`as`("Fixes matched by $regex: ${myFixture.getAllQuickFixes().map { it.text }}").hasSize(expectedFixes)
        return quickfixes
    }

    protected fun assertHighlightings(myFixture: JavaCodeInsightTestFixture, count: Int, snippet: String) {
        assertThat(myFixture.doHighlighting())
            .areExactly(count, Condition({ it.description?.contains(snippet) ?: false }, "containing"))
    }

    class CutOffFixtureDisplayNameGenerator : DisplayNameGenerator.ReplaceUnderscores() {
        override fun generateDisplayNameForMethod(testClass: Class<*>?, testMethod: Method?): String {
            val nameForMethod = super.generateDisplayNameForMethod(testClass, testMethod)
            return nameForMethod.substringBefore("$")
        }
    }
}
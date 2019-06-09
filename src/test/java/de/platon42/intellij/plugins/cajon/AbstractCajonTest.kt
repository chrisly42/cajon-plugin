package de.platon42.intellij.plugins.cajon

import com.intellij.pom.java.LanguageLevel
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.AddLocalJarToModule
import de.platon42.intellij.jupiter.LightCodeInsightExtension
import de.platon42.intellij.jupiter.TestDataPath
import de.platon42.intellij.jupiter.TestJdk
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.reflect.Method

@ExtendWith(LightCodeInsightExtension::class)
@TestDataPath("src/test/resources")
@TestJdk(LanguageLevel.JDK_1_8, annotations = true, useInternal = true)
@AddLocalJarToModule(Assertions::class)
@DisplayNameGeneration(AbstractCajonTest.CutOffFixtureDisplayNameGenerator::class)
abstract class AbstractCajonTest {

    protected fun executeQuickFixes(myFixture: JavaCodeInsightTestFixture, regex: Regex, expectedFixes: Int) {
        val quickfixes = myFixture.getAllQuickFixes().filter { it.text.matches(regex) }
        assertThat(quickfixes).`as`("Fixes matched by $regex: ${myFixture.getAllQuickFixes().map { it.text }}").hasSize(expectedFixes)
        quickfixes.forEach(myFixture::launchAction)
    }

    class CutOffFixtureDisplayNameGenerator : DisplayNameGenerator.ReplaceUnderscores() {
        override fun generateDisplayNameForMethod(testClass: Class<*>?, testMethod: Method?): String {
            val nameForMethod = super.generateDisplayNameForMethod(testClass, testMethod)
            return nameForMethod.substringBefore("$")
        }
    }
}
package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.AddLocalJarToModule
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

@AddLocalJarToModule(Assertions::class, Test::class, org.junit.Test::class)
internal class AssumeThatInsteadOfReturnInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/AssumeThat")
    internal fun conditional_returns_can_be_replaced_by_assumeThat(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(AssumeThatInsteadOfReturnInspection::class.java)
            myFixture.configureByFile("AssumeThatBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Replace if statement by assumeTrue()"), 4)
            myFixture.checkResultByFile("AssumeThatAfter.java")
        }
    }
}
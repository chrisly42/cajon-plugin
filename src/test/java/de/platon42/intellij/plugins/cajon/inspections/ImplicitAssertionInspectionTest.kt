package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.AddLocalJarToModule
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

@AddLocalJarToModule(com.google.common.base.Optional::class, org.assertj.guava.api.Assertions::class, Assertions::class)
internal class ImplicitAssertionInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/ImplicitAssertion")
    internal fun implicit_assertions_can_be_removed(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(ImplicitAssertionInspection::class.java)
        myFixture.configureByFile("ImplicitAssertionBefore.java")
        executeQuickFixes(myFixture, Regex("Delete implicit isNotNull\\(\\) covered by .*"), 101)
        executeQuickFixes(myFixture, Regex("Delete implicit isNotEmpty\\(\\) covered by .*"), 17)
        executeQuickFixes(myFixture, Regex("Delete implicit isPresent\\(\\) covered by .*"), 8)
        myFixture.checkResultByFile("ImplicitAssertionAfter.java")
    }
}
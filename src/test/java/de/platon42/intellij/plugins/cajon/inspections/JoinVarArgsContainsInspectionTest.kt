package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class JoinVarArgsContainsInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/JoinVarArgsContains")
    internal fun join_contains_and_doesNotContain_together_where_possible(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(JoinVarArgsContainsInspection::class.java)
        myFixture.configureByFile("JoinVarArgsContainsBefore.java")
        executeQuickFixes(myFixture, Regex.fromLiteral("Join multiple arguments to variadic argument method calls"), 3)
        myFixture.checkResultByFile("JoinVarArgsContainsAfter.java")
    }
}
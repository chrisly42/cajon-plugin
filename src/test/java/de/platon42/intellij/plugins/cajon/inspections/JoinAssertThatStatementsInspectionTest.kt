package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class JoinAssertThatStatementsInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/JoinStatements")
    internal fun assertThat_statements_can_be_joined_together(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(JoinAssertThatStatementsInspection::class.java)
            myFixture.configureByFile("JoinStatementsBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Join assertThat() statements"), 5)
            myFixture.checkResultByFile("JoinStatementsAfter.java")
        }
    }
}
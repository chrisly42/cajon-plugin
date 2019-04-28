package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class JoinAssertThatStatementsInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/JoinStatements")
    internal fun assertThat_size_of_array_or_collection_can_be_simplified(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        runTest {
            myFixture.enableInspections(JoinAssertThatStatementsInspection::class.java)
            myFixture.configureByFile("JoinStatementsBefore.java")
            executeQuickFixes(myFixture, Regex.fromLiteral("Join assertThat() statements"), 6)
            myFixture.checkResultByFile("JoinStatementsAfter.java")
        }
    }
}
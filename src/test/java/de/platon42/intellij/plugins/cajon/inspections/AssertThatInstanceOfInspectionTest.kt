package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatInstanceOfInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/InstanceOf")
    internal fun assertThat_with_instanceof_can_be_moved_out(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(AssertThatInstanceOfInspection::class.java)
        myFixture.configureByFile("InstanceOfBefore.java")
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove instanceof in actual expression and use assertThat().isInstanceOf() instead"), 6)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove instanceof in actual expression and use assertThat().isNotInstanceOf() instead"), 6)
        myFixture.checkResultByFile("InstanceOfAfter.java")
    }
}
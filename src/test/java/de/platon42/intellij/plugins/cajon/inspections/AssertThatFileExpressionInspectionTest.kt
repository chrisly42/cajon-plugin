package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.junit.jupiter.api.Test

internal class AssertThatFileExpressionInspectionTest : AbstractCajonTest() {

    @Test
    @TestDataSubPath("inspections/FileExpression")
    internal fun assertThat_with_certain_File_methods(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.enableInspections(AssertThatFileExpressionInspection::class.java)
        myFixture.configureByFile("FileExpressionBefore.java")
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove canRead() of actual expression and use assertThat().canRead() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove canWrite() of actual expression and use assertThat().canWrite() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove exists() of actual expression and use assertThat().exists() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove exists() of actual expression and use assertThat().doesNotExist() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove isAbsolute() of actual expression and use assertThat().isAbsolute() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove isAbsolute() of actual expression and use assertThat().isRelative() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove isDirectory() of actual expression and use assertThat().isDirectory() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove isFile() of actual expression and use assertThat().isFile() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove getName() of actual expression and use assertThat().hasName() instead"), 3)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove getParent() of actual expression and use assertThat().hasNoParent() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove getParentFile() of actual expression and use assertThat().hasNoParent() instead"), 2)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove getParent() of actual expression and use assertThat().hasParent() instead"), 1)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove getParentFile() of actual expression and use assertThat().hasParent() instead"), 1)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove listFiles() of actual expression and use assertThat().isEmptyDirectory() instead"), 1)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove listFiles() of actual expression and use assertThat().isNotEmptyDirectory() instead"), 1)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove list() of actual expression and use assertThat().isEmptyDirectory() instead"), 1)
        executeQuickFixes(myFixture, Regex.fromLiteral("Remove list() of actual expression and use assertThat().isNotEmptyDirectory() instead"), 1)
        myFixture.checkResultByFile("FileExpressionAfter.java")
    }
}
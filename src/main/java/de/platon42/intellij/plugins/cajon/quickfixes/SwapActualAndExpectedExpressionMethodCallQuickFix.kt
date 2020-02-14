package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import de.platon42.intellij.plugins.cajon.*

class SwapActualAndExpectedExpressionMethodCallQuickFix(
    description: String,
    private val replacementMethod: String
) : AbstractCommonQuickFix(description) {

    companion object {
        private const val SPLIT_EXPRESSION_DESCRIPTION = "Swap actual and expected expressions of assertions"
    }

    override fun getFamilyName(): String {
        return SPLIT_EXPRESSION_DESCRIPTION
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val assertThatMethodCall = descriptor.startElement.findStaticMethodCall() ?: return

        val methodToFix = assertThatMethodCall.collectMethodCallsUpToStatement()
            .filterNot(NOT_ACTUAL_ASSERTIONS::test)
            .first()

        val oldActualExpression = assertThatMethodCall.firstArg.copy()!!
        assertThatMethodCall.firstArg.replace(methodToFix.firstArg)

        val expectedExpression = createExpectedMethodCall(methodToFix, replacementMethod, oldActualExpression)
        expectedExpression.replaceQualifierFromMethodCall(methodToFix)
        methodToFix.replace(expectedExpression)
    }
}
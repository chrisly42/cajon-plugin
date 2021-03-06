package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.*

class RemoveActualOutmostMethodCallQuickFix(
    description: String,
    private val replacementMethod: String,
    private val noExpectedExpression: Boolean = false
) : AbstractCommonQuickFix(description) {

    companion object {
        private const val REMOVE_ACTUAL_EXPRESSION_DESCRIPTION = "Remove method calls in actual expressions and use better assertion"
    }

    override fun getFamilyName(): String {
        return REMOVE_ACTUAL_EXPRESSION_DESCRIPTION
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val outmostCallExpression = descriptor.startElement as? PsiMethodCallExpression ?: return
        val assertThatMethodCall = outmostCallExpression.findStaticMethodCall() ?: return
        val assertExpression = assertThatMethodCall.firstArg as? PsiMethodCallExpression ?: return

        val methodsToFix = assertThatMethodCall.gatherAssertionCalls()

        assertExpression.replace(assertExpression.qualifierExpression)

        methodsToFix
            .forEach {
                val args = if (noExpectedExpression) emptyArray() else it.argumentList.expressions
                val expectedExpression = createExpectedMethodCall(it, replacementMethod, *args)
                expectedExpression.replaceQualifierFromMethodCall(it)
                it.replace(expectedExpression)
            }
    }
}
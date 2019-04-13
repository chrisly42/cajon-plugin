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

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val assertExpression = methodCallExpression.firstArg as? PsiMethodCallExpression ?: return
        assertExpression.replace(assertExpression.qualifierExpression)

        val oldExpectedExpression = element.findOutmostMethodCall() ?: return
        val args = if (noExpectedExpression) emptyArray() else oldExpectedExpression.argumentList.expressions
        val expectedExpression = createExpectedMethodCall(element, replacementMethod, *args)
        expectedExpression.replaceQualifierFromMethodCall(oldExpectedExpression)
        oldExpectedExpression.replace(expectedExpression)
    }
}
package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.*

class SplitEqualsExpressionMethodCallQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val equalsMethodCall = methodCallExpression.firstArg as? PsiMethodCallExpression ?: return
        val expectedArgument = equalsMethodCall.firstArg.copy()
        equalsMethodCall.replace(equalsMethodCall.qualifierExpression)

        val oldExpectedExpression = element.findOutmostMethodCall() ?: return
        val expectedExpression = createExpectedMethodCall(element, replacementMethod, expectedArgument)
        expectedExpression.replaceQualifierFromMethodCall(oldExpectedExpression)
        oldExpectedExpression.replace(expectedExpression)
    }
}
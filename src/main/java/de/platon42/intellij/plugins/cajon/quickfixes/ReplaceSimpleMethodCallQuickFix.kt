package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethodCallExpression

class ReplaceSimpleMethodCallQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val factory = JavaPsiFacade.getElementFactory(element.project)
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val oldQualifier = methodCallExpression.methodExpression.qualifierExpression ?: return
        val expectedExpression =
            factory.createExpressionFromText("a.$replacementMethod", element) as PsiMethodCallExpression
        expectedExpression.methodExpression.qualifierExpression!!.replace(oldQualifier)
        element.replace(expectedExpression)
    }
}
package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethodCallExpression

class ReplaceSimpleMethodCallQuickFix(private val description: String, private val replacementMethod: String) :
    LocalQuickFix {
    override fun getFamilyName() = description

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val factory = JavaPsiFacade.getElementFactory(element.project)
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val oldQualifier = methodCallExpression.methodExpression.qualifierExpression ?: return
        val isEmptyExpression =
            factory.createExpressionFromText("a.$replacementMethod", null) as PsiMethodCallExpression
        isEmptyExpression.methodExpression.qualifierExpression!!.replace(oldQualifier)
        element.replace(isEmptyExpression)
    }
}
package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.replaceQualifierFromMethodCall

class ReplaceSimpleMethodCallQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return

        val factory = JavaPsiFacade.getElementFactory(element.project)
        val expectedExpression =
            factory.createExpressionFromText("a.$replacementMethod", element) as PsiMethodCallExpression
        expectedExpression.replaceQualifierFromMethodCall(methodCallExpression)
        element.replace(expectedExpression)
    }
}
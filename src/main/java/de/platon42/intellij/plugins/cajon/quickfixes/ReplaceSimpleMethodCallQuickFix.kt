package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.createExpectedMethodCall
import de.platon42.intellij.plugins.cajon.replaceQualifierFromMethodCall

class ReplaceSimpleMethodCallQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    companion object {
        private const val REPLACE_DESCRIPTION = "Replace methods by better ones"
    }

    override fun getFamilyName(): String {
        return REPLACE_DESCRIPTION
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val expectedExpression = createExpectedMethodCall(element, replacementMethod)
        expectedExpression.replaceQualifierFromMethodCall(methodCallExpression)
        element.replace(expectedExpression)
    }
}
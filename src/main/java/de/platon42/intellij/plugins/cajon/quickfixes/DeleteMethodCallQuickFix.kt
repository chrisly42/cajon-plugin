package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.qualifierExpression

class DeleteMethodCallQuickFix(description: String) : AbstractCommonQuickFix(description) {

    companion object {
        private const val DELETE_METHOD_DESCRIPTION = "Delete unnecessary method calls"
    }

    override fun getFamilyName(): String {
        return DELETE_METHOD_DESCRIPTION
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val methodCallExpression = descriptor.startElement as? PsiMethodCallExpression ?: return

        methodCallExpression.replace(methodCallExpression.qualifierExpression)
    }
}
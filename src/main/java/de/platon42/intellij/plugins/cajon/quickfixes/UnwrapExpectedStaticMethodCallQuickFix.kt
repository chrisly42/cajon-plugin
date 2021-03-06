package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.createExpectedMethodCall
import de.platon42.intellij.plugins.cajon.findOutmostMethodCall
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.replaceQualifierFromMethodCall

class UnwrapExpectedStaticMethodCallQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    companion object {
        private const val REMOVE_EXPECTED_OUTMOST_DESCRIPTION = "Unwrap expected expressions and use better assertion"
    }

    override fun getFamilyName(): String {
        return REMOVE_EXPECTED_OUTMOST_DESCRIPTION
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val oldExpectedExpression = element.findOutmostMethodCall() ?: return
        val expectedMethodCallExpression = oldExpectedExpression.firstArg as? PsiMethodCallExpression ?: return
        val expectedExpression = createExpectedMethodCall(element, replacementMethod, expectedMethodCallExpression.firstArg)
        expectedExpression.replaceQualifierFromMethodCall(oldExpectedExpression)
        oldExpectedExpression.replace(expectedExpression)
    }
}
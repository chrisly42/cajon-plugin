package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiStatement
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.replaceQualifierFromMethodCall

class ReplaceExpectedOutmostMethodCallQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val statement = PsiTreeUtil.getParentOfType(element, PsiStatement::class.java) ?: return
        val oldExpectedExpression = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java) ?: return

        val factory = JavaPsiFacade.getElementFactory(element.project)
        val expectedExpression =
            factory.createExpressionFromText("a.$replacementMethod(e)", element) as PsiMethodCallExpression
        val expectedMethodCallExpression = oldExpectedExpression.firstArg as? PsiMethodCallExpression ?: return
        expectedExpression.firstArg.replace(expectedMethodCallExpression.firstArg)
        expectedExpression.replaceQualifierFromMethodCall(oldExpectedExpression)
        oldExpectedExpression.replace(expectedExpression)
    }
}
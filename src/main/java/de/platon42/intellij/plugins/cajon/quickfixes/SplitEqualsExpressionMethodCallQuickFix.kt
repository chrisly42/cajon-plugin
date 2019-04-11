package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiStatement
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.qualifierExpression
import de.platon42.intellij.plugins.cajon.replaceQualifierFromMethodCall

class SplitEqualsExpressionMethodCallQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val equalsMethodCall = methodCallExpression.firstArg as? PsiMethodCallExpression ?: return
        val expectedArgument = equalsMethodCall.firstArg.copy()
        equalsMethodCall.replace(equalsMethodCall.qualifierExpression)

        val statement = PsiTreeUtil.getParentOfType(element, PsiStatement::class.java) ?: return
        val oldExpectedExpression = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java) ?: return

        val factory = JavaPsiFacade.getElementFactory(element.project)
        val expectedExpression = factory.createExpressionFromText(
            "a.$replacementMethod(e)", element
        ) as PsiMethodCallExpression
        expectedExpression.firstArg.replace(expectedArgument)
        expectedExpression.replaceQualifierFromMethodCall(oldExpectedExpression)
        oldExpectedExpression.replace(expectedExpression)
    }
}
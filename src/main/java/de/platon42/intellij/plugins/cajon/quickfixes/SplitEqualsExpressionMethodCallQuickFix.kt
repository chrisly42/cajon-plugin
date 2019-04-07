package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiStatement
import com.intellij.psi.util.PsiTreeUtil

class SplitEqualsExpressionMethodCallQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val factory = JavaPsiFacade.getElementFactory(element.project)
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val equalsMethodCall = methodCallExpression.argumentList.expressions[0] as? PsiMethodCallExpression ?: return
        val expectedArgument = equalsMethodCall.argumentList.expressions[0].copy()
        equalsMethodCall.replace(equalsMethodCall.methodExpression.qualifierExpression!!)

        val statement = PsiTreeUtil.getParentOfType(element, PsiStatement::class.java) ?: return
        val oldExpectedExpression = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java) ?: return
        val expectedExpression =
            factory.createExpressionFromText("a.${replacementMethod.replace("()", "(e)")}", element) as PsiMethodCallExpression
        expectedExpression.argumentList.expressions[0].replace(expectedArgument)
        expectedExpression.methodExpression.qualifierExpression!!.replace(oldExpectedExpression.methodExpression.qualifierExpression!!)
        oldExpectedExpression.replace(expectedExpression)
    }
}
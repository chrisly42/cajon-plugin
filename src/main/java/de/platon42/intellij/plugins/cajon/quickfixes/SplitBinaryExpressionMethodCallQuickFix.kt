package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiBinaryExpression
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiStatement
import com.intellij.psi.util.PsiTreeUtil

class SplitBinaryExpressionMethodCallQuickFix(
    description: String,
    private val replacementMethod: String,
    private val pickRightOperand: Boolean,
    private val noExpectedExpression: Boolean
) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val factory = JavaPsiFacade.getElementFactory(element.project)
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val binaryExpression = methodCallExpression.argumentList.expressions[0] as? PsiBinaryExpression ?: return
        val expectedArgument = (if (pickRightOperand) binaryExpression.lOperand else binaryExpression.rOperand)?.copy() ?: return
        binaryExpression.replace(if (pickRightOperand) binaryExpression.rOperand!! else binaryExpression.lOperand)

        val statement = PsiTreeUtil.getParentOfType(element, PsiStatement::class.java) ?: return
        val oldExpectedExpression = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java) ?: return
        val expectedExpression =
            factory.createExpressionFromText("a.${if (noExpectedExpression) replacementMethod else replacementMethod.replace("()", "(e)")}", element) as PsiMethodCallExpression
        if (!noExpectedExpression) {
            expectedExpression.argumentList.expressions[0].replace(expectedArgument)
        }
        expectedExpression.methodExpression.qualifierExpression!!.replace(oldExpectedExpression.methodExpression.qualifierExpression!!)
        oldExpectedExpression.replace(expectedExpression)
    }
}
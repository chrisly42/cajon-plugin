package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiBinaryExpression
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.createExpectedMethodCall
import de.platon42.intellij.plugins.cajon.findOutmostMethodCall
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.replaceQualifierFromMethodCall

class SplitBinaryExpressionMethodCallQuickFix(
    description: String,
    private val replacementMethod: String,
    private val pickRightOperand: Boolean = false,
    private val noExpectedExpression: Boolean = false
) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val binaryExpression = methodCallExpression.firstArg as? PsiBinaryExpression ?: return
        val expectedArgument = (if (pickRightOperand) binaryExpression.lOperand else binaryExpression.rOperand)?.copy() ?: return
        binaryExpression.replace(if (pickRightOperand) binaryExpression.rOperand!! else binaryExpression.lOperand)

        val oldExpectedExpression = element.findOutmostMethodCall() ?: return
        val args = if (noExpectedExpression) emptyArray() else arrayOf(expectedArgument)
        val expectedExpression = createExpectedMethodCall(element, replacementMethod, *args)
        expectedExpression.replaceQualifierFromMethodCall(oldExpectedExpression)
        oldExpectedExpression.replace(expectedExpression)
    }
}
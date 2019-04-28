package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiUnaryExpression
import com.intellij.psi.util.PsiUtil
import de.platon42.intellij.plugins.cajon.createExpectedMethodCall
import de.platon42.intellij.plugins.cajon.findOutmostMethodCall
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.replaceQualifierFromMethodCall

class RemoveUnaryExpressionQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val assertExpression = methodCallExpression.firstArg as? PsiUnaryExpression ?: return
        val operand = PsiUtil.skipParenthesizedExprDown(assertExpression.operand) ?: return
        assertExpression.replace(operand)

        val oldExpectedExpression = element.findOutmostMethodCall() ?: return
        val expectedExpression = createExpectedMethodCall(element, replacementMethod)
        expectedExpression.replaceQualifierFromMethodCall(oldExpectedExpression)
        oldExpectedExpression.replace(expectedExpression)
    }
}
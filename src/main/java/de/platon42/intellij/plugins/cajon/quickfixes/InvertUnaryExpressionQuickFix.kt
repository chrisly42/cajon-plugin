package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiUnaryExpression
import com.intellij.psi.util.PsiUtil
import de.platon42.intellij.plugins.cajon.createExpectedMethodCall
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.replaceQualifierFromMethodCall

class InvertUnaryExpressionQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    companion object {
        private const val INVERT_CONDITION_DESCRIPTION = "Invert condition in isEqualTo()/isNotEqualTo() expressions"
    }

    override fun getFamilyName(): String {
        return INVERT_CONDITION_DESCRIPTION
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val methodCall = descriptor.startElement as? PsiMethodCallExpression ?: return
        val assertExpression = methodCall.firstArg as? PsiUnaryExpression ?: return
        val operand = PsiUtil.skipParenthesizedExprDown(assertExpression.operand) ?: return

        val expectedExpression = createExpectedMethodCall(assertExpression, replacementMethod, operand)
        expectedExpression.replaceQualifierFromMethodCall(methodCall)
        methodCall.replace(expectedExpression)
    }
}
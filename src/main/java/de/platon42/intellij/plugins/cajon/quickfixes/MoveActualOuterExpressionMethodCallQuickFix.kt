package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.*

class MoveActualOuterExpressionMethodCallQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val assertExpression = methodCallExpression.firstArg as? PsiMethodCallExpression ?: return
        val assertExpressionArg = assertExpression.firstArg.copy()
        assertExpression.replace(assertExpression.qualifierExpression)

        val oldExpectedExpression = element.findOutmostMethodCall() ?: return
        val expectedExpression = createExpectedMethodCall(element, replacementMethod, assertExpressionArg)
        expectedExpression.replaceQualifierFromMethodCall(oldExpectedExpression)
        oldExpectedExpression.replace(expectedExpression)
    }
}
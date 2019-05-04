package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiUnaryExpression
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import de.platon42.intellij.plugins.cajon.*

class InvertUnaryStatementQuickFix : AbstractCommonQuickFix(INVERT_CONDITION_DESCRIPTION) {

    companion object {
        private const val INVERT_CONDITION_DESCRIPTION = "Invert condition in assertThat()"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val outmostCallExpression = descriptor.startElement as? PsiMethodCallExpression ?: return
        val assertThatMethodCall = outmostCallExpression.findStaticMethodCall() ?: return
        val assertExpression = assertThatMethodCall.firstArg as? PsiUnaryExpression ?: return
        val operand = PsiUtil.skipParenthesizedExprDown(assertExpression.operand) ?: return
        assertExpression.replace(operand)

        var methodCall: PsiMethodCallExpression? = assertThatMethodCall
        while (methodCall != null) {
            val expectedResult = methodCall.getExpectedBooleanResult()
            val nextMethodCall = PsiTreeUtil.getParentOfType(methodCall, PsiMethodCallExpression::class.java)
            if (expectedResult != null) {
                val replacementMethod = expectedResult.map(MethodNames.IS_FALSE, MethodNames.IS_TRUE)
                val expectedExpression = createExpectedMethodCall(methodCall, replacementMethod)
                expectedExpression.replaceQualifierFromMethodCall(methodCall)
                methodCall.replace(expectedExpression)
            }
            methodCall = nextMethodCall
        }
    }
}
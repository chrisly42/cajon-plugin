package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

class ReplaceSizeMethodCallQuickFix(
    description: String,
    private val replacementMethod: String,
    private val noExpectedExpression: Boolean,
    private val expectedIsCollection: Boolean
) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val factory = JavaPsiFacade.getElementFactory(element.project)
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val assertExpression = methodCallExpression.argumentList.expressions[0] ?: return
        replaceCollectionSizeOrArrayLength(assertExpression)
        val statement = PsiTreeUtil.getParentOfType(element, PsiStatement::class.java) ?: return
        val oldExpectedExpression = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java) ?: return
        val expectedExpression =
            factory.createExpressionFromText("a.${if (noExpectedExpression) replacementMethod else replacementMethod.replace("()", "(e)")}", element) as PsiMethodCallExpression
        if (!noExpectedExpression) {
            if (expectedIsCollection) {
                replaceCollectionSizeOrArrayLength(oldExpectedExpression.argumentList.expressions[0])
            }
            expectedExpression.argumentList.expressions[0].replace(oldExpectedExpression.argumentList.expressions[0])
        }
        expectedExpression.methodExpression.qualifierExpression!!.replace(oldExpectedExpression.methodExpression.qualifierExpression!!)
        oldExpectedExpression.replace(expectedExpression)
    }

    private fun replaceCollectionSizeOrArrayLength(assertExpression: PsiExpression) {
        assertExpression.replace(
            when (assertExpression) {
                is PsiReferenceExpression -> assertExpression.qualifierExpression!!
                is PsiMethodCallExpression -> assertExpression.methodExpression.qualifierExpression!!
                else -> return
            }
        )
    }
}
package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.map
import de.platon42.intellij.plugins.cajon.qualifierExpression
import de.platon42.intellij.plugins.cajon.replaceQualifierFromMethodCall

class ReplaceSizeMethodCallQuickFix(
    description: String,
    private val replacementMethod: String,
    private val noExpectedExpression: Boolean,
    private val expectedIsCollection: Boolean
) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val assertExpression = methodCallExpression.firstArg
        replaceCollectionSizeOrArrayLength(assertExpression)
        val statement = PsiTreeUtil.getParentOfType(element, PsiStatement::class.java) ?: return
        val oldExpectedExpression = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java) ?: return

        val factory = JavaPsiFacade.getElementFactory(element.project)
        val expectedExpression = factory.createExpressionFromText(
            "a.$replacementMethod${noExpectedExpression.map("()", "(e)")}", element
        ) as PsiMethodCallExpression
        if (!noExpectedExpression) {
            if (expectedIsCollection) {
                replaceCollectionSizeOrArrayLength(oldExpectedExpression.firstArg)
            }
            expectedExpression.firstArg.replace(oldExpectedExpression.firstArg)
        }
        expectedExpression.replaceQualifierFromMethodCall(oldExpectedExpression)
        oldExpectedExpression.replace(expectedExpression)
    }

    private fun replaceCollectionSizeOrArrayLength(assertExpression: PsiExpression) {
        assertExpression.replace(
            when (assertExpression) {
                is PsiReferenceExpression -> assertExpression.qualifierExpression!!
                is PsiMethodCallExpression -> assertExpression.qualifierExpression
                else -> return
            }
        )
    }
}
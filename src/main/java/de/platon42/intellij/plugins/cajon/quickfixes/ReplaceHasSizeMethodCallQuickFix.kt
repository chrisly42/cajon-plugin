package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiReferenceExpression
import de.platon42.intellij.plugins.cajon.createExpectedMethodCall
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.qualifierExpression
import de.platon42.intellij.plugins.cajon.replaceQualifierFromMethodCall

class ReplaceHasSizeMethodCallQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val methodCallExpression = descriptor.startElement as? PsiMethodCallExpression ?: return

        replaceCollectionAndMapSizeOrArrayLength(methodCallExpression.firstArg)

        val expectedExpression = createExpectedMethodCall(methodCallExpression, replacementMethod, methodCallExpression.firstArg)

        expectedExpression.replaceQualifierFromMethodCall(methodCallExpression)
        methodCallExpression.replace(expectedExpression)
    }

    private fun replaceCollectionAndMapSizeOrArrayLength(assertExpression: PsiExpression) {
        assertExpression.replace(
            when (assertExpression) {
                is PsiReferenceExpression -> assertExpression.qualifierExpression!!
                is PsiMethodCallExpression -> assertExpression.qualifierExpression
                else -> return
            }
        )
    }
}
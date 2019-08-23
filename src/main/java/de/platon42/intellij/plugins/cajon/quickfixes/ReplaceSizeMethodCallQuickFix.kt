package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiReferenceExpression
import de.platon42.intellij.plugins.cajon.*

class ReplaceSizeMethodCallQuickFix(
    description: String,
    private val replacementMethod: String,
    private val noExpectedExpression: Boolean = false,
    private val expectedIsCollection: Boolean = false
) : AbstractCommonQuickFix(description) {

    companion object {
        private const val REPLACE_DESCRIPTION = "Replace methods by better ones"
    }

    override fun getFamilyName(): String {
        return REPLACE_DESCRIPTION
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val outmostCallExpression = descriptor.startElement as? PsiMethodCallExpression ?: return
        val assertThatMethodCall = outmostCallExpression.findStaticMethodCall() ?: return
        val assertExpression = assertThatMethodCall.firstArg
        replaceCollectionAndMapSizeOrArrayLength(assertExpression)

        if (expectedIsCollection) {
            replaceCollectionAndMapSizeOrArrayLength(outmostCallExpression.firstArg)
        }

        val args = if (noExpectedExpression) emptyArray() else arrayOf(outmostCallExpression.firstArg)
        val expectedExpression = createExpectedMethodCall(outmostCallExpression, replacementMethod, *args)

        expectedExpression.replaceQualifierFromMethodCall(outmostCallExpression)
        outmostCallExpression.replace(expectedExpression)
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
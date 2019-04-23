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
    private val expectedIsCollection: Boolean = false,
    private val keepActualAsIs: Boolean = false
) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        if (!keepActualAsIs) {
            val assertExpression = methodCallExpression.firstArg
            replaceCollectionSizeOrArrayLength(assertExpression)
        }
        val oldExpectedExpression = element.findOutmostMethodCall() ?: return

        if (expectedIsCollection) {
            replaceCollectionSizeOrArrayLength(oldExpectedExpression.firstArg)
        }

        val args = if (noExpectedExpression) emptyArray() else arrayOf(oldExpectedExpression.firstArg)
        val expectedExpression = createExpectedMethodCall(element, replacementMethod, *args)

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
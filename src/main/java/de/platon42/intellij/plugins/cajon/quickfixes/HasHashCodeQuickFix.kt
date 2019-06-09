package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.*

class HasHashCodeQuickFix :
    AbstractCommonQuickFix(HASHCODE_DESCRIPTION) {

    companion object {
        private const val HASHCODE_DESCRIPTION = "Replace calls to hashCode() with hasSameHashCodeAs()"
    }

    override fun getFamilyName(): String {
        return HASHCODE_DESCRIPTION
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val outmostCallExpression = descriptor.startElement as? PsiMethodCallExpression ?: return
        val assertThatMethodCall = outmostCallExpression.findStaticMethodCall() ?: return

        val assertExpression = assertThatMethodCall.firstArg as? PsiMethodCallExpression ?: return

        val methodsToFix = assertThatMethodCall.gatherAssertionCalls()

        assertExpression.replace(assertExpression.qualifierExpression)

        methodsToFix
            .forEach {
                val innerHashCodeObject = (it.firstArg as PsiMethodCallExpression).qualifierExpression
                val expectedExpression = createExpectedMethodCall(it, "hasSameHashCodeAs", innerHashCodeObject)
                expectedExpression.replaceQualifierFromMethodCall(it)
                it.replace(expectedExpression)
            }
    }
}
package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.*

class MoveOutMethodCallExpressionQuickFix(
    description: String,
    private val replacementMethod: String,
    private val useNullNonNull: Boolean = false,
    private val noExpectedExpression: Boolean = false
) :
    AbstractCommonQuickFix(description) {

    companion object {
        private const val REMOVE_ACTUAL_EXPRESSION_DESCRIPTION = "Move method calls in actual expressions out of assertThat()"
    }

    override fun getFamilyName(): String {
        return REMOVE_ACTUAL_EXPRESSION_DESCRIPTION
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val outmostCallExpression = descriptor.startElement as? PsiMethodCallExpression ?: return
        val assertThatMethodCall = outmostCallExpression.findStaticMethodCall() ?: return
        val assertExpression = assertThatMethodCall.firstArg as? PsiMethodCallExpression ?: return
        val assertExpressionArg = if (noExpectedExpression) null else assertExpression.getArgOrNull(0)?.copy()

        val methodsToFix = assertThatMethodCall.collectMethodCallsUpToStatement()
            .filter { (if (useNullNonNull) it.getExpectedNullNonNullResult() else it.getExpectedBooleanResult()) != null }
            .toList()

        assertExpression.replace(assertExpression.qualifierExpression)

        methodsToFix
            .forEach {
                val expectedExpression = createExpectedMethodCall(it, replacementMethod, *listOfNotNull(assertExpressionArg).toTypedArray())
                expectedExpression.replaceQualifierFromMethodCall(it)
                it.replace(expectedExpression)
            }
    }
}
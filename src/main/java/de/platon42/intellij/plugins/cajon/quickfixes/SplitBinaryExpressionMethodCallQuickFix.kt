package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiBinaryExpression
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.*

class SplitBinaryExpressionMethodCallQuickFix(
    description: String,
    private val replacementMethod: String,
    private val pickRightOperand: Boolean = false,
    private val noExpectedExpression: Boolean = false
) : AbstractCommonQuickFix(description) {

    companion object {
        private const val SPLIT_EXPRESSION_DESCRIPTION = "Split binary expressions out of assertThat()"
    }

    override fun getFamilyName(): String {
        return SPLIT_EXPRESSION_DESCRIPTION
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val outmostCallExpression = descriptor.startElement as? PsiMethodCallExpression ?: return
        val assertThatMethodCall = outmostCallExpression.findStaticMethodCall() ?: return

        val methodsToFix = assertThatMethodCall.collectMethodCallsUpToStatement()
            .filter { it.getExpectedBooleanResult() != null }
            .toList()

        val binaryExpression = assertThatMethodCall.firstArg as? PsiBinaryExpression ?: return
        val expectedArgument = (if (pickRightOperand) binaryExpression.lOperand else binaryExpression.rOperand)?.copy() ?: return
        binaryExpression.replace(if (pickRightOperand) binaryExpression.rOperand!! else binaryExpression.lOperand)

        val args = if (noExpectedExpression) emptyArray() else arrayOf(expectedArgument)

        methodsToFix
            .forEach {
                val expectedExpression = createExpectedMethodCall(it, replacementMethod, *args)
                expectedExpression.replaceQualifierFromMethodCall(it)
                it.replace(expectedExpression)
            }
    }
}
package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiInstanceOfExpression
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.util.PsiUtil
import de.platon42.intellij.plugins.cajon.*

class MoveOutInstanceOfExpressionQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    companion object {
        private const val REMOVE_INSTANCEOF_DESCRIPTION = "Move instanceof in actual expressions out of assertThat()"
    }

    override fun getFamilyName(): String {
        return REMOVE_INSTANCEOF_DESCRIPTION
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val outmostCallExpression = descriptor.startElement as? PsiMethodCallExpression ?: return
        val assertThatMethodCall = outmostCallExpression.findStaticMethodCall() ?: return
        val assertExpression = assertThatMethodCall.firstArg as? PsiInstanceOfExpression ?: return
        val expectedClass = assertExpression.checkType ?: return

        val methodsToFix = assertThatMethodCall.collectMethodCallsUpToStatement()
            .filter { it.getExpectedBooleanResult() != null }
            .toList()

        val factory = JavaPsiFacade.getElementFactory(project)
        val classObjectAccess = factory.createExpressionFromText("${expectedClass.type.canonicalText}.class", null)

        val operand = PsiUtil.deparenthesizeExpression(assertExpression.operand) ?: return
        assertExpression.replace(operand)

        methodsToFix
            .forEach {
                val expectedExpression = createExpectedMethodCall(it, replacementMethod, classObjectAccess)
                expectedExpression.replaceQualifierFromMethodCall(it)
                it.replace(expectedExpression)
            }
    }
}
package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.*

class ReplaceJUnitAssumeMethodCallQuickFix(description: String, private val replacementMethod: String) :
    AbstractCommonQuickFix(description) {

    companion object {
        private const val CONVERT_DESCRIPTION = "Convert JUnit assumptions to assertJ"
    }

    override fun getFamilyName(): String {
        return CONVERT_DESCRIPTION
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val args = methodCallExpression.argumentList
        val count = args.expressions.size
        val actualExpression = args.expressions[count - 1] ?: return
        val messageExpression = args.expressions.getOrNull(count - 2)

        val expectedMethodCall = createExpectedMethodCall(element, replacementMethod)
        val newMethodCall = createAssumeThat(element, actualExpression)

        if (messageExpression != null) {
            val asExpression = createExpectedMethodCall(element, MethodNames.AS, messageExpression)
            asExpression.replaceQualifier(newMethodCall)
            expectedMethodCall.replaceQualifier(asExpression)
        } else {
            expectedMethodCall.replaceQualifier(newMethodCall)
        }

        newMethodCall.resolveMethod()?.addAsStaticImport(element)
        element.replace(expectedMethodCall).shortenAndReformat()
    }
}
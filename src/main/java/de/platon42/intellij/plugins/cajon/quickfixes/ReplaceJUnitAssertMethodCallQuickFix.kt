package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.GUAVA_ASSERTIONS_CLASSNAME

class ReplaceJUnitAssertMethodCallQuickFix(description: String, private val replacementMethod: String, private val noExpectedExpression: Boolean) :
    AbstractCommonQuickFix(description) {

    companion object {
        private const val CONVERT_DESCRIPTION = "Convert JUnit assertions to assertJ"
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
        val (expectedExpressions, messageExpression) = if (noExpectedExpression) {
            val message = args.expressions.getOrNull(count - 2)
            emptyArray<PsiExpression>() to message
        } else {
            val expected = args.expressions[count - 2] ?: return
            val message = args.expressions.getOrNull(count - 3)
            arrayOf(expected) to message
        }

        val swapActualAndExpected = ((expectedExpressions.getOrNull(0)?.calculateConstantValue() == null)
                && (actualExpression.calculateConstantValue() != null))
        val (expectedMethodCall, newMethodCall) = if (swapActualAndExpected) {
            createExpectedMethodCall(element, replacementMethod, actualExpression) to
                    createAssertThat(element, expectedExpressions.single())
        } else {
            createExpectedMethodCall(element, replacementMethod, *expectedExpressions) to
                    createAssertThat(element, actualExpression)
        }

        if (messageExpression != null) {
            val asExpression = createExpectedMethodCall(element, MethodNames.AS, messageExpression)
            asExpression.replaceQualifier(newMethodCall)
            expectedMethodCall.replaceQualifier(asExpression)
        } else {
            expectedMethodCall.replaceQualifier(newMethodCall)
        }

        newMethodCall.resolveMethod()?.addAsStaticImport(element, GUAVA_ASSERTIONS_CLASSNAME)
        element.replace(expectedMethodCall).shortenAndReformat()
    }
}
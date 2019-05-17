package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.GUAVA_ASSERTIONS_CLASSNAME

class ReplaceJUnitDeltaAssertMethodCallQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

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
        val messageExpression = args.expressions.getOrNull(count - 4)
        val expectedExpression = args.expressions[count - 3] ?: return
        val actualExpression = args.expressions[count - 2] ?: return
        val deltaExpression = args.expressions[count - 1] ?: return

        val offsetMethodCall = createMethodCall(element, "org.assertj.core.data.Offset.offset", deltaExpression)

        val swapActualAndExpected = ((expectedExpression.calculateConstantValue() == null)
                && (actualExpression.calculateConstantValue() != null))
        val (expectedMethodCall, newMethodCall) = if (swapActualAndExpected) {
            createExpectedMethodCall(element, replacementMethod, actualExpression, offsetMethodCall) to
                    createAssertThat(element, expectedExpression)
        } else {
            createExpectedMethodCall(element, replacementMethod, expectedExpression, offsetMethodCall) to
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
        offsetMethodCall.resolveMethod()?.addAsStaticImport(element)
        element.replace(expectedMethodCall).shortenAndReformat()
    }
}
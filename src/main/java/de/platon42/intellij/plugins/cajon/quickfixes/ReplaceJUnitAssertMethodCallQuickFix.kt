package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.GUAVA_ASSERTIONS_CLASSNAME

class ReplaceJUnitAssertMethodCallQuickFix(description: String, private val replacementMethod: String, private val noExpectedExpression: Boolean, private val junit5: Boolean) :
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
        val hasMessage = if (noExpectedExpression) count == 1 else count == 2;

        val actualExpression: PsiExpression
        val expectedExpressions: Array<PsiExpression>
        val messageExpression: PsiExpression?

        if (junit5) {
            actualExpression = args.expressions[if (noExpectedExpression) 0 else 1] ?: return
            messageExpression = if (hasMessage) null else args.expressions[count - 1]
            expectedExpressions = if (noExpectedExpression) emptyArray() else arrayOf(args.expressions[0])
        } else {
            actualExpression = args.expressions[count - 1] ?: return
            messageExpression = if (hasMessage) null else args.expressions[0]
            expectedExpressions = if (noExpectedExpression) emptyArray() else arrayOf(args.expressions[count - 2])
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
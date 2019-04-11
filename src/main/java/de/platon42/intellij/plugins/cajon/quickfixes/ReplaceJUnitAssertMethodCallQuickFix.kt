package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.GUAVA_ASSERTIONS_CLASSNAME
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.map
import de.platon42.intellij.plugins.cajon.replaceQualifier

class ReplaceJUnitAssertMethodCallQuickFix(description: String, private val noExpectedExpression: Boolean, private val replacementMethod: String) :
    AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val args = methodCallExpression.argumentList
        val count = args.expressions.size
        val actualExpression = args.expressions[count - 1] ?: return
        val (expectedExpression, messageExpression) = if (noExpectedExpression) {
            val message = if (count > 1) args.expressions[0] else null
            null to message
        } else {
            val expected = args.expressions[count - 2] ?: return
            val message = if (count > 2) args.expressions[0] else null
            expected to message
        }

        val factory = JavaPsiFacade.getElementFactory(element.project)
        val expectedMethodCall = factory.createExpressionFromText(
            "a.$replacementMethod${noExpectedExpression.map("()", "(e)")}", element
        ) as PsiMethodCallExpression
        if (!noExpectedExpression) {
            expectedMethodCall.firstArg.replace(expectedExpression!!)
        }

        val newMethodCall = createAssertThat(element, actualExpression)

        if (messageExpression != null) {
            val asExpression = factory.createExpressionFromText("a.${MethodNames.AS}(desc)", element) as PsiMethodCallExpression
            asExpression.firstArg.replace(messageExpression)
            asExpression.replaceQualifier(newMethodCall)
            expectedMethodCall.replaceQualifier(asExpression)
        } else {
            expectedMethodCall.replaceQualifier(newMethodCall)
        }

        val assertThatMethod = newMethodCall.resolveMethod() ?: return
        addStaticImport(assertThatMethod, element, factory, GUAVA_ASSERTIONS_CLASSNAME)

        val codeStyleManager = JavaCodeStyleManager.getInstance(element.project)
        val newElement = element.replace(expectedMethodCall)
        val shortened = codeStyleManager.shortenClassReferences(newElement)
        CodeStyleManager.getInstance(element.project).reformat(shortened)
    }
}
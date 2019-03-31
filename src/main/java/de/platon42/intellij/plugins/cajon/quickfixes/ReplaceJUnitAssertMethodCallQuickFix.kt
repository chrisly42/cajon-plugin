package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager

class ReplaceJUnitAssertMethodCallQuickFix(description: String, private val hasExpected: Boolean, private val replacementMethod: String) :
    AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val factory = JavaPsiFacade.getElementFactory(element.project)
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val args = methodCallExpression.argumentList
        val count = args.expressionCount
        val actualExpression = args.expressions[count - 1] ?: return
        val (expectedExpression, messageExpression) = if (hasExpected) {
            val expected = args.expressions[count - 2] ?: return
            val message = if (count > 2) args.expressions[0] else null
            Pair(expected, message)
        } else {
            val message = if (count > 1) args.expressions[0] else null
            Pair(null, message)
        }

        val expectedMethodCall = factory.createExpressionFromText(
            "a.${if (hasExpected) replacementMethod.replace("()", "(e)") else replacementMethod}", element
        ) as PsiMethodCallExpression
        if (hasExpected) {
            expectedMethodCall.argumentList.expressions[0].replace(expectedExpression!!)
        }

        val newMethodCall = factory.createExpressionFromText(
            "org.assertj.core.api.Assertions.assertThat(a)", element
        ) as PsiMethodCallExpression
        newMethodCall.argumentList.expressions[0].replace(actualExpression)

        if (messageExpression != null) {
            val asExpression = factory.createExpressionFromText("a.as(desc)", element) as PsiMethodCallExpression
            asExpression.argumentList.expressions[0].replace(messageExpression)
            asExpression.methodExpression.qualifierExpression!!.replace(newMethodCall)
            expectedMethodCall.methodExpression.qualifierExpression!!.replace(asExpression)
        } else {
            expectedMethodCall.methodExpression.qualifierExpression!!.replace(newMethodCall)
        }

        val assertThatMethod = newMethodCall.resolveMethod() ?: return
        addStaticImport(assertThatMethod, element, factory, GUAVA_ASSERTIONS_CLASSNAME)

        val codeStyleManager = JavaCodeStyleManager.getInstance(element.project)
        val newElement = element.replace(expectedMethodCall)
        val shortened = codeStyleManager.shortenClassReferences(newElement)
        CodeStyleManager.getInstance(element.project).reformat(shortened)
    }
}
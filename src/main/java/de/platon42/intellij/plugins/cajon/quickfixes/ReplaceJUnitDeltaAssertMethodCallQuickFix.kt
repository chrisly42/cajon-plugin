package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.GUAVA_ASSERTIONS_CLASSNAME
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.getArg
import de.platon42.intellij.plugins.cajon.replaceQualifier

class ReplaceJUnitDeltaAssertMethodCallQuickFix(description: String, private val replacementMethod: String) : AbstractCommonQuickFix(description) {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.startElement
        val methodCallExpression = element as? PsiMethodCallExpression ?: return
        val args = methodCallExpression.argumentList
        val count = args.expressionCount
        val actualExpression = args.expressions[count - 2] ?: return
        val messageExpression = if (count > 3) args.expressions[0] else null
        val expectedExpression = args.expressions[count - 3] ?: return
        val deltaExpression = args.expressions[count - 1] ?: return

        val factory = JavaPsiFacade.getElementFactory(element.project)
        val offsetMethodCall = factory.createExpressionFromText(
            "org.assertj.core.data.Offset.offset(c)", element
        ) as PsiMethodCallExpression

        offsetMethodCall.firstArg.replace(deltaExpression)

        val expectedMethodCall = factory.createExpressionFromText(
            "a.$replacementMethod(e, offs)", element
        ) as PsiMethodCallExpression

        expectedMethodCall.firstArg.replace(expectedExpression)
        expectedMethodCall.getArg(1).replace(offsetMethodCall)

        val newMethodCall = createAssertThat(element, actualExpression)

        if (messageExpression != null) {
            val asExpression = factory.createExpressionFromText("a.as(desc)", element) as PsiMethodCallExpression
            asExpression.firstArg.replace(messageExpression)
            asExpression.replaceQualifier(newMethodCall)
            expectedMethodCall.replaceQualifier(asExpression)
        } else {
            expectedMethodCall.replaceQualifier(newMethodCall)
        }

        val assertThatMethod = newMethodCall.resolveMethod() ?: return
        addStaticImport(assertThatMethod, element, factory, GUAVA_ASSERTIONS_CLASSNAME)
        val offsetMethod = offsetMethodCall.resolveMethod() ?: return
        addStaticImport(offsetMethod, element, factory)

        val codeStyleManager = JavaCodeStyleManager.getInstance(element.project)
        val newElement = element.replace(expectedMethodCall)
        val shortened = codeStyleManager.shortenClassReferences(newElement)
        CodeStyleManager.getInstance(element.project).reformat(shortened)
    }
}
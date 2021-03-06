package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiMethodCallExpression
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*

class MoveOutMethodCallExpressionQuickFix(
    description: String,
    private val replacementMethod: String,
    private val useNullNonNull: Boolean = false,
    private val noExpectedExpression: Boolean = false,
    private val keepExpectedAsSecondArgument: Boolean = false,
    private val replaceOnlyThisMethod: CallMatcher? = null,
    private val replaceFromOriginalMethod: Boolean = false
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
        val assertExpressionArg = if (noExpectedExpression) null else assertExpression.getArgOrNull(0)?.copy() as PsiExpression?

        when {
            replaceOnlyThisMethod != null -> {
                val methodsToFix = assertThatMethodCall.collectMethodCallsUpToStatement()
                    .filter(replaceOnlyThisMethod::test)
                    .toList()

                assertExpression.replace(assertExpression.qualifierExpression)

                methodsToFix
                    .forEach {
                        val expectedExpression = createExpectedMethodCall(
                            it,
                            replacementMethod,
                            *if (replaceFromOriginalMethod || noExpectedExpression) listOfNotNull(assertExpressionArg).toTypedArray() else it.argumentList.expressions
                        )
                        expectedExpression.replaceQualifierFromMethodCall(it)
                        it.replace(expectedExpression)
                    }
            }
            keepExpectedAsSecondArgument -> {
                assertExpressionArg ?: return
                val secondArg =
                    if (useNullNonNull) JavaPsiFacade.getElementFactory(project).createExpressionFromText("null", null) else outmostCallExpression.getArgOrNull(0)?.copy() ?: return

                assertExpression.replace(assertExpression.qualifierExpression)

                val expectedExpression = createExpectedMethodCall(outmostCallExpression, replacementMethod, assertExpressionArg, secondArg)
                expectedExpression.replaceQualifierFromMethodCall(outmostCallExpression)
                outmostCallExpression.replace(expectedExpression)
            }
            else -> {
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
    }
}
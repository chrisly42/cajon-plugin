package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiInstanceOfExpression
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.findOutmostMethodCall
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.map
import de.platon42.intellij.plugins.cajon.quickfixes.RemoveInstanceOfExpressionQuickFix

class AssertThatInstanceOfInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a class instance"
        private const val REPLACE_INSTANCEOF_DESCRIPTION_TEMPLATE = "Replace instanceof expression by assertThat().%s()"
        private const val MOVE_OUT_INSTANCEOF_MESSAGE = "instanceof expression could be moved out of assertThat()"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!ASSERT_THAT_BOOLEAN.test(expression)) {
                    return
                }

                val expectedCallExpression = expression.findOutmostMethodCall() ?: return
                val expectedResult = getExpectedBooleanResult(expectedCallExpression) ?: return

                if (expression.firstArg is PsiInstanceOfExpression) {
                    val replacementMethod = expectedResult.map(MethodNames.IS_INSTANCE_OF, MethodNames.IS_NOT_INSTANCE_OF)
                    registerRemoveInstanceOfMethod(holder, expression, replacementMethod, ::RemoveInstanceOfExpressionQuickFix)
                }
            }

            private fun registerRemoveInstanceOfMethod(
                holder: ProblemsHolder,
                expression: PsiMethodCallExpression,
                replacementMethod: String,
                quickFixSupplier: (String, String) -> LocalQuickFix
            ) {
                val description = REPLACE_INSTANCEOF_DESCRIPTION_TEMPLATE.format(replacementMethod)
                val quickfix = quickFixSupplier(description, replacementMethod)
                holder.registerProblem(expression, MOVE_OUT_INSTANCEOF_MESSAGE, quickfix)
            }
        }
    }
}
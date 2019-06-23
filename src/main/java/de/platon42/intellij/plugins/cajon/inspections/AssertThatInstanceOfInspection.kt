package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.MoveOutInstanceOfExpressionQuickFix

class AssertThatInstanceOfInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a class instance"
        private const val REMOVE_INSTANCEOF_DESCRIPTION_TEMPLATE = "Remove instanceof in actual expression and use assertThat().%s() instead"
        private const val MOVE_OUT_INSTANCEOF_MESSAGE = "instanceof expression could be moved out of assertThat()"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                if (!statement.hasAssertThat()) return
                val staticMethodCall = statement.findStaticMethodCall() ?: return
                if (!ASSERT_THAT_BOOLEAN.test(staticMethodCall)) return

                val expectedCallExpression = statement.findOutmostMethodCall() ?: return
                val expectedResult = expectedCallExpression.getAllTheSameExpectedBooleanConstants() ?: return

                if (staticMethodCall.firstArg is PsiInstanceOfExpression) {
                    val replacementMethod = expectedResult.map(MethodNames.IS_INSTANCE_OF, MethodNames.IS_NOT_INSTANCE_OF)
                    registerMoveOutInstanceOfMethod(holder, expectedCallExpression, replacementMethod, ::MoveOutInstanceOfExpressionQuickFix)
                }
            }
        }
    }

    private fun registerMoveOutInstanceOfMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        replacementMethod: String,
        quickFixSupplier: (String, String) -> LocalQuickFix
    ) {
        val description = REMOVE_INSTANCEOF_DESCRIPTION_TEMPLATE.format(replacementMethod)
        val quickfix = quickFixSupplier(description, replacementMethod)
        holder.registerProblem(expression, MOVE_OUT_INSTANCEOF_MESSAGE, quickfix)
    }
}
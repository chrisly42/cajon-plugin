package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiStatement
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.calculateConstantParameterValue
import de.platon42.intellij.plugins.cajon.hasAssertThat

class AssertThatEnumerableIsEmptyInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting an empty enumerable"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!expression.hasAssertThat()) return
                val isLastExpression = expression.parent is PsiStatement
                if (!(HAS_SIZE.test(expression) && isLastExpression)) return

                val value = expression.calculateConstantParameterValue(0) ?: return
                if (value == 0) {
                    registerSimplifyMethod(holder, expression, MethodNames.IS_EMPTY)
                }
            }
        }
    }
}
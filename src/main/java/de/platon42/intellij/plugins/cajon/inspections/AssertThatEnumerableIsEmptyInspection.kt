package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.MethodNames

class AssertThatEnumerableIsEmptyInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting an empty enumerable"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!HAS_SIZE.test(expression)) {
                    return
                }

                val value = calculateConstantParameterValue(expression, 0) ?: return
                if (value == 0) {
                    registerSimplifyMethod(holder, expression, MethodNames.IS_EMPTY)
                }
            }
        }
    }
}
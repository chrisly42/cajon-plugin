package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.annotations.NonNls

class AssertThatEnumerableIsEmptyInspection : AbstractAssertJInspection() {

    companion object {
        @NonNls
        private val DISPLAY_NAME = "Asserting an enumerable is empty"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                val hasSize = HAS_SIZE.test(expression)
                if (!hasSize) {
                    return
                }

                val value = calculateConstantParameterValue(expression, 0) ?: return
                if (value == 0) {
                    registerSimplifyMethod(holder, expression, "isEmpty()")
                }
            }
        }
    }
}
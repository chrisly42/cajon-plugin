package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiStatement
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.calculateConstantParameterValue
import de.platon42.intellij.plugins.cajon.hasAssertThat

class AssertThatEnumerableIsEmptyInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting an empty or not empty enumerable"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!expression.hasAssertThat()) return
                val isLastExpression = expression.parent is PsiStatement
                val value = expression.calculateConstantParameterValue(0) ?: return

                val isEmpty = (CallMatcher.anyOf(HAS_SIZE, HAS_SIZE_LESS_THAN_OR_EQUAL_TO_INT).test(expression) && (value == 0)) ||
                        (HAS_SIZE_LESS_THAN_INT.test(expression) && (value == 1));
                val isNotEmpty = (HAS_SIZE_GREATER_THAN_INT.test(expression) && (value == 0)) ||
                        (HAS_SIZE_GREATER_THAN_OR_EQUAL_TO_INT.test(expression) && (value == 1));

                if (isEmpty && isLastExpression) {
                    registerSimplifyMethod(holder, expression, MethodNames.IS_EMPTY)
                } else if (isNotEmpty) {
                    registerSimplifyMethod(holder, expression, MethodNames.IS_NOT_EMPTY)
                }
            }
        }
    }
}
package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.hasAssertThat
import de.platon42.intellij.plugins.cajon.map

class AssertThatObjectIsNullOrNotNullInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting null or not-null"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!expression.hasAssertThat()) {
                    return
                }
                val isNotEqualTo = IS_NOT_EQUAL_TO_OBJECT.test(expression)
                val isEqualTo = IS_EQUAL_TO_OBJECT.test(expression)
                val isLastExpression = expression.parent is PsiStatement
                if (!((isEqualTo && isLastExpression) || isNotEqualTo)) {
                    return
                }

                if (expression.firstArg.type == PsiType.NULL) {
                    registerSimplifyMethod(holder, expression, isEqualTo.map(MethodNames.IS_NULL, MethodNames.IS_NOT_NULL))
                }
            }
        }
    }
}
package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.annotations.NonNls

class AssertThatStringIsEmptyInspection : AbstractAssertJInspection() {

    companion object {
        @NonNls
        private val DISPLAY_NAME = "Asserting an empty string"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                val isEqual = IS_EQUAL_TO_OBJECT.test(expression)
                val hasSize = CHAR_SEQUENCE_HAS_SIZE.test(expression)
                if (!(isEqual || hasSize)) {
                    return
                }

                if (!checkAssertedType(expression, ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME)) {
                    return
                }

                if (isEqual) {
                    val psiExpression = expression.argumentList.expressions[0] as? PsiLiteralExpression ?: return

                    if (psiExpression.value == "") {
                        registerSimplifyMethod(holder, expression, "isEmpty()")
                    }
                } else {
                    val psiExpression = expression.argumentList.expressions[0] as? PsiLiteralExpression ?: return
                    if (psiExpression.value == 0) {
                        registerSimplifyMethod(holder, expression, "isEmpty()")
                    }
                }
            }
        }
    }
}
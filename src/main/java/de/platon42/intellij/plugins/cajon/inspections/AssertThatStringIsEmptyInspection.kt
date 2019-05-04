package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiStatement
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.calculateConstantParameterValue

class AssertThatStringIsEmptyInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting an empty string"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                val isEqual = IS_EQUAL_TO_OBJECT.test(expression)
                val hasSize = HAS_SIZE.test(expression)
                val isLastExpression = expression.parent is PsiStatement
                if (!((isEqual || hasSize) && isLastExpression)) {
                    return
                }

                if (!checkAssertedType(expression, ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME)) {
                    return
                }

                val value = expression.calculateConstantParameterValue(0) ?: return
                if ((isEqual && (value == "")) || (hasSize && (value == 0))) {
                    registerSimplifyMethod(holder, expression, MethodNames.IS_EMPTY)
                }
            }
        }
    }
}
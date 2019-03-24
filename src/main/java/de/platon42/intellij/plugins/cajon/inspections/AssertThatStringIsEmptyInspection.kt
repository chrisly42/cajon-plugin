package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiMethodCallExpression
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSimpleMethodCallQuickFix
import org.jetbrains.annotations.NonNls

class AssertThatStringIsEmptyInspection : AbstractAssertJInspection() {

    companion object {
        @NonNls
        private val DISPLAY_NAME = "Asserting an empty string"

        @NonNls
        private val INSPECTION_MESSAGE = "isEqualTo(\"\") can be simplified to isEmpty()"

        @NonNls
        private val QUICKFIX_DESCRIPTION = "Replace isEqualTo(\"\") with isEmpty()"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!IS_EQUAL_TO_OBJECT.test(expression)) {
                    return
                }

                val psiExpression = expression.argumentList.expressions[0] as? PsiLiteralExpression ?: return

                if (psiExpression.value == "") {
                    holder.registerProblem(
                        expression,
                        INSPECTION_MESSAGE,
                        ProblemHighlightType.INFORMATION,
                        null as TextRange?,
                        ReplaceSimpleMethodCallQuickFix(QUICKFIX_DESCRIPTION, "isEmpty()")
                    )
                }
            }
        }
    }

}
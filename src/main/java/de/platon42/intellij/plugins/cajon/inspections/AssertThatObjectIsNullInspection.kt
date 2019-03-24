package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiType
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSimpleMethodCallQuickFix
import org.jetbrains.annotations.NonNls

class AssertThatObjectIsNullInspection : AbstractAssertJInspection() {

    companion object {
        @NonNls
        private val DISPLAY_NAME = "Asserting null"

        @NonNls
        private val INSPECTION_MESSAGE = "isEqualTo(null) can be simplified to isNull()"

        @NonNls
        private val QUICKFIX_DESCRIPTION = "Replace isEqualTo(null) with isNull()"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!IS_EQUAL_TO_OBJECT.test(expression)) {
                    return
                }

                if (expression.argumentList.expressions[0].type == PsiType.NULL) {
                    holder.registerProblem(
                        expression,
                        INSPECTION_MESSAGE,
                        ProblemHighlightType.INFORMATION,
                        null as TextRange?,
                        ReplaceSimpleMethodCallQuickFix(QUICKFIX_DESCRIPTION, "isNull()")
                    )
                }
            }
        }
    }
}
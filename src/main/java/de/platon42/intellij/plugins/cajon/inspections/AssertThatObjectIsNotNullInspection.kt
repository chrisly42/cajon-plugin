package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.jetbrains.annotations.NonNls

class AssertThatObjectIsNotNullInspection : AbstractAssertJInspection() {

    companion object {
        @NonNls
        private val DISPLAY_NAME = "Asserting non-null"

        @NonNls
        private val INSPECTION_MESSAGE = "isNotEqualTo(null) can be simplified to isNotNull()"

        @NonNls
        private val QUICKFIX_DESCRIPTION = "Replace isNotEqualTo(null) with isNotNull()"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!IS_NOT_EQUAL_TO_OBJECT.test(expression)) {
                    return
                }

                if (expression.argumentList.expressions[0].type == PsiType.NULL) {
                    holder.registerProblem(
                        expression,
                        INSPECTION_MESSAGE,
                        ProblemHighlightType.INFORMATION,
                        null as TextRange?,
                        ReplaceWithIsNotNullQuickFix()
                    )
                }
            }
        }
    }

    private class ReplaceWithIsNotNullQuickFix : LocalQuickFix {
        override fun getFamilyName() = QUICKFIX_DESCRIPTION

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val element = descriptor.startElement
            val factory = JavaPsiFacade.getElementFactory(element.project)
            val methodCallExpression = element as? PsiMethodCallExpression ?: return
            val oldQualifier = methodCallExpression.methodExpression.qualifierExpression ?: return
            val isNotNullExpression =
                factory.createExpressionFromText("a.isNotNull()", null) as PsiMethodCallExpression
            isNotNullExpression.methodExpression.qualifierExpression!!.replace(oldQualifier)
            element.replace(isNotNullExpression)
        }
    }
}
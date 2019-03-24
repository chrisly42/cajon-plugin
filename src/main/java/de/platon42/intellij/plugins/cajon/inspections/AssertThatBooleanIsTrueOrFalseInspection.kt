package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.TypeConversionUtil
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSimpleMethodCallQuickFix
import org.jetbrains.annotations.NonNls

class AssertThatBooleanIsTrueOrFalseInspection : AbstractAssertJInspection() {

    companion object {
        @NonNls
        private val DISPLAY_NAME = "Asserting true or false"

        @NonNls
        private val INSPECTION_MESSAGE = "isEqualTo(true/false) can be simplified to isTrue()/isFalse()"

        @NonNls
        private val QUICKFIX_DESCRIPTION_IS_TRUE = "Replace isEqualTo(true/false) with isTrue()/isFalse()"

        @NonNls
        private val QUICKFIX_DESCRIPTION_NOT_IS_TRUE = "Replace isNotEqualTo(true/false) with isFalse()/isTrue()"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                val isEqualToObject = IS_EQUAL_TO_OBJECT.test(expression)
                val isEqualToBoolean = IS_EQUAL_TO_BOOLEAN.test(expression)
                val isNotEqualToObject = IS_NOT_EQUAL_TO_OBJECT.test(expression)
                val isNotEqualToBoolean = IS_NOT_EQUAL_TO_BOOLEAN.test(expression)
                val normalBooleanTest = isEqualToObject || isEqualToBoolean
                val flippedBooleanTest = isNotEqualToObject || isNotEqualToBoolean
                if (!(normalBooleanTest || flippedBooleanTest)) {
                    return
                }
                var assertedType = expression.methodExpression.qualifierExpression?.type
                if (assertedType is PsiCapturedWildcardType) {
                    assertedType = assertedType.upperBound
                }
                val assertedTypeIsBoolean =
                    assertedType?.canonicalText?.startsWith(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME) ?: false
                val equalToExpression = expression.argumentList.expressions[0]!!
                if (!TypeConversionUtil.isBooleanType(equalToExpression.type) || !assertedTypeIsBoolean) {
                    return
                }
                val constantEvaluationHelper = JavaPsiFacade.getInstance(holder.project).constantEvaluationHelper
                var result = constantEvaluationHelper.computeConstantExpression(equalToExpression)
                if (result == null) {
                    val field = (equalToExpression as? PsiReferenceExpression)?.resolve() as? PsiField
                    if (field?.containingClass?.qualifiedName == CommonClassNames.JAVA_LANG_BOOLEAN) {
                        when {
                            field.name == "TRUE" -> result = true
                            field.name == "FALSE" -> result = false
                        }
                    }
                }
                val expectedResult = result as? Boolean ?: return
                val description =
                    if (flippedBooleanTest) QUICKFIX_DESCRIPTION_NOT_IS_TRUE else QUICKFIX_DESCRIPTION_IS_TRUE
                val replacementMethod = if (expectedResult xor flippedBooleanTest) "isTrue()" else "isFalse()"
                holder.registerProblem(
                    expression,
                    INSPECTION_MESSAGE,
                    ProblemHighlightType.INFORMATION,
                    null as TextRange?,
                    ReplaceSimpleMethodCallQuickFix(description, replacementMethod)
                )
            }
        }
    }
}
package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.util.TypeConversionUtil
import org.jetbrains.annotations.NonNls

class AssertThatBooleanIsTrueOrFalseInspection : AbstractAssertJInspection() {

    companion object {
        @NonNls
        private val DISPLAY_NAME = "Asserting true or false"
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
                if (!checkAssertedType(expression, ABSTRACT_BOOLEAN_ASSERT_CLASSNAME)) {
                    return
                }

                val equalToExpression = expression.argumentList.expressions[0] ?: return
                if (!TypeConversionUtil.isBooleanType(equalToExpression.type)) {
                    return
                }
                var value = calculateConstantParameterValue(expression, 0)
                if (value == null) {
                    val field = (equalToExpression as? PsiReferenceExpression)?.resolve() as? PsiField
                    if (field?.containingClass?.qualifiedName == CommonClassNames.JAVA_LANG_BOOLEAN) {
                        when {
                            field.name == "TRUE" -> value = true
                            field.name == "FALSE" -> value = false
                        }
                    }
                }
                val expectedResult = value as? Boolean ?: return

                val replacementMethod = if (expectedResult xor flippedBooleanTest) "isTrue()" else "isFalse()"
                registerSimplifyMethod(holder, expression, replacementMethod)
            }
        }
    }
}
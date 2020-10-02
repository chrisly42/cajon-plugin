package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.util.TypeConversionUtil
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_BOOLEAN_ASSERT_CLASSNAME

class AssertThatBooleanConditionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a boolean condition"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!expression.hasAssertThat()) return
                val isEqualToObject = IS_EQUAL_TO_OBJECT.test(expression)
                val isEqualToPrimitive = IS_EQUAL_TO_BOOLEAN.test(expression)
                val isNotEqualToObject = IS_NOT_EQUAL_TO_OBJECT.test(expression)
                val isNotEqualToPrimitive = IS_NOT_EQUAL_TO_BOOLEAN.test(expression)

                if (!(isEqualToObject || isEqualToPrimitive || isNotEqualToObject || isNotEqualToPrimitive)) return
                if (!checkAssertedType(expression, ABSTRACT_BOOLEAN_ASSERT_CLASSNAME)) return

                val expectedExpression = expression.firstArg
                if (!TypeConversionUtil.isBooleanType(expectedExpression.type)) return

                val expectedResult = expression.calculateConstantParameterValue(0) as? Boolean ?: return
                val flippedBooleanTest = isNotEqualToObject || isNotEqualToPrimitive

                val replacementMethod = (expectedResult xor flippedBooleanTest).map(MethodNames.IS_TRUE, MethodNames.IS_FALSE)
                registerSimplifyMethod(holder, expression, replacementMethod)
            }
        }
    }
}
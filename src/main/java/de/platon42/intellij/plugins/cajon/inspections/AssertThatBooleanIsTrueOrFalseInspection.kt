package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.util.TypeConversionUtil
import de.platon42.intellij.plugins.cajon.firstArg

class AssertThatBooleanIsTrueOrFalseInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting true or false"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                val matchingCalls = listOf(
                    IS_EQUAL_TO_OBJECT, IS_EQUAL_TO_BOOLEAN,
                    IS_NOT_EQUAL_TO_OBJECT, IS_NOT_EQUAL_TO_BOOLEAN
                ).map { it.test(expression) }
                if (matchingCalls.none { it }) {
                    return
                }
                if (!checkAssertedType(expression, ABSTRACT_BOOLEAN_ASSERT_CLASSNAME)) {
                    return
                }

                val expectedExpression = expression.firstArg
                if (!TypeConversionUtil.isBooleanType(expectedExpression.type)) {
                    return
                }
                val expectedResult = calculateConstantParameterValue(expression, 0) as? Boolean ?: return
                val flippedBooleanTest = matchingCalls.drop(2).any { it }

                val replacementMethod = if (expectedResult xor flippedBooleanTest) "isTrue()" else "isFalse()"
                registerSimplifyMethod(holder, expression, replacementMethod)
            }
        }
    }
}
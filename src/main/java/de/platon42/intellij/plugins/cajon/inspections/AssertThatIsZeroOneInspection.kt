package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.util.TypeConversionUtil
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*

class AssertThatIsZeroOneInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a zero or one value"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!expression.hasAssertThat()) return
                val isEqualTo = CallMatcher.anyOf(IS_EQUAL_TO_OBJECT, IS_EQUAL_TO_SHORT, IS_EQUAL_TO_INT, IS_EQUAL_TO_LONG, IS_EQUAL_TO_FLOAT, IS_EQUAL_TO_DOUBLE).test(expression)
                val isNotEqualTo =
                    CallMatcher.anyOf(IS_NOT_EQUAL_TO_OBJECT, IS_NOT_EQUAL_TO_SHORT, IS_NOT_EQUAL_TO_INT, IS_NOT_EQUAL_TO_LONG, IS_NOT_EQUAL_TO_FLOAT, IS_NOT_EQUAL_TO_DOUBLE)
                        .test(expression)

                if (!(isEqualTo || isNotEqualTo)) return

                val expectedExpression = expression.firstArg
                if (!TypeConversionUtil.isNumericType(expectedExpression.type)) return

                val expectedResult = expression.calculateConstantParameterValue(0) ?: return
                var isZero = false
                var isOne = false
                when (expectedResult) {
                    is Short -> {
                        isZero = (expectedResult == 0.toShort())
                        isOne = (expectedResult == 1.toShort())
                    }

                    is Int -> {
                        isZero = (expectedResult == 0)
                        isOne = (expectedResult == 1)
                    }

                    is Long -> {
                        isZero = (expectedResult == 0L)
                        isOne = (expectedResult == 1L)
                    }

                    is Float -> {
                        isZero = (expectedResult == 0.0f)
                        isOne = (expectedResult == 1.0f)
                    }

                    is Double -> {
                        isZero = (expectedResult == 0.0)
                        isOne = (expectedResult == 1.0)
                    }
                }
                if (isZero || isOne) {
                    val numericBaseClass = listOf(
                        AssertJClassNames.ABSTRACT_SHORT_ASSERT_CLASSNAME,
                        AssertJClassNames.ABSTRACT_INTEGER_ASSERT_CLASSNAME,
                        AssertJClassNames.ABSTRACT_LONG_ASSERT_CLASSNAME,
                        AssertJClassNames.ABSTRACT_FLOAT_ASSERT_CLASSNAME,
                        AssertJClassNames.ABSTRACT_DOUBLE_ASSERT_CLASSNAME
                    ).any { checkAssertedType(expression, it) }
                    if (!numericBaseClass) return
                }
                if (isZero) {
                    registerSimplifyMethod(holder, expression, isEqualTo.map(MethodNames.IS_ZERO, MethodNames.IS_NOT_ZERO))
                } else if (isOne && isEqualTo) {
                    registerSimplifyMethod(holder, expression, MethodNames.IS_ONE)
                }
            }
        }
    }
}
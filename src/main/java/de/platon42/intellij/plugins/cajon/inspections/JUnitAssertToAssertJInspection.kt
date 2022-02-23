package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.CommonClassNames.*
import com.intellij.psi.PsiKeyword.*
import com.intellij.psi.search.GlobalSearchScope
import com.siyeh.ig.callMatcher.CallMatcher
import com.siyeh.ig.callMatcher.CallMatcher.anyOf
import com.siyeh.ig.callMatcher.CallMatcher.staticCall
import de.platon42.intellij.plugins.cajon.AssertJClassNames
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceJUnitAssertMethodCallQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceJUnitAssumeMethodCallQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceJUnitDeltaAssertMethodCallQuickFix

class JUnitAssertToAssertJInspection : AbstractJUnitAssertInspection() {

    companion object {
        private const val DISPLAY_NAME = "Convert JUnit assertions/assumptions to AssertJ"
        private const val CONVERT_MESSAGE_TEMPLATE = "%s can be converted to AssertJ style"
        private const val CONVERT_DESCRIPTION_TEMPLATE = "Convert %s() to %s().%s()"

        private const val DOUBLE_ARR = "$DOUBLE[]"
        private const val FLOAT_ARR = "$FLOAT[]"

        private val ASSERT_MAPPINGS = listOf(
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_TRUE_METHOD).parameterTypes(JAVA_LANG_STRING, BOOLEAN),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_TRUE_METHOD).parameterTypes(BOOLEAN),
                ),
                MethodNames.IS_TRUE, hasExpected = false
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_FALSE_METHOD).parameterTypes(JAVA_LANG_STRING, BOOLEAN),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_FALSE_METHOD).parameterTypes(BOOLEAN),
                ),
                MethodNames.IS_FALSE, hasExpected = false
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_NULL_METHOD).parameterTypes(JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_NULL_METHOD).parameterTypes(JAVA_LANG_OBJECT),
                ),
                MethodNames.IS_NULL, hasExpected = false
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_NOT_NULL_METHOD).parameterTypes(JAVA_LANG_STRING, JAVA_LANG_OBJECT),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_NOT_NULL_METHOD).parameterTypes(JAVA_LANG_OBJECT),
                ),
                MethodNames.IS_NOT_NULL, hasExpected = false
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterTypes(JAVA_LANG_STRING, DOUBLE, DOUBLE, DOUBLE),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterTypes(DOUBLE, DOUBLE, DOUBLE),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterTypes(JAVA_LANG_STRING, FLOAT, FLOAT, FLOAT),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterTypes(FLOAT, FLOAT, FLOAT),
                ),
                MethodNames.IS_CLOSE_TO, hasDelta = true
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterCount(3),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterCount(2),
                ),
                MethodNames.IS_EQUAL_TO
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterTypes(JAVA_LANG_STRING, DOUBLE, DOUBLE, DOUBLE),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterTypes(DOUBLE, DOUBLE, DOUBLE),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterTypes(JAVA_LANG_STRING, FLOAT, FLOAT, FLOAT),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterTypes(FLOAT, FLOAT, FLOAT),
                ),
                MethodNames.IS_NOT_CLOSE_TO, hasDelta = true
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterCount(3),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterCount(2),
                ),
                MethodNames.IS_NOT_EQUAL_TO
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_SAME_METHOD).parameterCount(3),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_SAME_METHOD).parameterCount(2),
                ),
                MethodNames.IS_SAME_AS
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_NOT_SAME_METHOD).parameterCount(3),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_NOT_SAME_METHOD).parameterCount(2),
                ),
                MethodNames.IS_NOT_SAME_AS
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterTypes(JAVA_LANG_STRING, DOUBLE_ARR, DOUBLE_ARR, DOUBLE),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterTypes(DOUBLE_ARR, DOUBLE_ARR, DOUBLE),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterTypes(JAVA_LANG_STRING, FLOAT_ARR, FLOAT_ARR, FLOAT),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterTypes(FLOAT_ARR, FLOAT_ARR, FLOAT),
                ),
                MethodNames.CONTAINS_EXACTLY, hasDelta = true
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterCount(2),
                    staticCall(JUNIT4_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterCount(3),
                ),
                MethodNames.CONTAINS_EXACTLY
            )
        )

        private val ASSUME_MAPPINGS = listOf(
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSUME_CLASSNAME, ASSUME_TRUE_METHOD).parameterTypes(JAVA_LANG_STRING, BOOLEAN),
                    staticCall(JUNIT4_ASSUME_CLASSNAME, ASSUME_TRUE_METHOD).parameterTypes(BOOLEAN),
                ),
                MethodNames.IS_TRUE, hasExpected = false
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSUME_CLASSNAME, ASSUME_FALSE_METHOD).parameterTypes(JAVA_LANG_STRING, BOOLEAN),
                    staticCall(JUNIT4_ASSUME_CLASSNAME, ASSUME_FALSE_METHOD).parameterTypes(BOOLEAN),
                ),
                MethodNames.IS_FALSE, hasExpected = false
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSUME_CLASSNAME, ASSUME_NOT_NULL_METHOD).parameterCount(1),
                ),
                MethodNames.IS_NOT_NULL, hasExpected = false, singleArgument = true
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT4_ASSUME_CLASSNAME, ASSUME_NO_EXCEPTION).parameterTypes(JAVA_LANG_STRING, JAVA_LANG_THROWABLE),
                    staticCall(JUNIT4_ASSUME_CLASSNAME, ASSUME_NO_EXCEPTION).parameterTypes(JAVA_LANG_THROWABLE),
                ),
                "doesNotThrowAnyException", hasExpected = false
            )
        )
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                when (expression.resolveMethod()?.containingClass?.qualifiedName) {
                    JUNIT4_ASSERT_CLASSNAME -> visitMethodCallExpressionAssert(holder, expression)
                    JUNIT4_ASSUME_CLASSNAME -> visitMethodCallExpressionAssume(holder, expression)
                }
            }
        }
    }

    private fun visitMethodCallExpressionAssert(holder: ProblemsHolder, expression: PsiMethodCallExpression) {
        JavaPsiFacade.getInstance(expression.project)
            .findClass(AssertJClassNames.ASSERTIONS_CLASSNAME, GlobalSearchScope.allScope(expression.project)) ?: return
        val mapping = ASSERT_MAPPINGS.firstOrNull { it.callMatcher.test(expression) } ?: return
        if (mapping.hasDelta) {
            registerConvertMethod(holder, expression, mapping.replacement, MethodNames.ASSERT_THAT) { desc, method ->
                ReplaceJUnitDeltaAssertMethodCallQuickFix(desc, method)
            }
        } else {
            registerConvertMethod(holder, expression, mapping.replacement, MethodNames.ASSERT_THAT) { desc, method ->
                ReplaceJUnitAssertMethodCallQuickFix(desc, method, !mapping.hasExpected)
            }
        }
    }

    private fun visitMethodCallExpressionAssume(holder: ProblemsHolder, expression: PsiMethodCallExpression) {
        JavaPsiFacade.getInstance(expression.project)
            .findClass(AssertJClassNames.ASSUMPTIONS_CLASSNAME, GlobalSearchScope.allScope(expression.project)) ?: return
        val mapping = ASSUME_MAPPINGS.firstOrNull { it.callMatcher.test(expression) } ?: return
        if (!mapping.singleArgument || expression.argumentList.expressions.size == 1) {
            registerConvertMethod(holder, expression, mapping.replacement, MethodNames.ASSUME_THAT) { desc, method ->
                ReplaceJUnitAssumeMethodCallQuickFix(desc, method)
            }
        }
    }

    private fun registerConvertMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        replacementMethod: String,
        staticMethod: String,
        quickFixSupplier: (String, String) -> LocalQuickFix
    ) {
        val originalMethod = getOriginalMethodName(expression) ?: return
        val description = CONVERT_DESCRIPTION_TEMPLATE.format(originalMethod, staticMethod, replacementMethod)
        val message = CONVERT_MESSAGE_TEMPLATE.format(originalMethod)
        val quickfix = quickFixSupplier(description, replacementMethod)
        holder.registerProblem(expression, message, quickfix)
    }

    private class Mapping(
        val callMatcher: CallMatcher,
        val replacement: String,
        val hasExpected: Boolean = true,
        val hasDelta: Boolean = false,
        val singleArgument: Boolean = false
    )
}
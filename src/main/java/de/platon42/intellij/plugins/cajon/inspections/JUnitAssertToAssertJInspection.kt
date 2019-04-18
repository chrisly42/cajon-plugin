package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.siyeh.ig.callMatcher.CallMatcher
import com.siyeh.ig.callMatcher.CallMatcher.anyOf
import com.siyeh.ig.callMatcher.CallMatcher.staticCall
import de.platon42.intellij.plugins.cajon.AssertJClassNames
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceJUnitAssertMethodCallQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceJUnitDeltaAssertMethodCallQuickFix

class JUnitAssertToAssertJInspection : AbstractJUnitAssertInspection() {

    companion object {
        private const val DISPLAY_NAME = "Convert JUnit assertions to AssertJ"

        private val MAPPINGS = listOf(
            Mapping(
                anyOf(
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_TRUE_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "boolean"),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_TRUE_METHOD).parameterTypes("boolean")
                ),
                MethodNames.IS_TRUE, false
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_FALSE_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "boolean"),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_FALSE_METHOD).parameterTypes("boolean")
                ),
                MethodNames.IS_FALSE, false
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NULL_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, CommonClassNames.JAVA_LANG_OBJECT),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NULL_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)
                ),
                MethodNames.IS_NULL, false
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_NULL_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, CommonClassNames.JAVA_LANG_OBJECT),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_NULL_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)
                ),
                MethodNames.IS_NOT_NULL, false
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "double", "double", "double"),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterTypes("double", "double", "double"),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "float", "float", "float"),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterTypes("float", "float", "float")
                ),
                MethodNames.IS_CLOSE_TO, hasDelta = true
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterCount(3),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterCount(2)
                ),
                MethodNames.IS_EQUAL_TO
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "double", "double", "double"),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterTypes("double", "double", "double"),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "float", "float", "float"),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterTypes("float", "float", "float")
                ),
                MethodNames.IS_NOT_CLOSE_TO, hasDelta = true
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterCount(3),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterCount(2)
                ),
                MethodNames.IS_NOT_EQUAL_TO
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_SAME_METHOD).parameterCount(3),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_SAME_METHOD).parameterCount(2)
                ),
                MethodNames.IS_SAME_AS
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_SAME_METHOD).parameterCount(3),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_SAME_METHOD).parameterCount(2)
                ),
                MethodNames.IS_NOT_SAME_AS
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "double[]", "double[]", "double"),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterTypes("double[]", "double[]", "double"),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "float[]", "float[]", "float"),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterTypes("float[]", "float[]", "float")
                ),
                MethodNames.CONTAINS_EXACTLY, hasDelta = true
            ),
            Mapping(
                anyOf(
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterCount(2),
                    staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterCount(3)
                ),
                MethodNames.CONTAINS_EXACTLY
            )
        )
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                val isJUnitAssertCall = expression.resolveMethod()?.containingClass?.qualifiedName == JUNIT_ASSERT_CLASSNAME
                if (!isJUnitAssertCall) {
                    return // early exit
                }
                JavaPsiFacade.getInstance(expression.project)
                    .findClass(AssertJClassNames.ASSERTIONS_CLASSNAME, GlobalSearchScope.allScope(expression.project)) ?: return
                val mapping = MAPPINGS.firstOrNull { it.callMatcher.test(expression) } ?: return
                if (mapping.hasDelta) {
                    registerConvertMethod(holder, expression, mapping.replacement, ::ReplaceJUnitDeltaAssertMethodCallQuickFix)
                } else {
                    registerConvertMethod(holder, expression, mapping.replacement) { desc, method ->
                        ReplaceJUnitAssertMethodCallQuickFix(desc, method, !mapping.hasExpected)
                    }
                }
            }
        }
    }

    private fun registerConvertMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        replacementMethod: String,
        quickFixSupplier: (String, String) -> LocalQuickFix
    ) {
        val originalMethod = getOriginalMethodName(expression) ?: return
        val description = REPLACE_DESCRIPTION_TEMPLATE.format(originalMethod, replacementMethod)
        val message = CONVERT_MESSAGE_TEMPLATE.format(originalMethod)
        val quickfix = quickFixSupplier(description, replacementMethod)
        holder.registerProblem(expression, message, quickfix)
    }

    private class Mapping(
        val callMatcher: CallMatcher,
        val replacement: String,
        val hasExpected: Boolean = true,
        val hasDelta: Boolean = false
    )
}
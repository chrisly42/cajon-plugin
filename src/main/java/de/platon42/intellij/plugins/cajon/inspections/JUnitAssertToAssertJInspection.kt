package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.CommonClassNames
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.siyeh.ig.callMatcher.CallMatcher
import com.siyeh.ig.callMatcher.CallMatcher.anyOf
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceJUnitAssertMethodCallQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceJUnitDeltaAssertMethodCallQuickFix

class JUnitAssertToAssertJInspection : AbstractJUnitAssertInspection() {

    companion object {
        private const val DISPLAY_NAME = "Convert JUnit assertions to AssertJ"

        private val MAPPINGS = listOf(
            Mapping(
                anyOf(
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_TRUE_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "boolean"),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_TRUE_METHOD).parameterTypes("boolean")
                ),
                "isTrue()", false
            ),
            Mapping(
                anyOf(
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_FALSE_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "boolean"),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_FALSE_METHOD).parameterTypes("boolean")
                ),
                "isFalse()", false
            ),
            Mapping(
                anyOf(
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NULL_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, CommonClassNames.JAVA_LANG_OBJECT),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NULL_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)
                ),
                "isNull()", false
            ),
            Mapping(
                anyOf(
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_NULL_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, CommonClassNames.JAVA_LANG_OBJECT),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_NULL_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)
                ),
                "isNotNull()", false
            ),
            Mapping(
                anyOf(
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "double", "double", "double"),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterTypes("double", "double", "double"),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "float", "float", "float"),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterTypes("float", "float", "float")
                ),
                "isCloseTo()", hasDelta = true
            ),
            Mapping(
                anyOf(
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterCount(3),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_EQUALS_METHOD).parameterCount(2)
                ),
                "isEqualTo()"
            ),
            Mapping(
                anyOf(
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "double", "double", "double"),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterTypes("double", "double", "double"),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "float", "float", "float"),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterTypes("float", "float", "float")
                ),
                "isNotCloseTo()", hasDelta = true
            ),
            Mapping(
                anyOf(
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterCount(3),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_EQUALS_METHOD).parameterCount(2)
                ),
                "isNotEqualTo()"
            ),
            Mapping(
                anyOf(
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_SAME_METHOD).parameterCount(3),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_SAME_METHOD).parameterCount(2)
                ),
                "isSameAs()"
            ),
            Mapping(
                anyOf(
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_SAME_METHOD).parameterCount(3),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_NOT_SAME_METHOD).parameterCount(2)
                ),
                "isNotSameAs()"
            ),
            Mapping(
                anyOf(
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "double[]", "double[]", "double"),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterTypes("double[]", "double[]", "double"),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterTypes(CommonClassNames.JAVA_LANG_STRING, "float[]", "float[]", "float"),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterTypes("float[]", "float[]", "float")
                ),
                "containsExactly()", hasDelta = true
            ),
            Mapping(
                anyOf(
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterCount(2),
                    CallMatcher.staticCall(JUNIT_ASSERT_CLASSNAME, ASSERT_ARRAY_EQUALS_METHOD).parameterCount(3)
                ),
                "containsExactly()"
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
                for (mapping in MAPPINGS) {
                    if (mapping.callMatcher.test(expression)) {
                        if (mapping.hasDelta) {
                            registerDeltaReplacementMethod(holder, expression, mapping.replacement)
                        } else {
                            registerSimpleReplacementMethod(holder, expression, mapping.hasExpected, mapping.replacement)
                        }
                        return
                    }
                }
            }
        }
    }

    private fun registerSimpleReplacementMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        hasExpected: Boolean,
        replacementMethod: String
    ) {
        val originalMethod = getOriginalMethodName(expression) ?: return
        val description = REPLACE_DESCRIPTION_TEMPLATE.format(originalMethod, replacementMethod)
        val message = CONVERT_MESSAGE_TEMPLATE.format(originalMethod)
        holder.registerProblem(
            expression,
            message,
            ReplaceJUnitAssertMethodCallQuickFix(description, hasExpected, replacementMethod)
        )
    }

    private fun registerDeltaReplacementMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        replacementMethod: String
    ) {
        val originalMethod = getOriginalMethodName(expression) ?: return
        val description = REPLACE_DESCRIPTION_TEMPLATE.format(originalMethod, replacementMethod)
        val message = CONVERT_MESSAGE_TEMPLATE.format(originalMethod)
        holder.registerProblem(
            expression,
            message,
            ReplaceJUnitDeltaAssertMethodCallQuickFix(description, replacementMethod)
        )
    }

    private class Mapping(
        val callMatcher: CallMatcher,
        val replacement: String,
        val hasExpected: Boolean = true,
        val hasDelta: Boolean = false
    )
}
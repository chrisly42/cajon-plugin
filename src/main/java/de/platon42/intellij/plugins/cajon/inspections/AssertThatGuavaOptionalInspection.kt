package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.search.GlobalSearchScope
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.*

class AssertThatGuavaOptionalInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting an Optional (Guava)"
        private const val REPLACE_GUAVA_DESCRIPTION_TEMPLATE = "Replace %s() with Guava assertThat().%s()"
        private const val REMOVE_EXPECTED_OUTMOST_GUAVA_DESCRIPTION_TEMPLATE = "Remove unwrapping of expected expression and replace %s() with Guava assertThat().%s()"
        private const val REMOVE_ACTUAL_OUTMOST_GUAVA_DESCRIPTION_TEMPLATE = "Unwrap actual expression and replace %s() with Guava assertThat().%s()"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                JavaPsiFacade.getInstance(expression.project)
                    .findClass(AssertJClassNames.GUAVA_ASSERTIONS_CLASSNAME, GlobalSearchScope.allScope(expression.project)) ?: return
                val assertThatGuava = ASSERT_THAT_GUAVA_OPTIONAL.test(expression)
                if (!(ASSERT_THAT_ANY.test(expression) || assertThatGuava)) {
                    return
                }
                val expectedCallExpression = expression.findOutmostMethodCall() ?: return

                val isEqualTo = IS_EQUAL_TO_OBJECT.test(expectedCallExpression)
                val isNotEqualTo = IS_NOT_EQUAL_TO_OBJECT.test(expectedCallExpression)
                if (assertThatGuava) {
                    if (isEqualTo) {
                        val innerExpectedCall = expectedCallExpression.firstArg as? PsiMethodCallExpression ?: return
                        if (CallMatcher.anyOf(GUAVA_OPTIONAL_OF, GUAVA_OPTIONAL_FROM_NULLABLE).test(innerExpectedCall)) {
                            registerRemoveExpectedOutmostMethod(holder, expression, expectedCallExpression, MethodNames.CONTAINS, ::RemoveExpectedOutmostMethodCallQuickFix)
                        } else if (GUAVA_OPTIONAL_ABSENT.test(innerExpectedCall)) {
                            registerSimplifyMethod(holder, expectedCallExpression, MethodNames.IS_ABSENT)
                        }
                    } else if (isNotEqualTo) {
                        val innerExpectedCall = expectedCallExpression.firstArg as? PsiMethodCallExpression ?: return
                        if (GUAVA_OPTIONAL_ABSENT.test(innerExpectedCall)) {
                            registerSimplifyMethod(holder, expectedCallExpression, MethodNames.IS_PRESENT)
                        }
                    }
                } else {
                    // we're not calling an assertThat() from Guava, but a core-AssertJ one!
                    // We need to replace that by the Guava one, if we want to apply a formally correct fix.
                    val actualExpression = expression.firstArg as? PsiMethodCallExpression
                    if (actualExpression != null) {
                        if (GUAVA_OPTIONAL_GET.test(actualExpression) && isEqualTo) {
                            registerRemoveActualOutmostForGuavaMethod(holder, expression, expectedCallExpression, MethodNames.CONTAINS)
                        } else if (GUAVA_OPTIONAL_IS_PRESENT.test(actualExpression)) {
                            val expectedPresence = getExpectedBooleanResult(expectedCallExpression) ?: return
                            val replacementMethod = expectedPresence.map(MethodNames.IS_PRESENT, MethodNames.IS_ABSENT)
                            registerRemoveActualOutmostForGuavaMethod(holder, expression, expectedCallExpression, replacementMethod, noExpectedExpression = true)
                        }
                    }
                    if (isEqualTo) {
                        val innerExpectedCall = expectedCallExpression.firstArg as? PsiMethodCallExpression ?: return
                        if (CallMatcher.anyOf(GUAVA_OPTIONAL_OF, GUAVA_OPTIONAL_FROM_NULLABLE).test(innerExpectedCall)) {
                            registerRemoveExpectedOutmostGuavaMethod(holder, expression, expectedCallExpression, MethodNames.CONTAINS)
                        } else if (GUAVA_OPTIONAL_ABSENT.test(innerExpectedCall)) {
                            registerSimplifyForGuavaMethod(holder, expectedCallExpression, MethodNames.IS_ABSENT)
                        }
                    } else if (isNotEqualTo) {
                        val innerExpectedCall = expectedCallExpression.firstArg as? PsiMethodCallExpression ?: return
                        if (GUAVA_OPTIONAL_ABSENT.test(innerExpectedCall)) {
                            registerSimplifyForGuavaMethod(holder, expectedCallExpression, MethodNames.IS_PRESENT)
                        }
                    }
                }
            }
        }
    }

    private fun registerRemoveExpectedOutmostGuavaMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        oldExpectedCallExpression: PsiMethodCallExpression,
        replacementMethod: String
    ) {
        registerConciseMethod(REMOVE_EXPECTED_OUTMOST_GUAVA_DESCRIPTION_TEMPLATE, holder, expression, oldExpectedCallExpression, replacementMethod) { desc, method ->
            QuickFixWithPostfixDelegate(
                RemoveExpectedOutmostMethodCallQuickFix(desc, method),
                ForGuavaPostFix.REPLACE_BY_GUAVA_ASSERT_THAT_AND_STATIC_IMPORT
            )
        }
    }

    private fun registerRemoveActualOutmostForGuavaMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        oldExpectedCallExpression: PsiMethodCallExpression,
        replacementMethod: String,
        noExpectedExpression: Boolean = false
    ) {
        registerConciseMethod(REMOVE_ACTUAL_OUTMOST_GUAVA_DESCRIPTION_TEMPLATE, holder, expression, oldExpectedCallExpression, replacementMethod) { desc, method ->
            QuickFixWithPostfixDelegate(
                RemoveActualOutmostMethodCallQuickFix(desc, method, noExpectedExpression),
                ForGuavaPostFix.REPLACE_BY_GUAVA_ASSERT_THAT_AND_STATIC_IMPORT
            )
        }
    }

    private fun registerSimplifyForGuavaMethod(holder: ProblemsHolder, expression: PsiMethodCallExpression, replacementMethod: String) {
        val originalMethod = getOriginalMethodName(expression) ?: return
        val description = REPLACE_GUAVA_DESCRIPTION_TEMPLATE.format(originalMethod, replacementMethod)
        val message = SIMPLIFY_MESSAGE_TEMPLATE.format(originalMethod, replacementMethod)
        val quickFix = QuickFixWithPostfixDelegate(
            ReplaceSimpleMethodCallQuickFix(description, replacementMethod),
            ForGuavaPostFix.REPLACE_BY_GUAVA_ASSERT_THAT_AND_STATIC_IMPORT
        )
        holder.registerProblem(expression, message, quickFix)
    }
}
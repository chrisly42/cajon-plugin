package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.*

class AssertThatGuavaOptionalInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting an Optional (Guava)"
        private const val REPLACE_GUAVA_DESCRIPTION_TEMPLATE = "Replace %s() with Guava assertThat().%s()"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                if (!statement.hasAssertThat()) return
                val staticMethodCall = statement.findStaticMethodCall() ?: return

                if (!checkPreconditions(staticMethodCall)) return

                val actualExpression = staticMethodCall.firstArg as? PsiMethodCallExpression ?: return
                val outmostMethodCall = statement.findOutmostMethodCall() ?: return
                if (GUAVA_OPTIONAL_GET.test(actualExpression)) {
                    val expectedCallExpression = staticMethodCall.gatherAssertionCalls().singleOrNull() ?: return
                    if (CallMatcher.anyOf(IS_EQUAL_TO_OBJECT, IS_EQUAL_TO_STRING).test(expectedCallExpression)) {
                        registerMoveOutMethod(holder, outmostMethodCall, actualExpression, MethodNames.CONTAINS) { desc, method ->
                            QuickFixWithPostfixDelegate(
                                RemoveActualOutmostMethodCallQuickFix(desc, method),
                                ForGuavaPostFix.REPLACE_BY_GUAVA_ASSERT_THAT_AND_STATIC_IMPORT
                            )
                        }
                    }
                } else if (GUAVA_OPTIONAL_IS_PRESENT.test(actualExpression)) {
                    val expectedPresence = outmostMethodCall.getAllTheSameExpectedBooleanConstants() ?: return
                    val replacementMethod = expectedPresence.map(MethodNames.IS_PRESENT, MethodNames.IS_ABSENT)
                    registerMoveOutMethod(holder, outmostMethodCall, actualExpression, replacementMethod) { desc, method ->
                        QuickFixWithPostfixDelegate(
                            MoveOutMethodCallExpressionQuickFix(desc, method),
                            ForGuavaPostFix.REPLACE_BY_GUAVA_ASSERT_THAT_AND_STATIC_IMPORT
                        )
                    }
                } else if (GUAVA_OPTIONAL_OR_NULL.test(actualExpression)) {
                    val expectedPresence = outmostMethodCall.getAllTheSameNullNotNullConstants() ?: return
                    val replacementMethod = expectedPresence.map(MethodNames.IS_PRESENT, MethodNames.IS_ABSENT)
                    registerMoveOutMethod(holder, outmostMethodCall, actualExpression, replacementMethod) { desc, method ->
                        QuickFixWithPostfixDelegate(
                            MoveOutMethodCallExpressionQuickFix(desc, method, useNullNonNull = true),
                            ForGuavaPostFix.REPLACE_BY_GUAVA_ASSERT_THAT_AND_STATIC_IMPORT
                        )
                    }
                }
            }

            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!expression.hasAssertThat()) return
                val staticMethodCall = expression.findStaticMethodCall() ?: return
                if (!checkPreconditions(staticMethodCall)) return

                // We're not calling an assertThat() from Guava, but a core-AssertJ one!
                // We need to replace that by the Guava one, if we want to apply a formally correct fix.
                if (IS_EQUAL_TO_OBJECT.test(expression)) {
                    val innerExpectedCall = expression.firstArg as? PsiMethodCallExpression ?: return
                    if (CallMatcher.anyOf(GUAVA_OPTIONAL_OF, GUAVA_OPTIONAL_FROM_NULLABLE).test(innerExpectedCall)) {
                        if (GUAVA_OPTIONAL_FROM_NULLABLE.test(innerExpectedCall)) {
                            innerExpectedCall.firstArg.calculateConstantValue() ?: return
                        }
                        registerRemoveExpectedOutmostMethod(holder, expression, expression, MethodNames.CONTAINS) { desc, method ->
                            QuickFixWithPostfixDelegate(
                                UnwrapExpectedStaticMethodCallQuickFix(desc, method),
                                ForGuavaPostFix.REPLACE_BY_GUAVA_ASSERT_THAT_AND_STATIC_IMPORT
                            )
                        }
                    } else if (GUAVA_OPTIONAL_ABSENT.test(innerExpectedCall)) {
                        registerSimplifyForGuavaMethod(holder, expression, MethodNames.IS_ABSENT)
                    }
                } else if (IS_NOT_EQUAL_TO_OBJECT.test(expression)) {
                    val innerExpectedCall = expression.firstArg as? PsiMethodCallExpression ?: return
                    if (GUAVA_OPTIONAL_ABSENT.test(innerExpectedCall)) {
                        registerSimplifyForGuavaMethod(holder, expression, MethodNames.IS_PRESENT)
                    }
                }
            }

            private fun checkPreconditions(staticMethodCall: PsiMethodCallExpression): Boolean {
                val assertThatGuava = GUAVA_ASSERT_THAT_ANY.test(staticMethodCall)

                if (ASSERT_THAT_ANY.test(staticMethodCall) || assertThatGuava) {
                    JavaPsiFacade.getInstance(staticMethodCall.project)
                        .findClass(AssertJClassNames.GUAVA_ASSERTIONS_CLASSNAME, GlobalSearchScope.allScope(staticMethodCall.project)) ?: return false
                    return true
                }
                return false
            }
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
        val textRange = TextRange(expression.qualifierExpression.textLength, expression.textLength)
        holder.registerProblem(expression, textRange, message, quickFix)
    }
}
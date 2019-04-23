package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.findOutmostMethodCall
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.map
import de.platon42.intellij.plugins.cajon.quickfixes.RemoveActualOutmostMethodCallQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.UnwrapExpectedStaticMethodCallQuickFix

class AssertThatJava8OptionalInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting an Optional (Java 8)"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!ASSERT_THAT_ANY.test(expression)) {
                    return
                }
                val expectedCallExpression = expression.findOutmostMethodCall() ?: return

                if (ASSERT_THAT_JAVA8_OPTIONAL.test(expression)) {
                    if (IS_EQUAL_TO_OBJECT.test(expectedCallExpression)) {
                        val innerExpectedCall = expectedCallExpression.firstArg as? PsiMethodCallExpression ?: return
                        if (CallMatcher.anyOf(OPTIONAL_OF, OPTIONAL_OF_NULLABLE).test(innerExpectedCall)) {
                            registerRemoveExpectedOutmostMethod(holder, expression, expectedCallExpression, MethodNames.CONTAINS, ::UnwrapExpectedStaticMethodCallQuickFix)
                        } else if (OPTIONAL_EMPTY.test(innerExpectedCall)) {
                            registerSimplifyMethod(holder, expectedCallExpression, MethodNames.IS_NOT_PRESENT)
                        }
                    } else if (IS_NOT_EQUAL_TO_OBJECT.test(expectedCallExpression)) {
                        val innerExpectedCall = expectedCallExpression.firstArg as? PsiMethodCallExpression ?: return
                        if (OPTIONAL_EMPTY.test(innerExpectedCall)) {
                            registerSimplifyMethod(holder, expectedCallExpression, MethodNames.IS_PRESENT)
                        }
                    }
                } else {
                    val actualExpression = expression.firstArg as? PsiMethodCallExpression ?: return

                    if (OPTIONAL_GET.test(actualExpression)) {
                        if (IS_EQUAL_TO_OBJECT.test(expectedCallExpression)) {
                            registerRemoveActualOutmostMethod(holder, expression, expectedCallExpression, MethodNames.CONTAINS) { desc, method ->
                                RemoveActualOutmostMethodCallQuickFix(desc, method)
                            }
                        } else if (IS_SAME_AS_OBJECT.test(expectedCallExpression)) {
                            registerRemoveActualOutmostMethod(holder, expression, expectedCallExpression, MethodNames.CONTAINS_SAME) { desc, method ->
                                RemoveActualOutmostMethodCallQuickFix(desc, method)
                            }
                        }
                    } else if (OPTIONAL_IS_PRESENT.test(actualExpression)) {
                        val expectedPresence = getExpectedBooleanResult(expectedCallExpression) ?: return
                        val replacementMethod = expectedPresence.map(MethodNames.IS_PRESENT, MethodNames.IS_NOT_PRESENT)
                        registerRemoveActualOutmostMethod(holder, expression, expectedCallExpression, replacementMethod) { desc, method ->
                            RemoveActualOutmostMethodCallQuickFix(desc, method, noExpectedExpression = true)
                        }
                    }
                }
            }
        }
    }
}
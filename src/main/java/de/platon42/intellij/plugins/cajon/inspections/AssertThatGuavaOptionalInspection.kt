package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.AssertJClassNames
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.map

class AssertThatGuavaOptionalInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting an Optional (Guava)"
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
                val statement = PsiTreeUtil.getParentOfType(expression, PsiStatement::class.java) ?: return
                val expectedCallExpression = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java) ?: return

                if (assertThatGuava) {
                    if (IS_EQUAL_TO_OBJECT.test(expectedCallExpression)) {
                        val innerExpectedCall = expectedCallExpression.firstArg as? PsiMethodCallExpression ?: return
                        if (GUAVA_OPTIONAL_OF.test(innerExpectedCall) || GUAVA_OPTIONAL_FROM_NULLABLE.test(innerExpectedCall)) {
                            registerRemoveExpectedOutmostMethod(holder, expression, expectedCallExpression, MethodNames.CONTAINS)
                        } else if (GUAVA_OPTIONAL_ABSENT.test(innerExpectedCall)) {
                            registerSimplifyMethod(holder, expectedCallExpression, MethodNames.IS_ABSENT)
                        }
                    } else if (IS_NOT_EQUAL_TO_OBJECT.test(expectedCallExpression)) {
                        val innerExpectedCall = expectedCallExpression.firstArg as? PsiMethodCallExpression ?: return
                        if (GUAVA_OPTIONAL_ABSENT.test(innerExpectedCall)) {
                            registerSimplifyMethod(holder, expectedCallExpression, MethodNames.IS_PRESENT)
                        }
                    }
                } else {
                    val actualExpression = expression.firstArg as? PsiMethodCallExpression ?: return

                    if (GUAVA_OPTIONAL_GET.test(actualExpression) && IS_EQUAL_TO_OBJECT.test(expectedCallExpression)) {
                        registerRemoveActualOutmostMethod(holder, expression, expectedCallExpression, MethodNames.CONTAINS)
                    } else if (GUAVA_OPTIONAL_IS_PRESENT.test(actualExpression)) {
                        val expectedPresence = getExpectedBooleanResult(expectedCallExpression) ?: return
                        val replacementMethod = expectedPresence.map(MethodNames.IS_PRESENT, MethodNames.IS_ABSENT)
                        registerRemoveActualOutmostMethod(holder, expression, expectedCallExpression, replacementMethod, noExpectedExpression = true)
                    }
                }
            }
        }
    }
}
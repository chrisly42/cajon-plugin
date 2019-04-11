package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiStatement
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.firstArg
import de.platon42.intellij.plugins.cajon.map

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
                val statement = PsiTreeUtil.getParentOfType(expression, PsiStatement::class.java) ?: return
                val expectedCallExpression = PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java) ?: return

                if (ASSERT_THAT_JAVA8_OPTIONAL.test(expression)) {
                    if (IS_EQUAL_TO_OBJECT.test(expectedCallExpression)) {
                        val innerExpectedCall = expectedCallExpression.firstArg as? PsiMethodCallExpression ?: return
                        if (OPTIONAL_OF.test(innerExpectedCall) || OPTIONAL_OF_NULLABLE.test(innerExpectedCall)) {
                            registerRemoveExpectedOutmostMethod(holder, expression, expectedCallExpression, MethodNames.CONTAINS)
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
                    val isGet = OPTIONAL_GET.test(actualExpression)
                    val isIsPresent = OPTIONAL_IS_PRESENT.test(actualExpression)

                    if (isGet || isIsPresent) {
                        if (isGet) {
                            if (IS_EQUAL_TO_OBJECT.test(expectedCallExpression)) {
                                registerRemoveActualOutmostMethod(holder, expression, expectedCallExpression, MethodNames.CONTAINS)
                            } else if (IS_SAME_AS_OBJECT.test(expectedCallExpression)) {
                                registerRemoveActualOutmostMethod(holder, expression, expectedCallExpression, MethodNames.CONTAINS_SAME)
                            }
                        } else {
                            val expectedPresence = getExpectedBooleanResult(expectedCallExpression) ?: return
                            val replacementMethod = expectedPresence.map(MethodNames.IS_PRESENT, MethodNames.IS_NOT_PRESENT)
                            registerRemoveActualOutmostMethod(holder, expression, expectedCallExpression, replacementMethod, noExpectedExpression = true)
                        }
                    }
                }
            }
        }
    }
}
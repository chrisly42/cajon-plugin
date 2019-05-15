package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.MoveOutMethodCallExpressionQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.RemoveActualOutmostMethodCallQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.UnwrapExpectedStaticMethodCallQuickFix

class AssertThatJava8OptionalInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting an Optional (Java 8)"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                if (!statement.hasAssertThat()) {
                    return
                }
                val staticMethodCall = statement.findStaticMethodCall() ?: return
                if (!ASSERT_THAT_ANY.test(staticMethodCall)) {
                    return
                }
                val actualExpression = staticMethodCall.firstArg as? PsiMethodCallExpression ?: return

                val outmostMethodCall = statement.findOutmostMethodCall() ?: return
                if (OPTIONAL_GET.test(actualExpression)) {
                    val expectedCallExpression = staticMethodCall.gatherAssertionCalls().singleOrNull() ?: return
                    if (IS_EQUAL_TO_OBJECT.test(expectedCallExpression)) {
                        registerMoveOutMethod(holder, outmostMethodCall, actualExpression, MethodNames.CONTAINS) { desc, method ->
                            RemoveActualOutmostMethodCallQuickFix(desc, method)
                        }
                    } else if (IS_SAME_AS_OBJECT.test(expectedCallExpression)) {
                        registerMoveOutMethod(holder, outmostMethodCall, actualExpression, MethodNames.CONTAINS_SAME) { desc, method ->
                            RemoveActualOutmostMethodCallQuickFix(desc, method)
                        }
                    }
                } else if (OPTIONAL_IS_PRESENT.test(actualExpression)) {
                    val expectedPresence = outmostMethodCall.getAllTheSameExpectedBooleanConstants() ?: return
                    val replacementMethod = expectedPresence.map(MethodNames.IS_PRESENT, MethodNames.IS_NOT_PRESENT)
                    registerMoveOutMethod(holder, outmostMethodCall, actualExpression, replacementMethod) { desc, method ->
                        MoveOutMethodCallExpressionQuickFix(desc, method)
                    }
                } else if (OPTIONAL_OR_ELSE.test(actualExpression) && (actualExpression.firstArg.type == PsiType.NULL)) {
                    val expectedPresence = outmostMethodCall.getAllTheSameNullNotNullConstants() ?: return
                    val replacementMethod = expectedPresence.map(MethodNames.IS_PRESENT, MethodNames.IS_NOT_PRESENT)
                    registerMoveOutMethod(holder, outmostMethodCall, actualExpression, replacementMethod) { desc, method ->
                        MoveOutMethodCallExpressionQuickFix(desc, method, useNullNonNull = true, noExpectedExpression = true)
                    }
                }
            }

            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                if (!expression.hasAssertThat()) {
                    return
                }
                val staticMethodCall = expression.findStaticMethodCall() ?: return
                if (!ASSERT_THAT_JAVA8_OPTIONAL.test(staticMethodCall)) {
                    return
                }
                if (IS_EQUAL_TO_OBJECT.test(expression)) {
                    val innerExpectedCall = expression.firstArg as? PsiMethodCallExpression ?: return
                    if (CallMatcher.anyOf(OPTIONAL_OF, OPTIONAL_OF_NULLABLE).test(innerExpectedCall)) {
                        registerRemoveExpectedOutmostMethod(holder, expression, expression, MethodNames.CONTAINS, ::UnwrapExpectedStaticMethodCallQuickFix)
                    } else if (OPTIONAL_EMPTY.test(innerExpectedCall)) {
                        registerSimplifyMethod(holder, expression, MethodNames.IS_NOT_PRESENT)
                    }
                } else if (IS_NOT_EQUAL_TO_OBJECT.test(expression)) {
                    val innerExpectedCall = expression.firstArg as? PsiMethodCallExpression ?: return
                    if (OPTIONAL_EMPTY.test(innerExpectedCall)) {
                        registerSimplifyMethod(holder, expression, MethodNames.IS_PRESENT)
                    }
                }
            }
        }
    }
}
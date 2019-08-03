package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiExpressionStatement
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.util.PsiTreeUtil
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.JoinVarArgsContainsQuickFix

class JoinVarArgsContainsInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Join variadic arguments of contains()/containsOnlyOnce()/doesNotContain()"
        private const val JOIN_VARARGS_MESSAGE = "Calls to same methods may be joined to variadic version"

        private val MATCHERS = listOf(MethodNames.CONTAINS, MethodNames.CONTAINS_ONLY_ONCE, MethodNames.DOES_NOT_CONTAIN)
            .map { CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_ITERABLE_ASSERT_CLASSNAME, it) }
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitStatement(statement)
                if (!statement.hasAssertThat()) return
                val assertThatCall = PsiTreeUtil.findChildrenOfType(statement, PsiMethodCallExpression::class.java).find { ALL_ASSERT_THAT_MATCHERS.test(it) } ?: return

                val allCalls = assertThatCall.collectMethodCallsUpToStatement().toList()

                if (allCalls.find(COMPLEX_CALLS_THAT_MAKES_STUFF_TRICKY::test) != null) return

                val onlyAssertionCalls = allCalls
                    .filterNot { NOT_ACTUAL_ASSERTIONS.test(it) }
                    .toList()

                for (methodMatcher in MATCHERS) {
                    if (onlyAssertionCalls.count(methodMatcher::test) > 1) {
                        val outmostMethodCall = statement.findOutmostMethodCall() ?: return
                        val quickFix = JoinVarArgsContainsQuickFix(MATCHERS)
                        val textRange = TextRange(outmostMethodCall.qualifierExpression.textLength, outmostMethodCall.textLength)
                        holder.registerProblem(outmostMethodCall, textRange, JOIN_VARARGS_MESSAGE, quickFix)
                        return
                    }
                }
            }
        }
    }
}

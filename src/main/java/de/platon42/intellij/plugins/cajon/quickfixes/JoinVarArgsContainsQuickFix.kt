package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*

class JoinVarArgsContainsQuickFix(private val matchers: Iterable<CallMatcher>) : AbstractCommonQuickFix(JOIN_VARARGS_DESCRIPTION) {

    companion object {
        private const val JOIN_VARARGS_DESCRIPTION = "Join multiple arguments to variadic argument method calls"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        var outmostCallExpression = descriptor.startElement as? PsiMethodCallExpression ?: return

        for (matcher in matchers) {
            val assertThatMethodCall = outmostCallExpression.findStaticMethodCall() ?: return
            val methodsToFix = assertThatMethodCall.gatherAssertionCalls()
            val matchedCalls = methodsToFix.filter(matcher::test)
            if (matchedCalls.size > 1) {
                val mainCall = matchedCalls.first()
                val args = mutableListOf(*mainCall.argumentList.expressions)
                for (secondaryCall in matchedCalls.asSequence().drop(1)) {
                    args.addAll(secondaryCall.argumentList.expressions)
                }
                val newMainCall = createExpectedMethodCall(mainCall, mainCall.methodExpression.qualifiedName, *args.toTypedArray())
                newMainCall.replaceQualifierFromMethodCall(mainCall)
                mainCall.replace(newMainCall)
                for (secondaryCall in matchedCalls.asSequence().drop(1)) {
                    val newQualifier = secondaryCall.qualifierExpression
                    outmostCallExpression = secondaryCall.replace(newQualifier).findOutmostMethodCall() ?: return
                }
            }
        }
    }
}
package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.siyeh.ig.psiutils.TrackingEquivalenceChecker
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.JoinStatementsQuickFix

class JoinAssertThatStatementsInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Joining multiple assertThat() statements with same actual expression"
        private const val CAN_BE_JOINED_DESCRIPTION = "Multiple assertThat() statements can be joined together"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitCodeBlock(block: PsiCodeBlock?) {
                super.visitCodeBlock(block)
                val statements = block?.statements ?: return
                var lastActualExpression: PsiExpression? = null
                var sameCount = 0
                var firstStatement: PsiStatement? = null
                var lastStatement: PsiStatement? = null
                val equivalenceChecker = TrackingEquivalenceChecker()
                for (statement in statements) {
                    val assertThatCall = isLegitAssertThatCall(statement)
                    var reset = true
                    var actualExpression: PsiExpression? = null
                    if (assertThatCall != null) {
                        reset = (lastActualExpression == null)
                        actualExpression = assertThatCall.firstArg
                        if (!reset) {
                            val isSame = when (actualExpression) {
                                is PsiMethodCallExpression -> equivalenceChecker.expressionsAreEquivalent(actualExpression, lastActualExpression)
                                        && !KNOWN_METHODS_WITH_SIDE_EFFECTS.test(actualExpression)
                                else -> equivalenceChecker.expressionsAreEquivalent(actualExpression, lastActualExpression)
                            }
                            if (isSame) {
                                sameCount++
                                lastStatement = statement
                            } else {
                                reset = true
                            }
                        }
                    }
                    if (reset) {
                        if (sameCount > 1) {
                            registerProblem(firstStatement, lastStatement)
                        }
                        firstStatement = statement
                        lastStatement = null
                        lastActualExpression = actualExpression
                        sameCount = 1
                    }
                }
                if (sameCount > 1) {
                    registerProblem(firstStatement, lastStatement)
                }
            }

            private fun registerProblem(firstStatement: PsiStatement?, lastStatement: PsiStatement?) {
                val problemDescriptor = holder.manager.createProblemDescriptor(
                    firstStatement!!,
                    lastStatement!!,
                    CAN_BE_JOINED_DESCRIPTION,
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    isOnTheFly,
                    JoinStatementsQuickFix()
                )
                holder.registerProblem(problemDescriptor)
            }

            private fun isLegitAssertThatCall(statement: PsiStatement?): PsiMethodCallExpression? {
                if ((statement is PsiExpressionStatement) && (statement.expression is PsiMethodCallExpression)) {
                    val assertThatCall = PsiTreeUtil.findChildrenOfType(statement, PsiMethodCallExpression::class.java).find { ALL_ASSERT_THAT_MATCHERS.test(it) }
                    return assertThatCall?.takeIf { it.findFluentCallTo(EXTRACTING_CALL_MATCHERS) == null }
                }
                return null
            }
        }
    }
}
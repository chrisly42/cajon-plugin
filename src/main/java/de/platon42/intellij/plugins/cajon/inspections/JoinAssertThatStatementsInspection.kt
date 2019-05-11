package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.siyeh.ig.psiutils.EquivalenceChecker
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.JoinStatementsQuickFix

class JoinAssertThatStatementsInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Join multiple assertThat() statements with same actual expression"
        private const val CAN_BE_JOINED_DESCRIPTION = "Multiple assertThat() statements can be joined together"
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitCodeBlock(block: PsiCodeBlock) {
                super.visitCodeBlock(block)
                var lastActualExpression: PsiExpression? = null
                var sameCount = 0
                var firstStatement: PsiStatement? = null
                var lastStatement: PsiStatement? = null
                // Note: replace with TrackingEquivalenceChecker() for IDEA >= 2019.1
                val equivalenceChecker = EquivalenceChecker.getCanonicalPsiEquivalence()!!
                for (statement in block.statements) {
                    val assertThatCall = isLegitAssertThatCall(statement)
                    var reset = true
                    var actualExpression: PsiExpression? = null
                    if (assertThatCall != null) {
                        reset = (lastActualExpression == null)
                        actualExpression = assertThatCall.firstArg
                        if (!reset) {
                            val isSame = equivalenceChecker.expressionsAreEquivalent(actualExpression, lastActualExpression)
                                    && !hasExpressionWithSideEffects(actualExpression)

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
                            registerProblem(holder, isOnTheFly, firstStatement!!, lastStatement!!)
                        }
                        firstStatement = statement
                        lastStatement = null
                        lastActualExpression = actualExpression
                        sameCount = 1
                    }
                }
                if (sameCount > 1) {
                    registerProblem(holder, isOnTheFly, firstStatement!!, lastStatement!!)
                }
            }

            private fun isLegitAssertThatCall(statement: PsiStatement?): PsiMethodCallExpression? {
                if ((statement is PsiExpressionStatement) && (statement.expression is PsiMethodCallExpression)) {
                    if (!statement.hasAssertThat()) {
                        return null
                    }
                    val assertThatCall = PsiTreeUtil.findChildrenOfType(statement, PsiMethodCallExpression::class.java).find { ALL_ASSERT_THAT_MATCHERS.test(it) }
                    return assertThatCall?.takeIf { it.findFluentCallTo(EXTRACTING_CALL_MATCHERS) == null }
                }
                return null
            }

            private fun hasExpressionWithSideEffects(actualExpression: PsiExpression): Boolean {
                var result = false
                PsiTreeUtil.processElements(actualExpression) { element ->
                    val matched = when (element) {
                        is PsiUnaryExpression -> (element.operationTokenType == JavaTokenType.PLUSPLUS)
                                || (element.operationTokenType == JavaTokenType.MINUSMINUS)
                        is PsiMethodCallExpression -> KNOWN_METHODS_WITH_SIDE_EFFECTS.test(element)
                        else -> false
                    }
                    if (matched) {
                        result = true
                        false
                    } else {
                        true
                    }
                }
                return result
            }
        }
    }

    private fun registerProblem(holder: ProblemsHolder, isOnTheFly: Boolean, firstStatement: PsiStatement, lastStatement: PsiStatement) {
        val problemDescriptor = holder.manager.createProblemDescriptor(
            firstStatement,
            lastStatement,
            CAN_BE_JOINED_DESCRIPTION,
            ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
            isOnTheFly,
            JoinStatementsQuickFix()
        )
        holder.registerProblem(problemDescriptor)
    }
}
package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import de.platon42.intellij.plugins.cajon.hasAssertThat
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceIfByAssumeThatQuickFix

class AssumeThatInsteadOfReturnInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Replace conditional test exits by assumeThat() statements with same actual expression"
        private const val REPLACE_RETURN_BY_ASSUME_THAT_DESCRIPTION = "Conditional return should probably be an assumeThat() statement instead"

        private const val MAX_RECURSION_DEPTH = 5
        private const val MAX_STATEMENTS_COUNT = 50

        private val TEST_ANNOTATIONS = listOf(
            "org.junit.Test",
            "org.junit.jupiter.api.Test",
            "org.junit.jupiter.api.TestTemplate",
            "org.junit.jupiter.api.params.ParameterizedTest"
        )

        private fun hasEmptyReturn(statement: PsiStatement): Boolean {
            return when (statement) {
                is PsiBlockStatement -> {
                    val psiReturnStatement = (statement.firstChild as? PsiCodeBlock)?.statements?.singleOrNull() as? PsiReturnStatement
                    (psiReturnStatement != null) && (psiReturnStatement.returnValue == null)
                }
                is PsiReturnStatement -> statement.returnValue == null
                else -> false
            }
        }

        private fun registerProblem(holder: ProblemsHolder, isOnTheFly: Boolean, statement: PsiStatement, removeElse: Boolean) {
            val problemDescriptor = holder.manager.createProblemDescriptor(
                statement,
                statement,
                REPLACE_RETURN_BY_ASSUME_THAT_DESCRIPTION,
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                isOnTheFly,
                ReplaceIfByAssumeThatQuickFix(removeElse)
            )
            holder.registerProblem(problemDescriptor)
        }
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitMethod(method: PsiMethod) {
                super.visitMethod(method)
                if (TEST_ANNOTATIONS.none(method::hasAnnotation)) {
                    return
                }
                val containingClass = method.containingClass ?: return
                val visitor: PsiElementVisitor = TestMethodVisitor(holder, isOnTheFly, containingClass)
                method.accept(visitor)
            }
        }
    }

    class TestMethodVisitor(
        private val holder: ProblemsHolder,
        private val isOnTheFly: Boolean,
        private val containingClass: PsiClass
    ) : JavaRecursiveElementWalkingVisitor() {

        private var contSearch = true

        override fun visitExpressionStatement(statement: PsiExpressionStatement) {
            if (contSearch) {
                val methodCallExpression = statement.expression as? PsiMethodCallExpression
                if (methodCallExpression != null) {
                    if (methodCallExpression.hasAssertThat()) {
                        contSearch = false
                    } else {
                        val method = methodCallExpression.resolveMethod()
                        if (method?.containingClass == containingClass) {
                            val recursionVisitor = CheckForAssertThatCallsVisitor(containingClass, 1)
                            method.accept(recursionVisitor)
                            if (recursionVisitor.aborted || recursionVisitor.foundAssertThat) {
                                contSearch = false
                            }
                        }
                    }
                }
            }
            if (contSearch) {
                super.visitExpressionStatement(statement)
            }
        }

        override fun visitIfStatement(statement: PsiIfStatement) {
            if (contSearch) {
                checkBranch(statement, statement.thenBranch, false)
                checkBranch(statement, statement.elseBranch, true)
            }
        }

        private fun checkBranch(statement: PsiIfStatement, branch: PsiStatement?, removeElse: Boolean) {
            if (branch != null) {
                if (hasEmptyReturn(branch)) {
                    registerProblem(holder, isOnTheFly, statement, removeElse)
                } else {
                    branch.accept(TestMethodVisitor(holder, isOnTheFly, containingClass))
                }
            }
        }
    }

    class CheckForAssertThatCallsVisitor(private val containingClass: PsiClass, private var depth: Int) : JavaRecursiveElementWalkingVisitor() {
        var foundAssertThat = false
        private var statementCount = 0
        var aborted = false

        override fun visitExpressionStatement(statement: PsiExpressionStatement) {
            if (foundAssertThat || aborted) {
                return
            }
            if (++statementCount > MAX_STATEMENTS_COUNT) {
                aborted = true
                return
            }
            super.visitExpressionStatement(statement)
            val methodCallExpression = statement.expression as? PsiMethodCallExpression
            if (methodCallExpression != null) {
                foundAssertThat = methodCallExpression.hasAssertThat()
                val method = methodCallExpression.resolveMethod()
                if (method?.containingClass == containingClass) {
                    if (depth < MAX_RECURSION_DEPTH) {
                        val recursionVisitor = CheckForAssertThatCallsVisitor(containingClass, depth + 1)
                        method.accept(recursionVisitor)
                        foundAssertThat = recursionVisitor.foundAssertThat
                        statementCount += recursionVisitor.statementCount
                        aborted = recursionVisitor.aborted
                    } else {
                        aborted = true
                    }
                }
            }
        }
    }
}
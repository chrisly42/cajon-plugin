package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.findStaticMethodCall
import de.platon42.intellij.plugins.cajon.shortenAndReformat

class JoinStatementsQuickFix(private val separateLineLimit: Int) : AbstractCommonQuickFix(JOIN_STATEMENTS_MESSAGE) {

    companion object {
        private const val JOIN_STATEMENTS_MESSAGE = "Join assertThat() statements"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val firstStatement = descriptor.startElement as PsiExpressionStatement
        val lastStatement = descriptor.endElement as PsiExpressionStatement

        val expressionCount = countExpressions(firstStatement, lastStatement)
        val addLineBreaks = (expressionCount > separateLineLimit)

        do {
            val commentsToKeep = ArrayList<PsiComment>()
            val stuffToDelete = ArrayList<PsiElement>()
            var previousStatement = lastStatement.prevSibling!!
            while (previousStatement !is PsiExpressionStatement) {
                if (previousStatement is PsiComment) {
                    commentsToKeep.add(previousStatement.copy() as PsiComment)
                }
                stuffToDelete.add(previousStatement)
                previousStatement = previousStatement.prevSibling!!
            }
            stuffToDelete.forEach { if (it.isValid) it.delete() }

            val statementComments = PsiTreeUtil.getChildrenOfAnyType(previousStatement, PsiComment::class.java)
            commentsToKeep.addAll(statementComments)

            val assertThatCallOfCursorStatement = lastStatement.findStaticMethodCall()!!

            val lastElementBeforeConcat = assertThatCallOfCursorStatement.parent
            commentsToKeep.forEach {
                lastElementBeforeConcat.addAfter(it, lastElementBeforeConcat.firstChild)
                addLineBreak(project, lastElementBeforeConcat)
            }
            if (commentsToKeep.isEmpty() && addLineBreaks) {
                addLineBreak(project, lastElementBeforeConcat)
            }

            val newLeaf = previousStatement.firstChild
            assertThatCallOfCursorStatement.replace(newLeaf)
            previousStatement.delete()
        } while (previousStatement !== firstStatement)
        val codeBlock = PsiTreeUtil.getParentOfType(lastStatement, PsiCodeBlock::class.java) ?: return
        codeBlock.shortenAndReformat()
    }

    private fun addLineBreak(project: Project, lastElementBeforeConcat: PsiElement) {
        val newLineNode = try {
            PsiParserFacade.getInstance(project).createWhiteSpaceFromText("\n\t")
        } catch (ex: NoSuchMethodError) {
            // scheduled for removal, but alternate version not even available in 2020.3.
            // So keep it here until we run into a problem.
            // Changing the min version from 2017.3 to 2021.x is a major thing.
            PsiParserFacade.SERVICE.getInstance(project).createWhiteSpaceFromText("\n\t")
        }
        lastElementBeforeConcat.addAfter(newLineNode, lastElementBeforeConcat.firstChild)
    }

    private fun countExpressions(firstStatement: PsiElement, lastStatement: PsiElement): Int {
        var count = 0
        var currentStatement = firstStatement
        do {
            while (currentStatement !is PsiExpressionStatement) {
                currentStatement = currentStatement.nextSibling!!
            }
            count++
            if (currentStatement === lastStatement) {
                break
            }
            currentStatement = currentStatement.nextSibling!!
        } while (true)
        return count
    }
}
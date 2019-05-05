package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.findStaticMethodCall
import de.platon42.intellij.plugins.cajon.shortenAndReformat

class JoinStatementsQuickFix : AbstractCommonQuickFix(JOIN_STATEMENTS_MESSAGE) {

    companion object {
        private const val JOIN_STATEMENTS_MESSAGE = "Join assertThat() statements"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val firstStatement = descriptor.startElement as PsiExpressionStatement
        val lastStatement = descriptor.endElement as PsiExpressionStatement
        do {
            val commentsToKeep = ArrayList<PsiComment>()
            val stuffToDelete = ArrayList<PsiElement>()
            var previousStatement = lastStatement.prevSibling ?: throw IllegalStateException("Internal error")
            while (previousStatement !is PsiExpressionStatement) {
                if (previousStatement is PsiComment) {
                    commentsToKeep.add(previousStatement.copy() as PsiComment)
                }
                stuffToDelete.add(previousStatement)
                previousStatement = previousStatement.prevSibling ?: throw IllegalStateException("Internal error")
            }
            stuffToDelete.forEach { if (it.isValid) it.delete() }

            val statementComments = PsiTreeUtil.getChildrenOfAnyType(previousStatement, PsiComment::class.java)
            commentsToKeep.addAll(statementComments)

            val assertThatCallOfCursorStatement = lastStatement.findStaticMethodCall() ?: throw IllegalStateException("Internal error")

            val lastElementBeforeConcat = assertThatCallOfCursorStatement.parent
            commentsToKeep.forEach {
                lastElementBeforeConcat.addAfter(it, lastElementBeforeConcat.firstChild)
                val newLineNode =
                    PsiParserFacade.SERVICE.getInstance(project).createWhiteSpaceFromText("\n\t")

                lastElementBeforeConcat.addAfter(newLineNode, lastElementBeforeConcat.firstChild)
            }

            val newLeaf = previousStatement.firstChild
            assertThatCallOfCursorStatement.replace(newLeaf)
            previousStatement.delete()
        } while (previousStatement !== firstStatement)
        val codeBlock = PsiTreeUtil.getParentOfType(lastStatement, PsiCodeBlock::class.java) ?: return
        codeBlock.shortenAndReformat()
    }
}
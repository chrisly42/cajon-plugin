package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiBlockStatement
import com.intellij.psi.PsiDeclarationStatement
import com.intellij.psi.PsiIfStatement
import de.platon42.intellij.plugins.cajon.*

class ReplaceIfByAssumeThatQuickFix(private val removeElse: Boolean) : AbstractCommonQuickFix(REPLACE_IF_MESSAGE) {

    companion object {
        private const val REPLACE_IF_MESSAGE = "Replace if statement by assumeTrue()"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val ifStatement = descriptor.startElement as PsiIfStatement

        val condition = ifStatement.condition ?: return
        val factory = JavaPsiFacade.getElementFactory(ifStatement.project)
        val assumptionExpression = if (removeElse) MethodNames.IS_TRUE else MethodNames.IS_FALSE
        val assumeThatStatement = factory.createStatementFromText(
            "${AssertJClassNames.ASSUMPTIONS_CLASSNAME}.${MethodNames.ASSUME_THAT}(true).$assumptionExpression();",
            ifStatement
        )
        val assumeThatMethodCall = assumeThatStatement.findStaticMethodCall() ?: return
        assumeThatMethodCall.firstArg.replace(condition)
        assumeThatMethodCall.resolveMethod()?.addAsStaticImport(ifStatement)

        val branchToKeep = (if (removeElse) ifStatement.thenBranch else ifStatement.elseBranch)?.copy()
        val parentBlock = ifStatement.parent
        if (branchToKeep != null) {
            val anchorElement = ifStatement.nextSibling
            if (branchToKeep is PsiBlockStatement) {
                val codeBlock = branchToKeep.codeBlock
                val hasDeclarations = codeBlock.statements.any { it is PsiDeclarationStatement }
                if (hasDeclarations) {
                    parentBlock.addAfter(branchToKeep, anchorElement)
                } else {
                    parentBlock.addRangeAfter(codeBlock.firstBodyElement, codeBlock.lastBodyElement, anchorElement)
                }
            } else {
                parentBlock.addAfter(branchToKeep, anchorElement)
            }
        }
        ifStatement.replace(assumeThatStatement).shortenAndReformat()
    }
}
package de.platon42.intellij.plugins.cajon

import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.util.PsiTreeUtil

val PsiMethodCallExpression.qualifierExpression: PsiExpression get() = this.methodExpression.qualifierExpression!!
val PsiMethodCallExpression.firstArg: PsiExpression get() = this.argumentList.expressions[0]!!

fun PsiMethodCallExpression.replaceQualifier(qualifier: PsiElement) {
    this.qualifierExpression.replace(qualifier)
}

fun PsiMethodCallExpression.replaceQualifierFromMethodCall(oldMethodCall: PsiMethodCallExpression) {
    this.qualifierExpression.replace(oldMethodCall.qualifierExpression)
}

fun PsiElement.findOutmostMethodCall(): PsiMethodCallExpression? {
    val statement = PsiTreeUtil.getParentOfType(this, PsiStatement::class.java) ?: return null
    return PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java)
}

fun PsiMethodCallExpression.getArg(n: Int): PsiExpression = this.argumentList.expressions[n]

fun <T> Boolean.map(forTrue: T, forFalse: T) = if (this) forTrue else forFalse

fun PsiMethod.addAsStaticImport(context: PsiElement, vararg allowedClashes: String) {
    val factory = JavaPsiFacade.getElementFactory(context.project)
    val methodName = this.name
    val containingClass = this.containingClass ?: return
    val importList = (context.containingFile as PsiJavaFile).importList ?: return
    val notImportedStatically = importList.importStaticStatements.none {
        val targetClass = it.resolveTargetClass() ?: return@none false
        ((it.referenceName == methodName) && !allowedClashes.contains(targetClass.qualifiedName))
                || (it.isOnDemand && (targetClass == this.containingClass))
    }
    if (notImportedStatically) {
        importList.add(factory.createImportStaticStatement(containingClass, methodName))
    }
}

fun PsiElement.shortenAndReformat() {
    val codeStyleManager = JavaCodeStyleManager.getInstance(project)
    codeStyleManager.shortenClassReferences(this)
    CodeStyleManager.getInstance(project).reformat(this)
}
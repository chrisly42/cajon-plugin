package de.platon42.intellij.plugins.cajon

import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiMethodCallExpression

val PsiMethodCallExpression.qualifierExpression: PsiExpression get() = this.methodExpression.qualifierExpression!!
val PsiMethodCallExpression.firstArg: PsiExpression get() = this.argumentList.expressions[0]!!

fun PsiMethodCallExpression.replaceQualifier(qualifier: PsiExpression) {
    this.qualifierExpression.replace(qualifier)
}

fun PsiMethodCallExpression.replaceQualifierFromMethodCall(oldMethodCall: PsiMethodCallExpression) {
    this.qualifierExpression.replace(oldMethodCall.qualifierExpression)
}

fun PsiMethodCallExpression.getArg(n: Int): PsiExpression = this.argumentList.expressions[n]

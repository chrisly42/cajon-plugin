package de.platon42.intellij.plugins.cajon

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiMethodCallExpression


fun createAssertThat(context: PsiElement, actualExpression: PsiExpression): PsiMethodCallExpression {
    return createAssertThat(context, AssertJClassNames.ASSERTIONS_CLASSNAME, actualExpression)
}

fun createGuavaAssertThat(context: PsiElement, actualExpression: PsiExpression): PsiMethodCallExpression {
    return createAssertThat(context, AssertJClassNames.GUAVA_ASSERTIONS_CLASSNAME, actualExpression)
}

fun createAssertThat(context: PsiElement, baseclass: String, actualExpression: PsiExpression): PsiMethodCallExpression {
    return createMethodCall(context, "$baseclass.${MethodNames.ASSERT_THAT}", actualExpression)
}

fun createExpectedMethodCall(context: PsiElement, methodName: String, vararg arguments: PsiElement): PsiMethodCallExpression {
    return createMethodCall(context, "a.$methodName", *arguments)
}

fun createMethodCall(context: PsiElement, fullQualifiedMethodName: String, vararg arguments: PsiElement): PsiMethodCallExpression {
    val factory = JavaPsiFacade.getElementFactory(context.project)
    val argString = generateSequence('b') { it + 1 }.take(arguments.size).joinToString(", ")
    val expectedExpression = factory.createExpressionFromText(
        "$fullQualifiedMethodName($argString)", context
    ) as PsiMethodCallExpression
    arguments.forEachIndexed { index, newArg -> expectedExpression.getArg(index).replace(newArg) }
    return expectedExpression
}
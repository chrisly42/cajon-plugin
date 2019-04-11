package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.psi.*
import de.platon42.intellij.plugins.cajon.AssertJClassNames
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.firstArg

abstract class AbstractCommonQuickFix(private val description: String) : LocalQuickFix {

    override fun getFamilyName() = description

    protected fun addStaticImport(method: PsiMethod, element: PsiMethodCallExpression, factory: PsiElementFactory, vararg allowedClashes: String) {
        val methodName = method.name
        val containingClass = method.containingClass ?: return
        val importList = (element.containingFile as PsiJavaFile).importList ?: return
        val notImportedStatically = importList.importStaticStatements.none {
            val targetClass = it.resolveTargetClass() ?: return@none false
            ((it.referenceName == methodName) && !allowedClashes.contains(targetClass.qualifiedName))
                    || (it.isOnDemand && (targetClass == method.containingClass))
        }
        if (notImportedStatically) {
            importList.add(factory.createImportStaticStatement(containingClass, methodName))
        }
    }

    protected fun createAssertThat(context: PsiElement, actualExpression: PsiExpression): PsiMethodCallExpression {
        val factory = JavaPsiFacade.getElementFactory(context.project)
        val newMethodCall = factory.createExpressionFromText(
            "${AssertJClassNames.ASSERTIONS_CLASSNAME}.${MethodNames.ASSERT_THAT}(a)", context
        ) as PsiMethodCallExpression
        newMethodCall.firstArg.replace(actualExpression)
        return newMethodCall
    }
}
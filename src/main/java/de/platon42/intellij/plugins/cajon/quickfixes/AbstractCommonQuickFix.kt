package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.annotations.NonNls

abstract class AbstractCommonQuickFix(private val description: String) : LocalQuickFix {

    override fun getFamilyName() = description

    companion object {
        @NonNls
        const val GUAVA_ASSERTIONS_CLASSNAME = "org.assertj.guava.api.Assertions"
    }

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
}
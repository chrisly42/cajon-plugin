package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiStatement
import com.intellij.psi.util.PsiTreeUtil
import de.platon42.intellij.plugins.cajon.*

class ForGuavaPostFix {

    companion object {
        val REPLACE_BY_GUAVA_ASSERT_THAT_AND_STATIC_IMPORT: (Project, ProblemDescriptor) -> Unit = exit@
        { _, descriptor ->
            val element = descriptor.startElement
            val statement = PsiTreeUtil.getParentOfType(element, PsiStatement::class.java) ?: return@exit
            val assertThatCall = statement.findStaticMethodCall() ?: return@exit

            val newMethodCall = createGuavaAssertThat(element, assertThatCall.firstArg)
            newMethodCall.resolveMethod()?.addAsStaticImport(element, AssertJClassNames.ASSERTIONS_CLASSNAME)
            val parentCall = PsiTreeUtil.getParentOfType(assertThatCall, PsiMethodCallExpression::class.java) ?: return@exit
            parentCall.replaceQualifier(newMethodCall)
            parentCall.shortenAndReformat()
        }
    }
}
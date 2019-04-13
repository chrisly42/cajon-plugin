package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiStatement
import com.intellij.psi.util.PsiTreeUtil
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*

class ForGuavaPostFix {
    companion object {
        private val CORE_MATCHER = CallMatcher.staticCall(AssertJClassNames.ASSERTIONS_CLASSNAME, MethodNames.ASSERT_THAT)
        val REPLACE_BY_GUAVA_ASSERT_THAT_AND_STATIC_IMPORT: (Project, ProblemDescriptor) -> Unit = exit@
        { _, descriptor ->
            val element = descriptor.startElement
            val statement = PsiTreeUtil.getParentOfType(element, PsiStatement::class.java) ?: return@exit
            val methodCallExpression = PsiTreeUtil.findChildrenOfType(statement, PsiMethodCallExpression::class.java).find { CORE_MATCHER.test(it) } ?: return@exit

            val newMethodCall = createGuavaAssertThat(element, methodCallExpression.firstArg)
            newMethodCall.resolveMethod()?.addAsStaticImport(element, AssertJClassNames.ASSERTIONS_CLASSNAME)
            val parentCall = PsiTreeUtil.getParentOfType(methodCallExpression, PsiMethodCallExpression::class.java) ?: return@exit
            parentCall.replaceQualifier(newMethodCall)
            parentCall.shortenAndReformat()
        }
    }
}
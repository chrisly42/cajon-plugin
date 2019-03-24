package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.CommonClassNames
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiCapturedWildcardType
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTypesUtil
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSimpleMethodCallQuickFix
import org.jetbrains.annotations.NonNls

open class AbstractAssertJInspection : AbstractBaseJavaLocalInspectionTool() {

    companion object {
        @NonNls
        const val SIMPLIFY_MESSAGE_TEMPLATE = "%s can be simplified to %s"

        @NonNls
        const val REPLACE_DESCRIPTION_TEMPLATE = "Replace %s with %s"

        const val ABSTRACT_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractAssert"
        const val ABSTRACT_BOOLEAN_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractBooleanAssert"
        const val ABSTRACT_STRING_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractStringAssert"
        const val ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractCharSequenceAssert"
        const val IS_EQUAL_TO = "isEqualTo"
        const val IS_NOT_EQUAL_TO = "isNotEqualTo"
        const val HAS_SIZE = "hasSize"

        val IS_EQUAL_TO_OBJECT = CallMatcher.instanceCall(ABSTRACT_ASSERT_CLASSNAME, IS_EQUAL_TO)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
        val IS_NOT_EQUAL_TO_OBJECT = CallMatcher.instanceCall(ABSTRACT_ASSERT_CLASSNAME, IS_NOT_EQUAL_TO)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
        val IS_EQUAL_TO_BOOLEAN = CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, IS_EQUAL_TO)
            .parameterTypes("boolean")!!
        val IS_NOT_EQUAL_TO_BOOLEAN = CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, IS_NOT_EQUAL_TO)
            .parameterTypes("boolean")!!
        val CHAR_SEQUENCE_HAS_SIZE = CallMatcher.instanceCall(ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME, HAS_SIZE)
            .parameterTypes("int")!!
    }

    override fun getGroupDisplayName(): String {
        return "AssertJ"
    }

    protected fun checkAssertedType(expression: PsiMethodCallExpression, classname: String): Boolean {
        var assertedType = expression.methodExpression.qualifierExpression?.type ?: return false
        if (assertedType is PsiCapturedWildcardType) {
            assertedType = assertedType.upperBound
        }
        val assertedClass = PsiTypesUtil.getPsiClass(assertedType) ?: return false
        val expectedClass = JavaPsiFacade.getInstance(expression.project)
            .findClass(classname, GlobalSearchScope.allScope(expression.project)) ?: return false
        return assertedClass.isEquivalentTo(expectedClass) || assertedClass.isInheritor(expectedClass, true)
    }

    protected fun getOriginalMethodName(expression: PsiMethodCallExpression) =
        expression.resolveMethod()?.name?.plus("()")

    protected fun registerSimplifyMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        replacementMethod: String
    ) {
        val originalMethod = getOriginalMethodName(expression) ?: return
        val description = String.format(REPLACE_DESCRIPTION_TEMPLATE, originalMethod, replacementMethod)
        val message = String.format(SIMPLIFY_MESSAGE_TEMPLATE, originalMethod, replacementMethod)
        holder.registerProblem(
            expression,
            message,
            ProblemHighlightType.INFORMATION,
            null as TextRange?,
            ReplaceSimpleMethodCallQuickFix(description, replacementMethod)
        )
    }
}
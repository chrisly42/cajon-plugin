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
        const val SIMPLIFY_MESSAGE_TEMPLATE = "%s can be simplified to %s"

        const val REPLACE_DESCRIPTION_TEMPLATE = "Replace %s with %s"

        @NonNls
        const val ABSTRACT_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractAssert"
        @NonNls
        const val ABSTRACT_BOOLEAN_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractBooleanAssert"
        @NonNls
        const val ABSTRACT_STRING_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractStringAssert"
        @NonNls
        const val ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractCharSequenceAssert"
        @NonNls
        const val ABSTRACT_ENUMERABLE_ASSERT_CLASSNAME = "org.assertj.core.api.EnumerableAssert"

        @NonNls
        const val IS_EQUAL_TO_METHOD = "isEqualTo"
        @NonNls
        const val IS_NOT_EQUAL_TO_METHOD = "isNotEqualTo"
        @NonNls
        const val HAS_SIZE_METHOD = "hasSize"

        val IS_EQUAL_TO_OBJECT = CallMatcher.instanceCall(ABSTRACT_ASSERT_CLASSNAME, IS_EQUAL_TO_METHOD)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
        val IS_NOT_EQUAL_TO_OBJECT = CallMatcher.instanceCall(ABSTRACT_ASSERT_CLASSNAME, IS_NOT_EQUAL_TO_METHOD)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
        val IS_EQUAL_TO_BOOLEAN = CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, IS_EQUAL_TO_METHOD)
            .parameterTypes("boolean")!!
        val IS_NOT_EQUAL_TO_BOOLEAN =
            CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, IS_NOT_EQUAL_TO_METHOD)
                .parameterTypes("boolean")!!
        val HAS_SIZE = CallMatcher.instanceCall(ABSTRACT_ENUMERABLE_ASSERT_CLASSNAME, HAS_SIZE_METHOD)
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
        val description = REPLACE_DESCRIPTION_TEMPLATE.format(originalMethod, replacementMethod)
        val message = SIMPLIFY_MESSAGE_TEMPLATE.format(originalMethod, replacementMethod)
        holder.registerProblem(
            expression,
            message,
            ProblemHighlightType.INFORMATION,
            null as TextRange?,
            ReplaceSimpleMethodCallQuickFix(description, replacementMethod)
        )
    }

    protected fun calculateConstantParameterValue(expression: PsiMethodCallExpression, argIndex: Int): Any? {
        val valueExpression = expression.argumentList.expressions[argIndex] ?: return null
        val constantEvaluationHelper = JavaPsiFacade.getInstance(expression.project).constantEvaluationHelper
        return constantEvaluationHelper.computeConstantExpression(valueExpression)
    }
}
package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTypesUtil
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.getArg
import de.platon42.intellij.plugins.cajon.qualifierExpression
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSimpleMethodCallQuickFix
import org.jetbrains.annotations.NonNls

open class AbstractAssertJInspection : AbstractBaseJavaLocalInspectionTool() {

    companion object {
        const val SIMPLIFY_MESSAGE_TEMPLATE = "%s can be simplified to %s"

        const val REPLACE_DESCRIPTION_TEMPLATE = "Replace %s with %s"

        @NonNls
        const val ASSERTIONS_CLASSNAME = "org.assertj.core.api.Assertions"

        @NonNls
        const val ABSTRACT_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractAssert"
        @NonNls
        const val ABSTRACT_BOOLEAN_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractBooleanAssert"
        @NonNls
        const val ABSTRACT_INTEGER_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractIntegerAssert"
        @NonNls
        const val ABSTRACT_COMPARABLE_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractComparableAssert"
        @NonNls
        const val ABSTRACT_STRING_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractStringAssert"
        @NonNls
        const val ABSTRACT_CHAR_SEQUENCE_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractCharSequenceAssert"
        @NonNls
        const val ABSTRACT_ENUMERABLE_ASSERT_CLASSNAME = "org.assertj.core.api.EnumerableAssert"

        @NonNls
        const val ASSERT_THAT_METHOD = "assertThat"
        @NonNls
        const val IS_EQUAL_TO_METHOD = "isEqualTo"
        @NonNls
        const val IS_NOT_EQUAL_TO_METHOD = "isNotEqualTo"
        @NonNls
        const val IS_GREATER_THAN_METHOD = "isGreaterThan"
        @NonNls
        const val IS_GREATER_THAN_OR_EQUAL_TO_METHOD = "isGreaterThanOrEqualTo"
        @NonNls
        const val IS_LESS_THAN_METHOD = "isLessThan"
        @NonNls
        const val IS_LESS_THAN_OR_EQUAL_TO_METHOD = "isLessThanOrEqualTo"
        @NonNls
        const val IS_ZERO_METHOD = "isZero"
        @NonNls
        const val IS_NOT_ZERO_METHOD = "isNotZero"
        @NonNls
        const val IS_TRUE_METHOD = "isTrue"
        @NonNls
        const val IS_FALSE_METHOD = "isFalse"
        @NonNls
        const val HAS_SIZE_METHOD = "hasSize"

        val ASSERT_THAT_INT = CallMatcher.staticCall(ASSERTIONS_CLASSNAME, ASSERT_THAT_METHOD)
            .parameterTypes("int")!!

        val ASSERT_THAT_BOOLEAN = CallMatcher.staticCall(ASSERTIONS_CLASSNAME, ASSERT_THAT_METHOD)
            .parameterTypes("boolean")!!

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

        val IS_EQUAL_TO_INT = CallMatcher.instanceCall(ABSTRACT_ASSERT_CLASSNAME, IS_EQUAL_TO_METHOD)
            .parameterTypes("int")!!
        val IS_GREATER_THAN_INT = CallMatcher.instanceCall(ABSTRACT_COMPARABLE_ASSERT_CLASSNAME, IS_GREATER_THAN_METHOD)
            .parameterTypes("int")!!
        val IS_GREATER_THAN_OR_EQUAL_TO_INT = CallMatcher.instanceCall(ABSTRACT_COMPARABLE_ASSERT_CLASSNAME, IS_GREATER_THAN_OR_EQUAL_TO_METHOD)
            .parameterTypes("int")!!

        val IS_LESS_THAN_INT = CallMatcher.instanceCall(ABSTRACT_COMPARABLE_ASSERT_CLASSNAME, IS_LESS_THAN_METHOD)
            .parameterTypes("int")!!
        val IS_LESS_THAN_OR_EQUAL_TO_INT = CallMatcher.instanceCall(ABSTRACT_COMPARABLE_ASSERT_CLASSNAME, IS_LESS_THAN_OR_EQUAL_TO_METHOD)
            .parameterTypes("int")!!

        val IS_ZERO = CallMatcher.instanceCall(ABSTRACT_INTEGER_ASSERT_CLASSNAME, IS_ZERO_METHOD)
            .parameterCount(0)!!
        val IS_NOT_ZERO = CallMatcher.instanceCall(ABSTRACT_INTEGER_ASSERT_CLASSNAME, IS_NOT_ZERO_METHOD)
            .parameterCount(0)!!

        val IS_TRUE = CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, IS_TRUE_METHOD)
            .parameterCount(0)!!
        val IS_FALSE = CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, IS_FALSE_METHOD)
            .parameterCount(0)!!

        val COLLECTION_SIZE = CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_COLLECTION, "size")
            .parameterCount(0)!!
        val OBJECT_EQUALS = CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_OBJECT, "equals")
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
    }

    override fun getGroupDisplayName(): String {
        return "AssertJ"
    }

    protected fun checkAssertedType(expression: PsiMethodCallExpression, classname: String): Boolean {
        var assertedType = expression.qualifierExpression.type ?: return false
        while (assertedType is PsiCapturedWildcardType) {
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
        val quickFix = ReplaceSimpleMethodCallQuickFix(description, replacementMethod)
        holder.registerProblem(expression, message, quickFix)
    }

    protected fun calculateConstantParameterValue(expression: PsiMethodCallExpression, argIndex: Int): Any? {
        if (argIndex >= expression.argumentList.expressionCount) return null
        val valueExpression = expression.getArg(argIndex)
        val constantEvaluationHelper = JavaPsiFacade.getInstance(expression.project).constantEvaluationHelper
        val value = constantEvaluationHelper.computeConstantExpression(valueExpression)
        if (value == null) {
            val field = (valueExpression as? PsiReferenceExpression)?.resolve() as? PsiField
            if (field?.containingClass?.qualifiedName == CommonClassNames.JAVA_LANG_BOOLEAN) {
                return when (field.name) {
                    "TRUE" -> true
                    "FALSE" -> false
                    else -> null
                }
            }
        }
        return value
    }

    protected fun hasAssertJMethod(element: PsiElement, classAndMethod: String): Boolean {
        val classname = "org.assertj.core.api.${classAndMethod.substringBeforeLast(".")}"
        val findClass =
            JavaPsiFacade.getInstance(element.project).findClass(classname, GlobalSearchScope.allScope(element.project))
                ?: return false
        return findClass.findMethodsByName(classAndMethod.substringAfterLast(".")).isNotEmpty()
    }
}
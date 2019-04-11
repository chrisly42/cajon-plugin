package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTypesUtil
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.getArg
import de.platon42.intellij.plugins.cajon.qualifierExpression
import de.platon42.intellij.plugins.cajon.quickfixes.RemoveActualOutmostMethodCallQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceExpectedOutmostMethodCallQuickFix
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSimpleMethodCallQuickFix
import org.jetbrains.annotations.NonNls

open class AbstractAssertJInspection : AbstractBaseJavaLocalInspectionTool() {

    companion object {
        const val SIMPLIFY_MESSAGE_TEMPLATE = "%s can be simplified to %s"
        const val MORE_CONCISE_MESSAGE_TEMPLATE = "%s would be more concise than %s"

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
        const val IS_SAME_AS_METHOD = "isSameAs"
        @NonNls
        const val IS_NOT_SAME_AS_METHOD = "isNotSameAs"
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


        val TOKEN_TO_ASSERTJ_FOR_PRIMITIVE_MAP = mapOf<IElementType, String>(
            JavaTokenType.EQEQ to IS_EQUAL_TO_METHOD,
            JavaTokenType.NE to IS_NOT_EQUAL_TO_METHOD,
            JavaTokenType.GT to IS_GREATER_THAN_METHOD,
            JavaTokenType.GE to IS_GREATER_THAN_OR_EQUAL_TO_METHOD,
            JavaTokenType.LT to IS_LESS_THAN_METHOD,
            JavaTokenType.LE to IS_LESS_THAN_OR_EQUAL_TO_METHOD
        )

        val TOKEN_TO_ASSERTJ_FOR_OBJECT_MAPPINGS = mapOf<IElementType, String>(
            JavaTokenType.EQEQ to IS_SAME_AS_METHOD,
            JavaTokenType.NE to IS_NOT_SAME_AS_METHOD
        )

        val SWAP_SIDE_OF_BINARY_OPERATOR = mapOf<IElementType, IElementType>(
            JavaTokenType.GT to JavaTokenType.LT,
            JavaTokenType.GE to JavaTokenType.LE,
            JavaTokenType.LT to JavaTokenType.GT,
            JavaTokenType.LE to JavaTokenType.GE
        )

        val INVERT_BINARY_OPERATOR = mapOf<IElementType, IElementType>(
            JavaTokenType.EQEQ to JavaTokenType.NE,
            JavaTokenType.NE to JavaTokenType.EQEQ,
            JavaTokenType.GT to JavaTokenType.LE,
            JavaTokenType.GE to JavaTokenType.LT,
            JavaTokenType.LT to JavaTokenType.GE,
            JavaTokenType.LE to JavaTokenType.GT
        )


        val ASSERT_THAT_INT = CallMatcher.staticCall(ASSERTIONS_CLASSNAME, ASSERT_THAT_METHOD)
            .parameterTypes("int")!!

        val ASSERT_THAT_BOOLEAN = CallMatcher.staticCall(ASSERTIONS_CLASSNAME, ASSERT_THAT_METHOD)
            .parameterTypes("boolean")!!

        val ASSERT_THAT_ANY = CallMatcher.staticCall(ASSERTIONS_CLASSNAME, ASSERT_THAT_METHOD)
            .parameterCount(1)!!

        val ASSERT_THAT_JAVA8_OPTIONAL = CallMatcher.staticCall(ASSERTIONS_CLASSNAME, ASSERT_THAT_METHOD)
            .parameterTypes(CommonClassNames.JAVA_UTIL_OPTIONAL)!!

        val IS_EQUAL_TO_OBJECT = CallMatcher.instanceCall(ABSTRACT_ASSERT_CLASSNAME, IS_EQUAL_TO_METHOD)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
        val IS_NOT_EQUAL_TO_OBJECT = CallMatcher.instanceCall(ABSTRACT_ASSERT_CLASSNAME, IS_NOT_EQUAL_TO_METHOD)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
        val IS_EQUAL_TO_BOOLEAN = CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, IS_EQUAL_TO_METHOD)
            .parameterTypes("boolean")!!
        val IS_NOT_EQUAL_TO_BOOLEAN =
            CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, IS_NOT_EQUAL_TO_METHOD)
                .parameterTypes("boolean")!!
        val IS_SAME_AS_OBJECT = CallMatcher.instanceCall(ABSTRACT_ASSERT_CLASSNAME, IS_SAME_AS_METHOD)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
        val IS_NOT_SAME_AS_OBJECT = CallMatcher.instanceCall(ABSTRACT_ASSERT_CLASSNAME, IS_NOT_SAME_AS_METHOD)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!

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

        val OPTIONAL_GET = CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "get")
            .parameterCount(0)!!
        val OPTIONAL_IS_PRESENT = CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "isPresent")
            .parameterCount(0)!!

        val OPTIONAL_OF = CallMatcher.staticCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "of")
            .parameterCount(1)!!
        val OPTIONAL_OF_NULLABLE = CallMatcher.staticCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "ofNullable")
            .parameterCount(1)!!
        val OPTIONAL_EMPTY = CallMatcher.staticCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "empty")
            .parameterCount(0)!!
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

    protected fun registerRemoveActualOutmostMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        expectedCallExpression: PsiMethodCallExpression,
        replacementMethod: String,
        noExpectedExpression: Boolean = false
    ) {
        val originalMethod = getOriginalMethodName(expectedCallExpression) ?: return
        val description = REPLACE_DESCRIPTION_TEMPLATE.format(originalMethod, replacementMethod)
        val message = MORE_CONCISE_MESSAGE_TEMPLATE.format(replacementMethod, originalMethod)
        val quickfix = RemoveActualOutmostMethodCallQuickFix(description, replacementMethod, noExpectedExpression)
        holder.registerProblem(expression, message, quickfix)
    }

    protected fun registerRemoveExpectedOutmostMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        expectedCallExpression: PsiMethodCallExpression,
        replacementMethod: String
    ) {
        val originalMethod = getOriginalMethodName(expectedCallExpression) ?: return
        val description = REPLACE_DESCRIPTION_TEMPLATE.format(originalMethod, replacementMethod)
        val message = MORE_CONCISE_MESSAGE_TEMPLATE.format(replacementMethod, originalMethod)
        val quickfix = ReplaceExpectedOutmostMethodCallQuickFix(description, replacementMethod)
        holder.registerProblem(expression, message, quickfix)
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

    protected fun getExpectedBooleanResult(expectedCallExpression: PsiMethodCallExpression): Boolean? {
        val isTrue = IS_TRUE.test(expectedCallExpression)
        val isFalse = IS_FALSE.test(expectedCallExpression)
        if (isTrue || isFalse) {
            return isTrue
        } else {
            val isEqualTo = IS_EQUAL_TO_BOOLEAN.test(expectedCallExpression) || IS_EQUAL_TO_OBJECT.test(expectedCallExpression)
            val isNotEqualTo = IS_NOT_EQUAL_TO_BOOLEAN.test(expectedCallExpression) || IS_NOT_EQUAL_TO_OBJECT.test(expectedCallExpression)
            if (isEqualTo || isNotEqualTo) {
                val constValue = calculateConstantParameterValue(expectedCallExpression, 0) as? Boolean ?: return null
                return isNotEqualTo xor constValue
            }
        }
        return null
    }

    protected fun hasAssertJMethod(element: PsiElement, classAndMethod: String): Boolean {
        val classname = "org.assertj.core.api.${classAndMethod.substringBeforeLast(".")}"
        val findClass =
            JavaPsiFacade.getInstance(element.project).findClass(classname, GlobalSearchScope.allScope(element.project))
                ?: return false
        return findClass.findMethodsByName(classAndMethod.substringAfterLast(".")).isNotEmpty()
    }
}
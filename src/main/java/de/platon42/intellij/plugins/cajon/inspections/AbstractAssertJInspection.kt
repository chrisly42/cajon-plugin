package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTypesUtil
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_BOOLEAN_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_COMPARABLE_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_DOUBLE_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_FLOAT_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_INTEGER_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_LONG_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ABSTRACT_STRING_ASSERT_CLASSNAME
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ASSERTIONS_CLASSNAME
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ASSERT_INTERFACE
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.ENUMERABLE_ASSERT_INTERFACE
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.GUAVA_ASSERTIONS_CLASSNAME
import de.platon42.intellij.plugins.cajon.AssertJClassNames.Companion.GUAVA_OPTIONAL_CLASSNAME
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.qualifierExpression
import de.platon42.intellij.plugins.cajon.quickfixes.ReplaceSimpleMethodCallQuickFix

open class AbstractAssertJInspection : AbstractBaseJavaLocalInspectionTool() {

    companion object {
        const val SIMPLIFY_MESSAGE_TEMPLATE = "%s() can be simplified to %s()"
        const val MORE_CONCISE_MESSAGE_TEMPLATE = "%s() would be more concise than %s()"

        const val REPLACE_DESCRIPTION_TEMPLATE = "Replace %s() with %s()"
        const val REMOVE_EXPECTED_OUTMOST_DESCRIPTION_TEMPLATE = "Unwrap expected expression and replace %s() with %s()"
        const val MOVE_ACTUAL_EXPRESSION_DESCRIPTION_TEMPLATE = "Remove %s() of actual expression and use assertThat().%s() instead"
        const val MOVING_OUT_MESSAGE_TEMPLATE = "Moving %s() expression out of assertThat() would be more concise"

        val TOKEN_TO_ASSERTJ_FOR_PRIMITIVE_MAP = mapOf<IElementType, String>(
            JavaTokenType.EQEQ to MethodNames.IS_EQUAL_TO,
            JavaTokenType.NE to MethodNames.IS_NOT_EQUAL_TO,
            JavaTokenType.GT to MethodNames.IS_GREATER_THAN,
            JavaTokenType.GE to MethodNames.IS_GREATER_THAN_OR_EQUAL_TO,
            JavaTokenType.LT to MethodNames.IS_LESS_THAN,
            JavaTokenType.LE to MethodNames.IS_LESS_THAN_OR_EQUAL_TO
        )

        val TOKEN_TO_ASSERTJ_FOR_OBJECT_MAPPINGS = mapOf<IElementType, String>(
            JavaTokenType.EQEQ to MethodNames.IS_SAME_AS,
            JavaTokenType.NE to MethodNames.IS_NOT_SAME_AS
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


        val ASSERT_THAT_INT = CallMatcher.staticCall(ASSERTIONS_CLASSNAME, MethodNames.ASSERT_THAT)
            .parameterTypes("int")!!

        val ASSERT_THAT_BOOLEAN = CallMatcher.staticCall(ASSERTIONS_CLASSNAME, MethodNames.ASSERT_THAT)
            .parameterTypes("boolean")!!

        val ASSERT_THAT_ANY = CallMatcher.staticCall(ASSERTIONS_CLASSNAME, MethodNames.ASSERT_THAT)
            .parameterCount(1)!!

        val ASSERT_THAT_JAVA8_OPTIONAL = CallMatcher.staticCall(ASSERTIONS_CLASSNAME, MethodNames.ASSERT_THAT)
            .parameterTypes(CommonClassNames.JAVA_UTIL_OPTIONAL)!!

        val GUAVA_ASSERT_THAT_ANY = CallMatcher.staticCall(GUAVA_ASSERTIONS_CLASSNAME, MethodNames.ASSERT_THAT)
            .parameterCount(1)!!

        val IS_EQUAL_TO_OBJECT = CallMatcher.instanceCall(ASSERT_INTERFACE, MethodNames.IS_EQUAL_TO)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
        val IS_EQUAL_TO_STRING = CallMatcher.instanceCall(ABSTRACT_STRING_ASSERT_CLASSNAME, MethodNames.IS_EQUAL_TO)
            .parameterTypes(CommonClassNames.JAVA_LANG_STRING)!!
        val IS_EQUAL_TO_INT = CallMatcher.instanceCall(ABSTRACT_INTEGER_ASSERT_CLASSNAME, MethodNames.IS_EQUAL_TO)
            .parameterTypes("int")!!
        val IS_EQUAL_TO_LONG = CallMatcher.instanceCall(ABSTRACT_LONG_ASSERT_CLASSNAME, MethodNames.IS_EQUAL_TO)
            .parameterTypes("long")!!
        val IS_EQUAL_TO_FLOAT = CallMatcher.instanceCall(ABSTRACT_FLOAT_ASSERT_CLASSNAME, MethodNames.IS_EQUAL_TO)
            .parameterTypes("float")!!
        val IS_EQUAL_TO_DOUBLE = CallMatcher.instanceCall(ABSTRACT_DOUBLE_ASSERT_CLASSNAME, MethodNames.IS_EQUAL_TO)
            .parameterTypes("double")!!
        val IS_EQUAL_TO_BOOLEAN = CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, MethodNames.IS_EQUAL_TO)
            .parameterTypes("boolean")!!

        val IS_NOT_EQUAL_TO_OBJECT = CallMatcher.instanceCall(ASSERT_INTERFACE, MethodNames.IS_NOT_EQUAL_TO)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
        val IS_NOT_EQUAL_TO_BOOLEAN = CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, MethodNames.IS_NOT_EQUAL_TO)
            .parameterTypes("boolean")!!
        val IS_NOT_EQUAL_TO_INT = CallMatcher.instanceCall(ABSTRACT_INTEGER_ASSERT_CLASSNAME, MethodNames.IS_NOT_EQUAL_TO)
            .parameterTypes("int")!!
        val IS_NOT_EQUAL_TO_LONG = CallMatcher.instanceCall(ABSTRACT_LONG_ASSERT_CLASSNAME, MethodNames.IS_NOT_EQUAL_TO)
            .parameterTypes("long")!!
        val IS_NOT_EQUAL_TO_FLOAT = CallMatcher.instanceCall(ABSTRACT_FLOAT_ASSERT_CLASSNAME, MethodNames.IS_NOT_EQUAL_TO)
            .parameterTypes("float")!!
        val IS_NOT_EQUAL_TO_DOUBLE = CallMatcher.instanceCall(ABSTRACT_DOUBLE_ASSERT_CLASSNAME, MethodNames.IS_NOT_EQUAL_TO)
            .parameterTypes("double")!!

        val IS_SAME_AS_OBJECT = CallMatcher.instanceCall(ASSERT_INTERFACE, MethodNames.IS_SAME_AS)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
        val IS_NOT_SAME_AS_OBJECT = CallMatcher.instanceCall(ASSERT_INTERFACE, MethodNames.IS_NOT_SAME_AS)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!

        val IS_NULL = CallMatcher.instanceCall(ASSERT_INTERFACE, MethodNames.IS_NULL)
            .parameterCount(0)!!
        val IS_NOT_NULL = CallMatcher.instanceCall(ASSERT_INTERFACE, MethodNames.IS_NOT_NULL)
            .parameterCount(0)!!

        val IS_NOT_EMPTY = CallMatcher.instanceCall(ENUMERABLE_ASSERT_INTERFACE, MethodNames.IS_NOT_EMPTY)
            .parameterCount(0)!!
        val HAS_SIZE = CallMatcher.instanceCall(ENUMERABLE_ASSERT_INTERFACE, MethodNames.HAS_SIZE)
            .parameterTypes("int")!!

        val IS_GREATER_THAN_INT = CallMatcher.instanceCall(ABSTRACT_COMPARABLE_ASSERT_CLASSNAME, MethodNames.IS_GREATER_THAN)
            .parameterTypes("int")!!
        val IS_GREATER_THAN_OR_EQUAL_TO_INT = CallMatcher.instanceCall(ABSTRACT_COMPARABLE_ASSERT_CLASSNAME, MethodNames.IS_GREATER_THAN_OR_EQUAL_TO)
            .parameterTypes("int")!!

        val IS_LESS_THAN_INT = CallMatcher.instanceCall(ABSTRACT_COMPARABLE_ASSERT_CLASSNAME, MethodNames.IS_LESS_THAN)
            .parameterTypes("int")!!
        val IS_LESS_THAN_OR_EQUAL_TO_INT = CallMatcher.instanceCall(ABSTRACT_COMPARABLE_ASSERT_CLASSNAME, MethodNames.IS_LESS_THAN_OR_EQUAL_TO)
            .parameterTypes("int")!!

        val IS_ZERO = CallMatcher.instanceCall(ABSTRACT_INTEGER_ASSERT_CLASSNAME, MethodNames.IS_ZERO)
            .parameterCount(0)!!
        val IS_NOT_ZERO = CallMatcher.instanceCall(ABSTRACT_INTEGER_ASSERT_CLASSNAME, MethodNames.IS_NOT_ZERO)
            .parameterCount(0)!!

        val IS_TRUE = CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, MethodNames.IS_TRUE)
            .parameterCount(0)!!
        val IS_FALSE = CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, MethodNames.IS_FALSE)
            .parameterCount(0)!!

        val COLLECTION_SIZE = CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_COLLECTION, "size")
            .parameterCount(0)!!
        val MAP_SIZE = CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_MAP, "size")
            .parameterCount(0)!!
        val CHAR_SEQUENCE_LENGTH = CallMatcher.instanceCall("java.lang.CharSequence", "length")
            .parameterCount(0)!!
        val OBJECT_EQUALS = CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_OBJECT, "equals")
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!

        val OPTIONAL_GET = CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "get")
            .parameterCount(0)!!
        val OPTIONAL_IS_PRESENT = CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "isPresent")
            .parameterCount(0)!!
        val OPTIONAL_OR_ELSE = CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "orElse")
            .parameterCount(1)!!

        val OPTIONAL_OF = CallMatcher.staticCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "of")
            .parameterCount(1)!!
        val OPTIONAL_OF_NULLABLE = CallMatcher.staticCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "ofNullable")
            .parameterCount(1)!!
        val OPTIONAL_EMPTY = CallMatcher.staticCall(CommonClassNames.JAVA_UTIL_OPTIONAL, "empty")
            .parameterCount(0)!!

        val GUAVA_OPTIONAL_GET = CallMatcher.instanceCall(GUAVA_OPTIONAL_CLASSNAME, "get")
            .parameterCount(0)!!
        val GUAVA_OPTIONAL_IS_PRESENT = CallMatcher.instanceCall(GUAVA_OPTIONAL_CLASSNAME, "isPresent")
            .parameterCount(0)!!
        val GUAVA_OPTIONAL_OR_NULL = CallMatcher.instanceCall(GUAVA_OPTIONAL_CLASSNAME, "orNull")
            .parameterCount(0)!!

        val GUAVA_OPTIONAL_OF = CallMatcher.staticCall(GUAVA_OPTIONAL_CLASSNAME, "of")
            .parameterCount(1)!!
        val GUAVA_OPTIONAL_FROM_NULLABLE = CallMatcher.staticCall(GUAVA_OPTIONAL_CLASSNAME, "fromNullable")
            .parameterCount(1)!!
        val GUAVA_OPTIONAL_ABSENT = CallMatcher.staticCall(GUAVA_OPTIONAL_CLASSNAME, "absent")
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

    protected fun getOriginalMethodName(expression: PsiMethodCallExpression) = expression.resolveMethod()?.name

    protected fun registerSimplifyMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        replacementMethod: String
    ) {
        val originalMethod = getOriginalMethodName(expression) ?: return
        val description = REPLACE_DESCRIPTION_TEMPLATE.format(originalMethod, replacementMethod)
        val message = SIMPLIFY_MESSAGE_TEMPLATE.format(originalMethod, replacementMethod)
        val quickFix = ReplaceSimpleMethodCallQuickFix(description, replacementMethod)
        val textRange = TextRange(expression.qualifierExpression.textLength, expression.textLength)
        holder.registerProblem(expression, textRange, message, quickFix)
    }

    protected fun registerMoveOutMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        oldActualExpression: PsiMethodCallExpression,
        replacementMethod: String,
        quickFixSupplier: (String, String) -> LocalQuickFix
    ) {
        val originalMethod = getOriginalMethodName(oldActualExpression) ?: return
        val description = MOVE_ACTUAL_EXPRESSION_DESCRIPTION_TEMPLATE.format(originalMethod, replacementMethod)
        val message = MOVING_OUT_MESSAGE_TEMPLATE.format(originalMethod)
        val quickfix = quickFixSupplier(description, replacementMethod)
        holder.registerProblem(expression, message, quickfix)
    }

    protected fun registerReplaceMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        oldExpectedCallExpression: PsiMethodCallExpression,
        replacementMethod: String,
        quickFixSupplier: (String, String) -> LocalQuickFix
    ) {
        registerConciseMethod(REPLACE_DESCRIPTION_TEMPLATE, holder, expression, oldExpectedCallExpression, replacementMethod, quickFixSupplier)
    }

    protected fun registerConciseMethod(
        descriptionTemplate: String,
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        oldExpectedCallExpression: PsiMethodCallExpression,
        replacementMethod: String,
        quickFixSupplier: (String, String) -> LocalQuickFix
    ) {
        val originalMethod = getOriginalMethodName(oldExpectedCallExpression) ?: return
        val description = descriptionTemplate.format(originalMethod, replacementMethod)
        val message = MORE_CONCISE_MESSAGE_TEMPLATE.format(replacementMethod, originalMethod)
        val quickfix = quickFixSupplier(description, replacementMethod)
        val textRange = TextRange(expression.qualifierExpression.textLength, expression.textLength)
        holder.registerProblem(expression, textRange, message, quickfix)
    }

    protected fun registerRemoveExpectedOutmostMethod(
        holder: ProblemsHolder,
        expression: PsiMethodCallExpression,
        oldExpectedCallExpression: PsiMethodCallExpression,
        replacementMethod: String,
        quickFixSupplier: (String, String) -> LocalQuickFix
    ) {
        registerConciseMethod(REMOVE_EXPECTED_OUTMOST_DESCRIPTION_TEMPLATE, holder, expression, oldExpectedCallExpression, replacementMethod, quickFixSupplier)
    }
}
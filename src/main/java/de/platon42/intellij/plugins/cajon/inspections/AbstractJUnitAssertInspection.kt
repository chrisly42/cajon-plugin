package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.annotations.NonNls

abstract class AbstractJUnitAssertInspection : AbstractBaseJavaLocalInspectionTool() {

    companion object {
        @NonNls
        const val JUNIT4_ASSERT_CLASSNAME = "org.junit.Assert"

        @NonNls
        const val JUNIT5_ASSERT_CLASSNAME = "org.junit.jupiter.api.Assertions"

        @NonNls
        const val JUNIT4_ASSUME_CLASSNAME = "org.junit.Assume"

        @NonNls
        const val JUNIT5_ASSUME_CLASSNAME = "org.junit.jupiter.api.Assumptions"

        @NonNls
        const val ASSERT_TRUE_METHOD = "assertTrue"

        @NonNls
        const val ASSERT_FALSE_METHOD = "assertFalse"

        @NonNls
        const val ASSERT_NULL_METHOD = "assertNull"

        @NonNls
        const val ASSERT_NOT_NULL_METHOD = "assertNotNull"

        @NonNls
        const val ASSERT_EQUALS_METHOD = "assertEquals"

        @NonNls
        const val ASSERT_NOT_EQUALS_METHOD = "assertNotEquals"

        @NonNls
        const val ASSERT_SAME_METHOD = "assertSame"

        @NonNls
        const val ASSERT_NOT_SAME_METHOD = "assertNotSame"

        @NonNls
        const val ASSERT_ARRAY_EQUALS_METHOD = "assertArrayEquals"

        @NonNls
        const val ASSUME_TRUE_METHOD = "assumeTrue"

        @NonNls
        const val ASSUME_FALSE_METHOD = "assumeFalse"

        @NonNls
        const val ASSUME_NOT_NULL_METHOD = "assumeNotNull"

        @NonNls
        const val ASSUME_NO_EXCEPTION = "assumeNoException"

    }

    override fun getGroupDisplayName(): String {
        return "AssertJ"
    }

    protected fun getOriginalMethodName(expression: PsiMethodCallExpression) = expression.resolveMethod()?.name
}
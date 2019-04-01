package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.annotations.NonNls

open class AbstractJUnitAssertInspection : AbstractBaseJavaLocalInspectionTool() {

    companion object {
        const val CONVERT_MESSAGE_TEMPLATE = "%s can be converted to AssertJ style"

        const val REPLACE_DESCRIPTION_TEMPLATE = "Replace %s with assertThat().%s"

        @NonNls
        const val JUNIT_ASSERT_CLASSNAME = "org.junit.Assert"

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
    }

    override fun getGroupDisplayName(): String {
        return "AssertJ"
    }

    protected fun getOriginalMethodName(expression: PsiMethodCallExpression) =
        expression.resolveMethod()?.name?.plus("()")
}
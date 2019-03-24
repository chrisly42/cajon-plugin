package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.psi.CommonClassNames
import com.siyeh.ig.callMatcher.CallMatcher

open class AbstractAssertJInspection : AbstractBaseJavaLocalInspectionTool() {

    companion object {
        const val ABSTRACT_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractAssert"
        const val ABSTRACT_BOOLEAN_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractBooleanAssert"
        const val ABSTRACT_STRING_ASSERT_CLASSNAME = "org.assertj.core.api.AbstractStringAssert"
        const val IS_EQUAL_TO = "isEqualTo"
        const val IS_NOT_EQUAL_TO = "isNotEqualTo"

        val IS_EQUAL_TO_OBJECT = CallMatcher.instanceCall(ABSTRACT_ASSERT_CLASSNAME, IS_EQUAL_TO)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
        val IS_NOT_EQUAL_TO_OBJECT = CallMatcher.instanceCall(ABSTRACT_ASSERT_CLASSNAME, IS_NOT_EQUAL_TO)
            .parameterTypes(CommonClassNames.JAVA_LANG_OBJECT)!!
        val IS_EQUAL_TO_BOOLEAN = CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, IS_EQUAL_TO)
            .parameterTypes("boolean")!!
        val IS_NOT_EQUAL_TO_BOOLEAN = CallMatcher.instanceCall(ABSTRACT_BOOLEAN_ASSERT_CLASSNAME, IS_NOT_EQUAL_TO)
            .parameterTypes("boolean")!!
    }

    override fun getGroupDisplayName(): String {
        return "AssertJ"
    }
}
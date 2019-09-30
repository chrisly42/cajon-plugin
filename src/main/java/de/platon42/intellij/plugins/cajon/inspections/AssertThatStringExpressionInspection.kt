package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.CommonClassNames
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiExpressionStatement
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.MethodNames

class AssertThatStringExpressionInspection : AbstractMoveOutInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a string specific expression"

        private val MAPPINGS = listOf(
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "isEmpty").parameterCount(0),
                MethodNames.IS_EMPTY, MethodNames.IS_NOT_EMPTY, expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.anyOf(
                    CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "equals").parameterCount(1),
                    CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "contentEquals").parameterCount(1)
                ),
                MethodNames.IS_EQUAL_TO, MethodNames.IS_NOT_EQUAL_TO, expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "equalsIgnoreCase").parameterTypes(CommonClassNames.JAVA_LANG_STRING),
                MethodNames.IS_EQUAL_TO_IC, MethodNames.IS_NOT_EQUAL_TO_IC, expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "contains").parameterCount(1),
                MethodNames.CONTAINS, MethodNames.DOES_NOT_CONTAIN, expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "startsWith").parameterTypes(CommonClassNames.JAVA_LANG_STRING),
                MethodNames.STARTS_WITH, MethodNames.DOES_NOT_START_WITH, expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "endsWith").parameterTypes(CommonClassNames.JAVA_LANG_STRING),
                MethodNames.ENDS_WITH, MethodNames.DOES_NOT_END_WITH, expectBoolean = true
            )
        )
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                createInspectionsForMappings(statement, holder, MAPPINGS)
            }
        }
    }
}
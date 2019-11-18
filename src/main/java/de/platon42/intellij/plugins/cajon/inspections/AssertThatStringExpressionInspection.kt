package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.calculateConstantValue
import de.platon42.intellij.plugins.cajon.firstArg

class AssertThatStringExpressionInspection : AbstractMoveOutInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a string specific expression"

        private val ARG_IS_ZERO_CONST: (PsiExpressionStatement, PsiMethodCallExpression) -> Boolean = { _, call -> call.firstArg.calculateConstantValue() == 0 }
        private val ARG_IS_MINUS_ONE_CONST: (PsiExpressionStatement, PsiMethodCallExpression) -> Boolean = { _, call -> call.firstArg.calculateConstantValue() == -1 }

        private val STRING_COMPARE_TO_IGNORE_CASE =
            CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "compareToIgnoreCase").parameterTypes(CommonClassNames.JAVA_LANG_STRING)
        private val STRING_INDEX_OF = CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "indexOf").parameterTypes(CommonClassNames.JAVA_LANG_STRING)
        private val STRING_TRIM = CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "trim").parameterCount(0)

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
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_STRING, "matches").parameterTypes(CommonClassNames.JAVA_LANG_STRING),
                "matches", "doesNotMatch", expectBoolean = true
            ),

            MoveOutMapping(
                STRING_COMPARE_TO_IGNORE_CASE,
                MethodNames.IS_EQUAL_TO_IC, expectedMatcher = IS_EQUAL_TO_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                STRING_COMPARE_TO_IGNORE_CASE,
                MethodNames.IS_EQUAL_TO_IC, expectedMatcher = IS_ZERO_INT, replaceFromOriginalMethod = true
            ),
            MoveOutMapping(
                STRING_COMPARE_TO_IGNORE_CASE,
                MethodNames.IS_NOT_EQUAL_TO_IC, expectedMatcher = IS_NOT_EQUAL_TO_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                STRING_COMPARE_TO_IGNORE_CASE,
                MethodNames.IS_NOT_EQUAL_TO_IC, expectedMatcher = IS_NOT_ZERO_INT, replaceFromOriginalMethod = true
            ),

            MoveOutMapping(
                STRING_INDEX_OF,
                MethodNames.STARTS_WITH, expectedMatcher = IS_EQUAL_TO_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                STRING_INDEX_OF,
                MethodNames.STARTS_WITH, expectedMatcher = IS_ZERO_INT, replaceFromOriginalMethod = true
            ),

            MoveOutMapping(
                STRING_INDEX_OF,
                MethodNames.DOES_NOT_START_WITH, expectedMatcher = IS_NOT_EQUAL_TO_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                STRING_INDEX_OF,
                MethodNames.DOES_NOT_START_WITH, expectedMatcher = IS_NOT_ZERO_INT, replaceFromOriginalMethod = true
            ),

            MoveOutMapping(
                STRING_INDEX_OF,
                MethodNames.CONTAINS, expectedMatcher = IS_NOT_NEGATIVE, replaceFromOriginalMethod = true
            ),
            MoveOutMapping(
                STRING_INDEX_OF,
                MethodNames.CONTAINS, expectedMatcher = IS_NOT_EQUAL_TO_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_MINUS_ONE_CONST
            ),
            MoveOutMapping(
                STRING_INDEX_OF,
                MethodNames.CONTAINS, expectedMatcher = IS_GREATER_THAN_OR_EQUAL_TO_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                STRING_INDEX_OF,
                MethodNames.CONTAINS, expectedMatcher = IS_GREATER_THAN_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_MINUS_ONE_CONST
            ),

            MoveOutMapping(
                STRING_INDEX_OF,
                MethodNames.DOES_NOT_CONTAIN, expectedMatcher = IS_EQUAL_TO_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_MINUS_ONE_CONST
            ),
            MoveOutMapping(
                STRING_INDEX_OF,
                MethodNames.DOES_NOT_CONTAIN, expectedMatcher = IS_NEGATIVE, replaceFromOriginalMethod = true
            ),
            MoveOutMapping(
                STRING_INDEX_OF,
                MethodNames.DOES_NOT_CONTAIN, expectedMatcher = IS_LESS_THAN_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                STRING_INDEX_OF,
                MethodNames.DOES_NOT_CONTAIN, expectedMatcher = IS_LESS_THAN_OR_EQUAL_TO_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_MINUS_ONE_CONST
            ),

            MoveOutMapping(
                STRING_TRIM,
                "isNotBlank", expectedMatcher = IS_NOT_EMPTY
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
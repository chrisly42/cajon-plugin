package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.MethodNames
import de.platon42.intellij.plugins.cajon.calculateConstantValue
import de.platon42.intellij.plugins.cajon.firstArg

class AssertThatComparableInspection : AbstractMoveOutInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a compareTo() expression"

        private val ARG_IS_ZERO_CONST: (PsiExpressionStatement, PsiMethodCallExpression) -> Boolean = { _, call -> call.firstArg.calculateConstantValue() == 0 }
        private val ARG_IS_PLUS_ONE_CONST: (PsiExpressionStatement, PsiMethodCallExpression) -> Boolean = { _, call -> call.firstArg.calculateConstantValue() == 1 }
        private val ARG_IS_MINUS_ONE_CONST: (PsiExpressionStatement, PsiMethodCallExpression) -> Boolean = { _, call -> call.firstArg.calculateConstantValue() == -1 }

        private val COMPARABLE_COMPARE_TO =
            CallMatcher.instanceCall(CommonClassNames.JAVA_LANG_COMPARABLE, "compareTo").parameterCount(1)

        private val MAPPINGS = listOf(
            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                "isEqualByComparingTo", expectedMatcher = IS_EQUAL_TO_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                "isEqualByComparingTo", expectedMatcher = IS_ZERO_INT, replaceFromOriginalMethod = true
            ),

            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                "isNotEqualByComparingTo", expectedMatcher = IS_NOT_EQUAL_TO_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                "isNotEqualByComparingTo", expectedMatcher = IS_NOT_ZERO_INT, replaceFromOriginalMethod = true
            ),

            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                MethodNames.IS_GREATER_THAN_OR_EQUAL_TO, expectedMatcher = IS_GREATER_THAN_OR_EQUAL_TO_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                MethodNames.IS_GREATER_THAN_OR_EQUAL_TO, expectedMatcher = CallMatcher.anyOf(IS_NOT_EQUAL_TO_INT, IS_GREATER_THAN_INT), replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_MINUS_ONE_CONST
            ),
            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                MethodNames.IS_GREATER_THAN_OR_EQUAL_TO, expectedMatcher = IS_NOT_NEGATIVE_INT, replaceFromOriginalMethod = true
            ),

            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                MethodNames.IS_GREATER_THAN, expectedMatcher = CallMatcher.anyOf(IS_EQUAL_TO_INT, IS_GREATER_THAN_OR_EQUAL_TO_INT), replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_PLUS_ONE_CONST
            ),
            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                MethodNames.IS_GREATER_THAN, expectedMatcher = IS_GREATER_THAN_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                MethodNames.IS_GREATER_THAN, expectedMatcher = CallMatcher.anyOf(IS_POSITIVE_INT, IS_ONE_INT), replaceFromOriginalMethod = true
            ),

            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                MethodNames.IS_LESS_THAN_OR_EQUAL_TO, expectedMatcher = IS_LESS_THAN_OR_EQUAL_TO_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                MethodNames.IS_LESS_THAN_OR_EQUAL_TO, expectedMatcher = CallMatcher.anyOf(IS_NOT_EQUAL_TO_INT, IS_LESS_THAN_INT), replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_PLUS_ONE_CONST
            ),
            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                MethodNames.IS_LESS_THAN_OR_EQUAL_TO, expectedMatcher = IS_NOT_POSITIVE_INT, replaceFromOriginalMethod = true
            ),

            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                MethodNames.IS_LESS_THAN, expectedMatcher = CallMatcher.anyOf(IS_EQUAL_TO_INT, IS_LESS_THAN_OR_EQUAL_TO_INT), replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_MINUS_ONE_CONST
            ),
            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                MethodNames.IS_LESS_THAN, expectedMatcher = IS_LESS_THAN_INT, replaceFromOriginalMethod = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                COMPARABLE_COMPARE_TO,
                MethodNames.IS_LESS_THAN, expectedMatcher = IS_NEGATIVE_INT, replaceFromOriginalMethod = true
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
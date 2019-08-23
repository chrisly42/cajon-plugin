package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.MoveOutMethodCallExpressionQuickFix

class AssertThatCollectionOrMapExpressionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a collection or map specific expression"

        private val MAP_GET_MATCHER = CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_MAP, "get").parameterCount(1)

        private val ANY_IS_EQUAL_TO_MATCHER = CallMatcher.anyOf(
            IS_EQUAL_TO_OBJECT,
            IS_EQUAL_TO_STRING,
            IS_EQUAL_TO_INT,
            IS_EQUAL_TO_LONG,
            IS_EQUAL_TO_FLOAT,
            IS_EQUAL_TO_DOUBLE,
            IS_EQUAL_TO_BOOLEAN
        )
        private val ANY_IS_NOT_EQUAL_TO_MATCHER = CallMatcher.anyOf(
            IS_NOT_EQUAL_TO_OBJECT,
            IS_NOT_EQUAL_TO_INT,
            IS_NOT_EQUAL_TO_LONG,
            IS_NOT_EQUAL_TO_FLOAT,
            IS_NOT_EQUAL_TO_DOUBLE,
            IS_NOT_EQUAL_TO_BOOLEAN
        )

        private val MAPPINGS = listOf(
            Mapping(
                CallMatcher.anyOf(
                    CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_COLLECTION, "isEmpty").parameterCount(0),
                    CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_MAP, "isEmpty").parameterCount(0)
                ),
                MethodNames.IS_EMPTY, MethodNames.IS_NOT_EMPTY
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_COLLECTION, "contains").parameterCount(1),
                MethodNames.CONTAINS, MethodNames.DOES_NOT_CONTAIN
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_COLLECTION, "containsAll").parameterCount(1),
                MethodNames.CONTAINS_ALL, null
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_MAP, "containsKey").parameterCount(1),
                MethodNames.CONTAINS_KEY, MethodNames.DOES_NOT_CONTAIN_KEY
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_MAP, "containsValue").parameterCount(1),
                MethodNames.CONTAINS_VALUE, MethodNames.DOES_NOT_CONTAIN_VALUE
            )
        )
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                if (!statement.hasAssertThat()) return
                val staticMethodCall = statement.findStaticMethodCall() ?: return

                val assertThatArgument = staticMethodCall.firstArg as? PsiMethodCallExpression ?: return
                val expectedCallExpression = statement.findOutmostMethodCall() ?: return
                if (MAP_GET_MATCHER.test(assertThatArgument)) {
                    val nullOrNotNull = expectedCallExpression.getAllTheSameNullNotNullConstants()
                    if (nullOrNotNull != null) {
                        val replacementMethod = nullOrNotNull.map("containsKey", "doesNotContainKey")
                        registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, replacementMethod) { desc, method ->
                            MoveOutMethodCallExpressionQuickFix(desc, method, useNullNonNull = true)
                        }
                    } else {
                        if (ANY_IS_EQUAL_TO_MATCHER.test(expectedCallExpression)) {
                            registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, "containsEntry") { desc, method ->
                                MoveOutMethodCallExpressionQuickFix(desc, method, keepExpectedAsSecondArgument = true)
                            }
                        } else if (ANY_IS_NOT_EQUAL_TO_MATCHER.test(expectedCallExpression)) {
                            registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, "doesNotContainEntry") { desc, method ->
                                MoveOutMethodCallExpressionQuickFix(desc, method, keepExpectedAsSecondArgument = true)
                            }
                        }
                    }
                } else {
                    if (!ASSERT_THAT_BOOLEAN.test(staticMethodCall)) return
                    val mapping = MAPPINGS.firstOrNull { it.callMatcher.test(assertThatArgument) } ?: return

                    val expectedResult = expectedCallExpression.getAllTheSameExpectedBooleanConstants() ?: return

                    val replacementMethod = if (expectedResult) mapping.replacementForTrue else mapping.replacementForFalse ?: return
                    registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, replacementMethod) { desc, method ->
                        MoveOutMethodCallExpressionQuickFix(desc, method)
                    }
                }
            }
        }
    }

    private class Mapping(
        val callMatcher: CallMatcher,
        val replacementForTrue: String,
        val replacementForFalse: String?
    )
}
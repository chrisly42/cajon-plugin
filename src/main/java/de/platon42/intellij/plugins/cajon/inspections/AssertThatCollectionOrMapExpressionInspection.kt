package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.MoveOutMethodCallExpressionQuickFix

class AssertThatCollectionOrMapExpressionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a collection or map specific expression"

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
                if (!statement.hasAssertThat()) {
                    return
                }
                val staticMethodCall = statement.findStaticMethodCall() ?: return
                if (!ASSERT_THAT_BOOLEAN.test(staticMethodCall)) {
                    return
                }
                val assertThatArgument = staticMethodCall.firstArg as? PsiMethodCallExpression ?: return
                val mapping = MAPPINGS.firstOrNull { it.callMatcher.test(assertThatArgument) } ?: return

                val expectedCallExpression = statement.findOutmostMethodCall() ?: return
                val expectedResult = expectedCallExpression.getAllTheSameExpectedBooleanConstants() ?: return

                val replacementMethod = if (expectedResult) mapping.replacementForTrue else mapping.replacementForFalse ?: return
                registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, replacementMethod, ::MoveOutMethodCallExpressionQuickFix)
            }
        }
    }

    private class Mapping(
        val callMatcher: CallMatcher,
        val replacementForTrue: String,
        val replacementForFalse: String?
    )
}
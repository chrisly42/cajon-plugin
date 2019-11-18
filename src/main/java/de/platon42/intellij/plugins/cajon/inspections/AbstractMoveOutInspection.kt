package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiExpressionStatement
import com.intellij.psi.PsiMethodCallExpression
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.MoveOutMethodCallExpressionQuickFix

abstract class AbstractMoveOutInspection : AbstractAssertJInspection() {

    protected fun createInspectionsForMappings(
        statement: PsiExpressionStatement,
        holder: ProblemsHolder,
        mappings: List<MoveOutMapping>
    ) {
        if (!statement.hasAssertThat()) return

        val staticMethodCall = statement.findStaticMethodCall() ?: return

        val assertThatArgument = staticMethodCall.getArgOrNull(0) as? PsiMethodCallExpression ?: return
        val expectedCallExpression = statement.findOutmostMethodCall() ?: return

        for (mapping in mappings.filter { it.callMatcher.test(assertThatArgument) }) {
            if (mapping.expectBoolean && ASSERT_THAT_BOOLEAN.test(staticMethodCall)) {
                val expectedBooleanResult = expectedCallExpression.getAllTheSameExpectedBooleanConstants() ?: continue
                if (mapping.additionalCondition?.invoke(statement, expectedCallExpression) == false) continue
                val replacementMethod = if (expectedBooleanResult) mapping.replacementForTrue else mapping.replacementForFalse ?: return
                registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, replacementMethod) { desc, method ->
                    MoveOutMethodCallExpressionQuickFix(desc, method)
                }
            } else if (mapping.expectNullNonNull != null) {
                val expectedNullNonNullResult = expectedCallExpression.getExpectedNullNonNullResult() ?: continue
                if (mapping.additionalCondition?.invoke(statement, expectedCallExpression) == false) continue
                val replacementMethod = if (expectedNullNonNullResult xor mapping.expectNullNonNull) mapping.replacementForTrue else mapping.replacementForFalse ?: continue
                registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, replacementMethod) { desc, method ->
                    MoveOutMethodCallExpressionQuickFix(desc, method, useNullNonNull = true)
                }
            } else if (mapping.expectedMatcher?.test(expectedCallExpression) == true) {
                if (mapping.additionalCondition?.invoke(statement, expectedCallExpression) == false) continue
                registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, mapping.replacementForTrue) { desc, method ->
                    MoveOutMethodCallExpressionQuickFix(
                        desc, method,
                        replaceOnlyThisMethod = mapping.expectedMatcher,
                        replaceFromOriginalMethod = mapping.replaceFromOriginalMethod,
                        noExpectedExpression = mapping.noExpectedExpression
                    )
                }
            }
        }
    }

    class MoveOutMapping(
        val callMatcher: CallMatcher,
        val replacementForTrue: String,
        val replacementForFalse: String? = null,
        val expectBoolean: Boolean = false,
        val expectNullNonNull: Boolean? = null,
        val expectedMatcher: CallMatcher? = null,
        val replaceFromOriginalMethod: Boolean = false,
        val noExpectedExpression: Boolean = false,
        val additionalCondition: ((PsiExpressionStatement, PsiMethodCallExpression) -> Boolean)? = null
    )
}
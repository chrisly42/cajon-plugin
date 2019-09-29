package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.MoveOutMethodCallExpressionQuickFix


class AssertThatFileExpressionInspection : AbstractAssertJInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a file specific expression"

        private val MAPPINGS = listOf(
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "canRead"),
                "canRead", expectBoolean = true
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "canWrite"),
                "canWrite", expectBoolean = true
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "exists"),
                "exists", "doesNotExist", expectBoolean = true
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "isAbsolute"),
                "isAbsolute", "isRelative", expectBoolean = true
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "isDirectory"),
                "isDirectory", expectBoolean = true
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "isFile"),
                "isFile", expectBoolean = true
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "getName"),
                "hasName",
                expectedMatcher = CallMatcher.anyOf(IS_EQUAL_TO_OBJECT, IS_EQUAL_TO_STRING)
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "getParent", "getParentFile"),
                "hasNoParent", expectNullNonNull = true
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "getParent"),
                "hasParent",
                expectedMatcher = CallMatcher.anyOf(IS_EQUAL_TO_OBJECT, IS_EQUAL_TO_STRING)
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "getParentFile"),
                "hasParent",
                expectedMatcher = IS_EQUAL_TO_OBJECT
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "list", "listFiles"),
                "isEmptyDirectory",
                expectedMatcher = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_OBJECT_ARRAY_ASSERT_CLASSNAME, MethodNames.IS_EMPTY)
                    .parameterCount(0)!!
            ),
            Mapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "list", "listFiles"),
                "isNotEmptyDirectory",
                expectedMatcher = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_OBJECT_ARRAY_ASSERT_CLASSNAME, MethodNames.IS_NOT_EMPTY)
                    .parameterCount(0)!!
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

                val assertThatArgument = staticMethodCall.getArgOrNull(0) as? PsiMethodCallExpression ?: return
                val expectedCallExpression = statement.findOutmostMethodCall() ?: return

                for (mapping in MAPPINGS.filter { it.callMatcher.test(assertThatArgument) }) {
                    if (mapping.expectBoolean && ASSERT_THAT_BOOLEAN.test(staticMethodCall)) {
                        val expectedBooleanResult = expectedCallExpression.getAllTheSameExpectedBooleanConstants() ?: continue
                        val replacementMethod = if (expectedBooleanResult) mapping.replacementForTrue else mapping.replacementForFalse ?: return
                        registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, replacementMethod) { desc, method ->
                            MoveOutMethodCallExpressionQuickFix(desc, method)
                        }
                    } else if (mapping.expectNullNonNull != null) {
                        val expectedNullNonNullResult = expectedCallExpression.getExpectedNullNonNullResult() ?: continue
                        val replacementMethod = if (expectedNullNonNullResult xor mapping.expectNullNonNull) mapping.replacementForTrue else mapping.replacementForFalse ?: continue
                        registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, replacementMethod) { desc, method ->
                            MoveOutMethodCallExpressionQuickFix(desc, method, useNullNonNull = true)
                        }
                    } else if (mapping.expectedMatcher?.test(expectedCallExpression) == true) {
                        registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, mapping.replacementForTrue) { desc, method ->
                            MoveOutMethodCallExpressionQuickFix(desc, method, replaceOnlyThisMethod = mapping.expectedMatcher)
                        }
                    }
                }
            }
        }
    }

    private class Mapping(
        val callMatcher: CallMatcher,
        val replacementForTrue: String,
        val replacementForFalse: String? = null,
        val expectBoolean: Boolean = false,
        val expectNullNonNull: Boolean? = null,
        val expectedMatcher: CallMatcher? = null
    )
}
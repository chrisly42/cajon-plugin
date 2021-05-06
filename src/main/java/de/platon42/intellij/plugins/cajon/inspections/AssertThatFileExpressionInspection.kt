package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*


class AssertThatFileExpressionInspection : AbstractMoveOutInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a file specific expression"

        private val ARG_IS_ZERO_CONST: (PsiExpressionStatement, PsiMethodCallExpression) -> Boolean = { _, call -> call.firstArg.calculateConstantValue() == 0 }
        private val ARG_IS_NOT_ZERO_CONST: (PsiExpressionStatement, PsiMethodCallExpression) -> Boolean = { _, call ->
            val constant =
                call.firstArg.calculateConstantValue()
            (constant != null) && (constant != 0)
        }

        private val MAPPINGS = listOf(
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "canRead").parameterCount(0),
                "canRead", expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "canWrite").parameterCount(0),
                "canWrite", expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "exists").parameterCount(0),
                "exists", "doesNotExist", expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "isAbsolute").parameterCount(0),
                "isAbsolute", "isRelative", expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "isDirectory").parameterCount(0),
                "isDirectory", expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "isFile").parameterCount(0),
                "isFile", expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "getName").parameterCount(0),
                "hasName",
                expectedMatcher = CallMatcher.anyOf(IS_EQUAL_TO_OBJECT, IS_EQUAL_TO_STRING)
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "getParent", "getParentFile").parameterCount(0),
                "hasNoParent", expectNullNonNull = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "getParent").parameterCount(0),
                "hasParent",
                expectedMatcher = CallMatcher.anyOf(IS_EQUAL_TO_OBJECT, IS_EQUAL_TO_STRING)
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "getParentFile").parameterCount(0),
                "hasParent",
                expectedMatcher = IS_EQUAL_TO_OBJECT
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "list", "listFiles").parameterCount(0),
                "isEmptyDirectory",
                expectedMatcher = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_OBJECT_ARRAY_ASSERT_CLASSNAME, MethodNames.IS_EMPTY)
                    .parameterCount(0)
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "list", "listFiles").parameterCount(0),
                "isNotEmptyDirectory",
                expectedMatcher = CallMatcher.instanceCall(AssertJClassNames.ABSTRACT_OBJECT_ARRAY_ASSERT_CLASSNAME, MethodNames.IS_NOT_EMPTY)
                    .parameterCount(0)
            )
        )

        private val MAPPINGS_SINCE_ASSERTJ_3_14_0 = listOf(
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "length").parameterCount(0),
                MethodNames.IS_EMPTY, expectedMatcher = IS_ZERO_LONG, noExpectedExpression = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "length").parameterCount(0),
                MethodNames.IS_EMPTY, expectedMatcher = IS_EQUAL_TO_LONG, noExpectedExpression = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "length").parameterCount(0),
                MethodNames.IS_NOT_EMPTY, expectedMatcher = IS_NOT_ZERO_LONG, noExpectedExpression = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "length").parameterCount(0),
                MethodNames.IS_NOT_EMPTY, expectedMatcher = IS_NOT_EQUAL_TO_LONG, noExpectedExpression = true,
                additionalCondition = ARG_IS_ZERO_CONST
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_IO_FILE, "length").parameterCount(0),
                MethodNames.HAS_SIZE, expectedMatcher = IS_EQUAL_TO_LONG,
                additionalCondition = ARG_IS_NOT_ZERO_CONST
            )
        )
    }

    override fun getDisplayName() = DISPLAY_NAME

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                createInspectionsForMappings(statement, holder, MAPPINGS)
                if (hasAssertJMethod(statement, AssertJClassNames.ABSTRACT_FILE_ASSERT_CLASSNAME, MethodNames.HAS_SIZE)) {
                    createInspectionsForMappings(statement, holder, MAPPINGS_SINCE_ASSERTJ_3_14_0)
                }
            }
        }
    }
}
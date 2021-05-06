package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiExpressionStatement
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.MethodNames


class AssertThatPathExpressionInspection : AbstractMoveOutInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a path specific expression"
        private const val JAVA_NIO_PATH = "java.nio.file.Path"

        private val MAPPINGS = listOf(
            MoveOutMapping(
                CallMatcher.instanceCall(JAVA_NIO_PATH, "isAbsolute").parameterCount(0),
                "isAbsolute", "isRelative", expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(JAVA_NIO_PATH, MethodNames.STARTS_WITH).parameterTypes(JAVA_NIO_PATH),
                "startsWithRaw", expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(JAVA_NIO_PATH, MethodNames.ENDS_WITH).parameterTypes(JAVA_NIO_PATH),
                "endsWithRaw", expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(JAVA_NIO_PATH, "getParent").parameterCount(0),
                "hasParentRaw",
                expectedMatcher = IS_EQUAL_TO_OBJECT
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(JAVA_NIO_PATH, "getParent").parameterCount(0),
                "hasNoParentRaw", expectNullNonNull = true
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
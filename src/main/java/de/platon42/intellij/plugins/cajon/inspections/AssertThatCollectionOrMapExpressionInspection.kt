package de.platon42.intellij.plugins.cajon.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.ui.ComboBox
import com.intellij.psi.*
import com.intellij.util.ui.FormBuilder
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*
import de.platon42.intellij.plugins.cajon.quickfixes.MoveOutMethodCallExpressionQuickFix
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel


class AssertThatCollectionOrMapExpressionInspection : AbstractMoveOutInspection() {

    companion object {
        private const val DISPLAY_NAME = "Asserting a collection or map specific expression"
        private const val DEFAULT_MAP_VALUES_NEVER_NULL = 2

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
            MoveOutMapping(
                CallMatcher.anyOf(
                    CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_COLLECTION, MethodNames.IS_EMPTY).parameterCount(0),
                    CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_MAP, MethodNames.IS_EMPTY).parameterCount(0)
                ),
                MethodNames.IS_EMPTY, MethodNames.IS_NOT_EMPTY, expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_COLLECTION, MethodNames.CONTAINS).parameterCount(1),
                MethodNames.CONTAINS, MethodNames.DOES_NOT_CONTAIN, expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_COLLECTION, MethodNames.CONTAINS_ALL).parameterCount(1),
                MethodNames.CONTAINS_ALL, expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_MAP, MethodNames.CONTAINS_KEY).parameterCount(1),
                MethodNames.CONTAINS_KEY, MethodNames.DOES_NOT_CONTAIN_KEY, expectBoolean = true
            ),
            MoveOutMapping(
                CallMatcher.instanceCall(CommonClassNames.JAVA_UTIL_MAP, MethodNames.CONTAINS_VALUE).parameterCount(1),
                MethodNames.CONTAINS_VALUE, MethodNames.DOES_NOT_CONTAIN_VALUE, expectBoolean = true
            )
        )
    }

    override fun getDisplayName() = DISPLAY_NAME

    @JvmField
    var behaviorForMapValueEqualsNull: Int = DEFAULT_MAP_VALUES_NEVER_NULL

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                if (!statement.hasAssertThat()) return
                val staticMethodCall = statement.findStaticMethodCall() ?: return

                val assertThatArgument = staticMethodCall.getArgOrNull(0) as? PsiMethodCallExpression ?: return
                if (MAP_GET_MATCHER.test(assertThatArgument)) {
                    val expectedCallExpression = statement.findOutmostMethodCall() ?: return
                    val nullOrNotNull = expectedCallExpression.getAllTheSameNullNotNullConstants()
                    if (nullOrNotNull == true) {
                        registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, MethodNames.CONTAINS_KEY) { desc, method ->
                            MoveOutMethodCallExpressionQuickFix(desc, method, useNullNonNull = true)
                        }
                    } else if (nullOrNotNull == false) {
                        when (behaviorForMapValueEqualsNull) {
                            1 -> // warning only
                                registerMoveOutMethod(
                                    holder,
                                    expectedCallExpression,
                                    assertThatArgument,
                                    ""
                                ) { _ -> emptyList() }
                            2 -> // as doesNotContainKey(key)
                                registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, MethodNames.DOES_NOT_CONTAIN_KEY) { desc, method ->
                                    MoveOutMethodCallExpressionQuickFix(desc, method, useNullNonNull = true)
                                }
                            3 -> // as containsEntry(key, null)
                                registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, MethodNames.CONTAINS_ENTRY) { desc, method ->
                                    MoveOutMethodCallExpressionQuickFix(desc, method, keepExpectedAsSecondArgument = true, useNullNonNull = true)
                                }
                            4 -> // both
                                registerMoveOutMethod(
                                    holder,
                                    expectedCallExpression,
                                    assertThatArgument,
                                    MethodNames.DOES_NOT_CONTAIN_KEY + "/" + MethodNames.CONTAINS_ENTRY
                                ) { _ ->
                                    listOf(
                                        MoveOutMethodCallExpressionQuickFix(
                                            "Remove get() of actual expression and use assertThat().doesNotContainKey() instead (regular map)",
                                            MethodNames.DOES_NOT_CONTAIN_KEY,
                                            useNullNonNull = true
                                        ),
                                        MoveOutMethodCallExpressionQuickFix(
                                            "Remove get() of actual expression and use assertThat().containsEntry(key, null) instead (degenerated map)",
                                            MethodNames.CONTAINS_ENTRY,
                                            keepExpectedAsSecondArgument = true,
                                            useNullNonNull = true
                                        )
                                    )
                                }
                        }
                    } else {
                        if (ANY_IS_EQUAL_TO_MATCHER.test(expectedCallExpression)) {
                            registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, MethodNames.CONTAINS_ENTRY) { desc, method ->
                                MoveOutMethodCallExpressionQuickFix(desc, method, keepExpectedAsSecondArgument = true)
                            }
                        } else if (ANY_IS_NOT_EQUAL_TO_MATCHER.test(expectedCallExpression)) {
                            registerMoveOutMethod(holder, expectedCallExpression, assertThatArgument, MethodNames.DOES_NOT_CONTAIN_ENTRY) { desc, method ->
                                MoveOutMethodCallExpressionQuickFix(desc, method, keepExpectedAsSecondArgument = true)
                            }
                        }
                    }
                } else {
                    createInspectionsForMappings(statement, holder, MAPPINGS)
                }
            }
        }
    }

    override fun createOptionsPanel(): JComponent {
        val comboBox = ComboBox(
            arrayOf("ignore", "warning only, no fixes", "as doesNotContainKey(key)", "as containsEntry(key, null)", "both choices")
        )
        comboBox.selectedIndex = behaviorForMapValueEqualsNull
        comboBox.addActionListener { behaviorForMapValueEqualsNull = comboBox.selectedIndex }
        val panel = JPanel(BorderLayout())
        panel.add(
            FormBuilder.createFormBuilder().addLabeledComponent("Fix get() on maps expecting null values:", comboBox).panel,
            BorderLayout.NORTH
        )
        return panel
    }
}
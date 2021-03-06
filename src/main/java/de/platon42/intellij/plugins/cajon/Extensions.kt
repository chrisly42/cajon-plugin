package de.platon42.intellij.plugins.cajon

import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.inspections.AbstractAssertJInspection

val PsiMethodCallExpression.qualifierExpression: PsiExpression get() = methodExpression.qualifierExpression!!
val PsiMethodCallExpression.firstArg: PsiExpression get() = getArg(0)

fun PsiElement.hasAssertThat(): Boolean {
    val elementText = text
    return elementText.startsWith("${MethodNames.ASSERT_THAT}(") || elementText.contains(".${MethodNames.ASSERT_THAT}(")
}

fun PsiMethodCallExpression.replaceQualifier(qualifier: PsiElement) {
    qualifierExpression.replace(qualifier)
}

fun PsiMethodCallExpression.replaceQualifierFromMethodCall(oldMethodCall: PsiMethodCallExpression) {
    qualifierExpression.replace(oldMethodCall.qualifierExpression)
}

fun PsiElement.findOutmostMethodCall(): PsiMethodCallExpression? {
    val statement = PsiTreeUtil.getParentOfType(this, PsiStatement::class.java, false) ?: return null
    return PsiTreeUtil.findChildOfType(statement, PsiMethodCallExpression::class.java)
}

fun PsiElement.findStaticMethodCall(): PsiMethodCallExpression? {
    var elem: PsiElement? = this
    while (elem != null) {
        if ((elem is PsiMethodCallExpression) && (elem.resolveMethod()?.hasModifierProperty(PsiModifier.STATIC) == true)) {
            return elem
        }
        elem = elem.firstChild
    }
    return null
}

fun PsiElement.gatherAssertionCalls(): List<PsiMethodCallExpression> {
    val assertThatMethodCall = findStaticMethodCall() ?: return emptyList()
    return assertThatMethodCall.collectMethodCallsUpToStatement()
        .filterNot(NOT_ACTUAL_ASSERTIONS::test)
        .toList()
}

fun PsiMethodCallExpression.collectMethodCallsUpToStatement(): Sequence<PsiMethodCallExpression> {
    return generateSequence(this) { PsiTreeUtil.getParentOfType(it, PsiMethodCallExpression::class.java, true, PsiStatement::class.java) }
}

fun PsiMethodCallExpression.findFluentCallTo(matcher: CallMatcher): PsiMethodCallExpression? {
    return collectMethodCallsUpToStatement().find { matcher.test(it) }
}

fun PsiMethodCallExpression.getArg(n: Int): PsiExpression = PsiUtil.skipParenthesizedExprDown(argumentList.expressions[n])!!

fun PsiMethodCallExpression.getArgOrNull(n: Int): PsiExpression? = argumentList.expressions.getOrNull(n)

fun <T> Boolean.map(forTrue: T, forFalse: T) = if (this) forTrue else forFalse

fun PsiMethod.addAsStaticImport(context: PsiElement, vararg allowedClashes: String) {
    val factory = JavaPsiFacade.getElementFactory(context.project)
    val methodName = name
    val containingClass = containingClass ?: return
    val importList = (context.containingFile as PsiJavaFile).importList ?: return
    val notImportedStatically = importList.importStaticStatements.none {
        val targetClass = it.resolveTargetClass() ?: return@none false
        ((it.referenceName == methodName) && !allowedClashes.contains(targetClass.qualifiedName))
                || (it.isOnDemand && (targetClass == containingClass))
    }
    if (notImportedStatically) {
        importList.add(factory.createImportStaticStatement(containingClass, methodName))
    }
}

fun PsiElement.shortenAndReformat() {
    val codeStyleManager = JavaCodeStyleManager.getInstance(project)
    codeStyleManager.shortenClassReferences(this)
    CodeStyleManager.getInstance(project).reformat(this)
}

fun PsiMethodCallExpression.getExpectedBooleanResult(): Boolean? {
    val isTrue = AbstractAssertJInspection.IS_TRUE.test(this)
    val isFalse = AbstractAssertJInspection.IS_FALSE.test(this)
    if (isTrue || isFalse) {
        return isTrue
    } else {
        val isEqualTo = AbstractAssertJInspection.IS_EQUAL_TO_BOOLEAN.test(this) || AbstractAssertJInspection.IS_EQUAL_TO_OBJECT.test(this)
        val isNotEqualTo =
            AbstractAssertJInspection.IS_NOT_EQUAL_TO_BOOLEAN.test(this) || AbstractAssertJInspection.IS_NOT_EQUAL_TO_OBJECT.test(this)
        if (isEqualTo || isNotEqualTo) {
            val constValue = calculateConstantParameterValue(0) as? Boolean ?: return null
            return isNotEqualTo xor constValue
        }
    }
    return null
}

fun PsiMethodCallExpression.getExpectedNullNonNullResult(): Boolean? {
    val isNull = AbstractAssertJInspection.IS_NULL.test(this)
    val isNotNull = AbstractAssertJInspection.IS_NOT_NULL.test(this)
    if (isNull || isNotNull) {
        return isNotNull
    } else {
        val isEqualTo = CallMatcher.anyOf(AbstractAssertJInspection.IS_EQUAL_TO_OBJECT, AbstractAssertJInspection.IS_EQUAL_TO_STRING).test(this)
        val isNotEqualTo = AbstractAssertJInspection.IS_NOT_EQUAL_TO_OBJECT.test(this)
        if ((isEqualTo || isNotEqualTo) && firstArg.type == PsiType.NULL) {
            return isNotEqualTo
        }
    }
    return null
}

fun PsiMethodCallExpression.calculateConstantParameterValue(argIndex: Int): Any? {
    if (argIndex >= argumentList.expressions.size) return null
    return getArg(argIndex).calculateConstantValue()
}

fun PsiExpression.calculateConstantValue(): Any? {
    val constantEvaluationHelper = JavaPsiFacade.getInstance(project).constantEvaluationHelper
    val value = constantEvaluationHelper.computeConstantExpression(this)
    if (value == null) {
        val field = (this as? PsiReferenceExpression)?.resolve() as? PsiField
        if (field?.containingClass?.qualifiedName == CommonClassNames.JAVA_LANG_BOOLEAN) {
            return when (field.name) {
                "TRUE" -> true
                "FALSE" -> false
                else -> null
            }
        }
    }
    return value
}

fun PsiExpression.getAllTheSameExpectedBooleanConstants(): Boolean? {
    val assertThatMethodCall = findStaticMethodCall() ?: return null
    var lockedResult: Boolean? = null
    val methodsToView = generateSequence(assertThatMethodCall) { PsiTreeUtil.getParentOfType(it, PsiMethodCallExpression::class.java) }

    for (methodCall in methodsToView) {
        val expectedResult = methodCall.getExpectedBooleanResult()
        if (expectedResult != null) {
            if ((lockedResult != null) && (lockedResult != expectedResult)) return null
            lockedResult = expectedResult
        } else {
            val isNotConstant = CallMatcher.anyOf(
                EXTENSION_POINTS,
                MORE_EXTENSION_POINTS,
                AbstractAssertJInspection.IS_EQUAL_TO_BOOLEAN,
                AbstractAssertJInspection.IS_EQUAL_TO_OBJECT,
                AbstractAssertJInspection.IS_NOT_EQUAL_TO_BOOLEAN,
                AbstractAssertJInspection.IS_NOT_EQUAL_TO_OBJECT
            ).test(methodCall)
            if (isNotConstant) return null
        }
    }
    return lockedResult
}

fun PsiExpression.getAllTheSameNullNotNullConstants(): Boolean? {
    val assertThatMethodCall = findStaticMethodCall() ?: return null
    var lockedResult: Boolean? = null
    val methodsToView = generateSequence(assertThatMethodCall) { PsiTreeUtil.getParentOfType(it, PsiMethodCallExpression::class.java) }

    for (methodCall in methodsToView) {
        val expectedResult = methodCall.getExpectedNullNonNullResult()
        if (expectedResult != null) {
            if ((lockedResult != null) && (lockedResult != expectedResult)) return null
            lockedResult = expectedResult
        } else {
            val isNotConstant = CallMatcher.anyOf(
                EXTENSION_POINTS,
                MORE_EXTENSION_POINTS,
                AbstractAssertJInspection.IS_EQUAL_TO_OBJECT,
                AbstractAssertJInspection.IS_NOT_EQUAL_TO_OBJECT
            ).test(methodCall)
            if (isNotConstant) return null
        }
    }
    return lockedResult
}
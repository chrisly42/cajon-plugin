package de.platon42.intellij.plugins.cajon.references

import com.intellij.lang.jvm.JvmModifier
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.util.PropertyUtilBase
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiTypesUtil
import com.intellij.util.ArrayUtil
import com.intellij.util.ProcessingContext
import com.siyeh.ig.callMatcher.CallMatcher
import de.platon42.intellij.plugins.cajon.*

class ExtractorReferenceContributor : PsiReferenceContributor() {

    companion object {
        private val BY_NAME = CallMatcher.staticCall(AssertJClassNames.EXTRACTORS_CLASSNAME, "byName")
        private val RESULT_OF = CallMatcher.staticCall(AssertJClassNames.EXTRACTORS_CLASSNAME, "resultOf")
            .parameterTypes(CommonClassNames.JAVA_LANG_STRING)!!

        private val propertyOrFieldReferenceProvider = PropertyOrFieldReferenceProvider()
        private val iterablePropertyOrFieldReferenceProvider = IterablePropertyOrFieldReferenceProvider()
        private val iterableResultOfReferenceProvider = IterableResultOfReferenceProvider()

        private fun lookupFieldOrProperty(containingClass: PsiClass, path: String, startOffset: Int): List<Pair<TextRange, List<PsiElement>>> {
            val partName = path.substring(startOffset).substringBefore(".")
            val nextOffset = startOffset + partName.length + 1

            val matchedGetter = PropertyUtilBase.findPropertyGetter(containingClass, partName, false, true)
            val fieldResult = PropertyUtilBase.findPropertyField(containingClass, partName, false)
            val textRange = TextRange(startOffset + 1, nextOffset)
            val matchedBareMethod = containingClass.allMethods.find { (it.name == partName) && !it.hasModifier(JvmModifier.STATIC) }
            val targets = listOfNotNull<PsiElement>(fieldResult, matchedGetter, matchedBareMethod)
            if (targets.isNotEmpty()) {
                val results = listOf(textRange to targets)
                if (nextOffset >= path.length) {
                    return results
                }
                val nextClass = PsiTypesUtil.getPsiClass(matchedGetter?.returnType ?: fieldResult?.type) ?: return results
                return listOf(results, lookupFieldOrProperty(nextClass, path, nextOffset)).flatten()
            }
            return emptyList()
        }

        private fun lookupMethod(containingClass: PsiClass, methodName: String): List<Pair<TextRange, List<PsiElement>>>? {
            val matchedMethod = containingClass.allMethods.find { (it.name == methodName) && !it.hasModifier(JvmModifier.STATIC) } ?: return null
            val textRange = TextRange(1, methodName.length + 1)
            return listOf(textRange to listOf(matchedMethod))
        }

        private fun findActualType(element: PsiElement): PsiClassType? {
            val assertThatCall = element.findStaticMethodCall()
            return assertThatCall?.firstArg?.type as? PsiClassType
        }

        private fun findAndCreateReferences(element: PsiElement, finder: (PsiLiteralExpression) -> List<Pair<TextRange, List<PsiElement>>>?): Array<PsiReference> {
            val literal = element as PsiLiteralExpression
            val results = finder(literal) ?: return PsiReference.EMPTY_ARRAY

            return results.map { ExtractorReference(literal, it.first, it.second) }.toTypedArray()
        }
    }

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression::class.java), propertyOrFieldReferenceProvider)
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression::class.java), iterablePropertyOrFieldReferenceProvider)
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression::class.java), iterableResultOfReferenceProvider)
    }

    class ExtractorReference(literal: PsiLiteralExpression, range: TextRange, private val targets: List<PsiElement>) :
        PsiPolyVariantReferenceBase<PsiLiteralExpression>(literal, range, true) {

        // Do not remove due to compatiblity issue with IDEA <= 2018.2
        override fun getVariants(): Array<Any> {
            return ArrayUtil.EMPTY_OBJECT_ARRAY
        }

        override fun resolve(): PsiElement? {
            return multiResolve(false).map(ResolveResult::getElement).firstOrNull()
        }

        override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
            return PsiElementResolveResult.createResults(targets)
        }
    }

    class PropertyOrFieldReferenceProvider : PsiReferenceProvider() {

        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> = findAndCreateReferences(element, ::findReferences)

        fun findReferences(element: PsiLiteralExpression): List<Pair<TextRange, List<PsiElement>>>? {
            val literal = element.value as? String ?: return null
            var methodCallExpression = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression::class.java) ?: return null
            var isResultOf = false
            if (BY_NAME.test(methodCallExpression)) {
                methodCallExpression = PsiTreeUtil.getParentOfType(methodCallExpression, PsiMethodCallExpression::class.java) ?: return null
            } else if (RESULT_OF.test(methodCallExpression)) {
                methodCallExpression = PsiTreeUtil.getParentOfType(methodCallExpression, PsiMethodCallExpression::class.java) ?: return null
                isResultOf = true
            }
            if (!EXTRACTING_FROM_OBJECT.test(methodCallExpression)) {
                return emptyList()
            }
            val containingClass = PsiTypesUtil.getPsiClass(findActualType(methodCallExpression)) ?: return null
            return if (isResultOf) lookupMethod(containingClass, literal) else lookupFieldOrProperty(containingClass, literal, 0)
        }
    }

    class IterablePropertyOrFieldReferenceProvider : PsiReferenceProvider() {

        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> = findAndCreateReferences(element, ::findReferences)

        fun findReferences(element: PsiLiteralExpression): List<Pair<TextRange, List<PsiElement>>>? {
            val literal = element.value as? String ?: return null
            var methodCallExpression = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression::class.java) ?: return null
            var isResultOf = false
            if (BY_NAME.test(methodCallExpression)) {
                methodCallExpression = PsiTreeUtil.getParentOfType(methodCallExpression, PsiMethodCallExpression::class.java) ?: return null
            } else if (RESULT_OF.test(methodCallExpression)) {
                methodCallExpression = PsiTreeUtil.getParentOfType(methodCallExpression, PsiMethodCallExpression::class.java) ?: return null
                isResultOf = true
            }

            if (!CallMatcher.anyOf(EXTRACTING_FROM_ITERABLE, FLAT_EXTRACTING_FROM_ITERABLE).test(methodCallExpression)) return null

            val iterableType = findActualType(methodCallExpression) ?: return null
            val innerType = iterableType.resolveGenerics().substitutor.substitute(iterableType.parameters[0])
            val containingClass = PsiTypesUtil.getPsiClass(innerType) ?: return null
            return if (isResultOf) lookupMethod(containingClass, literal) else lookupFieldOrProperty(containingClass, literal, 0)
        }
    }

    class IterableResultOfReferenceProvider : PsiReferenceProvider() {

        override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> = findAndCreateReferences(element, ::findReferences)

        fun findReferences(element: PsiLiteralExpression): List<Pair<TextRange, List<PsiElement>>>? {
            val literal = element.value as? String ?: return null
            val methodCallExpression = PsiTreeUtil.getParentOfType(element, PsiMethodCallExpression::class.java) ?: return null
            if (!EXTRACTING_RESULT_OF_FROM_ITERABLE.test(methodCallExpression)) return null

            val iterableType = findActualType(methodCallExpression) ?: return null
            val innerType = iterableType.resolveGenerics().substitutor.substitute(iterableType.parameters[0])
            val containingClass = PsiTypesUtil.getPsiClass(innerType) ?: return null
            return lookupMethod(containingClass, literal)
        }
    }
}
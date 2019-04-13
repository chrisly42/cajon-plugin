package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project

class QuickFixWithPostfixDelegate(
    private val mainFix: LocalQuickFix,
    private val postfix: (Project, ProblemDescriptor) -> Unit
) : LocalQuickFix by mainFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        mainFix.applyFix(project, descriptor)
        postfix(project, descriptor)
    }
}
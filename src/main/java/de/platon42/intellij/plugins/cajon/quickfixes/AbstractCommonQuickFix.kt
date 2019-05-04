package de.platon42.intellij.plugins.cajon.quickfixes

import com.intellij.codeInspection.LocalQuickFix

abstract class AbstractCommonQuickFix(private val description: String) : LocalQuickFix {

    override fun getName() = description

    override fun getFamilyName() = description
}
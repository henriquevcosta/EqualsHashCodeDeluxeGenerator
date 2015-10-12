package pl.mjedynak.idea.plugins.factory

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import groovy.transform.TypeChecked
import pl.mjedynak.idea.plugins.model.EqualsAndHashCodeType
import pl.mjedynak.idea.plugins.wizard.GenerateEqualsHashCodeDeluxeWizard

@TypeChecked
class GenerateEqualsHashCodeDeluxeWizardFactory {

    GenerateEqualsHashCodeDeluxeWizard createWizard(Project project, PsiClass aClass, boolean needEquals, boolean needHashCode, boolean needToString, EqualsAndHashCodeType type) {
        new GenerateEqualsHashCodeDeluxeWizard(project, aClass, needEquals, needHashCode, needToString, type)
    }
}

package pl.mjedynak.idea.plugins.action

import com.intellij.codeInsight.generation.actions.BaseGenerateAction
import groovy.transform.CompileStatic
import org.picocontainer.MutablePicoContainer
import org.picocontainer.defaults.DefaultPicoContainer
import pl.mjedynak.idea.plugins.factory.GenerateEqualsHashCodeDeluxeWizardFactory
import pl.mjedynak.idea.plugins.generator.EqualsGenerator
import pl.mjedynak.idea.plugins.generator.EqualsMethodTextCreator
import pl.mjedynak.idea.plugins.generator.HashCodeGenerator
import pl.mjedynak.idea.plugins.generator.ToStringGenerator
import pl.mjedynak.idea.plugins.generator.ToStringMethodTextCreator
import pl.mjedynak.idea.plugins.psi.EqualsMethodFinder
import pl.mjedynak.idea.plugins.psi.HashCodeMethodFinder
import pl.mjedynak.idea.plugins.psi.ParentClassChecker
import pl.mjedynak.idea.plugins.psi.ToStringMethodFinder

@SuppressWarnings('UnnecessaryObjectReferences')
@CompileStatic
class GenerateEqualsHashCodeDeluxeAction extends BaseGenerateAction {

    private static MutablePicoContainer picoContainer = new DefaultPicoContainer()

    private static GenerateEqualsHashCodeDeluxeActionHandler handler

    static {
        picoContainer.registerComponentImplementation(EqualsMethodFinder)
        picoContainer.registerComponentImplementation(HashCodeMethodFinder)
        picoContainer.registerComponentImplementation(ToStringMethodFinder)
        picoContainer.registerComponentImplementation(ParentClassChecker)
        picoContainer.registerComponentImplementation(EqualsMethodTextCreator)
        picoContainer.registerComponentImplementation(ToStringMethodTextCreator)
        picoContainer.registerComponentImplementation(HashCodeGenerator)
        picoContainer.registerComponentImplementation(EqualsGenerator)
        picoContainer.registerComponentImplementation(ToStringGenerator)
        picoContainer.registerComponentImplementation(TypeChooser)
        picoContainer.registerComponentImplementation(GenerateEqualsHashCodeDeluxeWizardFactory)
        picoContainer.registerComponentImplementation(GenerateEqualsHashCodeDeluxeActionHandler)
        handler = (GenerateEqualsHashCodeDeluxeActionHandler) picoContainer.getComponentInstanceOfType(GenerateEqualsHashCodeDeluxeActionHandler)
    }

    protected GenerateEqualsHashCodeDeluxeAction() {
        super(handler)
    }
}

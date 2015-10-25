package pl.mjedynak.idea.plugins.generator

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import pl.mjedynak.idea.plugins.psi.ParentClassChecker
import pl.mjedynak.idea.plugins.psi.ToStringHelperMethodFinder

/**
 * //TODO
 * @author Henrique Costa (henrique.costa@feedzai.com)
 * @since 15.1.0 //TODO
 */
class ToStringHelperMethodTextGenerator {

    private ParentClassChecker parentClassChecker
    private ToStringHelperMethodFinder toStringHelperMethodFinder

    ToStringHelperMethodTextGenerator(ParentClassChecker parentClassChecker, ToStringHelperMethodFinder toStringHelperMethodFinder) {
        this.parentClassChecker = parentClassChecker
        this.toStringHelperMethodFinder = toStringHelperMethodFinder
    }

    String createMethodText(List<PsiField> psiFields, PsiClass psiClass) {
        if (parentClassChecker.hasClassWithOverriddenMethodInInheritanceHierarchy(toStringHelperMethodFinder, psiClass)) {
            return null;
        }
        StringBuilder methodText = new StringBuilder()
        methodText << '/** {@inheritDoc} */'
        methodText << '@Override public String toString() {'
        methodText << ' return MoreObjects.toStringHelper(this)\n'

        psiFields.each { PsiField field ->
            methodText << ".add(\"${field.name}\", this.${field.name})\n"
        }
        methodText << '.toString();}'
        methodText.toString()
    }
}

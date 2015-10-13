package pl.mjedynak.idea.plugins.generator

import com.intellij.psi.PsiField
import pl.mjedynak.idea.plugins.psi.ParentClassChecker
import pl.mjedynak.idea.plugins.psi.ToStringMethodFinder

/**
 * //TODO
 * @author Henrique Costa (henrique.costa@feedzai.com)
 * @since 1.0.0
 */
class ToStringMethodTextCreator {

    private ParentClassChecker parentClassChecker
    private ToStringMethodFinder toStringMethodFinder

    ToStringMethodTextCreator(ParentClassChecker parentClassChecker, ToStringMethodFinder toStringMethodFinder) {
        this.parentClassChecker = parentClassChecker
        this.toStringMethodFinder = toStringMethodFinder
    }

    String createMethodText(List<PsiField> psiFields) {
        StringBuilder methodText = new StringBuilder()
        methodText << '/** {@inheritDoc} */'
        methodText << '@Override public String toString() {'
        methodText << ' return MoreObjects.toStringHelper(this)\n'
//        if (parentClassChecker.hasClassWithOverriddenMethodInInheritanceHierarchy(equalsMethodFinder, psiClass)) {
//            methodText << ' if (!super.equals(obj)) {return false;}'
//        }

        psiFields.each { PsiField field ->
                methodText << ".add(\"${field.name}\", this.${field.name})\n"
        }
        methodText << '.toString();}'
        methodText.toString()
    }
}

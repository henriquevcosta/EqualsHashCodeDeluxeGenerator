package pl.mjedynak.idea.plugins.generator

import com.intellij.psi.PsiField
import pl.mjedynak.idea.plugins.psi.ParentClassChecker
import pl.mjedynak.idea.plugins.psi.ToStringMethodFinder

/**
 * //TODO
 * @author Henrique Costa (henrique.costa@feedzai.com)
 * @since 15.1.0 //TODO
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
        methodText << ' MoreObjects.toStringHelper(this)'
//        if (parentClassChecker.hasClassWithOverriddenMethodInInheritanceHierarchy(equalsMethodFinder, psiClass)) {
//            methodText << ' if (!super.equals(obj)) {return false;}'
//        }

        psiFields.each { PsiField field ->
                methodText << ".add(\"${field.name}\", this.${field.name})"
        }
        methodText << '.toString();}'
        methodText.toString()
    }
}

package pl.mjedynak.idea.plugins.generator

import com.intellij.pom.java.LanguageLevel
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import org.jetbrains.annotations.NotNull

/**
 * Generator for toString method.
 *
 * @author Henrique Costa (henrique.costa@feedzai.com)
 * @since 1.0.0
 */
class ToStringGenerator {

    private ToStringMethodTextCreator toStringMethodTextCreator

    ToStringGenerator(ToStringMethodTextCreator toStringMethodTextCreator) {
        this.toStringMethodTextCreator = toStringMethodTextCreator
    }

    PsiMethod toStringMethod(@NotNull List<PsiField> psiFields, PsiClass psiClass) {
        if (!psiFields.isEmpty()) {
            PsiElementFactory factory = getFactory(psiFields[0])
            String methodText = toStringMethodTextCreator.createMethodText(psiFields, psiClass)
            factory.createMethodFromText(methodText, null, LanguageLevel.JDK_1_6)
        }
    }

    private PsiElementFactory getFactory(PsiField psiField) {
        JavaPsiFacade.getInstance(psiField.project).elementFactory
    }
}

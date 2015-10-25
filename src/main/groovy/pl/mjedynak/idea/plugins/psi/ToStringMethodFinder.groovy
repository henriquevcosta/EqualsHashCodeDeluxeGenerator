package pl.mjedynak.idea.plugins.psi

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import groovy.transform.CompileStatic

/**
 * Helper class to determine if a superclass already overrides the toString method.
 *
 * @author Henrique Costa (henrique.costa@feedzai.com)
 * @since 1.0.0
 */
@CompileStatic
class ToStringMethodFinder implements MethodFinder {
    boolean hasMethod(PsiClass psiClass) {
        psiClass.findMethodsByName('toString', false).any { PsiMethod method ->
            isPublic(method) && isNotStatic(method) && hasNoParameters(method) && returnsString(method)
        }
    }

    private static boolean isNotStatic(PsiMethod method) {
        !method.hasModifierProperty(PsiModifier.STATIC)
    }

    private static boolean returnsString(PsiMethod method) {
        method.returnType.equalsToText('String')
    }

    private static boolean isPublic(PsiMethod method) {
        method.hasModifierProperty(PsiModifier.PUBLIC)
    }

    private static boolean hasNoParameters(PsiMethod method) {
        method.parameterList?.parameters?.size() == 0
    }
}

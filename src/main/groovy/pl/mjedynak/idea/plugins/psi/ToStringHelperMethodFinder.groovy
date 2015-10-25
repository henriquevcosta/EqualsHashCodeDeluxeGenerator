package pl.mjedynak.idea.plugins.psi

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import groovy.transform.CompileStatic

/**
 * Helper class to determine if a superclass already implements the toStringHelper method.
 *
 * @author Henrique Costa (henrique.costa@feedzai.com)
 * @since 1.0.0
 */
@CompileStatic
class ToStringHelperMethodFinder  implements MethodFinder {
    boolean hasMethod(PsiClass psiClass) {
        psiClass.findMethodsByName('toStringHelper', false).any { PsiMethod method ->
            isProtected(method) && isNotStatic(method) && hasNoParameters(method) && returnsStringHelper(method)
        }
    }

    private static boolean isNotStatic(PsiMethod method) {
        !method.hasModifierProperty(PsiModifier.STATIC)
    }

    private static boolean returnsStringHelper(PsiMethod method) {
        method.returnType.equalsToText('MoreObjects.ToStringHelper')
    }

    private static boolean isProtected(PsiMethod method) {
        method.hasModifierProperty(PsiModifier.PROTECTED)
    }

    private static boolean hasNoParameters(PsiMethod method) {
        method.parameterList?.parameters?.size() == 0
    }

}

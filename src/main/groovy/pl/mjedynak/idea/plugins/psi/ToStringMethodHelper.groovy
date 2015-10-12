package pl.mjedynak.idea.plugins.psi

import com.intellij.psi.PsiSubstitutor
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypeParameter
import com.intellij.psi.util.MethodSignature
import com.intellij.psi.util.MethodSignatureUtil

/**
 * //TODO
 * @author Henrique Costa (henrique.costa@feedzai.com)
 * @since 15.1.0 //TODO
 */
class ToStringMethodHelper {

    static MethodSignature toStringSignature() {
        MethodSignatureUtil.createMethodSignature('toString', PsiType.EMPTY_ARRAY, PsiTypeParameter.EMPTY_ARRAY, PsiSubstitutor.EMPTY)
    }
}

package pl.mjedynak.idea.plugins.action

import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiMethod
import com.intellij.util.IncorrectOperationException
import groovy.transform.CompileStatic

@CompileStatic
class DeleteExistingMethodsComputable implements Computable<Boolean> {
    PsiMethod equalsMethod
    PsiMethod hashCodeMethod
    PsiMethod toStringMethod

    DeleteExistingMethodsComputable(PsiMethod equalsMethod, PsiMethod hashCodeMethod, PsiMethod toStringMethod) {
        this.equalsMethod = equalsMethod
        this.hashCodeMethod = hashCodeMethod
        this.toStringMethod = toStringMethod
    }

    Boolean compute() {
        try {
            equalsMethod?.delete()
            hashCodeMethod?.delete()
            toStringMethod?.delete()
            true
        }
        catch (IncorrectOperationException e) {
            false
        }
    }
}

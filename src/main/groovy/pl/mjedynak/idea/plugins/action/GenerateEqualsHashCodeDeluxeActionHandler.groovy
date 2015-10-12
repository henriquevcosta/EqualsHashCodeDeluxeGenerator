package pl.mjedynak.idea.plugins.action

import com.intellij.codeInsight.CodeInsightBundle
import com.intellij.codeInsight.generation.ClassMember
import com.intellij.codeInsight.generation.GenerateEqualsHelper
import com.intellij.codeInsight.generation.GenerateMembersHandlerBase
import com.intellij.codeInsight.generation.GenerationInfo
import com.intellij.codeInsight.generation.OverrideImplementUtil
import com.intellij.codeInsight.generation.PsiElementClassMember
import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiAnonymousClass
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.IncorrectOperationException
import pl.mjedynak.idea.plugins.factory.GenerateEqualsHashCodeDeluxeWizardFactory
import pl.mjedynak.idea.plugins.generator.EqualsGenerator
import pl.mjedynak.idea.plugins.generator.HashCodeGenerator
import pl.mjedynak.idea.plugins.generator.ToStringGenerator
import pl.mjedynak.idea.plugins.model.EqualsAndHashCodeType
import pl.mjedynak.idea.plugins.psi.ToStringMethodHelper
import pl.mjedynak.idea.plugins.wizard.GenerateEqualsHashCodeDeluxeWizard

import static java.lang.String.format

class GenerateEqualsHashCodeDeluxeActionHandler extends GenerateMembersHandlerBase {

    static final String METHODS_DEFINED_FOR_ANONYMOUS_CLASS = 'Methods "boolean equals(Object)", "int hashCode()" or "String toString()" are already defined \nfor this anonymous class. Do you want to delete them and proceed?'
    static final String METHODS_DEFINED_FOR_CLASS = 'Methods "boolean equals(Object)", "int hashCode()" or "String toString()" are already defined\nfor class %s. Do you want to delete them and proceed?'
    static final String TITLE = 'generate.equals.and.hashcode.already.defined.title'

    static final PsiElementClassMember[] DUMMY_RESULT = new PsiElementClassMember[1] //cannot return empty array, but this result won't be used anyway
    static final String ONLY_STATIC_FIELDS_ERROR = 'No fields to include in equals/hashCode/toString have been found'

    HashCodeGenerator hashCodeGenerator
    EqualsGenerator equalsGenerator
    ToStringGenerator toStringGenerator
    TypeChooser typeChooser
    EqualsAndHashCodeType type
    GenerateEqualsHashCodeDeluxeWizardFactory factory

    PsiField[] equalsFields = null
    PsiField[] hashCodeFields = null
    PsiField[] toStringFields = null

    GenerateEqualsHashCodeDeluxeActionHandler(HashCodeGenerator hashCodeGenerator,
                                              EqualsGenerator equalsGenerator,
                                              ToStringGenerator toStringGenerator,
                                              GenerateEqualsHashCodeDeluxeWizardFactory factory,
                                              TypeChooser typeChooser) {
        super('')
        this.hashCodeGenerator = hashCodeGenerator
        this.equalsGenerator = equalsGenerator
        this.toStringGenerator = toStringGenerator
        this.typeChooser = typeChooser
        this.factory = factory
    }

    @Override
    protected List<? extends GenerationInfo> generateMemberPrototypes(PsiClass psiClass, ClassMember[] originalMembers) throws IncorrectOperationException {
        String hashCodeMethodName = type.hashCodeMethodName()
        PsiMethod hashCodeMethod = hashCodeGenerator.hashCodeMethod(hashCodeFields as List, psiClass, hashCodeMethodName)
        PsiMethod equalsMethod = equalsGenerator.equalsMethod(equalsFields as List, psiClass, type)
        PsiMethod toStringMethod = toStringGenerator.toStringMethod(toStringFields as List)

        OverrideImplementUtil.convert2GenerationInfos([hashCodeMethod, equalsMethod, toStringMethod])
    }

    @SuppressWarnings('ReturnsNullInsteadOfEmptyArray')
    @Override
    protected ClassMember[] chooseOriginalMembers(PsiClass aClass, Project project, Editor editor) {
        equalsFields = null
        hashCodeFields = null

        GlobalSearchScope scope = aClass.resolveScope
        PsiMethod equalsMethod = GenerateEqualsHelper.findMethod(aClass, GenerateEqualsHelper.getEqualsSignature(project, scope))
        PsiMethod hashCodeMethod = GenerateEqualsHelper.findMethod(aClass, GenerateEqualsHelper.hashCodeSignature)
        PsiMethod toStringMethod = GenerateEqualsHelper.findMethod(aClass, ToStringMethodHelper.toStringSignature())

        boolean methodsExist = equalsExist(equalsMethod) || hashCodeExists(hashCodeMethod) || toStringExists(toStringMethod)
        boolean needEquals = !equalsExist(equalsMethod)
        boolean needHashCode = !hashCodeExists(hashCodeMethod)
        boolean needToString = !toStringExists(toStringMethod)

        if (methodsExist) {
            String text = chooseText(aClass)
            if (shouldDeleteMethods(project, text) && methodsDeletedSuccessfully(equalsMethod, hashCodeMethod, toStringMethod)) {
                needEquals = needHashCode = true
            } else {
                return null
            }
        }
        if (hasOnlyStaticFields(aClass)) {
            showErrorMessage(editor)
            return null
        }

        GenerateEqualsHashCodeDeluxeWizard wizard = factory.createWizard(project, aClass, needEquals, needHashCode, needToString, typeChooser.chooseType(aClass))

        wizard.show()
        if (!wizard.isOK()) {
            return null
        }
        equalsFields = wizard.equalsFields
        hashCodeFields = wizard.hashCodeFields
        toStringFields = wizard.toStringFields
        type = wizard.type
        DUMMY_RESULT
    }

    private showErrorMessage(Editor editor) {
        HintManager.instance.showErrorHint(editor, ONLY_STATIC_FIELDS_ERROR)
    }

    private boolean hasOnlyStaticFields(PsiClass aClass) {
        boolean hasOnlyStaticFields = true
        for (PsiField field: aClass.fields) {
            if (!field.hasModifierProperty(PsiModifier.STATIC)) {
                hasOnlyStaticFields = false
                break
            }
        }
        hasOnlyStaticFields
    }

    private boolean methodsDeletedSuccessfully(PsiMethod equalsMethod, PsiMethod hashCodeMethod, PsiMethod toStringMethod) {
        Application application = ApplicationManager.application
        application.runWriteAction(new DeleteExistingMethodsComputable(equalsMethod, hashCodeMethod, toStringMethod))
    }

    private boolean hashCodeExists(PsiMethod hashCodeMethod) {
        hashCodeMethod != null
    }

    private boolean equalsExist(PsiMethod equalsMethod) {
        equalsMethod != null
    }

    private boolean toStringExists(PsiMethod toStringMethod) {
        toStringMethod != null
    }

    private boolean shouldDeleteMethods(Project project, String text) {
        Messages.showYesNoDialog(project, text, CodeInsightBundle.message(TITLE), Messages.questionIcon) == DialogWrapper.OK_EXIT_CODE
    }

    private String chooseText(PsiClass aClass) {
        (aClass instanceof PsiAnonymousClass) ? METHODS_DEFINED_FOR_ANONYMOUS_CLASS : format(METHODS_DEFINED_FOR_CLASS, aClass.name)
    }

    @Override
    protected void cleanup() {
        super.cleanup()
        equalsFields = null
        hashCodeFields = null
    }

    @Override
    protected ClassMember[] getAllOriginalMembers(PsiClass psiClass) {
        null
    }

    @Override
    protected GenerationInfo[] generateMemberPrototypes(PsiClass psiClass, ClassMember classMember) {
        null
    }

    //TODO
//    private MethodSignature toStringHelperSignature() {
//        return MethodSignatureUtil.createMethodSignature('toStringHelper', PsiType.EMPTY_ARRAY, PsiTypeParameter.EMPTY_ARRAY, PsiSubstitutor.EMPTY);
//    }
}

package io.github.maciejlagowski.jarmodifier.service;

import io.github.maciejlagowski.jarmodifier.enums.EBodyInsertion;
import io.github.maciejlagowski.jarmodifier.util.ClassMember;
import javassist.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemberService {

    private final JarService jarService = JarService.getInstance();
    private final ClassService classService = new ClassService();

    public CtMethod addMethod(ClassMember method) throws Exception {
        int modifiers = method.getModifiersValue();
        CtClass returnClass = classService.parseClass(method.getReturnType());
        String name = method.getName();
        CtClass[] parameters = classService.parseClasses(method.getParameters());
        CtClass[] exceptions = new CtClass[]{};
        String body = "{\n" + method.getBody() + "\n}";
        CtClass methodClass = method.getMotherClass();
        CtMethod ctMethod = CtNewMethod.make(modifiers, returnClass, name, parameters, exceptions, body, methodClass);
        defrostAndAddToList(methodClass);
        methodClass.addMethod(ctMethod);
        return ctMethod;
    }

    public CtMethod editMethod(CtMethod method, EBodyInsertion bodyInsertion, String methodBody) throws Exception {
        CtClass declaring = method.getDeclaringClass();
        defrostAndAddToList(declaring);
        switch (bodyInsertion) {
            case AFTER -> method.insertAfter(methodBody);
            case BEFORE -> method.insertBefore(methodBody);
            case OVERRIDE -> method.setBody(methodBody);
        }
        return method;
    }

    public List<ClassMember> getMembers(CtClass actualClass) {
        List<ClassMember> members = new ArrayList<>();
        members.addAll(Arrays.stream(actualClass.getDeclaredMethods()).map(ClassMember::new).toList());
        members.addAll(Arrays.stream(actualClass.getDeclaredConstructors()).map(ClassMember::new).toList());
        members.addAll(Arrays.stream(actualClass.getDeclaredFields()).map(ClassMember::new).toList());
        return members;
    }

    public CtField addField(ClassMember field) throws Exception {
        CtClass motherClass = field.getMotherClass();
        defrostAndAddToList(motherClass);
        String src = field.getModifier().toString().toLowerCase() + " ";
        if (field.isStatic()) {
            src += "static ";
        }
        src += field.getReturnType() + " " + field.getName() + ";";

        CtField ctField = CtField.make(src, motherClass);
        motherClass.addField(ctField);
        return ctField;
    }

    public CtClass removeMember(ClassMember member) throws NotFoundException {
        CtClass motherClass = member.getMotherClass();
        defrostAndAddToList(motherClass);
        switch (member.getType()) {
            case METHOD -> motherClass.removeMethod((CtMethod) member.getCtMember());
            case CONSTRUCTOR -> motherClass.removeConstructor((CtConstructor) member.getCtMember());
            case FIELD -> motherClass.removeField((CtField) member.getCtMember());
        }
        return motherClass;
    }

    private CtClass defrostAndAddToList(CtClass ctClass) {
        if (ctClass.isFrozen()) {
            ctClass.defrost();
        }
        jarService.addChangedClass(ctClass);
        return ctClass;
    }
}

package io.github.maciejlagowski.jarmodifier.util;

import io.github.maciejlagowski.jarmodifier.enums.EModifier;
import io.github.maciejlagowski.jarmodifier.enums.EType;
import javassist.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassMember {

    private CtMember ctMember;
    private EType type;

    private String name;
    private EModifier modifier;
    private boolean isStatic;
    private String returnType;

    private List<String> parameters;
    private String body;
    private CtClass motherClass;

    public ClassMember(CtMethod ctMethod) {
        this.ctMember = ctMethod;
        this.type = EType.METHOD;
        this.name = ctMethod.getName();
        setModifier(ctMethod.getModifiers());
    }

    public ClassMember(CtField ctField) {
        this.ctMember = ctField;
        this.type = EType.FIELD;
        this.name = ctField.getName();
        setModifier(ctField.getModifiers());
    }

    public ClassMember(CtConstructor ctConstructor) {
        this.ctMember = ctConstructor;
        this.type = EType.CONSTRUCTOR;
        this.name = ctConstructor.getName();
        setModifier(ctConstructor.getModifiers());
    }

    public int getModifiersValue() {
        int value = modifier.getValue();
        value += isStatic ? 8 : 0;
        return value;
    }

    @Override
    public String toString() {
        if (type.equals(EType.FIELD)) {
            CtField ctField = (CtField) ctMember;
            String fieldType = "";
            try {
                fieldType = ctField.getType().getName() + " ";
            } catch (Exception ignored) {
            }
            return type + ": " + fieldType + ctField.getName();
        } else {
            CtBehavior ctBehavior = (CtBehavior) ctMember;
            String attributes = ctBehavior.getLongName().replaceAll(".*\\(", "(");
            String returnType = "";
            if (type.equals(EType.METHOD)) {
                try {
                    returnType = ((CtMethod) ctBehavior).getReturnType().getName() + " ";
                } catch (Exception ignored) {
                }
            }
            return type + ": " + returnType + ctBehavior.getName() + attributes;
        }
    }

    public String getProperties() {
        String properties = this.toString().replaceAll(".*:", "");
        properties = "Type: " + type + "\n" + modifier.toString().toLowerCase() + (isStatic ? " static" : "") + properties;
        if (motherClass != null) {
            properties += "\nMother class: " + motherClass.getName();
        }
        return properties;
    }

    private void setModifier(int modifier) {
        List<EModifier> modifiers = EModifier.getModifiersFromInt(modifier);
        if (modifiers.contains(EModifier.STATIC)) {
            isStatic = true;
            modifiers.remove(EModifier.STATIC);
        }
        if (!modifiers.isEmpty()) {
            this.modifier = modifiers.get(0);
        }
    }
}

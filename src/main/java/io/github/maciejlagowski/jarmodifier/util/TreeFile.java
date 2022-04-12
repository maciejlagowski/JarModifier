package io.github.maciejlagowski.jarmodifier.util;

import io.github.maciejlagowski.jarmodifier.enums.EType;
import javassist.ClassPool;
import javassist.CtClass;
import lombok.Getter;

import static io.github.maciejlagowski.jarmodifier.enums.EType.*;

public class TreeFile {

    public final EType type;
    @Getter
    private final String name;
    @Getter
    private final String fullName;
    private CtClass ctClass;

    public TreeFile(String name, String fullName) {
        this.fullName = fullName;
        this.name = name.replaceAll(".*/", "");
        if (name.contains(".class")) {
            type = CLASS;
            try {
                ctClass = getCtClass();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (name.endsWith(".jar")) {
            type = JAR;
        } else if (!name.contains(".")) {
            type = DIRECTORY;
        } else {
            type = FILE;
        }
    }

    public TreeFile(CtClass ctClass) {
        this.fullName = ctClass.getName();
        this.name = ctClass.getSimpleName();
        this.type = CLASS;
        this.ctClass = ctClass;
    }

    public CtClass getCtClass() throws Exception {
        if (type.equals(CLASS)) {
            if (ctClass == null) {
                ctClass = ClassPool.getDefault().getCtClass(fullName);
            }
            return ctClass;
        } else {
            throw new Exception("Selected TreeFile is not a class");
        }
    }

    public String getProperties() {
        return "Type: " + type + "\nName: " + name + "\nFull path: " + fullName;
    }

    @Override
    public String toString() {
        return name + (type.equals(CLASS) ? ".class" : "");
    }
}

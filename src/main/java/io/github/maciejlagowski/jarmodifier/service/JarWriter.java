package io.github.maciejlagowski.jarmodifier.service;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

class JarWriter {

    public void saveJar(File pathToSave, JarFile originalJar, List<CtClass> changedClasses, List<CtClass> classesToRemove) throws Exception {
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(pathToSave));
        Enumeration<JarEntry> originalEntries = originalJar.entries();

        while(originalEntries.hasMoreElements()) {
            JarEntry entry = originalEntries.nextElement();
            CtClass classToRemove = findClassInList(entry, classesToRemove);
            CtClass classToChange = findClassInList(entry, changedClasses);

            if (classToRemove != null) {            // Class was removed
                changedClasses.remove(classToRemove);
                classesToRemove.remove(classToRemove);
            } else if (classToChange != null) {     // Class was changed
                jos.putNextEntry(new JarEntry(entry.getName()));
                classToChange.writeFile();
                InputStream is = new FileInputStream(entry.getName());
                write(jos, is);
                changedClasses.remove(classToChange);
            } else {                                // Class wasn't modified
                jos.putNextEntry(entry);
                InputStream is = originalJar.getInputStream(entry);
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    jos.write(buf, 0, len);

                }
            }
            jos.closeEntry();
        }
        createNewClasses(jos, changedClasses);
        jos.close();
        System.out.println("Jar saved successfully");
    }

    private CtClass findClassInList(JarEntry entry, List<CtClass> classes) {
        for (CtClass ctClass: classes) {
            if (entry.getName().equals(ctClass.getName().replace('.', '/') + ".class")) {
                return ctClass;
            }
        }
        return null;
    }

    private void write(JarOutputStream jos, InputStream is) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = (is.read(buf))) > 0) {
            jos.write(buf, 0, Math.min(len, buf.length));
        }
    }

    private void createNewClasses(JarOutputStream jos, List<CtClass> changedClasses) throws IOException, NotFoundException, CannotCompileException {
        for (CtClass ctClass : changedClasses) {
            jos.putNextEntry(new JarEntry(ctClass.getName().replace('.', '/') + ".class"));
            ctClass.writeFile();
            InputStream is = new FileInputStream(ctClass.getName().replace('.', '/') + ".class");
            write(jos, is);
        }
    }
}

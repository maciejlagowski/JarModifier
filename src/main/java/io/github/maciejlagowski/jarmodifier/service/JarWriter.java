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

    public void saveJar(File pathToSave, File pathToJar, List<CtClass> changedClasses, List<String> addedPackages, List<CtClass> classesToRemove) throws Exception {
        if (!pathToSave.getAbsolutePath().contains(".jar")) {
            pathToSave = new File(pathToSave.getAbsolutePath() + ".jar");
        }
        JarFile jarFile = new JarFile(pathToJar);
        final JarOutputStream jos = new JarOutputStream(new FileOutputStream(pathToSave));
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry entry = jarEntries.nextElement();
            boolean entryIsNotChanged = true;
            for (int i = 0; i < classesToRemove.size(); i++) {
                CtClass removeClass = classesToRemove.get(i);
                if (entry.getName().equals(removeClass.getName().replace('.', '/') + ".class")) {
                    changedClasses.remove(removeClass);
                    classesToRemove.remove(removeClass);
                    i--;
                    entryIsNotChanged = false;
                }
            }
            if (entryIsNotChanged) {
                for (int i = 0; i < changedClasses.size(); i++) {
                    CtClass changedClass = changedClasses.get(i);
                    if (entry.getName().equals(changedClass.getName().replace('.', '/') + ".class")) {
                        jos.putNextEntry(new JarEntry(entry.getName()));
                        changedClass.writeFile();
                        InputStream is = new FileInputStream(entry.getName());
                        write(jos, is);
                        entryIsNotChanged = false;
                        changedClasses.remove(changedClass);
                        i--;
                    }
                }
            }
            if (entryIsNotChanged) {
                jos.putNextEntry(entry);
                InputStream is = jarFile.getInputStream(entry);
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    jos.write(buf, 0, len);

                }
            }
            jos.closeEntry();
        }
        createNewClasses(jos, changedClasses);
        addPackages(jos, addedPackages);
        jos.close();
        System.out.println("Jar saved successfully");
    }

    private void write(JarOutputStream jos, InputStream is) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = (is.read(buf))) > 0) {
            jos.write(buf, 0, Math.min(len, buf.length));
        }
    }

    private void addPackages(JarOutputStream jos, List<String> addedPackages) throws IOException {
        for (String addedPackage : addedPackages) {
            jos.putNextEntry(new JarEntry(addedPackage.replace('.', '/') + "/"));
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

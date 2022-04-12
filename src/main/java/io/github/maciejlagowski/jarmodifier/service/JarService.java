package io.github.maciejlagowski.jarmodifier.service;

import io.github.maciejlagowski.jarmodifier.util.JarTreeItem;
import io.github.maciejlagowski.jarmodifier.util.TreeFile;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static io.github.maciejlagowski.jarmodifier.enums.EType.FILE;
import static io.github.maciejlagowski.jarmodifier.util.StringBytesConverter.getStringFromBytes;


public class JarService {

    private static JarService instance;
    private final ClassPool classPool = ClassPool.getDefault();
    private final List<CtClass> changedClasses = new ArrayList<>();
    private final List<CtClass> classesToRemove = new ArrayList<>();
    private File jar;

    private JarService() {
    }

    public static JarService getInstance() {
        if (instance == null) {
            instance = new JarService();
        }
        return instance;
    }

    public File chooseFile(Stage stage) throws NotFoundException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Java Archive Files (*.jar)", "*.jar");
        fileChooser.getExtensionFilters().add(extFilter);
        jar = fileChooser.showOpenDialog(stage);
        classPool.insertClassPath(jar.getAbsolutePath());
        return jar;
    }

    public JarTreeItem buildJarTree(JarFile jarFile) throws Exception {
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        Node rootIcon = fontAwesome.create(FontAwesome.Glyph.ARCHIVE).color(Color.BLACK);
        ClassService classService = new ClassService();
        JarTreeItem root = new JarTreeItem(new TreeFile(jarFile.getName().replaceAll(".*/", ""), jarFile.getName()), rootIcon);
        List<String> entries = jarFile.stream().map(ZipEntry::getName).toList();
        JarTreeItem activeNode;
        for (String entry : entries) {
            activeNode = root;
            for (String split : entry.split("/")) {
                if (activeNode.childrenContains(split)) {
                    activeNode = activeNode.getChild(split);
                } else {
                    TreeFile treeFile = new TreeFile(split, entry);
                    if (split.contains(".class")) {
                        String className = activeNode.getFullName()
                                .replaceAll(".*\\.jar.", "")
                                .replaceAll("/", ".") + "." + split.replace(".class", "");
                        try {
                            treeFile = new TreeFile(classService.parseClass(className));
                        } catch (Exception e) {
                            System.out.println("Cannot find " + className);
                        }
                    }
                    JarTreeItem newItem = new JarTreeItem(treeFile);
                    activeNode.getChildren().add(newItem);
                    activeNode = newItem;
                }
            }
        }
        return root;
    }

    public File saveFile(Stage stage) throws Exception {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Java Archive Files (*.jar)", "*.jar");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            new JarWriter().saveJar(file, jar, changedClasses, new ArrayList<>(), classesToRemove);
        }
        return file;
    }

    public String getJarPath() {
        return jar.getAbsolutePath();
    }

    public List<CtClass> addChangedClass(CtClass ctClass) {
        if (!changedClasses.contains(ctClass)) {
            changedClasses.add(ctClass);
        }
        return changedClasses;
    }

    public List<CtClass> addRemoveClass(CtClass ctClass) {
        if (!classesToRemove.contains(ctClass)) {
            classesToRemove.add(ctClass);
        }
        return classesToRemove;
    }

    public String getFileContent(TreeFile file) {
        if (file.type.equals(FILE)) {
            try {
                ZipFile zip = new ZipFile(jar);
                ZipEntry entry = zip.getEntry(file.getFullName());
                return getStringFromBytes(zip.getInputStream(entry).readAllBytes());
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        } else {
            return "Not a file";
        }
    }
}

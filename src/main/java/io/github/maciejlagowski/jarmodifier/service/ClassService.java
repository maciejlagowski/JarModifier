package io.github.maciejlagowski.jarmodifier.service;

import io.github.maciejlagowski.jarmodifier.enums.EType;
import io.github.maciejlagowski.jarmodifier.util.JarTreeItem;
import io.github.maciejlagowski.jarmodifier.util.TreeFile;
import javafx.scene.control.TreeView;
import javassist.ClassPool;
import javassist.CtClass;

import java.util.ArrayList;
import java.util.List;

public class ClassService {

    private final ClassPool classPool = ClassPool.getDefault();
    private final JarService jarService = JarService.getInstance();

    public CtClass addClass(String className, String actualPackage) {
        className = actualPackage + "." + className;
        CtClass ctClass = classPool.makeClass(className);
        defrostAndAddToList(ctClass);
        return ctClass;
    }

    public CtClass removeClass(CtClass actualClass) {
        jarService.addRemoveClass(actualClass);
        return actualClass;
    }

    public CtClass[] parseClasses(List<String> classNames) throws ClassNotFoundException {
        List<CtClass> ctClasses = new ArrayList<>();
        for (String className : classNames) {
            ctClasses.add(parseClass(className));
        }
        return ctClasses.toArray(new CtClass[0]);
    }

    public CtClass parseClass(String className) throws ClassNotFoundException {
        if (className.contains("void") || className.equals("")) {
            return CtClass.voidType;
        } else {
            try {
                return classPool.getCtClass(className);
            } catch (Exception e) {
                throw new ClassNotFoundException("Cannot find class for className:" + className);
            }
        }
    }

    public TreeView<TreeFile> addClassToTree(CtClass ctClass, TreeView<TreeFile> treeView) {
        JarTreeItem item = (JarTreeItem) treeView.getSelectionModel().getSelectedItem();
        if (item.getValue().type.equals(EType.CLASS)) {
            item = (JarTreeItem) item.getParent();
        }
        item.getChildren().add(new JarTreeItem(new TreeFile(ctClass)));
        return treeView;
    }

    public TreeFile removePackage(TreeFile packageFile) {
        // TODO
        System.err.println("Removing packages is not implemented yet");
        return packageFile;
    }

    public void defrostAndAddToList(CtClass ctClass) {
        if (ctClass.isFrozen()) {
            ctClass.defrost();
        }
        jarService.addChangedClass(ctClass);
    }
}

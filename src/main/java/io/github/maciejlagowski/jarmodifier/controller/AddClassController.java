package io.github.maciejlagowski.jarmodifier.controller;

import io.github.maciejlagowski.jarmodifier.enums.EType;
import io.github.maciejlagowski.jarmodifier.enums.EView;
import io.github.maciejlagowski.jarmodifier.service.ClassService;
import io.github.maciejlagowski.jarmodifier.util.JarTreeItem;
import io.github.maciejlagowski.jarmodifier.view.ViewManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javassist.CtClass;

public class AddClassController extends Controller {

    @FXML
    protected TextField className;

    public void onAddClassButtonClick() {
        String className = this.className.getText();
        if (className.equals("")) {
            viewManager.showError("Class name cannot be empty");
        } else {
            ClassService classService = new ClassService();
            ApplicationController ac = (ApplicationController) ViewManager.getInstance().getController(EView.APPLICATION);
            String packageName = getActualPackage((JarTreeItem) ac.getTreeView().getSelectionModel().getSelectedItem());
            CtClass ctClass = classService.addClass(className, packageName);
            classService.addClassToTree(ctClass, ac.getTreeView());
            viewManager.showInformation("Class added successfully");
        }
    }

    private String getActualPackage(JarTreeItem selectedItem) {
        if (selectedItem != null) {
            EType type = selectedItem.getValue().type;
            String actualPackage = selectedItem.getFullName().replaceFirst(".*jar/", "").replaceAll("/", ".");
            switch (type) {
                case CLASS -> {
                    return actualPackage.replaceAll("\\.([a-z]*[A-Z]*)*\\.class", "");
                }
                case JAR -> {
                    return "/";
                }
                case DIRECTORY -> {
                    return actualPackage;
                }
            }
        }
        return "";
    }
}

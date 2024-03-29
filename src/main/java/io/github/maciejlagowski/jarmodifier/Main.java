package io.github.maciejlagowski.jarmodifier;

import io.github.maciejlagowski.jarmodifier.controller.ApplicationController;
import io.github.maciejlagowski.jarmodifier.enums.EView;
import io.github.maciejlagowski.jarmodifier.service.ClassService;
import io.github.maciejlagowski.jarmodifier.service.MemberService;
import io.github.maciejlagowski.jarmodifier.service.decompiler.DecompilerService;
import io.github.maciejlagowski.jarmodifier.util.ClassMember;
import io.github.maciejlagowski.jarmodifier.view.ViewManager;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
        DecompilerService.getInstance().deleteDecompiledFile();
    }

    @Override
    public void start(Stage stage) {
        ViewManager viewManager = ViewManager.getInstance();
        viewManager.loadFXMLs();
        stage.setTitle("JarModifier");
        try {
            Parent root = viewManager.getRoot();
            stage.setScene(new Scene(root));
            ApplicationController applicationController = (ApplicationController) viewManager.getController(EView.APPLICATION);
            applicationController.setStage(stage);
            stage.show();
        } catch (Exception e) {
            viewManager.showError(e);
        }
    }
}

// TODO
// PRIORITIES
// status bar show info
// application reset to open new file
// text files editing
// before exit app asks about save changes
// showing changes to classes

// NICE TO HAVE
// https://github.com/FXMisc/RichTextFX syntax highlighting
// more use of controlsfx to get better looking app
// adding and removing packages
// operations on constructors
// help/manual
// scripts
// before decompilation there should be licence to submit

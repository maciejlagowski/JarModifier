package io.github.maciejlagowski.jarmodifier;

import io.github.maciejlagowski.jarmodifier.controller.ApplicationController;
import io.github.maciejlagowski.jarmodifier.enums.EView;
import io.github.maciejlagowski.jarmodifier.service.decompiler.DecompilerService;
import io.github.maciejlagowski.jarmodifier.view.ViewManager;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
        DecompilerService.getInstance().deleteDecompiledFile();
    }

    @Override
    public void start(Stage stage) {
        ViewManager viewManager = ViewManager.getInstance();
        viewManager.loadFXMLs();
        stage.setTitle("Reflection App");
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
// more use of controlsfx to get better looking app
// adding and removing packages
// operations on constructors
// help/manual
// scripts
// before decompilation there should be licence to submit

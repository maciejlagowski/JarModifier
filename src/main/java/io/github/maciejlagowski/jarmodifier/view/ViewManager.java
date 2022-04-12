package io.github.maciejlagowski.jarmodifier.view;

import io.github.maciejlagowski.jarmodifier.Main;
import io.github.maciejlagowski.jarmodifier.controller.Controller;
import io.github.maciejlagowski.jarmodifier.enums.EView;
import io.github.maciejlagowski.jarmodifier.util.ClassMember;
import io.github.maciejlagowski.jarmodifier.util.TreeFile;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ViewManager {

    private static ViewManager instance;
    private final Map<EView, DialogStage> dialogs = new HashMap<>();
    private final Map<EView, Controller> controllers = new HashMap<>();
    @Getter
    private Parent root;

    private ViewManager() {
    }

    public synchronized static ViewManager getInstance() {
        if (instance == null) {
            instance = new ViewManager();
        }
        return instance;
    }

    public Controller getController(EView view) {
        return controllers.get(view);
    }

    public void loadFXMLs() {
        try {
            root = loadFXML("/application.fxml");
            File dir = new File(Main.class.getResource("/dialogs").getFile());
            if (dir.isDirectory()) {
                String[] fxmls = dir.list();
                for (String name : fxmls) {
                    loadFXML("/dialogs/" + name);
                }
            }
        } catch (Exception e) {
            showError(e);
        }
        createLoadingDialog();
    }

    public void runLongerTask(Runnable runnable) {
        Stage application = getController(EView.APPLICATION).getStage();
        new Thread(() -> {
            runnable.run();
            Platform.runLater(() -> {
                dialogs.get(EView.LOADING).close();
                application.show();
            });
        }).start();
        application.hide();
        Platform.runLater(() -> showDialog(EView.LOADING));
    }

    public void showDialog(EView dialog) {
        dialogs.get(dialog).showDialog();
    }

    public void showDialogWithTreeSelection(EView dialog, TreeFile treeFile) {
        getController(dialog).setTreeFile(treeFile);
        dialogs.get(dialog).showDialog();
    }

    public void showDialogWithMemberSelection(EView dialog, ClassMember member) {
        getController(dialog).setClassMember(member);
        dialogs.get(dialog).showDialog();
    }

    public void showInformation(String information) {
        new InformationDialog(information).showDialog();
    }

    public void showError(Exception e) {
        e.printStackTrace();
        new ErrorDialog(e.getMessage()).showDialog();
    }

    public void showError(String error) {
        new ErrorDialog(error).showDialog();
    }

    private Parent loadFXML(String fxml) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
        Parent parent = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        EView viewName = EView.getEnum(fxml);
        controllers.put(viewName, controller);
        if (fxml.contains("/dialogs/")) {
            DialogStage ds = new DialogStage(new Scene(parent));
            controller.setStage(ds);
            dialogs.put(viewName, ds);
        }
        return parent;
    }

    private DialogStage createLoadingDialog() {
        VBox pane = new VBox();
        pane.setSpacing(10);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        Label label = new Label("Please wait...");
        hBox.getChildren().add(progressIndicator);
        pane.getChildren().addAll(label, hBox);
        DialogStage ds = new DialogStage(new Scene(pane));
        dialogs.put(EView.LOADING, ds);
        return ds;
    }
}

package io.github.maciejlagowski.jarmodifier.view;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class DialogStage extends Stage {

    public DialogStage() {
    }

    public DialogStage(Scene scene) {
        setScene(scene);
    }

    public void showDialog() {
        showAndWait();
    }

}

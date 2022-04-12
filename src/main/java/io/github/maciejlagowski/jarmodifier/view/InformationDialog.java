package io.github.maciejlagowski.jarmodifier.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class InformationDialog extends DialogStage {

    public InformationDialog(String message) {
        setScene(buildInformationDialogScene(message));
    }

    public Scene buildInformationDialogScene(String message) {
        VBox pane = new VBox();
        pane.setSpacing(10);
        pane.setMinWidth(300);
        Button button = new Button("OK");
        button.setOnAction((event) -> this.close());
        Label label = new Label("Info: " + message);
        pane.getChildren().addAll(label, button);
        return new Scene(pane);
    }
}

package io.github.maciejlagowski.jarmodifier.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


public class ErrorDialog extends DialogStage {

    public ErrorDialog(String message) {
        setScene(buildErrorDialogScene(message));
    }

    public Scene buildErrorDialogScene(String message) {
        VBox pane = new VBox();
        pane.setSpacing(10);
        pane.setMinWidth(300);
        Button button = new Button("OK");
        button.setOnAction((event) -> this.close());
        Label label = new Label("Error: " + message);
        label.setTextFill(Color.web("#FF0000"));
        pane.getChildren().addAll(label, button);
        return new Scene(pane);
    }
}

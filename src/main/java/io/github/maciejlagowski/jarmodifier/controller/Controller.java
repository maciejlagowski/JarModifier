package io.github.maciejlagowski.jarmodifier.controller;

import io.github.maciejlagowski.jarmodifier.util.ClassMember;
import io.github.maciejlagowski.jarmodifier.util.TreeFile;
import io.github.maciejlagowski.jarmodifier.view.ViewManager;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public abstract class Controller {
    @Setter
    @Getter
    protected Stage stage;

    @Setter
    protected TreeFile treeFile;

    @Setter
    protected ClassMember classMember;

    protected ViewManager viewManager = ViewManager.getInstance();

    public void onExitClick() {
        stage.close();
    }
}

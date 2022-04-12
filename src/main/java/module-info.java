module io.github.maciejlagowski.jarmodifier {
    requires javafx.controls;
    requires javafx.fxml;
    requires javassist;
    requires lombok;
    requires org.apache.commons.lang3;
    requires java.decompiler;
    requires org.controlsfx.controls;

    opens io.github.maciejlagowski.jarmodifier to javafx.fxml;
    exports io.github.maciejlagowski.jarmodifier;
    exports io.github.maciejlagowski.jarmodifier.controller;
    exports io.github.maciejlagowski.jarmodifier.view;

    opens io.github.maciejlagowski.jarmodifier.controller to javafx.fxml;
    exports io.github.maciejlagowski.jarmodifier.enums;
    opens io.github.maciejlagowski.jarmodifier.enums to javafx.fxml;
    opens io.github.maciejlagowski.jarmodifier.view to javafx.fxml;
    exports io.github.maciejlagowski.jarmodifier.util;
    opens io.github.maciejlagowski.jarmodifier.util to javafx.fxml;
    exports io.github.maciejlagowski.jarmodifier.service;
    opens io.github.maciejlagowski.jarmodifier.service to javafx.fxml;
    exports io.github.maciejlagowski.jarmodifier.service.decompiler;
    opens io.github.maciejlagowski.jarmodifier.service.decompiler to javafx.fxml;
}

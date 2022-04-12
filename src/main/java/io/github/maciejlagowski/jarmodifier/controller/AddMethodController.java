package io.github.maciejlagowski.jarmodifier.controller;

import io.github.maciejlagowski.jarmodifier.enums.EModifier;
import io.github.maciejlagowski.jarmodifier.service.MemberService;
import io.github.maciejlagowski.jarmodifier.util.ClassMember;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javassist.CtClass;

import java.util.ArrayList;
import java.util.List;

public class AddMethodController extends Controller {

    @FXML
    protected ChoiceBox<EModifier> modifiers;
    @FXML
    protected CheckBox staticModifier;
    @FXML
    protected TextField returnType;
    @FXML
    protected TextField methodName;
    @FXML
    protected TextArea methodBody;
    @FXML
    protected VBox parametersVbox;

    public void onAddParameterButtonClick() {
        parametersVbox.getChildren().add(new TextField());
    }

    public void onRemoveParameterButtonClick() {
        if (!parametersVbox.getChildren().isEmpty()) {
            parametersVbox.getChildren().remove(parametersVbox.getChildren().size() - 1);
        }
    }

    public void onAddMethodButtonClick() {
        try {
            CtClass motherClass = treeFile.getCtClass();
            if (areRequiredParametersFilled()) {
                ClassMember method = ClassMember.builder()
                        .modifier(modifiers.getValue())
                        .isStatic(staticModifier.isSelected())
                        .body(methodBody.getText())
                        .returnType(returnType.getText())
                        .name(methodName.getText())
                        .parameters(getParameters())
                        .motherClass(motherClass)
                        .build();
                new MemberService().addMethod(method);
                viewManager.showInformation("Method successfully added.");
            }
        } catch (Exception e) {
            viewManager.showError(e);
        }
    }

    private boolean areRequiredParametersFilled() {
        if (modifiers.getValue() == null) {
            viewManager.showError("You have to select method visibility");
            return false;
        }
        if (methodName.getText().equals("")) {
            viewManager.showError("You have to provide method name");
            return false;
        }
        return true;
    }

    private List<String> getParameters() {
        ObservableList<Node> observableList = parametersVbox.getChildren();
        List<String> parameters = new ArrayList<>();
        for (Node node : observableList) {
            parameters.add(((TextField) node).getText());
        }
        return parameters;
    }
}

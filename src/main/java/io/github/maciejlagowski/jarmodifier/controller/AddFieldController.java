package io.github.maciejlagowski.jarmodifier.controller;

import io.github.maciejlagowski.jarmodifier.enums.EModifier;
import io.github.maciejlagowski.jarmodifier.enums.EType;
import io.github.maciejlagowski.jarmodifier.service.MemberService;
import io.github.maciejlagowski.jarmodifier.util.ClassMember;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javassist.CtClass;

public class AddFieldController extends Controller {

    @FXML
    protected ChoiceBox<EModifier> modifiers;
    @FXML
    protected CheckBox staticModifier;
    @FXML
    protected TextField fieldType;
    @FXML
    protected TextField fieldName;

    public void onAddFieldButtonClick() {
        try {
            CtClass motherClass = treeFile.getCtClass();
            if (areRequiredParametersFilled()) {
                ClassMember field = ClassMember.builder()
                        .type(EType.FIELD)
                        .modifier(modifiers.getValue())
                        .isStatic(staticModifier.isSelected())
                        .returnType(fieldType.getText())
                        .name(fieldName.getText())
                        .motherClass(motherClass)
                        .build();
                new MemberService().addField(field);
                viewManager.showInformation("Field successfully added.");

            }
        } catch (Exception e) {
            viewManager.showError(e);
        }
    }

    private boolean areRequiredParametersFilled() {
        if (modifiers.getValue() == null) {
            viewManager.showError("You have to select field visibility");
            return false;
        }
        if (fieldType.getText().equals("")) {
            viewManager.showError("You have to provide field name");
            return false;
        }
        return true;
    }
}

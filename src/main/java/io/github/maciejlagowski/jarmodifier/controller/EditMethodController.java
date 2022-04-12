package io.github.maciejlagowski.jarmodifier.controller;

import io.github.maciejlagowski.jarmodifier.enums.EBodyInsertion;
import io.github.maciejlagowski.jarmodifier.service.MemberService;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javassist.CtMethod;

public class EditMethodController extends Controller {

    @FXML
    protected ToggleGroup group;
    @FXML
    protected RadioButton before;
    @FXML
    protected RadioButton after;
    @FXML
    protected RadioButton override;
    @FXML
    protected TextArea methodBody;

    public void onEditButtonClick() {
        try {
            CtMethod method = (CtMethod) classMember.getCtMember();
            new MemberService().editMethod(method, getBodyInsertion(), methodBody.getText());
            viewManager.showInformation("Method successfully edited.");
        } catch (Exception e) {
            viewManager.showError(e);
        }
    }

    private EBodyInsertion getBodyInsertion() {
        if (before.isSelected()) {
            return EBodyInsertion.BEFORE;
        } else if (after.isSelected()) {
            return EBodyInsertion.AFTER;
        } else {
            return EBodyInsertion.OVERRIDE;
        }
    }
}

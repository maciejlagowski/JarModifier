package io.github.maciejlagowski.jarmodifier.controller;

import io.github.maciejlagowski.jarmodifier.enums.EType;
import io.github.maciejlagowski.jarmodifier.enums.EView;
import io.github.maciejlagowski.jarmodifier.service.ClassService;
import io.github.maciejlagowski.jarmodifier.service.JarService;
import io.github.maciejlagowski.jarmodifier.service.MemberService;
import io.github.maciejlagowski.jarmodifier.service.decompiler.DecompilerService;
import io.github.maciejlagowski.jarmodifier.util.ClassMember;
import io.github.maciejlagowski.jarmodifier.util.JarTreeItem;
import io.github.maciejlagowski.jarmodifier.util.TreeFile;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javassist.CtClass;
import lombok.Getter;

import java.io.File;
import java.util.List;
import java.util.jar.JarFile;

public class ApplicationController extends Controller {

    private final JarService jarService = JarService.getInstance();
    private final DecompilerService decompilerService = DecompilerService.getInstance();
    private final MemberService memberService = new MemberService();
    private final ClassService classService = new ClassService();
    @FXML
    protected MenuItem decompileBarMenuItem;
    @FXML
    protected MenuItem removeTreeBarMenuItem;
    @FXML
    protected MenuItem addClassBarMenuItem;
    @FXML
    protected MenuItem addMethodBarMenuItem;
    @FXML
    protected MenuItem editMethodBarMenuItem;
    @FXML
    protected MenuItem removeMemberBarMenuItem;
    @FXML
    protected MenuItem addFieldBarMenuItem;
    @FXML
    protected MenuItem removeTreeMenuItem;
    @FXML
    protected MenuItem removeMemberMenuItem;
    @FXML
    protected MenuItem addClassMenuItem;
    @FXML
    protected MenuItem saveFileMenuItem;
    @FXML
    protected MenuItem addMethodMenuItem;
    @FXML
    protected MenuItem editMethodMenuItem;
    @FXML
    protected MenuItem addFieldMenuItem;
    @FXML
    protected ContextMenu contextList;
    @FXML
    protected Button decompileBtn;
    @FXML
    protected Button addClassBtn;
    @FXML
    protected Button removeTreeBtn;
    @FXML
    protected Button addMethodBtn;
    @FXML
    protected Button editMethodBtn;
    @FXML
    protected Button removeMemberBtn;
    @FXML
    protected Button addFieldBtn;
    @FXML
    protected ContextMenu contextTree;
    @FXML
    @Getter
    protected TreeView<TreeFile> treeView;
    @FXML
    protected ListView<ClassMember> listView;
    @FXML
    protected TextArea propertiesArea;
    @FXML
    protected TextArea fileContentArea;
    @Getter
    private TreeFile actualTreeSelection;
    @Getter
    private ClassMember actualMemberSelection;
    private List<Button> buttons;
    private List<MenuItem> menuItems;

    public void onTreeItemMouseClick(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        // Accept clicks only on node cells, and not on empty spaces of the TreeView
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell<?>) node).getText() != null)) {
            actualTreeSelection = treeView.getSelectionModel().getSelectedItem().getValue();
            showButtons(actualTreeSelection.type);
            setPropertiesArea(actualTreeSelection.getProperties());
            setMembersListItems(List.of());
            setFileContentArea(actualTreeSelection);
            if (actualTreeSelection.type.equals(EType.CLASS)) {
                try {
                    setMembersListItems(memberService.getMembers(actualTreeSelection.getCtClass()));
                } catch (Exception e) {
                    viewManager.showError(e);
                }
            }
        }
    }

    public void onListViewMouseClick(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        // Accept clicks only on node cells, and not on empty spaces of the ListView
        if (node instanceof Text || (node instanceof ListCell && ((ListCell<?>) node).getText() != null)) {
            actualMemberSelection = listView.getSelectionModel().getSelectedItem();
            showButtons(actualMemberSelection.getType());
            setPropertiesArea(actualMemberSelection.getProperties());
        }
    }

    public void onOpenFileClick() {
        try {
            // todo reset application
            File jar = jarService.chooseFile(stage);
            JarTreeItem jarTree = jarService.buildJarTree(new JarFile(jar));
            treeView.setRoot(jarTree);
            stage.setTitle(jar.getName() + " - JarModifier");
            showButtons(EType.JAR);
        } catch (Exception e) {
            viewManager.showError(e);
        }
    }

    public void onDecompileButtonClick() {
        viewManager.runLongerTask(() -> {
            decompilerService.decompile(jarService.getJarPath());
            System.out.println("Done decompiling.");
        });
    }

    public void onAddMethodClick() {
        viewManager.showDialogWithTreeSelection(EView.ADD_METHOD, actualTreeSelection);
    }

    public void onEditMethodClick() {
        viewManager.showDialogWithMemberSelection(EView.EDIT_METHOD, actualMemberSelection);
    }

    public void onAddFieldClick() {
        viewManager.showDialogWithTreeSelection(EView.ADD_FIELD, actualTreeSelection);
    }

    public void onAddClassClick() {
        viewManager.showDialog(EView.ADD_CLASS);
    }

    public void onSaveFileClick() {
        try {
            jarService.saveFile(stage);
        } catch (Exception e) {
            viewManager.showError(e);
        }
    }

    public void onRemoveMemberClick() {
        try {
            CtClass actualClass = actualTreeSelection.getCtClass();
            actualMemberSelection.setMotherClass(actualClass);
            memberService.removeMember(actualMemberSelection);
            setMembersListItems(memberService.getMembers(actualClass));
        } catch (Exception e) {
            viewManager.showError(e);
        }
    }

    public void onRemoveTreeClick() {
        TreeItem<TreeFile> treeItem = treeView.getSelectionModel().getSelectedItem();
        TreeFile file = treeItem.getValue();
        switch (file.type) {
            case CLASS -> {
                try {
                    classService.removeClass(file.getCtClass());
                } catch (Exception e) {
                    viewManager.showError(e);
                }
            }
            case DIRECTORY -> classService.removePackage(file);
            default -> {
                viewManager.showError("Not a class or package");
                return;
            }
        }
        treeView.getSelectionModel().getSelectedItem().getParent().getChildren().remove(treeItem);
        treeView.refresh();
    }

    public void onAboutClick(ActionEvent event) {
        viewManager.showDialog(EView.ABOUT);
    }

    private void setFileContentArea(TreeFile file) {
        String content;
        switch (file.type) {
            case CLASS -> content = decompilerService.getDecompiledClassCode(file.getFullName());
            case FILE -> content = jarService.getFileContent(file);
            default -> content = "";
        }
        fileContentArea.setText(content);
    }

    private void setPropertiesArea(String properties) {
        propertiesArea.setText(properties);
    }

    private void setMembersListItems(List<ClassMember> classMembers) {
        listView.setItems(FXCollections.observableList(classMembers));
    }

    private void showButtons(EType type) {
        disableAllButtons();
        if (!decompilerService.wasDecompiled()) {
            decompileBtn.setDisable(false);
        }
        saveFileMenuItem.setDisable(false);
        switch (type) {
            case METHOD:
                editMethodBtn.setDisable(false);
                editMethodBarMenuItem.setDisable(false);
                editMethodMenuItem.setDisable(false);
            case FIELD:
                removeMemberBtn.setDisable(false);
                removeMemberBarMenuItem.setDisable(false);
                removeMemberMenuItem.setDisable(false);
            case CLASS:
                addMethodMenuItem.setDisable(false);
                addMethodBtn.setDisable(false);
                addMethodBarMenuItem.setDisable(false);
                addFieldBtn.setDisable(false);
                addFieldMenuItem.setDisable(false);
                addFieldBarMenuItem.setDisable(false);
            case FILE:
            case DIRECTORY:
                removeTreeBtn.setDisable(false);
                removeTreeMenuItem.setDisable(false);
                removeTreeBarMenuItem.setDisable(false);
                addClassMenuItem.setDisable(false);
                addClassBtn.setDisable(false);
                addClassBarMenuItem.setDisable(false);
        }
    }

    private void disableAllButtons() {
        if (buttons == null) {
            buttons = List.of(decompileBtn, editMethodBtn, removeMemberBtn, addMethodBtn,
                    addFieldBtn, removeTreeBtn, addClassBtn);
            menuItems = List.of(editMethodBarMenuItem, editMethodMenuItem, removeMemberBarMenuItem,
                    removeMemberMenuItem, addMethodMenuItem, addMethodBarMenuItem, addFieldMenuItem,
                    addFieldBarMenuItem, removeTreeMenuItem, removeTreeBarMenuItem, addClassMenuItem,
                    saveFileMenuItem, addClassBarMenuItem);
        }
        buttons.forEach(button -> button.setDisable(true));
        menuItems.forEach(item -> item.setDisable(true));
    }
}

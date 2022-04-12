package io.github.maciejlagowski.jarmodifier.enums;

public enum EView {
    APPLICATION, ADD_METHOD, LOADING, EDIT_METHOD, ADD_FIELD, ADD_CLASS, ABOUT;

    public static EView getEnum(String value) {
        String name = value.replace(".fxml", "").replaceAll(".*\\/", "");
        name = name.toUpperCase().replaceAll("-", "_");
        return EView.valueOf(name);
    }
}

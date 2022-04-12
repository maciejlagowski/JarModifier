package io.github.maciejlagowski.jarmodifier.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public enum EModifier {
    PUBLIC(1), PRIVATE(2), PROTECTED(4), STATIC(8);

    @Getter
    private final int value;

    EModifier(int value) {
        this.value = value;
    }

    public static List<EModifier> getModifiersFromInt(int value) {
        List<EModifier> modifiers = new ArrayList<>(2);
        if ((value & 1) != 0) {
            modifiers.add(PUBLIC);
        } else if ((value & 2) != 0) {
            modifiers.add(PRIVATE);
        } else if ((value & 4) != 0) {
            modifiers.add(PROTECTED);
        }
        if ((value & 8) != 0) {
            modifiers.add(STATIC);
        }
        return modifiers;
    }
}

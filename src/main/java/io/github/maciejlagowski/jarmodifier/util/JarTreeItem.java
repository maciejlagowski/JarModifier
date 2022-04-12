package io.github.maciejlagowski.jarmodifier.util;

import io.github.maciejlagowski.jarmodifier.enums.EType;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

public class JarTreeItem extends TreeItem<TreeFile> {

    public JarTreeItem(TreeFile root) {
        super(root, createGraphic(root.type));
    }

    public JarTreeItem(TreeFile root, Node graphic) {
        super(root, graphic);
    }

    private static Node createGraphic(EType type) {
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        return switch (type) {
            case JAR -> fontAwesome.create(FontAwesome.Glyph.ARCHIVE);
            case FILE -> fontAwesome.create(FontAwesome.Glyph.FILE);
            case CLASS -> fontAwesome.create(FontAwesome.Glyph.CODE);
            case DIRECTORY -> fontAwesome.create(FontAwesome.Glyph.FOLDER);
            default -> fontAwesome.create(FontAwesome.Glyph.AMBULANCE);
        };
    }

    public boolean childrenContains(String s) {
        for (TreeItem<TreeFile> x : getChildren()) {
            if (x.getValue().toString().equals(s)) {
                return true;
            }
        }
        return false;
    }

    public JarTreeItem getChild(String s) throws Exception {
        for (TreeItem<TreeFile> x : getChildren()) {
            if (x.getValue().toString().equals(s)) {
                return (JarTreeItem) x;
            }
        }
        throw new Exception("Child not found");
    }

    public String getFullName() {
        if (this.getParent() != null) {
            return ((JarTreeItem) this.getParent()).getFullName() + "/" + this.getValue().toString();
        }
        return this.getValue().toString();
    }
}

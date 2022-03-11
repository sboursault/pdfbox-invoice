package poc.util;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;

public class Style {

    private int size = 12;
    private int lineHeight = 16;
    private PDType1Font font = PDType1Font.HELVETICA;
    private Color color = Color.black;

    public Style size(int size) {
        this.size = size;
        return this;
    }

    public Style lineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        return this;
    }

    public Style font(PDType1Font font) {
        this.font = font;
        return this;
    }

    public Style color(Color color) {
        this.color = color;
        return this;
    }

    public int getSize() {
        return size;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public PDType1Font getFont() {
        return font;
    }

    public Color getColor() {
        return color;
    }

    public static Style defaultStyle() {
        return new Style();
    }
}

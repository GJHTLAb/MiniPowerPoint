package model.objects;

import java.awt.*;

public class TextObject extends SlideObject {

    private String text;
    private Font font = new Font("SansSerif", Font.PLAIN, 20);
    private Color color = Color.BLACK;

    public TextObject(int x, int y, String text) {
        super(x, y);
        this.text = text;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.setFont(font);
        g2.drawString(text, x, y);
    }

    @Override
    public boolean containsPoint(int px, int py) {
        return getBounds().contains(px, py);
    }

    @Override
    public Rectangle getBounds() {
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        int w = fm.stringWidth(text);
        int h = fm.getHeight();
        return new Rectangle(x, y - h, w, h);
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    public Color getColor() {
        return color;
    }
}

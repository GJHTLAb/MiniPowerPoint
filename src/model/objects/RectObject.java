package model.objects;

import java.awt.*;

public class RectObject extends ShapeObject {

    private int width;
    private int height;

    public RectObject(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(fillColor);
        g2.fillRect(x, y, width, height);

        g2.setColor(strokeColor);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawRect(x, y, width, height);
    }

    @Override
    public boolean containsPoint(int px, int py) {
        return getBounds().contains(px, py);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

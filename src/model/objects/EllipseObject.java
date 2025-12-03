package model.objects;

import java.awt.*;

public class EllipseObject extends ShapeObject {
    private int rx;
    private int ry;

    public EllipseObject(int x, int y, int rx, int ry) {
        super(x, y);
        this.rx = rx;
        this.ry = ry;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(fillColor);
        g2.fillOval(x, y, rx * 2, ry * 2);

        g2.setColor(strokeColor);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawOval(x, y, rx * 2, ry * 2);
    }

    @Override
    public boolean containsPoint(int px, int py) {
        double dx = px - (x + rx);
        double dy = py - (y + ry);
        return (dx * dx) / (rx * rx) + (dy * dy) / (ry * ry) <= 1.0;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, rx * 2, ry * 2);
    }
}

package model.objects;

import java.awt.*;

public class LineObject extends ShapeObject {

    private int x2;
    private int y2;

    public LineObject(int x, int y, int x2, int y2) {
        super(x, y);
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(strokeColor);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawLine(x, y, x2, y2);
    }

    @Override
    public boolean containsPoint(int px, int py) {
        Rectangle r = getBounds();
        return r.contains(px, py);
    }

    @Override
    public Rectangle getBounds() {
        int minX = Math.min(x, x2);
        int maxX = Math.max(x, x2);
        int minY = Math.min(y, y2);
        int maxY = Math.max(y, y2);

        return new Rectangle(minX - 3, minY - 3, (maxX - minX) + 6, (maxY - minY) + 6);
    }
}

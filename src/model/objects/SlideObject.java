package model.objects;

import java.awt.*;

public abstract class SlideObject {

    protected int x;
    protected int y;

    public SlideObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void draw(Graphics2D g2);

    public abstract boolean containsPoint(int px, int py);

    public abstract Rectangle getBounds();

    public void moveBy(int dx, int dy) {
        x += dx;
        y += dy;
    }
}

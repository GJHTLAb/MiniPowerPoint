package model.objects;

import java.awt.*;

public abstract class ShapeObject extends SlideObject {

    public Color strokeColor = Color.BLACK;
    public Color fillColor = new Color(0,0,0,0);
    protected float strokeWidth = 2.0f;
    protected double rotation = 0;   // 当前旋转角度（弧度）

    // 旋转控制柄的大小
    protected static final int HANDLE_RADIUS = 8;

    public ShapeObject(int x, int y) {
        super(x, y);
    }

    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    public void setFillColor(Color color) {
        this.fillColor = color;
    }

    public void moveBy(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public void drawSelection(Graphics2D g2) {
        // 边框
        g2.setColor(Color.BLUE);
        g2.draw(getBounds());

        // 旋转柄位置：矩形上边中点往上移动 20px
        Rectangle b = getBounds();
        int cx = b.x + b.width/2;
        int cy = b.y - 20;

        g2.setColor(Color.RED);
        g2.fillOval(cx - HANDLE_RADIUS, cy - HANDLE_RADIUS,
                    HANDLE_RADIUS * 2, HANDLE_RADIUS * 2);
    }


}

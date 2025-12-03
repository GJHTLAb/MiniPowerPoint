package model.objects;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class RectObject extends ShapeObject {

    public RectObject(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics2D g2) {
        AffineTransform old = g2.getTransform();

        double cx = x + width / 2.0;
        double cy = y + height / 2.0;

        // 旋转画布
        g2.rotate(Math.toRadians(getRotation()), cx, cy);

        // 绘制矩形
        g2.setColor(fillColor);
        g2.fillRect(x, y, width, height);
        g2.setColor(strokeColor);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawRect(x, y, width, height);

        // 如果选中，绘制选中框和旋转柄（在旋转坐标系下）
        if (selected) {
            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(x, y, width, height);

            int handleX = x + width / 2;
            int handleY = y - 30; // 旋转柄相对于矩形顶部
            g2.setColor(Color.RED);
            g2.fillOval(handleX - 8, handleY - 8, 16, 16);
            g2.drawLine(x + width / 2, y, handleX, handleY);
        }

        // 恢复原始画布
        g2.setTransform(old);
    }


@Override
public boolean containsPoint(int px, int py) {
    double cx = x + width / 2.0;
    double cy = y + height / 2.0;

    double rad = Math.toRadians(-getRotation()); // 使用 getRotation()
    double cos = Math.cos(rad);
    double sin = Math.sin(rad);

    double dx = px - cx;
    double dy = py - cy;

    double rx = dx * cos - dy * sin + cx;
    double ry = dx * sin + dy * cos + cy;

    // 精度容差，可根据需要加上 strokeWidth / 2
    return (rx >= x && rx <= x + width &&
            ry >= y && ry <= y + height);
}

    @Override
    public Rectangle getBounds() {

        // 矩形的四角
        double[][] pts = {
            {x, y},
            {x + width, y},
            {x, y + height},
            {x + width, y + height}
        };

        double cx = x + width / 2.0;
        double cy = y + height / 2.0;
        double rad = Math.toRadians(rotation);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

        // 旋转四角
        for (double[] p : pts) {
            double dx = p[0] - cx;
            double dy = p[1] - cy;

            double rx = dx * cos - dy * sin + cx;
            double ry = dx * sin + dy * cos + cy;

            minX = Math.min(minX, rx);
            minY = Math.min(minY, ry);
            maxX = Math.max(maxX, rx);
            maxY = Math.max(maxY, ry);
        }

        return new Rectangle(
            (int) minX, (int) minY,
            (int) (maxX - minX), (int) (maxY - minY)
        );
    }

}

package model.objects;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class LineObject extends ShapeObject {

    private int x2;
    private int y2;

    private static final int ROTATE_HANDLE_OFFSET = 30;
    private static final int HANDLE_R = 8;

    public LineObject(int x, int y, int x2, int y2) {
        super(x, y);
        this.x2 = x2;
        this.y2 = y2;
        this.width = x2 - x;
        this.height = y2 - y;
    }

    

    public void setX2(int x2) {
        this.x2 = x2;
        this.width = x2 - x;
    }

    public void setY2(int y2) {
        this.y2 = y2;
        this.height = y2 - y;
    }

    @Override
    public void moveBy(int dx, int dy) {
        this.x += dx;
        this.y += dy;
        this.x2 += dx;
        this.y2 += dy;
    }

    @Override
    public void draw(Graphics2D g2) {
        AffineTransform old = g2.getTransform();

        double cx = (x + x2) / 2.0;
        double cy = (y + y2) / 2.0;

        // 旋转画布绘制线段
        g2.rotate(Math.toRadians(getRotation()), cx, cy);
        g2.setColor(strokeColor);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawLine(x, y, x2, y2);
        g2.setTransform(old);

        // 选中状态
        if (selected) {
            Rectangle b = getBounds();

            g2.setColor(Color.BLUE);
            g2.setStroke(new BasicStroke(1));
            g2.draw(b);

            // 旋转柄位置（旋转后的线段上方）
            double rx = cx;
            double ry = Math.min(y, y2) - ROTATE_HANDLE_OFFSET;

            // 绕线段中心旋转
            double rad = Math.toRadians(getRotation());
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);
            int handleX = (int) (cos * (rx - cx) - sin * (ry - cy) + cx);
            int handleY = (int) (sin * (rx - cx) + cos * (ry - cy) + cy);

            g2.setColor(Color.RED);
            g2.fillOval(handleX - HANDLE_R, handleY - HANDLE_R, HANDLE_R * 2, HANDLE_R * 2);

            g2.setColor(Color.BLUE);
            g2.drawLine((int) cx, (int) cy, handleX, handleY);
        }
    }

    @Override
    public boolean containsPoint(int px, int py) {
        double cx = (x + x2) / 2.0;
        double cy = (y + y2) / 2.0;

        // 将点旋回未旋转坐标系
        double rad = Math.toRadians(-getRotation());
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double dx = px - cx;
        double dy = py - cy;
        double rx = dx * cos - dy * sin + cx;
        double ry = dx * sin + dy * cos + cy;

        return pointToLineDistance(rx, ry, x, y, x2, y2) <= 3.0;
    }

    public boolean hitRotateHandle(int mx, int my) {
        double cx = (x + x2) / 2.0;
        double cy = (y + y2) / 2.0;

        // 原旋转柄位置
        double rx = cx;
        double ry = Math.min(y, y2) - ROTATE_HANDLE_OFFSET;

        double rad = Math.toRadians(getRotation());
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double rotatedX = cos * (rx - cx) - sin * (ry - cy) + cx;
        double rotatedY = sin * (rx - cx) + cos * (ry - cy) + cy;

        double dx = mx - rotatedX;
        double dy = my - rotatedY;

        return dx * dx + dy * dy <= HANDLE_R * HANDLE_R;
    }

    private double pointToLineDistance(double px, double py, double x1, double y1, double x2, double y2) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = dot / lenSq;

        if (param < 0 || lenSq == 0) return Math.hypot(px - x1, py - y1);
        if (param > 1) return Math.hypot(px - x2, py - y2);

        double xx = x1 + param * C;
        double yy = y1 + param * D;
        return Math.hypot(px - xx, py - yy);
    }

    @Override
    public Rectangle getBounds() {
        double cx = (x + x2) / 2.0;
        double cy = (y + y2) / 2.0;

        double rad = Math.toRadians(getRotation());
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double[][] pts = {{x, y}, {x2, y2}};

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

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

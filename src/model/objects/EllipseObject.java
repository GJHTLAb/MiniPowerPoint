package model.objects;

import java.awt.*;

public class EllipseObject extends ShapeObject {

    private static final int ROTATE_HANDLE_OFFSET = 30;
    private static final int HANDLE_R = 8;

    public EllipseObject(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics2D g2) {
        Graphics2D g = (Graphics2D) g2.create();

        double cx = x + width / 2.0;
        double cy = y + height / 2.0;

        // 旋转画布
        g.rotate(Math.toRadians(getRotation()), cx, cy);

        // 绘制椭圆
        g.setColor(fillColor);
        g.fillOval(x, y, width, height);
        g.setColor(strokeColor);
        g.setStroke(new BasicStroke(strokeWidth));
        g.drawOval(x, y, width, height);

        g.dispose();

        // 绘制旋转柄（在原始画布坐标下）
        if (isSelected()) {
            // 绘制旋转柄
            double rx = cx;
            double ry = y - ROTATE_HANDLE_OFFSET;

            double rad = Math.toRadians(getRotation());
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);

            int handleX = (int) (cos * (rx - cx) - sin * (ry - cy) + cx);
            int handleY = (int) (sin * (rx - cx) + cos * (ry - cy) + cy);

            Graphics2D gHandle = (Graphics2D) g2.create();
            gHandle.setColor(Color.RED);
            gHandle.fillOval(handleX - HANDLE_R, handleY - HANDLE_R, HANDLE_R * 2, HANDLE_R * 2);
            gHandle.drawLine((int) cx, (int) cy, handleX, handleY);
            gHandle.dispose();

            // 绘制旋转后的外接矩形
            Rectangle bounds = getBounds();
            Graphics2D gBounds = (Graphics2D) g2.create();
            gBounds.setColor(Color.BLUE);
            gBounds.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                                            new float[]{4, 4}, 0)); // 虚线矩形
            gBounds.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            gBounds.dispose();
        }
    }

    @Override
    public boolean containsPoint(int px, int py) {
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;

        double rad = Math.toRadians(-getRotation());
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double dx = px - cx;
        double dy = py - cy;

        double rx = dx * cos - dy * sin + cx;
        double ry = dx * sin + dy * cos + cy;

        double rx2 = rx - cx;
        double ry2 = ry - cy;

        return (rx2 * rx2) / Math.pow(width / 2.0, 2) +
               (ry2 * ry2) / Math.pow(height / 2.0, 2) <= 1.0;
    }

    @Override
    public boolean hitRotateHandle(int mx, int my) {
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;

        // 原旋转柄位置
        double rx = cx;
        double ry = y - ROTATE_HANDLE_OFFSET;

        // 旋转柄绕中心旋转
        double rad = Math.toRadians(getRotation());
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double rotatedX = cos * (rx - cx) - sin * (ry - cy) + cx;
        double rotatedY = sin * (rx - cx) + cos * (ry - cy) + cy;

        double dx = mx - rotatedX;
        double dy = my - rotatedY;

        return dx * dx + dy * dy <= HANDLE_R * HANDLE_R;
    }

@Override
public Rectangle getBounds() {
    double cx = x + width / 2.0;
    double cy = y + height / 2.0;

    double rad = Math.toRadians(getRotation());
    double cos = Math.cos(rad);
    double sin = Math.sin(rad);

    double a = width / 2.0;
    double b = height / 2.0;

    // 旋转椭圆后的外接矩形半宽半高
    double rotatedHalfWidth  = Math.sqrt(a * a * cos * cos + b * b * sin * sin);
    double rotatedHalfHeight = Math.sqrt(a * a * sin * sin + b * b * cos * cos);

    return new Rectangle(
        (int)Math.floor(cx - rotatedHalfWidth),
        (int)Math.floor(cy - rotatedHalfHeight),
        (int)Math.ceil(rotatedHalfWidth * 2),
        (int)Math.ceil(rotatedHalfHeight * 2)
    );
}

}

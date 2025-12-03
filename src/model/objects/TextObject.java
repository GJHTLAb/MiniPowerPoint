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

    // ------------------------------
    // 计算未旋转的文本矩形（基础）
    // ------------------------------
    private Rectangle getLocalBounds() {
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        int w = fm.stringWidth(text);
        int h = fm.getHeight();
        // baseline 在 y，所以 top 为 y - h
        return new Rectangle(x, y - h, w, h);
    }

    // ------------------------------
    // 计算旋转后的四个角
    // ------------------------------
    private Point[] getRotatedCorners() {

        Rectangle b = getLocalBounds();

        double cx = b.getCenterX();
        double cy = b.getCenterY();

        double rad = Math.toRadians(getRotation());
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double[][] pts = {
                {b.x, b.y},
                {b.x + b.width, b.y},
                {b.x + b.width, b.y + b.height},
                {b.x, b.y + b.height}
        };

        Point[] out = new Point[4];
        for (int i = 0; i < 4; i++) {
            double dx = pts[i][0] - cx;
            double dy = pts[i][1] - cy;

            double rx = dx * cos - dy * sin + cx;
            double ry = dx * sin + dy * cos + cy;

            out[i] = new Point((int) rx, (int) ry);
        }
        return out;
    }

    // ------------------------------
    // 绘制
    // ------------------------------
    @Override
    public void draw(Graphics2D g2) {
        Graphics2D g = (Graphics2D) g2.create();

        Rectangle b = getLocalBounds();
        double cx = b.getCenterX();
        double cy = b.getCenterY();

        // 旋转
        g.rotate(Math.toRadians(getRotation()), cx, cy);

        g.setColor(color);
        g.setFont(font);
        g.drawString(text, x, y); // 注意：坐标不变，因为已旋转画布

        g.dispose();

        // ----- 选中框 & 旋转柄 -----
        if (selected) {

            Point[] pts = getRotatedCorners();
            Polygon poly = new Polygon();
            for (Point p : pts) poly.addPoint(p.x, p.y);

            g2.setColor(Color.BLUE);
            g2.draw(poly);

            // 顶边中心
            int topX = (pts[0].x + pts[1].x) / 2;
            int topY = (pts[0].y + pts[1].y) / 2;

            double hx = topX + (topX - cx) * 0.3; // 简单外延 30%
            double hy = topY + (topY - cy) * 0.3;

            g2.setColor(Color.RED);
            g2.fillOval((int) hx - 8, (int) hy - 8, 16, 16);

            g2.setColor(Color.BLUE);
            g2.drawLine(topX, topY, (int) hx, (int) hy);
        }
    }

    // ------------------------------
    // 点击检测（逆旋转坐标）
    // ------------------------------
    @Override
    public boolean containsPoint(int px, int py) {
        Rectangle b = getLocalBounds();

        double cx = b.getCenterX();
        double cy = b.getCenterY();

        double rad = Math.toRadians(-rotation);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double dx = px - cx;
        double dy = py - cy;

        double rx = dx * cos - dy * sin + cx;
        double ry = dx * sin + dy * cos + cy;

        return b.contains(rx, ry);
    }

    // ------------------------------
    // 外接矩形（旋转后）
    // ------------------------------
    @Override
    public Rectangle getBounds() {

        Point[] pts = getRotatedCorners();

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Point p : pts) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    // ------------------------------
    // 旋转柄点击判定
    // ------------------------------
    @Override
    public boolean hitRotateHandle(int px, int py) {
        Point[] pts = getRotatedCorners();

        double topX = (pts[0].x + pts[1].x) / 2.0;
        double topY = (pts[0].y + pts[1].y) / 2.0;

        Rectangle b = getLocalBounds();
        double cx = b.getCenterX();
        double cy = b.getCenterY();

        double hx = topX + (topX - cx) * 0.3;
        double hy = topY + (topY - cy) * 0.3;

        double dx = px - hx;
        double dy = py - hy;

        return dx * dx + dy * dy <= 8 * 8;
    }


    // ------------------------------
    // setters
    // ------------------------------
    public void setText(String text) { this.text = text; }
    public void setFont(Font font) { this.font = font; }
    public void setColor(Color color) { this.color = color; }

    public String getText() { return text; }
    public Font getFont() { return font; }
    public Color getColor() { return color; }
}

package model.objects;

import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageObject extends SlideObject {

    private Image image;
    private String path;
    private Image originalImage; // 保存原始图片

    public ImageObject(int x, int y, String path) {
        super(x, y);
        this.path = path;

        try {
            this.originalImage = ImageIO.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
            this.originalImage = null;
        }

        if (originalImage != null) {
            int rawW = originalImage.getWidth(null);
            int rawH = originalImage.getHeight(null);

            int maxSize = 200;
            double scale = Math.min(1.0, (double) maxSize / Math.max(rawW, rawH));

            this.width = (int) (rawW * scale);
            this.height = (int) (rawH * scale);

            this.image = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }
    }

    private Point[] getRotatedCorners() {
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;
        double rad = Math.toRadians(rotation);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double[][] pts = {
            {x, y},
            {x + width, y},
            {x + width, y + height},
            {x, y + height}
        };

        Point[] out = new Point[4];

        for (int i = 0; i < 4; i++) {
            double dx = pts[i][0] - cx;
            double dy = pts[i][1] - cy;

            double rx = dx * cos - dy * sin + cx;
            double ry = dx * sin + dy * cos + cy;

            out[i] = new Point((int)rx, (int)ry);
        }

        return out;
    }

    public Point getRotateHandleCenter() {

        Point[] pts = getRotatedCorners();

        // 顶边中心
        double topCenterX = (pts[0].x + pts[1].x) / 2.0;
        double topCenterY = (pts[0].y + pts[1].y) / 2.0;

        double cx = x + width / 2.0;
        double cy = y + height / 2.0;

        // 延长出去的 30px
        double hx = topCenterX + (topCenterX - cx) * (30.0 / (height / 2.0));
        double hy = topCenterY + (topCenterY - cy) * (30.0 / (height / 2.0));

        return new Point((int) hx, (int) hy);
    }

    @Override
    public boolean hitRotateHandle(int px, int py) {

        Point h = getRotateHandleCenter();

        int dx = px - h.x;
        int dy = py - h.y;

        // 旋转柄半径为 8
        return dx * dx + dy * dy <= 8 * 8;
    }

    public void setImage(Image newImage) {
        this.image = newImage;
        this.width = newImage.getWidth(null);
        this.height = newImage.getHeight(null);
    }


    @Override
    public void draw(Graphics2D g2) {

        Graphics2D g = (Graphics2D) g2.create();
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;
        g.rotate(Math.toRadians(rotation), cx, cy);

        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            // 占位矩形
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, width, height);
            g.setColor(Color.DARK_GRAY);
            g.drawRect(x, y, width, height);
        }

        g.dispose();

        // 绘制选中框和旋转柄（原来的逻辑）
        if (selected) {
            Point[] pts = getRotatedCorners();
            Polygon poly = new Polygon();
            for (Point p : pts) poly.addPoint(p.x, p.y);
            g2.setColor(Color.BLUE);
            g2.draw(poly);

            int topCenterX = (pts[0].x + pts[1].x) / 2;
            int topCenterY = (pts[0].y + pts[1].y) / 2;

            double hx = topCenterX + (topCenterX - cx) * (30.0 / (height / 2.0));
            double hy = topCenterY + (topCenterY - cy) * (30.0 / (height / 2.0));

            g2.setColor(Color.RED);
            g2.fillOval((int) hx - 8, (int) hy - 8, 16, 16);
            g2.setColor(Color.BLUE);
            g2.drawLine(topCenterX, topCenterY, (int) hx, (int) hy);
        }
    }


    public void setScaledImage(Image newImage, int w, int h) {
        System.out.println(w + " " + h);
        this.image = newImage;
        this.width = w;
        this.height = h;
    }

    @Override
    public boolean containsPoint(int px, int py) {

        double cx = x + width / 2.0;
        double cy = y + height / 2.0;

        double rad = Math.toRadians(-rotation);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double dx = px - cx;
        double dy = py - cy;

        double rx = dx * cos - dy * sin + cx;
        double ry = dx * sin + dy * cos + cy;

        return (rx >= x && rx <= x + width &&
                ry >= y && ry <= y + height);
    }

    @Override
    public Rectangle getBounds() {
        // 同 RectObject
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

    public String getImagePath()
    {
        return path;
    }

    // 缩放方法
    public void scaleTo(int newWidth, int newHeight, int newX, int newY) {
        if (originalImage != null) {
            this.image = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        }
        this.x = newX;
        this.y = newY;
        this.width = newWidth;
        this.height = newHeight;
    }


}

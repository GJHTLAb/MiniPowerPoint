package model.objects;

import java.awt.*;

public abstract class SlideObject {

    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected double rotation = 0; // 角度，单位为度
    private static final int ROTATE_HANDLE_OFFSET = 30;
    private static final int HANDLE_R = 8;
    protected boolean selected;

    protected Color strokeColor = Color.BLACK;
    protected Color fillColor = Color.WHITE;


    public SlideObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void resizeBy(int dw, int dh) {
        this.width += dw;
        this.height += dh;

        if (this.width < 5) this.width = 5;
        if (this.height < 5) this.height = 5;
    }

    public void rotateBy(double dAngle) {
        rotation = (rotation + dAngle) % 360;
    }



    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }


    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getX() { return x; }
    public int getY() { return y; }
    
    public abstract void draw(Graphics2D g2);

    public abstract boolean containsPoint(int px, int py);

    public abstract Rectangle getBounds();

    public void moveBy(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void drawSelection(Graphics2D g2) {
    // 创建临时画布
    Graphics2D g = (Graphics2D) g2.create();

    // 计算矩形旋转后的四角和中点
    double rad = Math.toRadians(rotation);
    double cos = Math.cos(rad);
    double sin = Math.sin(rad);
    double cx = x + width / 2.0;
    double cy = y + height / 2.0;

    // 旋转矩形的四角坐标
    int[][] corners = new int[4][2];
    double[][] pts = {
        {x, y},
        {x + width, y},
        {x + width, y + height},
        {x, y + height}
    };
    for (int i = 0; i < 4; i++) {
        double dx = pts[i][0] - cx;
        double dy = pts[i][1] - cy;
        corners[i][0] = (int) (dx * cos - dy * sin + cx);
        corners[i][1] = (int) (dx * sin + dy * cos + cy);
    }

    // 画边框
    g.setColor(strokeColor.BLUE);
    g.setStroke(new BasicStroke(1));
    g.drawPolygon(
        new int[]{corners[0][0], corners[1][0], corners[2][0], corners[3][0]},
        new int[]{corners[0][1], corners[1][1], corners[2][1], corners[3][1]},
        4
    );

    // 控制点大小
    int size = 8;

    // 画四角和中点控制点
    drawHandle(g, corners[0][0], corners[0][1], size);
    drawHandle(g, corners[1][0], corners[1][1], size);
    drawHandle(g, corners[2][0], corners[2][1], size);
    drawHandle(g, corners[3][0], corners[3][1], size);

    // 中点（上下左右）
    drawHandle(g, (corners[0][0]+corners[1][0])/2, (corners[0][1]+corners[1][1])/2, size);
    drawHandle(g, (corners[1][0]+corners[2][0])/2, (corners[1][1]+corners[2][1])/2, size);
    drawHandle(g, (corners[2][0]+corners[3][0])/2, (corners[2][1]+corners[3][1])/2, size);
    drawHandle(g, (corners[3][0]+corners[0][0])/2, (corners[3][1]+corners[0][1])/2, size);

    // 旋转柄
    int rx = (int)(cx);
    int ry = (int)(cy - 30); // 旋转柄在矩形中心上方30px
    g.drawLine((int)cx, (int)cy, rx, ry);
    drawHandle(g, rx, ry, size);

    g.dispose();
}


    private void drawHandle(Graphics2D g, int hx, int hy, int size) {
        g.setColor(Color.WHITE);
        g.fillRect(hx - size/2, hy - size/2, size, size);
        g.setColor(Color.BLUE);
        g.drawRect(hx - size/2, hy - size/2, size, size);
    }

    public Point getCenter() {
        Rectangle b = getBounds();
        return new Point(
            b.x + b.width / 2,
            b.y + b.height / 2
        );
    }

    public double getRotation() { 
        return rotation; 
    }
    public void setRotation(double r) { 
        rotation = r; 
    }

    public void scaleByDrag(int mouseX, int mouseY, int anchorX, int anchorY) {
        // 将鼠标和锚点逆旋转到对象局部坐标系
        double rad = Math.toRadians(-rotation);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        // 锚点在局部坐标
        double localAnchorX = cos * (anchorX - x) - sin * (anchorY - y);
        double localAnchorY = sin * (anchorX - x) + cos * (anchorY - y);

        // 鼠标在局部坐标
        double localMouseX = cos * (mouseX - x) - sin * (mouseY - y);
        double localMouseY = sin * (mouseX - x) + cos * (mouseY - y);

        // 新宽高 = 鼠标坐标 - 锚点坐标
        int newWidth = (int)Math.abs(localMouseX - localAnchorX);
        int newHeight = (int)Math.abs(localMouseY - localAnchorY);

        // 防止过小
        newWidth = Math.max(5, newWidth);
        newHeight = Math.max(5, newHeight);

        // 根据鼠标相对锚点位置，确定左上角新坐标
        int newX = x + (int)Math.min(localAnchorX, localMouseX) - (int)localAnchorX;
        int newY = y + (int)Math.min(localAnchorY, localMouseY) - (int)localAnchorY;

        // 更新
        this.x = newX;
        this.y = newY;
        this.width = newWidth;
        this.height = newHeight;
    }


    // 按比例缩放矩形（以中心为锚点）
    public void scale(double scaleX, double scaleY) {
        int newWidth = (int)(width * scaleX);
        int newHeight = (int)(height * scaleY);

        // 防止尺寸过小
        if (newWidth < 5) newWidth = 5;
        if (newHeight < 5) newHeight = 5;

        // 中心不变
        int centerX = x + width / 2;
        int centerY = y + height / 2;

        width = newWidth;
        height = newHeight;

        x = centerX - width / 2;
        y = centerY - height / 2;
    }


    public boolean hitRotateHandle(int mx, int my) {
        Rectangle b = getBounds();
        double rad = Math.toRadians(rotation);

        // 原旋转柄在未旋转矩形坐标
        double rx = b.x + b.width / 2.0;
        double ry = b.y - ROTATE_HANDLE_OFFSET;

        // 旋转柄绕中心旋转
        Point center = getCenter();
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double rotatedX = cos * (rx - center.x) - sin * (ry - center.y) + center.x;
        double rotatedY = sin * (rx - center.x) + cos * (ry - center.y) + center.y;

        double dx = mx - rotatedX;
        double dy = my - rotatedY;

        return dx * dx + dy * dy <= HANDLE_R * HANDLE_R;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

}

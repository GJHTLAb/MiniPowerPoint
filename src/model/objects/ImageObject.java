package model.objects;

import javax.swing.*;
import java.awt.*;

public class ImageObject extends SlideObject {

    private Image image;
    private String path;
    private int width;
    private int height;

    public ImageObject(int x, int y, String path) {
        super(x, y);
        this.path = path;
        ImageIcon icon = new ImageIcon(path);
        this.image = icon.getImage();
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, width, height, null);
    }

    @Override
    public boolean containsPoint(int px, int py) {
        return getBounds().contains(px, py);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void setScaledImage(Image newImage, int w, int h) {
        this.image = newImage;
        this.width = w;
        this.height = h;
    }

    public String getImagePath() {
        return path;
    }
}

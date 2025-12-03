package model.factory;

import model.objects.ImageObject;

import javax.swing.*;
import java.awt.*;

public class ImageFactory {

    public static ImageObject createImage(int x, int y, String path) {
        ImageIcon icon = new ImageIcon(path);

        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            System.err.println("加载图片失败：" + path);
            return null;
        }

        return new ImageObject(x, y, path);
    }

    public static ImageObject createScaledImage(int x, int y, String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);

        if (icon.getIconWidth() <= 0) {
            System.err.println("加载图片失败：" + path);
            return null;
        }

        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageObject obj = new ImageObject(x, y, path);
        obj.setScaledImage(scaledImage, width, height);

        return obj;
    }
}

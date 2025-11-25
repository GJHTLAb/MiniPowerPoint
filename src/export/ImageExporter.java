package export;

import model.SlideDocument;
import model.SlidePage;
import model.objects.SlideObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageExporter {

    public static boolean exportPageAsImage(SlidePage page, String outPath, String format) {
        try {
            int width = 1280;
            int height = 720;

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);

            for (SlideObject obj : page.getObjects()) {
                obj.draw(g2);
            }

            g2.dispose();

            if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
                BufferedImage rgbImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                Graphics2D g = rgbImg.createGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, width, height);
                g.drawImage(img, 0, 0, null);
                g.dispose();

                ImageIO.write(rgbImg, "jpg", new File(outPath));
                return true;
            }

            ImageIO.write(img, format, new File(outPath));

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exportPNG(SlidePage page, String outPath) {
        return exportPageAsImage(page, outPath, "png");
    }

    public static boolean exportJPG(SlidePage page, String outPath) {
        return exportPageAsImage(page, outPath, "jpg");
    }
}

package util;

import java.awt.Color;

public class ColorUtil {

    public static Color decode(String hex) {
        if (hex == null) return Color.BLACK;

        hex = hex.trim();
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.length() == 3) {
            hex = "" + hex.charAt(0) + hex.charAt(0)
                    + hex.charAt(1) + hex.charAt(1)
                    + hex.charAt(2) + hex.charAt(2);
        }

        if (hex.length() == 6) {
            int r = Integer.valueOf(hex.substring(0, 2), 16);
            int g = Integer.valueOf(hex.substring(2, 4), 16);
            int b = Integer.valueOf(hex.substring(4, 6), 16);
            return new Color(r, g, b);
        }

        if (hex.length() == 8) {
            int a = Integer.valueOf(hex.substring(0, 2), 16);
            int r = Integer.valueOf(hex.substring(2, 4), 16);
            int g = Integer.valueOf(hex.substring(4, 6), 16);
            int b = Integer.valueOf(hex.substring(6, 8), 16);
            return new Color(r, g, b, a);
        }

        return Color.BLACK;
    }

    public static String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                c.getRed(), c.getGreen(), c.getBlue());
    }

    public static String toHexWithAlpha(Color c) {
        return String.format("#%02X%02X%02X%02X",
                c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue());
    }
}

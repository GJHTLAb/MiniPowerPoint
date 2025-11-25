package model.factory;

import model.objects.*;

public class ShapeFactory {

    public enum ShapeType {
        RECTANGLE,
        ELLIPSE,
        LINE,
        TEXT
    }

    public static SlideObject createShape(ShapeType type, int x, int y) {
        return switch (type) {
            case RECTANGLE -> new RectObject(x, y, 120, 80);
            case ELLIPSE -> new EllipseObject(x, y, 60, 40);
            case LINE -> new LineObject(x, y, x + 80, y + 80);
            case TEXT -> new TextObject(x, y, "New Text");
            default -> throw new IllegalArgumentException("Unknown shape type");
        };
    }
}

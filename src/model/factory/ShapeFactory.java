package model.factory;

import model.objects.*;

public class ShapeFactory {

    public enum ShapeType {
        RECTANGLE,
        ELLIPSE,
        LINE,
        TEXT
    }

    public static SlideObject createShape(ShapeType type, int x, int y, int w, int h) {
        return switch (type) {
            case RECTANGLE -> new RectObject(x, y, w, h);
            case ELLIPSE -> new EllipseObject(x, y, w, h);
            case LINE -> new LineObject(x, y, x + w, y + h);
            case TEXT -> new TextObject(x, y, "New Text");
            default -> throw new IllegalArgumentException("Unknown shape type");
        };
    }
}

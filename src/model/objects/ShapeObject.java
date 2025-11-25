package model.objects;

import java.awt.*;

public abstract class ShapeObject extends SlideObject {

    public Color strokeColor = Color.BLACK;
    public Color fillColor = new Color(0,0,0,0);
    protected float strokeWidth = 2.0f;

    public ShapeObject(int x, int y) {
        super(x, y);
    }

    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    public void setFillColor(Color color) {
        this.fillColor = color;
    }
}

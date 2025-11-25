package file;

import model.SlideDocument;
import model.SlidePage;
import model.factory.ShapeFactory;
import model.objects.*;
import org.json.JSONArray;
import org.json.JSONObject;
import util.ColorUtil;

import java.awt.*;

public class SlideDeserializer {

    public static SlideDocument fromJSON(JSONObject root) {
        SlideDocument doc = new SlideDocument();
        doc.getPages().clear();

        JSONArray pages = root.getJSONArray("pages");
        for (int i = 0; i < pages.length(); i++) {
            JSONObject pageObj = pages.getJSONObject(i);
            doc.addPage(deserializePage(pageObj));
        }


        return doc;
    }

    private static SlidePage deserializePage(JSONObject obj) {
        SlidePage page = new SlidePage();

        JSONArray arr = obj.getJSONArray("objects");
        for (int i = 0; i < arr.length(); i++) {
            page.addObject(deserializeObject(arr.getJSONObject(i)));
        }

        return page;
    }

    private static SlideObject deserializeObject(JSONObject o) {
        String type = o.getString("type");

        switch (type) {
            case "rect":
                RectObject r = new RectObject(
                        o.getInt("x"), o.getInt("y"),
                        o.getInt("width"), o.getInt("height")
                );
                r.setStrokeColor(ColorUtil.decode(o.getString("strokeColor")));
                r.setFillColor(ColorUtil.decode(o.getString("fillColor")));
                return r;

            case "ellipse":
                EllipseObject e = new EllipseObject(
                        o.getInt("x"), o.getInt("y"),
                        o.getInt("rx"), o.getInt("ry")
                );
                e.setStrokeColor(ColorUtil.decode(o.getString("strokeColor")));
                e.setFillColor(ColorUtil.decode(o.getString("fillColor")));
                return e;

            case "line":
                return new LineObject(
                        o.getInt("x"), o.getInt("y"),
                        o.getInt("x2"), o.getInt("y2")
                );

            case "text":
                TextObject t = new TextObject(
                        o.getInt("x"), o.getInt("y"),
                        o.getString("text")
                );
                t.setFont(new Font("SansSerif", Font.PLAIN, o.getInt("fontSize")));
                t.setColor(ColorUtil.decode(o.getString("color")));
                return t;

            case "image":
                return new ImageObject(
                        o.getInt("x"), o.getInt("y"),
                        o.getString("path")
                );
        }

        return null;
    }
}

package file;

import model.SlideDocument;
import model.SlidePage;
import model.objects.*;
import util.ColorUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;

public class SlideSerializer {

    public static JSONObject toJSON(SlideDocument document) {
        JSONObject root = new JSONObject();
        JSONArray pages = new JSONArray();

        for (SlidePage page : document.getPages()) {
            pages.put(serializePage(page));
        }

        root.put("pages", pages);
        return root;
    }

    private static JSONObject serializePage(SlidePage page) {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();

        for (SlideObject o : page.getObjects()) {
            arr.put(serializeObject(o));
        }

        obj.put("objects", arr);
        return obj;
    }

    private static JSONObject serializeObject(SlideObject obj) {

        JSONObject o = new JSONObject();

        if (obj instanceof RectObject r) {
            o.put("type", "rect");
            o.put("x", r.getBounds().x);
            o.put("y", r.getBounds().y);
            o.put("width", r.getBounds().width);
            o.put("height", r.getBounds().height);
            o.put("strokeColor", ColorUtil.toHexWithAlpha(r.strokeColor));
            o.put("fillColor", ColorUtil.toHexWithAlpha(r.fillColor));
        }

        else if (obj instanceof EllipseObject e) {
            o.put("type", "ellipse");
            o.put("x", e.getBounds().x);
            o.put("y", e.getBounds().y);
            o.put("rx", e.getBounds().width / 2);
            o.put("ry", e.getBounds().height / 2);
            o.put("strokeColor", ColorUtil.toHexWithAlpha(e.strokeColor));
            o.put("fillColor", ColorUtil.toHexWithAlpha(e.fillColor));
        }

        else if (obj instanceof LineObject l) {
            o.put("type", "line");
            o.put("x", l.getBounds().x);
            o.put("y", l.getBounds().y);
            o.put("x2", l.getBounds().x + l.getBounds().width);
            o.put("y2", l.getBounds().y + l.getBounds().height);
            o.put("strokeColor", ColorUtil.toHexWithAlpha(l.strokeColor));
        }

        else if (obj instanceof TextObject t) {
            o.put("type", "text");
            o.put("x", t.getBounds().x);
            o.put("y", t.getBounds().y);
            o.put("text", t.getText());
            o.put("fontSize", t.getFont().getSize());
            o.put("color", ColorUtil.toHexWithAlpha(t.getColor()));
        }

        else if (obj instanceof ImageObject i) {
            o.put("type", "image");
            o.put("x", i.getBounds().x);
            o.put("y", i.getBounds().y);
            o.put("path", i.getImagePath());
        }

        return o;
    }

}

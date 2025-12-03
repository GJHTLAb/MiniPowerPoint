package model;

import model.objects.SlideObject;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;


public class SlidePage {

    private final List<SlideObject> objects = new ArrayList<>();

    public SlidePage(SlidePage original) {
        objects.addAll(original.getObjects());
    }

    public SlidePage() {

    }

    public void addObject(SlideObject obj) {
        objects.add(obj);
    }

    public void removeObject(SlideObject obj) {
        objects.remove(obj);
    }

    public List<SlideObject> getObjects() {
        return objects;
    }


}

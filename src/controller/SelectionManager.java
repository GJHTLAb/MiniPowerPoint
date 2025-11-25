package controller;

import model.objects.SlideObject;

public class SelectionManager {

    private SlideObject selected = null;

    public SlideObject getSelected() {
        return selected;
    }

    public void setSelected(SlideObject obj) {
        this.selected = obj;
    }

    public void clearSelection() {
        this.selected = null;
    }

    public boolean hasSelection() {
        return selected != null;
    }
}

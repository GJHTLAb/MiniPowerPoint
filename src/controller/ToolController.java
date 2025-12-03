package controller;

import java.awt.Color;
import model.objects.SlideObject;

public class ToolController {

    public enum ToolType {
        SELECT,
        RECT,
        ELLIPSE,
        LINE,
        TEXT,
        IMAGE,
        DELETE
    }

    private ToolType currentTool = ToolType.SELECT;

    // 当前选中的图形
    private SlideObject selectedObject;

    // ---- 工具相关 ----
    public ToolType getCurrentTool() {
        return currentTool;
    }

    public void setCurrentTool(ToolType tool) {
        this.currentTool = tool;
    }

    // ---- 选中对象相关 ----
    public void setSelectedObject(SlideObject obj) {
        this.selectedObject = obj;
    }

    public SlideObject getSelectedObject() {
        return selectedObject;
    }

    // ---- 修改选中对象颜色 ----
    public void setSelectedStrokeColor(Color color) {
        if (selectedObject != null) {
            selectedObject.setStrokeColor(color);
        }
    }

    public void setSelectedFillColor(Color color) {
        if (selectedObject != null) {
            selectedObject.setFillColor(color);
        }
    }
}

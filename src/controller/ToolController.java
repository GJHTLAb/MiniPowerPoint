package controller;

public class ToolController {

    public enum ToolType {
        SELECT,
        RECT,
        ELLIPSE,
        LINE,
        TEXT,
        IMAGE
    }

    private ToolType currentTool = ToolType.SELECT;

    public ToolType getCurrentTool() {
        return currentTool;
    }

    public void setCurrentTool(ToolType tool) {
        this.currentTool = tool;
    }
}

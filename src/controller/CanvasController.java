package controller;

import context.DocumentContext;
import model.SlideDocument;
import model.factory.ShapeFactory;
import model.objects.SlideObject;
import view.SlideCanvas;
import view.SlideListPanel;
import command.*;

import javax.swing.*;
import java.awt.event.*;

public class CanvasController implements MouseListener, MouseMotionListener {

    private DocumentContext context;
    private SlideCanvas canvas;
    private SelectionManager selectionManager;
    private ToolController toolController;
    private CommandManager commandManager;
    private SlideListPanel slideListPanel;
    private MoveObjectCommand currentMoveCmd;
    private int lastX, lastY;

    public CanvasController(DocumentContext context,SlideListPanel  slideListPanel,SlideCanvas canvas,
                            SelectionManager selectionManager, ToolController toolController, CommandManager commandManager) {
        this.context = context;
        this.canvas = canvas;
        this.slideListPanel = slideListPanel;
        this.selectionManager = selectionManager;
        this.toolController = toolController;
        this.commandManager = commandManager;

        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }


    void refresh() {
        canvas.refresh();
        slideListPanel.refreshList();
        int Index = context.getDocument().getCurrentPageIndex();
        slideListPanel.refreshImages(Index);
    }
    @Override
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();

        ToolController.ToolType tool = toolController.getCurrentTool();

        switch (tool) {

            case SELECT:
                SlideObject clicked = findObjectAt(e.getX(), e.getY());
                selectionManager.setSelected(clicked);
                lastX = e.getX();
                lastY = e.getY();
                if (selectionManager.hasSelection()) {
                    currentMoveCmd = new MoveObjectCommand(selectionManager.getSelected());
                }
                refresh();
                break;

            case RECT:
                var rect = ShapeFactory.createShape(ShapeFactory.ShapeType.RECTANGLE, e.getX(), e.getY());
                commandManager.executeCommand(new AddObjectCommand(context.getDocument(), rect));
                refresh();
                break;

            case ELLIPSE:
                var ellipse = ShapeFactory.createShape(ShapeFactory.ShapeType.ELLIPSE, e.getX(), e.getY());
                commandManager.executeCommand(new AddObjectCommand(context.getDocument(), ellipse));
                refresh();
                break;

            case LINE:
                var line = ShapeFactory.createShape(ShapeFactory.ShapeType.LINE, e.getX(), e.getY());
                commandManager.executeCommand(new AddObjectCommand(context.getDocument(), line));
                refresh();
                break;

            case TEXT:
                var text = ShapeFactory.createShape(ShapeFactory.ShapeType.TEXT, e.getX(), e.getY());
                commandManager.executeCommand(new AddObjectCommand(context.getDocument(), text));
                refresh();
                break;

            case IMAGE:
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(canvas) == JFileChooser.APPROVE_OPTION) {
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    var img = model.factory.ImageFactory.createImage(e.getX(), e.getY(), path);
                    if (img != null) {
                        commandManager.executeCommand(new AddObjectCommand(context.getDocument(), img));
                    }
                }
                refresh();
                break;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!selectionManager.hasSelection()) return;

        SlideObject obj = selectionManager.getSelected();

        int dx = e.getX() - lastX;
        int dy = e.getY() - lastY;

        obj.moveBy(dx, dy);

        if (currentMoveCmd != null) {
            currentMoveCmd.accumulateMove(dx, dy);
        }

        lastX = e.getX();
        lastY = e.getY();

        refresh();
    }


    @Override public void mouseReleased(MouseEvent e) {
        if (currentMoveCmd != null) {
            commandManager.executeCommand(currentMoveCmd);
            currentMoveCmd = null;
        }
    }

    private SlideObject findObjectAt(int x, int y) {
        var objects = context.getDocument().getCurrentPage().getObjects();
        for (int i = objects.size() - 1; i >= 0; i--) {
            SlideObject obj = objects.get(i);
            if (obj.containsPoint(x, y)) {
                return obj;
            }
        }
        return null;
    }


    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}

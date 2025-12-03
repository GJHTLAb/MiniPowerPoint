package controller;

import command.*;
import context.DocumentContext;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import javax.swing.*;
import model.factory.ShapeFactory;
import model.objects.EllipseObject;
import model.objects.ImageObject;
import model.objects.LineObject;
import model.objects.RectObject;
import model.objects.SlideObject;
import model.objects.TextObject;
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
    private TextObject editingText = null;


    private SlideListPanel slideListPanel;
    private MoveObjectCommand currentMoveCmd;
    private int lastX, lastY;
    private int startX, startY;
    private SlideObject previewShape = null;
    private boolean creating = false;

    // ---- 旋转控制逻辑 ----
    private boolean rotating = false;
    private SlideObject rotatingTarget = null;
    private double startAngle = 0;

    private boolean scaling = false;
    private SlideObject scalingTarget = null;
    private int scaleAnchorX, scaleAnchorY;
    private int originalWidth, originalHeight;
    private int originalX, originalY;
    private int scaleCorner = 0; // 0=右下,1=右上,2=左下,3=左上




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

        JTextField editor = canvas.getTextEditor();
        editor.setFocusable(true);  // 确保可以获得焦点

        // 按 Enter 确认
        editor.addActionListener(e -> {
            if (editingText != null) {
                exitEditMode();
            }
        });

        // 点击别处退出
        editor.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (editingText != null) {
                    exitEditMode();
                }
            }
        });
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
        startX = lastX;
        startY = lastY;

        ToolController.ToolType tool = toolController.getCurrentTool();

        if (tool == ToolController.ToolType.DELETE) {
            SlideObject selected = selectionManager.getSelected();
            if (selected != null) {
                // 删除对象
                commandManager.executeCommand(new RemoveObjectCommand(context.getDocument(), selected));

                // 清空选择
                selected.setSelected(false);
                selectionManager.clearSelection();

                canvas.refresh();
            }
            return; // 删除后不执行其他逻辑
        }

        if (tool == ToolController.ToolType.RECT ||
            tool == ToolController.ToolType.ELLIPSE ||
            tool == ToolController.ToolType.LINE) {

            creating = true;

            // 初始形状（宽高为0）
            ShapeFactory.ShapeType stype = ShapeFactory.ShapeType.RECTANGLE;
            if (tool == ToolController.ToolType.ELLIPSE) stype = ShapeFactory.ShapeType.ELLIPSE;
            if (tool == ToolController.ToolType.LINE) stype = ShapeFactory.ShapeType.LINE;

            previewShape = ShapeFactory.createShape(stype, startX, startY, 0, 0);

            canvas.setPreviewShape(previewShape);
            canvas.refresh();
            return;
        }

        // --------------------------------------
        // SELECT：点击选中、拖动或旋转
        // --------------------------------------
        if (tool == ToolController.ToolType.SELECT) {
            SlideObject selected = selectionManager.getSelected();
            
            SlideObject rotateTarget = null;

            if (selected != null) {
                int threshold = 10;

                // 旋转中心
                int cx = selected.getX() + selected.getWidth()/2;
                int cy = selected.getY() + selected.getHeight()/2;

                double theta = Math.toRadians(selected.getRotation());
                double cos = Math.cos(theta);
                double sin = Math.sin(theta);

                // 旋转后的四个角点坐标
                Point[] corners = new Point[4];
                int[][] offsets = {
                    {selected.getWidth()/2, selected.getHeight()/2},   // 右下
                    {selected.getWidth()/2, -selected.getHeight()/2},  // 右上
                    {-selected.getWidth()/2, selected.getHeight()/2},  // 左下
                    {-selected.getWidth()/2, -selected.getHeight()/2}  // 左上
                };

                for (int i=0; i<4; i++) {
                    int dx = offsets[i][0];
                    int dy = offsets[i][1];
                    int rotatedX = (int)(cx + dx * cos - dy * sin);
                    int rotatedY = (int)(cy + dx * sin + dy * cos);
                    corners[i] = new Point(rotatedX, rotatedY);
                }

                // 检测鼠标是否点击在角点上
                for (int i=0; i<4; i++) {
                    Point c = corners[i];
                    if (Math.abs(e.getX()-c.x) < threshold && Math.abs(e.getY()-c.y) < threshold) {
                        scaling = true;
                        scalingTarget = selected;
                        scaleCorner = i;

                        originalWidth = selected.getWidth();
                        originalHeight = selected.getHeight();
                        originalX = selected.getX();
                        originalY = selected.getY();

                        // 对角点作为锚点（旋转后的对角点）
                        scaleAnchorX = cx*2 - c.x;
                        scaleAnchorY = cy*2 - c.y;

                        return;
                    }
                }
            }


            // ---- 旋转柄检测 ----
            if (selected != null && selected.hitRotateHandle(e.getX(), e.getY())) {
                rotateTarget = selected;
            } else {
                for (SlideObject obj : context.getDocument().getCurrentPage().getObjects()) {
                    if (obj.hitRotateHandle(e.getX(), e.getY())) {
                        rotateTarget = obj;
                        break;
                    }
                }
            }

            if (rotateTarget != null) {
                if (selectionManager.getSelected() != rotateTarget) {
                    SlideObject prev = selectionManager.getSelected();
                    if (prev != null) prev.setSelected(false);
                    selectionManager.setSelected(rotateTarget);
                    rotateTarget.setSelected(true);
                }

                rotating = true;
                rotatingTarget = rotateTarget;

                Point center = rotateTarget.getCenter();
                double initialMouseAngleRad = Math.atan2(e.getY() - center.y, e.getX() - center.x);
                double initialRotationRad = Math.toRadians(rotateTarget.getRotation());
                startAngle = initialMouseAngleRad - initialRotationRad;
                return;
            }

            // ---- 普通点击选中逻辑 ----
            SlideObject clicked = findObjectAt(e.getX(), e.getY());
            SlideObject prev = selectionManager.getSelected();
            if (prev != null) prev.setSelected(false);

            if (clicked != null) {
                clicked.setSelected(true);
                selectionManager.setSelected(clicked);
                toolController.setSelectedObject(clicked);  // ← 关键
                currentMoveCmd = new MoveObjectCommand(clicked);
            } else {
                selectionManager.clearSelection();
                toolController.setSelectedObject(null);  // 没选中时清空
            }



            canvas.refresh();
            return;
        }


        // --------------------------------------
        // 3. TEXT：拖拽创建文本框
        // --------------------------------------
        if (tool == ToolController.ToolType.TEXT) {
            creating = true;
            previewShape = ShapeFactory.createShape(ShapeFactory.ShapeType.TEXT, startX, startY, 0, 0);
            canvas.setPreviewShape(previewShape);
            return;
        }

        if (tool == ToolController.ToolType.IMAGE) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(canvas) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();

                // 1️⃣ 先创建 ImageObject 占位（小尺寸即可）
                ImageObject img = model.factory.ImageFactory.createImage(e.getX(), e.getY(), path);
                img.setWidth(50);
                img.setHeight(50);

                // 2️⃣ 加入文档并立即刷新显示占位
                commandManager.executeCommand(new AddObjectCommand(context.getDocument(), img));
                canvas.setPreviewShape(img);
                creating = true;
                startX = e.getX();
                startY = e.getY();
                canvas.refresh();  // ✅ 确保立即显示占位

                // 3️⃣ 异步加载真实图片
                new Thread(() -> {
                    try {
                        // 使用 ImageIO 同步加载，避免延迟
                        Image raw = javax.imageio.ImageIO.read(new java.io.File(path));

                        int rawW = raw.getWidth(null);
                        int rawH = raw.getHeight(null);

                        // 设置最大尺寸
                        int maxSize = 200;
                        double scale = (rawW > rawH) ? (double) maxSize / rawW : (double) maxSize / rawH;

                        int newW = (int)(rawW * scale);
                        int newH = (int)(rawH * scale);

                        Image scaled = raw.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);

                        // 4️⃣ 更新 ImageObject 尺寸和图片
                        img.setScaledImage(scaled, newW, newH);

                        // 5️⃣ 在 EDT 刷新 Canvas
                        SwingUtilities.invokeLater(() -> canvas.refresh());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        }

        if (editingText != null) {
            JTextField editor = canvas.getTextEditor();
            // 转换 MouseEvent 坐标为 editor 的父容器坐标（Canvas）
            if (!editor.getBounds().contains(e.getX(), e.getY())) {
                exitEditMode();
            }
        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        ToolController.ToolType tool = toolController.getCurrentTool();

        // ---- 缩放逻辑 ----
    if (scaling && scalingTarget != null) {
        SlideObject obj = scalingTarget;

        // 使用物体中心作为局部坐标原点（旋转参考）
        int cx = originalX + originalWidth / 2;
        int cy = originalY + originalHeight / 2;

        // 打印物体原始信息
        System.out.println("======【缩放调试信息】======");
        System.out.println("旋转角度（度） = " + obj.getRotation());
        System.out.println("物体中心点 = (" + cx + ", " + cy + ")");
        System.out.println("原始位置 = (" + originalX + ", " + originalY + ")");
        System.out.println("原始宽高 = " + originalWidth + " x " + originalHeight);

        // 当前鼠标点（全局）
        int mx = e.getX();
        int my = e.getY();

        // 锚点（全局）
        // ⚠️ 修正：直接使用旋转后的对角点作为锚点，而不是 cx*2 - c.x
        int ax = scaleAnchorX;
        int ay = scaleAnchorY;

        System.out.println("鼠标全局坐标 = (" + mx + ", " + my + ")");
        System.out.println("锚点全局坐标 = (" + ax + ", " + ay + ")");

        // 逆旋转角度
        double theta = Math.toRadians(-obj.getRotation());
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);

        // ---- 把鼠标点 & 锚点 转到局部坐标 ----
        double localMx = cos * (mx - ax) - sin * (my - ay);
        double localMy = sin * (mx - ax) + cos * (my - ay);

        System.out.println("鼠标局部坐标 = (" + localMx + ", " + localMy + ")");

        // ---- 新宽高 ----
        int newW = (int)Math.max(5, Math.abs(localMx));
        int newH = (int)Math.max(5, Math.abs(localMy));

        System.out.println("新宽高 = " + newW + " x " + newH);

        // ---- 新左上角（全局坐标）----
        // 左上角 = 锚点 + (鼠标局部坐标中为负的部分)
        int offsetX = (localMx < 0) ? (int)localMx : 0;
        int offsetY = (localMy < 0) ? (int)localMy : 0;

        int newX = ax + offsetX;
        int newY = ay + offsetY;

        System.out.println("新左上角（全局）= (" + newX + ", " + newY + ")");
        System.out.println("========== 结束 ==========\n");

        if (obj instanceof ImageObject imgObj) {
            imgObj.scaleTo(newW, newH, newX, newY);
        } else if (obj instanceof RectObject || obj instanceof EllipseObject || obj instanceof TextObject) {
            obj.setX(newX);
            obj.setY(newY);
            obj.setWidth(newW);
            obj.setHeight(newH);
        } else if (obj instanceof LineObject line) {
            int newX1 = originalX;
            int newY1 = originalY;
            int newX2 = originalX + originalWidth;
            int newY2 = originalY + originalHeight;
            switch (scaleCorner) {
                case 0: newX2 = e.getX(); newY2 = e.getY(); break;
                case 1: newX2 = e.getX(); newY1 = e.getY(); break;
                case 2: newX1 = e.getX(); newY2 = e.getY(); break;
                case 3: newX1 = e.getX(); newY1 = e.getY(); break;
            }
            line.setX(newX1); line.setY(newY1);
            line.setX2(newX2); line.setY2(newY2);
        }

        canvas.refresh();
        return;
    }

        // ---- 旋转逻辑 ----
        if (rotating && rotatingTarget != null) {
            var center = rotatingTarget.getCenter();
            double angle = Math.atan2(e.getY() - center.y, e.getX() - center.x);
            double newRotationRad = angle - startAngle;
            rotatingTarget.setRotation(Math.toDegrees(newRotationRad));
            canvas.refresh();
            return;
        }

        // ---- 创建对象拖拽逻辑 ----
        if (creating && previewShape != null &&
            (tool == ToolController.ToolType.RECT || tool == ToolController.ToolType.ELLIPSE ||
            tool == ToolController.ToolType.LINE || tool == ToolController.ToolType.TEXT)) {

            int newX = Math.min(e.getX(), startX);
            int newY = Math.min(e.getY(), startY);
            int newWidth = Math.abs(e.getX() - startX);
            int newHeight = Math.abs(e.getY() - startY);

            if (previewShape instanceof RectObject || previewShape instanceof EllipseObject || previewShape instanceof TextObject) {
                previewShape.setX(newX);
                previewShape.setY(newY);
                previewShape.setWidth(newWidth);
                previewShape.setHeight(newHeight);
            } else if (previewShape instanceof LineObject line) {
                line.setX2(e.getX());
                line.setY2(e.getY());
            }

            canvas.refresh();
            return;
        }

        // ---- SELECT 拖动逻辑 ----
        if (!selectionManager.hasSelection()) return;
        SlideObject obj = selectionManager.getSelected();
        int dx = e.getX() - lastX;
        int dy = e.getY() - lastY;
        obj.moveBy(dx, dy);
        if (currentMoveCmd != null) currentMoveCmd.accumulateMove(dx, dy);
        lastX = e.getX();
        lastY = e.getY();
        canvas.refresh();
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        // --- 结束旋转 ---
        if (scaling) {
            scaling = false;
            scalingTarget = null;
            return;
        }


            // --- 结束旋转 ---
        if (rotating) {
            rotating = false;
            rotatingTarget = null;
        }


        if (creating && previewShape != null) {
            // 加入文档
            commandManager.executeCommand(new AddObjectCommand(context.getDocument(), previewShape));

            // 清空预览
            previewShape = null;
            creating = false;
            canvas.setPreviewShape(null);
            canvas.refresh();
        }

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

    private void enterEditMode(TextObject t) {
        editingText = t;

        JTextField editor = canvas.getTextEditor();
        editor.setText(t.getText());
        editor.setFont(t.getFont());
        editor.setForeground(t.getColor());

        Rectangle r = t.getBounds();
        editor.setBounds(r.x, r.y, r.width + 20, r.height + 5); 
        editor.setVisible(true);
        editor.requestFocusInWindow(); // 更可靠的获得焦点
    }

    private void exitEditMode() {
        if (editingText == null) return;

        JTextField editor = canvas.getTextEditor();
        String newText = editor.getText().trim();

        // 写回 TextObject
        editingText.setText(newText);

        // 如果你愿意，也可以加入命令支持撤销
        // commandManager.executeCommand(new ChangeTextCommand(editingText, newText));

        editor.setVisible(false);
        editingText = null;

        canvas.refresh();
    }


    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            SlideObject obj = findObjectAt(e.getX(), e.getY());
            if (obj instanceof TextObject textObj) {
                enterEditMode(textObj);
            }
        }
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }



    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}

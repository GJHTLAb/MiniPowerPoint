package view;

import controller.ToolController;
import java.awt.*;
import javax.swing.*;
import model.objects.SlideObject;

public class ToolBarView extends JToolBar {

    private SlideCanvas canvas;
    private ToolController toolController;

    public ToolBarView(SlideCanvas canvas, ToolController toolController) {
        this.canvas = canvas;
        this.toolController = toolController;
        setFloatable(false);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton selectBtn = new JButton("选择");
        JButton rectBtn = new JButton("矩形");
        JButton ellipseBtn = new JButton("椭圆");
        JButton lineBtn = new JButton("直线");
        JButton textBtn = new JButton("文本");
        JButton imgBtn = new JButton("图片");
        JButton deleteBtn = new JButton("删除");

        JButton strokeColorBtn = new JButton("边框色(/文字大小)");
        JButton fillColorBtn = new JButton("填充色(/文字色)");

        selectBtn.addActionListener(e -> toolController.setCurrentTool(ToolController.ToolType.SELECT));
        rectBtn.addActionListener(e -> toolController.setCurrentTool(ToolController.ToolType.RECT));
        ellipseBtn.addActionListener(e -> toolController.setCurrentTool(ToolController.ToolType.ELLIPSE));
        lineBtn.addActionListener(e -> toolController.setCurrentTool(ToolController.ToolType.LINE));
        textBtn.addActionListener(e -> toolController.setCurrentTool(ToolController.ToolType.TEXT));
        imgBtn.addActionListener(e -> toolController.setCurrentTool(ToolController.ToolType.IMAGE));
        deleteBtn.addActionListener(e -> toolController.setCurrentTool(ToolController.ToolType.DELETE));

        // ---- 边框颜色 ----
        strokeColorBtn.addActionListener(e -> {
            SlideObject selected = toolController.getSelectedObject();
            if (selected != null) {
                if (selected instanceof model.objects.TextObject textObj) {
                    // ---- 改文字大小 ----
                    String sizeStr = JOptionPane.showInputDialog(this, "请输入文字大小（数字）", textObj.getFont().getSize());
                    if (sizeStr != null && !sizeStr.isEmpty()) {
                        try {
                            int newSize = Integer.parseInt(sizeStr);
                            if (newSize > 0) {
                                Font oldFont = textObj.getFont();
                                textObj.setFont(new Font(oldFont.getName(), oldFont.getStyle(), newSize));
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "请输入有效数字", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    // ---- 普通图形改边框色 ----
                    Color newColor = JColorChooser.showDialog(this, "选择边框颜色", selected.getStrokeColor());
                    if (newColor != null) {
                        toolController.setSelectedStrokeColor(newColor);
                    }
                }
                canvas.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "请先选择一个图形", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });


        // ---- 填充颜色 ----
        fillColorBtn.addActionListener(e -> {
            SlideObject selected = toolController.getSelectedObject();
            if (selected != null) {
                if (selected instanceof model.objects.TextObject textObj) {
                    // ---- 改文字颜色 ----
                    Color newColor = JColorChooser.showDialog(this, "选择文字颜色", textObj.getColor());
                    if (newColor != null) {
                        textObj.setColor(newColor);
                    }
                } else {
                    // ---- 普通图形改填充色 ----
                    Color newColor = JColorChooser.showDialog(this, "选择填充颜色", selected.getFillColor());
                    if (newColor != null) {
                        selected.setFillColor(newColor);
                    }
                }
                canvas.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "请先选择一个图形", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });


        add(selectBtn);
        add(rectBtn);
        add(ellipseBtn);
        add(lineBtn);
        add(textBtn);
        add(imgBtn);
        add(deleteBtn);
        add(strokeColorBtn);
        add(fillColorBtn);
    }
}

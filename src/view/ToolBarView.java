package view;

import controller.ToolController;
import model.factory.ImageFactory;
import model.factory.ShapeFactory;

import javax.swing.*;
import java.awt.*;

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

        selectBtn.addActionListener(e ->
                toolController.setCurrentTool(ToolController.ToolType.SELECT)
        );

        rectBtn.addActionListener(e ->
                toolController.setCurrentTool(ToolController.ToolType.RECT)
        );

        ellipseBtn.addActionListener(e ->
                toolController.setCurrentTool(ToolController.ToolType.ELLIPSE)
        );

        lineBtn.addActionListener(e ->
                toolController.setCurrentTool(ToolController.ToolType.LINE)
        );

        textBtn.addActionListener(e ->
                toolController.setCurrentTool(ToolController.ToolType.TEXT)
        );

        imgBtn.addActionListener(e -> {
                toolController.setCurrentTool(ToolController.ToolType.IMAGE);
        });

        add(selectBtn);
        add(rectBtn);
        add(ellipseBtn);
        add(lineBtn);
        add(textBtn);
        add(imgBtn);
    }
}

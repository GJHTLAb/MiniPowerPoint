package view;

import context.DocumentContext;
import controller.CanvasController;
import java.awt.*;
import javax.swing.*;
import model.objects.SlideObject;
import model.SlidePage;
import model.objects.SlideObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SlideCanvas extends JPanel {

    private DocumentContext context;
    private JTextField textEditor = new JTextField();
    private SlideObject previewShape = null;
    private CanvasController controller;
    private SlidePage currentPage;
    Dimension dimension;

    public SlideCanvas(DocumentContext context) {
        this.context = context;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        setLayout(null); // 绝对布局

        textEditor = new JTextField();
        textEditor.setVisible(false);
        add(textEditor);
    }

    public JTextField getTextEditor() {
        return textEditor;
    }

    public void setPreviewShape(SlideObject shape) {
        this.previewShape = shape;
        dimension = new Dimension(800,600);
        setPreferredSize(dimension);

        // 初始化当前页面
        this.currentPage = context.getDocument().getCurrentPage();
    }

    public void setPage(SlidePage page) {
        this.currentPage = page;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (currentPage == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 画已有对象
        for (SlideObject obj : context.getDocument().getCurrentPage().getObjects()) {
            obj.draw(g2);
        }

        // 画预览对象（如果有）
        if (previewShape != null) {
            previewShape.draw(g2);
        }
    }

    public void refresh() {
        repaint();
    }

    public void setController(CanvasController controller) {
        this.controller = controller;
    }

    public CanvasController getController() {
        return controller;
    }
    public BufferedImage renderThumbnail(int pageIndex, double scale) {
        SlidePage page;
        int thumbWidth = (int) (this.getWidth() * scale);
        int  thumbHeight = (int) (this.getHeight() * scale);

        if(thumbWidth ==0 || thumbHeight ==0) {
            return new BufferedImage(217, 157, BufferedImage.TYPE_INT_ARGB);
        }
        try {
            page = context.getDocument().getPages().get(pageIndex);
        } catch (IndexOutOfBoundsException e) {
            // 页索引无效，返回空白缩略图
            return new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_ARGB);
        }

        BufferedImage img = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        // 启用高质量渲染设置
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  // 启用抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);  // 使用高质量插值
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);  // 启用高质量渲染
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);  // 矢量路径使用更高质量的线条绘制

        // 白色背景
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, thumbWidth, thumbHeight);
        g2.scale(scale, scale);
        // 绘制页面对象
        for (var obj : page.getObjects()) {
            obj.draw(g2); // 绘制每个页面对象
        }

        g2.dispose();
        return img;
    }



}

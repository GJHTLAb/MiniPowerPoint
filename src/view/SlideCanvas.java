package view;

import context.DocumentContext;
import controller.CanvasController;
import java.awt.*;
import javax.swing.*;
import model.objects.SlideObject;

public class SlideCanvas extends JPanel {

    private DocumentContext context;
    private JTextField textEditor = new JTextField();
    private SlideObject previewShape = null;
    private CanvasController controller;

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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

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
}

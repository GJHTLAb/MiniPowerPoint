package view;

import context.DocumentContext;
import model.SlideDocument;
import model.objects.SlideObject;

import javax.swing.*;
import java.awt.*;

public class SlideCanvas extends JPanel {

    private DocumentContext context;

    public SlideCanvas(DocumentContext context) {
        this.context = context;

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));

        // Controller 可以在这里添加监听器，例如：
        // addMouseListener(new CanvasMouseListener(document, this));
        // addMouseMotionListener(...)
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (SlideObject obj : context.getDocument().getCurrentPage().getObjects()) {
            obj.draw(g2);
        }
    }

    public void refresh() {
        repaint();
    }
}

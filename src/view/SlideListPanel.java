package view;

import context.DocumentContext;
import model.SlideDocument;

import javax.swing.*;
import java.awt.*;

public class SlideListPanel extends JPanel {

    private DocumentContext context;
    private SlideCanvas canvas;

    public SlideListPanel(DocumentContext context, SlideCanvas canvas) {
        this.context = context;
        this.canvas = canvas;

        setPreferredSize(new Dimension(180, 800));
        setBackground(new Color(240, 240, 240));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        refreshList();
    }

    public void refreshList() {
        removeAll();

        for (int i = 0; i < context.getDocument().getPages().size(); i++) {
            int pageIndex = i;

            JButton btn = new JButton("页面 " + (i + 1));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);

            btn.addActionListener(e -> {
                context.getDocument().setCurrentPage(pageIndex);
                canvas.refresh();
            });

            add(btn);
        }

        revalidate();
        repaint();
    }
}

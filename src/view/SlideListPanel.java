package view;

import context.DocumentContext;
import model.SlidePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SlideListPanel extends JPanel {

    private DocumentContext context;
    private SlideCanvas canvas;
    private List<JPanel> pagePanels = new ArrayList<>(); // 改为存储包含页码和按钮的面板
    private double scale;
    private JScrollPane scrollPane; // 添加滚动面板
    private JPanel contentPanel; // 内容面板
    private int scrollPosition = 0;
    private int maxScroll = 0;

    // 将右键菜单声明为类级别的成员变量
    private JPopupMenu popupMenu;

    // 常量定义
    private static final int PANEL_WIDTH = 220;
    private static final int THUMBNAIL_MARGIN = 10;
    private static final int PAGE_NUMBER_HEIGHT = 25;

    public SlideListPanel(DocumentContext context, SlideCanvas canvas) {
        this.context = context;
        this.canvas = canvas;
        setPreferredSize(new Dimension(PANEL_WIDTH, 900));
        setBackground(new Color(240, 240, 240));
        scale = 0.225;

        // 使用BorderLayout作为主布局
        setLayout(new BorderLayout());

        // 创建内容面板
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 240, 240));

        // 创建滚动面板
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 设置滚动速度

        // 自定义滚动条样式
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        verticalScrollBar.setUI(new ModernScrollBarUI());

        add(scrollPane, BorderLayout.CENTER);

        // 初始化右键菜单
        popupMenu = createPopupMenu();

        // 添加鼠标滚轮监听
        setupMouseWheelListening();

        refreshList();
    }

    // 设置鼠标滚轮监听
    private void setupMouseWheelListening() {
        // 为滚动面板和内容面板都添加鼠标滚轮监听
        MouseWheelListener wheelListener = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (canScroll()) {
                    JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                    int notches = e.getWheelRotation();
                    int scrollAmount = notches * verticalBar.getUnitIncrement() * 3; // 增加滚动速度

                    int newValue = verticalBar.getValue() + scrollAmount;
                    newValue = Math.max(verticalBar.getMinimum(),
                            Math.min(newValue, verticalBar.getMaximum() - verticalBar.getVisibleAmount()));

                    verticalBar.setValue(newValue);
                }
            }
        };

        scrollPane.addMouseWheelListener(wheelListener);
        contentPanel.addMouseWheelListener(wheelListener);

        // 也为当前面板添加监听
        this.addMouseWheelListener(wheelListener);
    }

    // 检查是否可以滚动
    private boolean canScroll() {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        return verticalBar.getMaximum() > verticalBar.getVisibleAmount();
    }

    public void refreshImages(int Index) {
        if (Index >= 0 && Index < pagePanels.size()) {
            JPanel pagePanel = pagePanels.get(Index);
            // 更新缩略图
            JButton pageButton = (JButton) pagePanel.getComponent(1); // 第二个组件是按钮
            pageButton.setIcon(new ImageIcon(canvas.renderThumbnail(Index, scale)));
        }
    }

    public void refreshList() {
        contentPanel.removeAll();
        pagePanels.clear();

        // 获取所有页面
        List<SlidePage> pages = context.getDocument().getPages();

        for (int i = 0; i < pages.size(); i++) {
            // 创建单个页面的容器面板
            JPanel pagePanel = createPagePanel(i);
            pagePanels.add(pagePanel);
            contentPanel.add(pagePanel);
        }

        // 添加弹性空间以推动所有内容向上
        contentPanel.add(Box.createVerticalGlue());

        revalidate();
        repaint();

        // 更新滚动限制
        updateScrollLimits();
    }

    // 创建单个页面的面板（包含页码和缩略图按钮）
    private JPanel createPagePanel(int pageIndex) {
        JPanel pagePanel = new JPanel();
        pagePanel.setLayout(new BorderLayout());
        pagePanel.setBackground(new Color(240, 240, 240));
        pagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 创建页码标签
        JLabel pageNumberLabel = new JLabel("Page " + (pageIndex + 1));
        pageNumberLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pageNumberLabel.setForeground(new Color(80, 80, 80));
        pageNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pageNumberLabel.setPreferredSize(new Dimension(PANEL_WIDTH - 10, PAGE_NUMBER_HEIGHT));
        pageNumberLabel.setOpaque(true);
        pageNumberLabel.setBackground(new Color(225, 225, 225));
        pageNumberLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));

        // 创建缩略图按钮
        JButton pageButton = new JButton();
        pageButton.setIcon(new ImageIcon(canvas.renderThumbnail(pageIndex, scale)));

        // 计算合适的按钮大小
        ImageIcon icon = (ImageIcon) pageButton.getIcon();
        int buttonWidth = Math.min(icon.getIconWidth() + THUMBNAIL_MARGIN, PANEL_WIDTH - 20);
        int buttonHeight = icon.getIconHeight() + THUMBNAIL_MARGIN;
        pageButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        pageButton.setBackground(Color.WHITE);
        pageButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        pageButton.setContentAreaFilled(true);
        pageButton.setFocusPainted(false);

        // 添加鼠标悬停效果
        pageButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                pageButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(100, 150, 255), 2),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pageButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    // 右键点击页面按钮
                    popupMenu.show(pageButton, e.getX(), e.getY());
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    context.getDocument().setCurrentPage(pageIndex);
                    canvas.setPage(context.getDocument().getCurrentPage());
                    // 高亮显示当前选中的页面
                    highlightCurrentPage(pageIndex);
                }
            }
        });

        // 将页码标签和按钮添加到页面面板
        pagePanel.add(pageNumberLabel, BorderLayout.NORTH);
        pagePanel.add(pageButton, BorderLayout.CENTER);

        // 添加一些间距
        pagePanel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.SOUTH);

        return pagePanel;
    }

    // 高亮显示当前选中的页面
    private void highlightCurrentPage(int currentIndex) {
        for (int i = 0; i < pagePanels.size(); i++) {
            JPanel pagePanel = pagePanels.get(i);
            JLabel pageNumberLabel = (JLabel) pagePanel.getComponent(0);
            JButton pageButton = (JButton) pagePanel.getComponent(1);

            if (i == currentIndex) {
                // 当前选中的页面
                pageNumberLabel.setBackground(new Color(100, 150, 255));
                pageNumberLabel.setForeground(Color.WHITE);
                pageButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(100, 150, 255), 2),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            } else {
                // 未选中的页面
                pageNumberLabel.setBackground(new Color(225, 225, 225));
                pageNumberLabel.setForeground(new Color(80, 80, 80));
                pageButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        }
    }

    // 更新滚动限制
    private void updateScrollLimits() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
            maxScroll = Math.max(0, verticalBar.getMaximum() - verticalBar.getVisibleAmount());
        });
    }

    // 创建右键菜单
    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        // 新建页面选项
        JMenuItem newPageItem = new JMenuItem("New Page");
        newPageItem.addActionListener(e -> {
            context.getDocument().addPage(new SlidePage());
            refreshList();
            // 自动滚动到新页面
            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                verticalBar.setValue(verticalBar.getMaximum());
            });
        });
        popupMenu.add(newPageItem);

        // 删除页面选项
        JMenuItem deletePageItem = new JMenuItem("Delete Page");
        deletePageItem.addActionListener(e -> {
            int currentPageIndex = context.getDocument().getCurrentPageIndex();
            if (context.getDocument().getPages().size() > 1) {
                context.getDocument().removePage(currentPageIndex);
                refreshList();
                canvas.refresh();
            } else {
                JOptionPane.showMessageDialog(SlideListPanel.this, "Cannot delete the last page.");
            }
        });
        popupMenu.add(deletePageItem);

        return popupMenu;
    }

    // 现代化的滚动条UI
    private static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        private static final int SCROLL_BAR_WIDTH = 8;
        private static final Color THUMB_COLOR = new Color(150, 150, 150, 150);
        private static final Color THUMB_HOVER_COLOR = new Color(100, 100, 100, 200);
        private boolean hover = false;

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // 不绘制轨道
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color color = hover ? THUMB_HOVER_COLOR : THUMB_COLOR;
            g2.setColor(color);

            // 绘制圆角矩形作为滑块
            int arc = SCROLL_BAR_WIDTH;
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, arc, arc);

            g2.dispose();
        }

        @Override
        protected void setThumbBounds(int x, int y, int width, int height) {
            super.setThumbBounds(x, y, width, height);
        }

        @Override
        protected TrackListener createTrackListener() {
            return new MyTrackListener();
        }

        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(SCROLL_BAR_WIDTH, 40);
        }

        @Override
        protected Dimension getMaximumThumbSize() {
            return new Dimension(SCROLL_BAR_WIDTH, Integer.MAX_VALUE);
        }

        private class MyTrackListener extends TrackListener {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                scrollbar.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                scrollbar.repaint();
            }
        }
    }
}
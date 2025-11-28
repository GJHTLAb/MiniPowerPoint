package view;

import context.DocumentContext;
import model.SlidePage;
import controller.SlideListPanelController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

public class SlideListPanel extends JPanel {

    private DocumentContext context;
    private SlideCanvas canvas;
    private List<JPanel> pagePanels = new ArrayList<>();
    private double scale;

    // 常量定义
    private static final int PANEL_WIDTH = 220;
    private static final int THUMBNAIL_MARGIN = 10;
    private static final int PAGE_NUMBER_HEIGHT = 25;

    // Controller 引用
    private SlideListPanelController controller;

    // 滚动相关
    private JScrollPane scrollPane;
    private JPanel contentPanel;
    private int scrollPosition = 0;

    public SlideListPanel(DocumentContext context, SlideCanvas canvas) {
        this.context = context;
        this.canvas = canvas;
        setPreferredSize(new Dimension(PANEL_WIDTH, 900));
        setBackground(new Color(240, 240, 240));
        scale = 0.225;

        // 使用 BorderLayout 作为主布局
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

        // 添加鼠标滚轮监听
        setupMouseWheelListening();

        // 为内容面板添加鼠标监听器来处理空白区域右键点击
        setupContentPanelMouseListener();

        refreshList();
    }

    /**
     * 设置内容面板的鼠标监听器
     */
    private void setupContentPanelMouseListener() {
        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showGlobalPopupMenu(e);
                }
            }
        });
    }

    /**
     * 显示全局右键菜单
     */
    private void showGlobalPopupMenu(MouseEvent e) {
        JPopupMenu globalMenu = createGlobalPopupMenu();
        if (globalMenu != null) {
            globalMenu.show(contentPanel, e.getX(), e.getY());
        }
    }

    /**
     * 设置鼠标滚轮监听
     */
    private void setupMouseWheelListening() {
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

        // 为多个组件添加滚轮监听
        scrollPane.addMouseWheelListener(wheelListener);
        contentPanel.addMouseWheelListener(wheelListener);
        this.addMouseWheelListener(wheelListener);
    }

    /**
     * 检查是否可以滚动
     */
    private boolean canScroll() {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        return verticalBar.getMaximum() > verticalBar.getVisibleAmount();
    }

    /**
     * 设置控制器
     */
    public void setController(SlideListPanelController controller) {
        this.controller = controller;
    }

    /**
     * 刷新页面列表
     */
    public void refreshList() {
        contentPanel.removeAll();
        pagePanels.clear();

        List<SlidePage> pages = context.getDocument().getPages();

        for (int i = 0; i < pages.size(); i++) {
            JPanel pagePanel = createPagePanel(i);
            pagePanels.add(pagePanel);
            contentPanel.add(pagePanel);
        }

        // 添加弹性空间以推动所有内容向上
        contentPanel.add(Box.createVerticalGlue());

        contentPanel.revalidate();
        contentPanel.repaint();

        // 更新滚动限制
        updateScrollLimits();
    }

    /**
     * 更新滚动限制
     */
    private void updateScrollLimits() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
            // 确保滚动位置有效
            if (verticalBar.getValue() > verticalBar.getMaximum() - verticalBar.getVisibleAmount()) {
                verticalBar.setValue(verticalBar.getMaximum() - verticalBar.getVisibleAmount());
            }
        });
    }

    /**
     * 刷新指定页面的缩略图
     */
    public void refreshImages(int index) {
        if (index >= 0 && index < pagePanels.size()) {
            JPanel pagePanel = pagePanels.get(index);
            JButton pageButton = (JButton) pagePanel.getComponent(1);
            pageButton.setIcon(new ImageIcon(canvas.renderThumbnail(index, scale)));
            updatePageHighlight();
        }
    }

    /**
     * 创建单个页面面板
     */
    private JPanel createPagePanel(int pageIndex) {
        JPanel pagePanel = new JPanel();
        pagePanel.setLayout(new BorderLayout());
        pagePanel.setBackground(new Color(240, 240, 240));
        pagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 创建页码标签
        JLabel pageNumberLabel = createPageNumberLabel(pageIndex);

        // 创建缩略图按钮
        JButton pageButton = createThumbnailButton(pageIndex);

        // 将组件添加到面板
        pagePanel.add(pageNumberLabel, BorderLayout.NORTH);
        pagePanel.add(pageButton, BorderLayout.CENTER);
        pagePanel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.SOUTH);

        return pagePanel;
    }

    /**
     * 创建页码标签
     */
    private JLabel createPageNumberLabel(int pageIndex) {
        JLabel pageNumberLabel = new JLabel("Page " + (pageIndex + 1));
        pageNumberLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pageNumberLabel.setForeground(new Color(80, 80, 80));
        pageNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pageNumberLabel.setPreferredSize(new Dimension(PANEL_WIDTH - 20, PAGE_NUMBER_HEIGHT));
        pageNumberLabel.setOpaque(true);
        pageNumberLabel.setBackground(new Color(225, 225, 225));
        pageNumberLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));

        return pageNumberLabel;
    }

    /**
     * 创建缩略图按钮
     */
    private JButton createThumbnailButton(int pageIndex) {
        JButton pageButton = new JButton();
        pageButton.setIcon(new ImageIcon(canvas.renderThumbnail(pageIndex, scale)));

        // 计算合适的按钮大小
        ImageIcon icon = (ImageIcon) pageButton.getIcon();
        int buttonWidth = Math.min(icon.getIconWidth() + THUMBNAIL_MARGIN, PANEL_WIDTH - 20);
        int buttonHeight = icon.getIconHeight() + THUMBNAIL_MARGIN;
        pageButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        // 设置按钮样式
        pageButton.setBackground(Color.WHITE);
        pageButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        pageButton.setContentAreaFilled(true);
        pageButton.setFocusPainted(false);

        // 为每个按钮设置独立的右键菜单
        pageButton.setComponentPopupMenu(createButtonPopupMenu(pageIndex));

        // 左键点击选择页面
        pageButton.addActionListener(e -> {
            if (controller != null) {
                controller.selectPage(pageIndex);
            }
        });

        // 悬停效果
        pageButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (pageIndex != context.getDocument().getCurrentPageIndex()) {
                    pageButton.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(100, 150, 255), 2),
                            BorderFactory.createEmptyBorder(4, 4, 4, 4)
                    ));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (pageIndex != context.getDocument().getCurrentPageIndex()) {
                    pageButton.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                }
            }
        });

        return pageButton;
    }

    /**
     * 创建按钮的右键菜单
     */
    private JPopupMenu createButtonPopupMenu(int pageIndex) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem selectItem = new JMenuItem("Select Page");
        selectItem.addActionListener(e -> {
            if (controller != null) {
                controller.selectPage(pageIndex);
            }
        });
        menu.add(selectItem);

        menu.addSeparator();

        JMenuItem deleteItem = new JMenuItem("Delete Page");
        deleteItem.addActionListener(e -> {
            if (controller != null) {
                controller.deletePage(pageIndex);
            }
        });
        menu.add(deleteItem);

        JMenuItem duplicateItem = new JMenuItem("Duplicate Page");
        duplicateItem.addActionListener(e -> {
            if (controller != null) {
                controller.duplicatePage(pageIndex);
            }
        });
        menu.add(duplicateItem);

        return menu;
    }

    /**
     * 创建全局右键菜单（用于空白区域）
     */
    private JPopupMenu createGlobalPopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem newPageItem = new JMenuItem("New Page");
        newPageItem.addActionListener(e -> {
            if (controller != null) {
                controller.addNewPage();
            }
        });
        menu.add(newPageItem);

        return menu;
    }

    /**
     * 更新页面高亮显示
     */
    private void updatePageHighlight() {
        int currentIndex = context.getDocument().getCurrentPageIndex();

        for (int i = 0; i < pagePanels.size(); i++) {
            JPanel pagePanel = pagePanels.get(i);
            JLabel pageNumberLabel = (JLabel) pagePanel.getComponent(0);
            JButton pageButton = (JButton) pagePanel.getComponent(1);

            if (i == currentIndex) {
                pageNumberLabel.setBackground(new Color(100, 150, 255));
                pageNumberLabel.setForeground(Color.WHITE);
                pageButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(100, 150, 255), 2),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            } else {
                pageNumberLabel.setBackground(new Color(225, 225, 225));
                pageNumberLabel.setForeground(new Color(80, 80, 80));
                pageButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        }
    }

    /**
     * 现代化的滚动条UI
     */
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
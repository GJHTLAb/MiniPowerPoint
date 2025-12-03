package view;

import command.CommandManager;
import context.DocumentContext;
import model.SlideDocument;
import controller.*;
import file.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

public class MainWindow extends JFrame {

    private DocumentContext context;

    private SlideCanvas canvas;
    private SelectionManager selectionManager;
    private ToolController toolController;
    private CommandManager commandManager;
    private CanvasController canvasController;
    private FileController fileController;
    private SlideListPanelController slideListPanelController;
    private SlideListPanel slideListPanel ;

    public MainWindow(DocumentContext context) {
        this.context = context;
        this.canvas = new SlideCanvas(context);
        this.slideListPanel = new SlideListPanel(context, canvas);
        this.selectionManager = new SelectionManager();
        this.toolController = new ToolController();
        this.commandManager = new CommandManager();
        this.fileController = new FileController();
        this.slideListPanelController = new SlideListPanelController(context, slideListPanel, canvas, commandManager);
        this.canvasController = new CanvasController(context, slideListPanel, canvas, selectionManager, toolController, commandManager);
        slideListPanel.setController(slideListPanelController);
        setTitle("MiniPowerPoint- 幻灯片制作软件");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1200,800));
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new BorderLayout());

        setJMenuBar(new MenuBarView(commandManager, canvas, context, fileController, this));

        add(new ToolBarView(canvas, toolController), BorderLayout.NORTH);
        add(slideListPanel, BorderLayout.WEST);

        add(canvas, BorderLayout.CENTER);

        setVisible(true);
        setupEventQueueMonitor();
    }

    private void setupEventQueueMonitor() {
        // 替换事件队列
        EventQueue originalQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new EventQueue() {
            @Override
            protected void dispatchEvent(AWTEvent event) {
                // 先让事件正常处理
                super.dispatchEvent(event);

                // 事件处理完成后，检查是否需要刷新
                if (event instanceof MouseEvent) {
                    MouseEvent mouseEvent = (MouseEvent) event;
                    handleMouseEventPostProcess(mouseEvent);
                }
            }
        });
    }

    /**
     * 鼠标事件后处理
     */
    private void handleMouseEventPostProcess(MouseEvent event) {
        // 只在特定情况下刷新
        if (shouldRefreshAfterEvent(event)) {
            SwingUtilities.invokeLater(() -> {
                refreshAllDisplays();
            });
        }
    }

    /**
     * 判断事件后是否需要刷新
     */
    private boolean shouldRefreshAfterEvent(MouseEvent event) {
        // 只处理释放事件，避免干扰按下事件的处理
        if (event.getID() != MouseEvent.MOUSE_RELEASED) {
            return false;
        }

        // 检查事件源，避免重复刷新
        Component source = event.getComponent();
        if (source instanceof JButton) {
            // 按钮点击由按钮自己的监听器处理，我们不需要额外刷新
            return false;
        }

        // 只对画布和特定区域的事件进行刷新
        if (source == canvas) {
            return true;
        }

        return true;
    }


    private void refreshAllDisplays() {
        // 您的刷新逻辑
        if (canvas != null) {
            canvas.setPage(context.getDocument().getCurrentPage());
            canvas.refresh();
        }
        if (slideListPanel != null) {
            slideListPanel.refreshList();
            int currentIndex = context.getDocument().getCurrentPageIndex();
            slideListPanel.refreshImages(currentIndex);

        }
    }
}

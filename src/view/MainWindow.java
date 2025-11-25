package view;

import command.CommandManager;
import context.DocumentContext;
import model.SlideDocument;
import controller.*;
import file.*;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private DocumentContext context;

    private SlideCanvas canvas;
    private SelectionManager selectionManager;
    private ToolController toolController;
    private CommandManager commandManager;
    private CanvasController canvasController;
    private FileController fileController;

    public MainWindow(DocumentContext context) {
        this.context = context;
        this.canvas = new SlideCanvas(context);

        this.selectionManager = new SelectionManager();
        this.toolController = new ToolController();
        this.commandManager = new CommandManager();
        this.fileController = new FileController();

        this.canvasController = new CanvasController(context, canvas, selectionManager, toolController, commandManager);

        setTitle("MiniPowerPoint- 幻灯片制作软件");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        setJMenuBar(new MenuBarView(commandManager, canvas, context, fileController, this));

        add(new ToolBarView(canvas, toolController), BorderLayout.NORTH);

        add(new SlideListPanel(context, canvas), BorderLayout.WEST);

        add(canvas, BorderLayout.CENTER);

        setVisible(true);
    }
}

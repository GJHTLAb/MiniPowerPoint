package view;

import command.*;
import context.DocumentContext;
import export.*;
import model.*;
import file.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class MenuBarView extends JMenuBar {

    private DocumentContext context;
    private CommandManager commandManager;
    private SlideCanvas slideCanvas;
    private FileController fileController;

    public MenuBarView(CommandManager commandManager, SlideCanvas slideCanvas, DocumentContext context, FileController fileController, MainWindow mainWindow) {

        this.context = context;
        this.commandManager = commandManager;
        this.slideCanvas = slideCanvas;
        this.fileController = fileController;

        JMenu fileMenu = new JMenu("文件");

        JMenuItem newFile = new JMenuItem("新建");
        JMenuItem openFile = new JMenuItem("打开");
        JMenuItem saveFile = new JMenuItem("保存");
        JMenuItem exportPNG = new JMenuItem("导出为 PNG");
        JMenuItem exportJPG = new JMenuItem("导出为 JPG");
        JMenuItem exportPDF = new JMenuItem("导出为 PDF");

        // 示例：绑定事件（实际逻辑由 controller 补充）
        newFile.addActionListener((ActionEvent e) -> {
            context.setDocument(new SlideDocument());
            slideCanvas.refresh();
        });

        saveFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                FileController.save(context.getDocument(), path + ".json");
            }
        });

        openFile.addActionListener((ActionEvent e) -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                SlideDocument newDoc = FileController.load(path);
                if (newDoc != null) {
                    System.out.println("newDoc replaced");
                    context.setDocument(newDoc);
                    slideCanvas.refresh();
                }
            }
        });

        exportPNG.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                ImageExporter.exportPNG(context.getDocument().getCurrentPage(), path + ".png");
            }
        });

        exportJPG.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                ImageExporter.exportJPG(context.getDocument().getCurrentPage(), path + ".jpg");
            }
        });

        exportPDF.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                ImageExporter.exportPDF(context.getDocument(), path + ".pdf");
            }
        });


        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.addSeparator();
        fileMenu.add(exportPNG);
        fileMenu.add(exportJPG);
        fileMenu.add(exportPDF);

        add(fileMenu);

        JMenu commandMenu = new JMenu("命令");

        JMenuItem undo = new JMenuItem("撤销");
        JMenuItem redo = new JMenuItem("重做");

        undo.addActionListener(e -> {
            commandManager.undo();
            slideCanvas.refresh();
        });

        redo.addActionListener(e -> {
            commandManager.redo();
            slideCanvas.refresh();
        });

        commandMenu.add(undo);
        commandMenu.add(redo);

        add(commandMenu);
    }
}

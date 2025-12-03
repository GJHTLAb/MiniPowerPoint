package controller;

import context.DocumentContext;
import model.SlideDocument;
import model.SlidePage;
import view.SlideCanvas;
import view.SlideListPanel;
import command.*;

import javax.swing.*;

public class SlideListPanelController {

    private DocumentContext context;
    private SlideCanvas canvas;
    private SlideListPanel slideListPanel;
    private CommandManager commandManager;

    public SlideListPanelController(DocumentContext context, SlideListPanel slideListPanel,
                                    SlideCanvas canvas, CommandManager commandManager) {
        this.context = context;
        this.canvas = canvas;
        this.slideListPanel = slideListPanel;
        this.commandManager = commandManager;
    }

    public void refresh() {
        if (slideListPanel != null) {
            slideListPanel.refreshList();
            int currentIndex = context.getDocument().getCurrentPageIndex();
            slideListPanel.refreshImages(currentIndex);
        }
    }

    public void selectPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < context.getDocument().getPages().size()) {
            context.getDocument().setCurrentPage(pageIndex);
            if (canvas != null) {
                canvas.setPage(context.getDocument().getCurrentPage());
            }
        }
    }

    public void insertPageAt(int position) {
        // 在指定位置插入新页面的逻辑
        SlidePage newPage = new SlidePage();
        AddPageCommand cmd = new AddPageCommand(context.getDocument(), newPage,position);
        commandManager.executeCommand(cmd);
        selectPage(position);
    }

    public void addNewPage() {
        SlidePage newPage = new SlidePage();
        AddPageCommand cmd = new AddPageCommand(context.getDocument(), newPage);
        commandManager.executeCommand(cmd);

        int newPageIndex = context.getDocument().getPages().size() - 1;
        selectPage(newPageIndex);
    }

    public void deletePage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < getPageCount()) {
            if (getPageCount() > 1) {
                SlidePage pageToRemove = context.getDocument().getPages().get(pageIndex);
                RemovePageCommand cmd = new RemovePageCommand(context.getDocument(), pageToRemove, pageIndex);
                commandManager.executeCommand(cmd);

                int newIndex = Math.min(pageIndex, getPageCount() - 1);
                selectPage(newIndex);
            } else {
                JOptionPane.showMessageDialog(slideListPanel, "Cannot delete the last page.");
            }
        }
    }

    public void duplicatePage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < getPageCount()) {
            SlidePage currentPage = context.getDocument().getPages().get(pageIndex);
            SlidePage duplicatedPage = new SlidePage(currentPage);
            AddPageCommand cmd = new AddPageCommand(context.getDocument(), duplicatedPage);
            commandManager.executeCommand(cmd);

            int newPageIndex = getPageCount() - 1;
            selectPage(newPageIndex);
        }
    }

    private int getPageCount() {
        return context.getDocument().getPages().size();
    }
}
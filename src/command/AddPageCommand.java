package command;

import model.SlideDocument;
import model.SlidePage;

public class AddPageCommand extends Command {
    private SlideDocument document;
    private SlidePage page;
    private int index;

    public AddPageCommand(SlideDocument document, SlidePage page) {
        this.document = document;
        this.page = page;
        this.index = -1;
        setCommandType(CommandType.AddPage);
    }

    public AddPageCommand(SlideDocument document, SlidePage page, int index) {
        this.document = document;
        this.page = page;
        this.index = index;
        setCommandType(CommandType.AddPage);
    }

    @Override
    public void execute() {
        if (index == -1) {
            document.addPage(page);
        } else {
            document.insertPage(index, page);
        }
    }

    @Override
    public void undo() {
        if (index == -1) {
            // 删除最后一个页面
            document.removePage(document.getPages().size() - 1);
        } else {
            // 删除指定位置的页面
            document.removePage(index);
        }
    }
}
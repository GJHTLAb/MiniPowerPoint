package command;

import model.SlideDocument;
import model.SlidePage;

public class RemovePageCommand extends Command {
    private SlideDocument document;
    private SlidePage page;
    private int index;

    public RemovePageCommand(SlideDocument document, SlidePage page, int index) {
        this.document = document;
        this.page = page;
        this.index = index;
        setCommandType(CommandType.RemovePage);
    }

    @Override
    public void execute() {
        document.removePage(index);
    }

    @Override
    public void undo() {
        document.insertPage(index, page);
    }
}
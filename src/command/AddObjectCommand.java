package command;

import model.SlideDocument;
import model.objects.SlideObject;

public class AddObjectCommand extends Command {

    private SlideDocument document;
    private SlideObject object;
    private int PageIndex;

    public AddObjectCommand(SlideDocument document, SlideObject obj, int pageIndex) {
        this.document = document;
        this.object = obj;
        this.PageIndex = pageIndex;
        super.setCommandType(CommandType.AddObject);
    }

    @Override
    public void execute() {
        System.out.println("execute object=" + object);
        document.getPage(PageIndex).getObjects().add(object);
    }

    @Override
    public void undo() {
        System.out.println("undo object=" + object);
        document.getPage(PageIndex).getObjects().remove(object);
    }
}

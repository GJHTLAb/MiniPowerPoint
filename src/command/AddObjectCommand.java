package command;

import model.SlideDocument;
import model.objects.SlideObject;

public class AddObjectCommand extends Command {

    private SlideDocument document;
    private SlideObject object;

    public AddObjectCommand(SlideDocument document, SlideObject obj) {
        this.document = document;
        this.object = obj;
        super.setCommandType(CommandType.AddObject);
    }

    @Override
    public void execute() {
        System.out.println("execute object=" + object);
        document.getCurrentPage().addObject(object);
    }

    @Override
    public void undo() {
        System.out.println("undo object=" + object);
        document.getCurrentPage().removeObject(object);
    }
}

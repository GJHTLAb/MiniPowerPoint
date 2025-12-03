package command;

import model.SlideDocument;
import model.objects.SlideObject;

public class RemoveObjectCommand extends Command {

    private SlideDocument document;
    private SlideObject object;

    public RemoveObjectCommand(SlideDocument document, SlideObject object) {
        this.document = document;
        this.object = object;
        super.setCommandType(CommandType.RemoveObject); // 如果你有 CommandType 枚举
    }

    @Override
    public void execute() {
        if (document != null && object != null) {
            document.getCurrentPage().removeObject(object);
        }
    }

    @Override
    public void undo() {
        if (document != null && object != null) {
            document.getCurrentPage().addObject(object);
        }
    }
}

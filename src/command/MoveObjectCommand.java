package command;

import model.objects.SlideObject;

public class MoveObjectCommand extends Command {

    private SlideObject object;
    private int totalDx = 0;
    private int totalDy = 0;

    public MoveObjectCommand(SlideObject object) {
        this.object = object;
        super.setCommandType(CommandType.MoveObject);
    }

    public void accumulateMove(int dx, int dy) {
        totalDx += dx;
        totalDy += dy;
    }

    @Override
    public void execute() {
        object.moveBy(totalDx, totalDy);
    }

    @Override
    public void undo() {
        object.moveBy(-totalDx, -totalDy);
    }
}

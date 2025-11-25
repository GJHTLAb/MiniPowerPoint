package command;

public abstract class  Command {

    private CommandType  commandType;

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public CommandType getCommandType(){
        return commandType;
    }

    public abstract void execute();

    public abstract void undo();
}

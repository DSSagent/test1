package comix.command;

public final class CommandManager {
    public void executeCommand(CollectionCommand command) {
        command.execute();
    }
}
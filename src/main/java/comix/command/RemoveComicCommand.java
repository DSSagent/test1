package comix.command;

import comix.collection.PersonalCollection;

public class RemoveComicCommand implements CollectionCommand {
    private final PersonalCollection collection;
    private final String id;

    public RemoveComicCommand(PersonalCollection collection, String id) {
        this.collection = collection;
        this.id = id;
    }

    @Override
    public void execute() {
        collection.removeComic(id);
    }
}

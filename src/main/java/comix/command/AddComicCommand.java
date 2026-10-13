package comix.command;

import comix.collection.PersonalCollection;
import comix.model.PersonalComic;

public class AddComicCommand implements CollectionCommand {
    private final PersonalCollection collection;
    private final PersonalComic comic;

    public AddComicCommand(PersonalCollection collection, PersonalComic comic) {
        this.collection = collection;
        this.comic = comic;
    }

    @Override
    public void execute() {
        collection.addComic(comic);
    }
}

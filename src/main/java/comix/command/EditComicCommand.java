package comix.command;

import comix.model.PersonalComic;
import comix.model.Comix;

public class EditComicCommand implements CollectionCommand {
    private final PersonalComic target;
    private final Comix replacement;
    private final Double baseValue;

    public EditComicCommand(PersonalComic target, Comix replacement, Double baseValue) {
        this.target = target;
        this.replacement = replacement;
        this.baseValue = baseValue;
    }

    @Override
    public void execute() {
        replacement.validateForManualEntry();
        target.setBaseValue(baseValue);
        target.setComic(replacement);
    }
}

package comix.command;

import comix.model.PersonalComic;

public class SlabComicCommand implements CollectionCommand {
    private final PersonalComic target;
    private final boolean slabbed;

    public SlabComicCommand(PersonalComic target, boolean slabbed) {
        this.target = target;
        this.slabbed = slabbed;
    }

    @Override
    public void execute() {
        target.setSlabbed(slabbed);
    }
}
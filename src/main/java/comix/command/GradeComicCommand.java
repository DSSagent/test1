package comix.command;

import comix.model.PersonalComic;

public class GradeComicCommand implements CollectionCommand {
    private final PersonalComic target;
    private final Integer grade;

    public GradeComicCommand(PersonalComic target, Integer grade) {
        this.target = target;
        this.grade = grade;
    }

    @Override
    public void execute() {
        target.setGrade(grade);
    }
}

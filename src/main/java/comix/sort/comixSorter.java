package comix.sort;

import comix.model.PersonalComic;
import java.util.List;

public interface comixSorter {
    List<PersonalComic> doSort(List<PersonalComic> comics);
}
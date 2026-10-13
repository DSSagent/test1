package comix.sort;

import comix.model.PersonalComic;
import comix.model.PublicationDates;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class sortByPublication implements comixSorter, Comparator<PersonalComic> {
    @Override
    public int compare(PersonalComic first, PersonalComic second) {
        LocalDate a = PublicationDates.sortKey(first.getComic().getPublicationDate());
        LocalDate b = PublicationDates.sortKey(second.getComic().getPublicationDate());
        // Put missing dates after known dates.
        if (a == null && b != null) {
            return 1;
        }
        if (a != null && b == null) {
            return -1;
        }
        if (a != null && b != null) {
            int result = a.compareTo(b);
            if (result != 0) {
                return result;
            }
        }
        return new sortBySeries().compare(first, second);
    }

    @Override
    public List<PersonalComic> doSort(List<PersonalComic> comics) {
        List<PersonalComic> result = new ArrayList<>(comics);
        Collections.sort(result, this);
        return result;
    }
}
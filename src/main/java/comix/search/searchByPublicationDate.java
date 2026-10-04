package comix.search;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import comix.model.Comix;

public class searchByPublicationDate implements comixSearch{
    @Override
    public List<Comix> doSearch(String term, Collection<Comix> comix) {
        List<Comix> results = new ArrayList<>();

        LocalDate searchDate = LocalDate.parse(term);

        for (Comix comic : comix) {
            if (comic.getPublicationDate().equals(searchDate)) {
                results.add(comic);
            }
    }

    return results;
}
}

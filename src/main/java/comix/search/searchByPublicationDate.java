package comix.search;

import comix.model.Comix;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class searchByPublicationDate implements comixSearch {
    @Override
    public List<Comix> doSearch(String term, Collection<Comix> comics, boolean exact) {
        List<Comix> results = new ArrayList<>();
        for (Comix comic : comics) {
            if (SearchSupport.matches(comic.getPublicationDate(), term, exact)) {
                results.add(comic);
            }
        }
        return results;
    }
}
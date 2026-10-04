package comix.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import comix.model.Comix;

public class searchbyPublisher implements comixSearch{
    @Override
    public List<Comix> doSearch(String term, Collection<Comix> comix) {
        List<Comix> results = new ArrayList<>();

        for (Comix comic : comix) {
            if (comic.getPublisher().equalsIgnoreCase(term)) {
                results.add(comic);
            }
        }

        return results;
    }
}

package comix.search;

import comix.model.Comix;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class searchByPersonalFields implements comixSearch {
    @Override
    public List<Comix> doSearch(String term, Collection<Comix> comics, boolean exact) {
        List<Comix> results = new ArrayList<>();
        for (Comix comic : comics) {
            boolean series = SearchSupport.matches(comic.getSeries(), term, exact)
                    || SearchSupport.matches(comic.getSeriesTitle(), term, exact);
            boolean characters = SearchSupport.matchesAny(comic.getPrincipalCharacters(), term, exact);
            boolean creators = SearchSupport.matchesAny(comic.getCreators(), term, exact);
            boolean description = SearchSupport.matches(comic.getDescription(), term, exact);
            if (series || characters || creators || description) {
                results.add(comic);
            }
        }
        return results;
    }
}
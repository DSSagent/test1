package comix.search;

import comix.model.Comix;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class searchByCharacter implements comixSearch {
    @Override
    public List<Comix> doSearch(String term, Collection<Comix> comics, boolean exact) {
        List<Comix> results = new ArrayList<>();
        for (Comix comic : comics) {
            if (SearchSupport.matchesAny(comic.getPrincipalCharacters(), term, exact)) {
                results.add(comic);
            }
        }
        return results;
    }
}
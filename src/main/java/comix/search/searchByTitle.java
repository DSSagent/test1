package comix.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import comix.model.Comix;

public class searchByTitle implements comixSearch{
    //NTS implement partial search by regex
    @Override
    public List<Comix> doSearch(String term, Collection<Comix> comix) {
        List<Comix> results = new ArrayList<>();

        for (Comix comic : comix) {
            if (comic.getTitle().equalsIgnoreCase(term)) {
                results.add(comic);
            }
        }
        return results;
    }
    
}

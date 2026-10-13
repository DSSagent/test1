package comix.search;

import comix.model.Comix;
import java.util.Collection;
import java.util.List;

public interface comixSearch {
    List<Comix> doSearch(String term, Collection<Comix> comics, boolean exact);
    default List<Comix> doSearch(String term, Collection<Comix> comics) {
        return doSearch(term, comics, true);
    }
}

package comix.search;
import comix.model.*;

import java.util.Collection;


public interface comixSearch {
    Collection<Comix> doSearch(String term, Collection<Comix> comix);
}

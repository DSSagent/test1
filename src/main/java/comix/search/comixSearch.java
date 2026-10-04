package comix.search;
import comix.model.*;

import java.util.Collection;
import java.util.List;


public interface comixSearch {
    List<Comix> doSearch(String term, Collection<Comix> comix);
}

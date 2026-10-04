package comix.search;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import comix.model.*;

public class searchBySeries implements comixSearch {

    @Override
    public List<Comix> doSearch(String term, Collection<Comix> comix) {
        List<Comix> results = new ArrayList<>();

        for (Comix comic : comix) {
            if (comic.getSeries().equalsIgnoreCase(term)) {
                results.add(comic);
            }
        }

        return results;
    }
}
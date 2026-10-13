package comix.collection;

import comix.model.Comix;
import comix.search.comixSearch;
import comix.search.searchBySeries;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class masterDatabase {
    private final Map<String, Comix> comics;
    private comixSearch searcher = new searchBySeries();
    public masterDatabase() {
        this(List.of());
    }
    public masterDatabase(Collection<Comix> records) {
        Map<String, Comix> map = new LinkedHashMap<>();
        int index = 1;
        for (Comix comic : records) {
            map.put("catalog-" + index++, comic);
        }
        comics = Collections.unmodifiableMap(map);
    }
    public Map<String, Comix> getComix() {
        return comics;
    }
    public void setComixSearcher(comixSearch searcher) {
        this.searcher = Objects.requireNonNull(searcher);
    }
    public List<Comix> doSearch(String term) {
        return doSearch(term, true);
    }
    public List<Comix> doSearch(String term, boolean exact) {
        return searcher.doSearch(term, comics.values(), exact);
    }
}
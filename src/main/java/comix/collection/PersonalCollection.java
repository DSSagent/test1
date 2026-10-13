package comix.collection;

import comix.model.PersonalComic;
import comix.model.Comix;
import comix.search.comixSearch;
import comix.search.searchByPersonalFields;
import comix.sort.comixSorter;
import comix.sort.sortBySeries;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;

public final class PersonalCollection {
    private final Map<String, PersonalComic> comics = new LinkedHashMap<>();
    private comixSorter sorter = new sortBySeries();
    private comixSearch searcher = new searchByPersonalFields();
    public void addComic(PersonalComic comic) {
        if (comics.containsKey(comic.getId())) {
            throw new IllegalArgumentException("Duplicate personal copy ID.");
        }
        comics.put(comic.getId(), comic);
    }
    public PersonalComic find(String id) {
        PersonalComic comic = comics.get(id);
        if (comic == null) {
            throw new IllegalArgumentException("No personal comic with that ID.");
        }
        return comic;
    }
    public void removeComic(String id) {
        find(id);
        comics.remove(id);
    }
    public List<PersonalComic> getComix() {
        return new ArrayList<>(comics.values());
    }
    public void setSorter(comixSorter sorter) {
        this.sorter = Objects.requireNonNull(sorter);
    }
    public List<PersonalComic> sort(List<PersonalComic> items) {
        return sorter.doSort(items);
    }
    public void setComixSearcher(comixSearch searcher) {
        this.searcher = Objects.requireNonNull(searcher);
    }
    public List<PersonalComic> search(String term, boolean exact) {
        List<Comix> records = new ArrayList<>();
        for (PersonalComic copy : comics.values()) {
            records.add(copy.getComic());
        }
        List<Comix> matches = searcher.doSearch(term, records, exact);
        List<PersonalComic> results = new ArrayList<>();
        for (PersonalComic copy : comics.values()) {
            if (matches.contains(copy.getComic())) {
                results.add(copy);
            }
        }
        return sorter.doSort(results);
    }
}

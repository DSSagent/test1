package comix;

import comix.collection.PersonalCollection;
import comix.model.Comix;
import comix.model.PersonalComic;
import comix.persistence.CatalogLoader;
import comix.persistence.CollectionStore;
import comix.search.searchBySeries;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class R1SmokeTest {
    @TempDir Path directory;

    private Comix example() {
        return new Comix("Marvel", "Spider-Man", 1, "2A", "Jan 1986",
                List.of("Stan Lee"), List.of("Peter Parker"), "A hero story",
                "First Adventure", "", "Comic", "Jan 01, 2020");
    }

    @Test void fullCatalogLoadsAndSeriesSearchSupportsBothModes() throws Exception {
        assertEquals(14303, new CatalogLoader().load(Path.of("data/comics.json")).getComix().size());
        Comix comic = example();
        searchBySeries search = new searchBySeries();
        assertEquals(List.of(comic), search.doSearch("SPIDER-MAN", List.of(comic), true));
        assertEquals(List.of(comic), search.doSearch("spider", List.of(comic), false));
        assertTrue(search.doSearch("spider", List.of(comic), true).isEmpty());
    }

    @Test void gradesAndSlabbingUseBaseValueWithoutCompounding() {
        PersonalComic copy = new PersonalComic(example(), 100.0);
        assertThrows(IllegalArgumentException.class, () -> copy.setSlabbed(true));
        for (int grade = 1; grade <= 10; grade++) {
            copy.setGrade(grade);
            assertEquals(100 * (grade == 1 ? 0.10 : Math.log10(grade)), copy.getValue(), 0.00001);
        }
        copy.setSlabbed(true);
        copy.setSlabbed(true);
        assertEquals(200, copy.getValue(), 0.00001);
        copy.setSlabbed(false);
        assertEquals(100, copy.getValue(), 0.00001);
    }

    @Test void savingRetainsOwnedCopyAndMarkingsAndRemoval() throws Exception {
        CollectionStore store = new CollectionStore(directory.resolve("profile.json"));
        PersonalCollection collection = new PersonalCollection();
        PersonalComic copy = new PersonalComic(example(), 100.0);
        copy.setGrade(5);
        copy.setSlabbed(true);
        collection.addComic(copy);
        store.save(collection);
        PersonalCollection restored = store.load();
        PersonalComic loaded = restored.find(copy.getId());
        assertEquals(copy.getComic().getSeries(), loaded.getComic().getSeries());
        assertEquals(copy.getComic().getCreators(), loaded.getComic().getCreators());
        assertEquals(copy.getComic().getPrincipalCharacters(), loaded.getComic().getPrincipalCharacters());
        assertEquals(copy.getComic().getDescription(), loaded.getComic().getDescription());
        assertEquals(5, loaded.getGrade());
        assertTrue(loaded.isSlabbed());
        assertEquals(copy.getValue(), loaded.getValue(), 0.00001);
        restored.removeComic(loaded.getId());
        store.save(restored);
        assertTrue(store.load().getComix().isEmpty());
    }
}

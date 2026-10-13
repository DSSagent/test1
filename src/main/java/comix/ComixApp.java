package comix;

import comix.persistence.CatalogLoader;
import comix.collection.masterDatabase;
import comix.collection.PersonalCollection;
import comix.persistence.CollectionStore;
import comix.ui.PTUI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.nio.file.Path;
import java.util.Scanner;

@Component
@Profile("!test")
public final class ComixApp implements CommandLineRunner {
    @Value("${comix.catalog-file:data/comics.json}")
    private String catalogFile;
    @Value("${comix.collection-file:data/personal-collection.json}")
    private String collectionFile;

    @Override
    public void run(String... args) throws Exception {
        masterDatabase catalog = new CatalogLoader().load(Path.of(catalogFile));
        CollectionStore store = new CollectionStore(Path.of(collectionFile));
        PersonalCollection collection = store.load();
        new PTUI(catalog, collection, store, new Scanner(System.in), System.out).run();
    }
}

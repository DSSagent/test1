package comix.persistence;

import com.google.gson.*;
import comix.collection.PersonalCollection;
import comix.model.Comix;
import comix.model.PersonalComic;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public final class CollectionStore {
    private final Path path;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    public CollectionStore(Path path) {
        this.path = path.toAbsolutePath();
    }
    public PersonalCollection load() throws IOException {
        PersonalCollection collection = new PersonalCollection();
        if (!Files.exists(path)) {
            return collection;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject row = element.getAsJsonObject();
                collection.addComic(new PersonalComic(row.get("id").getAsString(),
                        CatalogLoader.readComic(row.getAsJsonObject("comic")),
                        nullableDouble(row, "baseValue"), nullableInt(row, "grade"),
                        row.has("slabbed") && row.get("slabbed").getAsBoolean()));
            }
            return collection;
        } catch (RuntimeException e) {
            // Fail closed: never replace a corrupt profile with an empty collection.
            throw new IOException("Invalid personal collection; keep the file and repair it: " + path, e);
        }
    }
    private static Double nullableDouble(JsonObject row, String key) {
        return row.has(key) && !row.get(key).isJsonNull() ? row.get(key).getAsDouble() : null;
    }
    private static Integer nullableInt(JsonObject row, String key) {
        return row.has(key) && !row.get(key).isJsonNull() ? row.get(key).getAsInt() : null;
    }
    public void save(PersonalCollection collection) throws IOException {
        JsonArray array = new JsonArray();
        for (PersonalComic p : collection.getComix()) {
            JsonObject row = new JsonObject();
            row.addProperty("id", p.getId());
            row.add("comic", writeComic(p.getComic()));
            row.addProperty("baseValue", p.getBaseValue());
            row.addProperty("grade", p.getGrade());
            row.addProperty("slabbed", p.isSlabbed());
            array.add(row);
        }
        Files.createDirectories(path.getParent());
        Path temporary = Files.createTempFile(path.getParent(), ".comix-", ".tmp");
        try {
            Files.writeString(temporary, gson.toJson(array), StandardCharsets.UTF_8);
            try { Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally { Files.deleteIfExists(temporary); }
    }
    private JsonObject writeComic(Comix c) {
        JsonObject row = new JsonObject();
        row.addProperty("publisher", c.getPublisher());
        row.addProperty("series", c.getSeries());
        row.addProperty("volumeNumber", c.getVolumeNumber());
        row.addProperty("issue", c.getIssueNumber());
        row.addProperty("releaseDate", c.getPublicationDate());
        row.add("creators", gson.toJsonTree(c.getCreators()));
        row.add("principalCharacters", gson.toJsonTree(c.getPrincipalCharacters()));
        row.addProperty("description", c.getDescription());
        row.addProperty("fullTitle", c.getTitle());
        row.addProperty("variantDescription", c.getVariantDescription());
        row.addProperty("format", c.getFormat());
        row.addProperty("addedDate", c.getAddedDate());
        return row;
    }
}
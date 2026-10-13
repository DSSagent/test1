package comix.persistence;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import comix.collection.masterDatabase;
import comix.model.Comix;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CatalogLoader {
    public masterDatabase load(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonArray rows = JsonParser.parseReader(reader).getAsJsonArray();
            List<Comix> comics = new ArrayList<>();
            for (JsonElement row : rows) {
                comics.add(readComic(row.getAsJsonObject()));
            }
            return new masterDatabase(comics);
        } catch (RuntimeException e) {
            throw new IOException("Invalid catalog JSON: " + path, e);
        }
    }
    static Comix readComic(JsonObject row) {
        String series = string(row, "series");
        int volume = row.has("volumeNumber") ? row.get("volumeNumber").getAsInt()
                : Comix.volumeFromSeries(series);
        return new Comix(string(row, "publisher"), series, volume, string(row, "issue"),
                string(row, "releaseDate"), strings(row, "creators"),
                strings(row, "principalCharacters"), string(row, "description"),
                string(row, "fullTitle"), string(row, "variantDescription"),
                string(row, "format"), string(row, "addedDate"));
    }
    static String string(JsonObject row, String key) {
        return row.has(key) && !row.get(key).isJsonNull() ? row.get(key).getAsString() : "";
    }
    static List<String> strings(JsonObject row, String key) {
        List<String> result = new ArrayList<>();
        if (row.has(key) && !row.get(key).isJsonNull()) {
            for (JsonElement item : row.getAsJsonArray(key)) {
                result.add(item.getAsString());
            }
        }
        return result;
    }
}

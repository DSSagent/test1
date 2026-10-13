package comix.search;

import java.util.Collection;
import java.util.Locale;

public class SearchSupport {
    public static boolean matches(String value, String term, boolean exact) {
        if (value == null) {
            value = "";
        }
        if (term == null) {
            term = "";
        }
        value = value.toLowerCase(Locale.ROOT);
        term = term.trim().toLowerCase(Locale.ROOT);
        if (exact) {
            return value.equals(term);
        }
        return value.contains(term);
    }

    public static boolean matchesAny(Collection<String> names, String term, boolean exact) {
        for (String name : names) {
            if (matches(name, term, exact)) {
                return true;
            }
        }
        return false;
    }
}

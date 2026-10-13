package comix.model;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public final class PublicationDates {
    private PublicationDates() {}
    private static final DateTimeFormatter DAY = DateTimeFormatter
            .ofPattern("MMM dd, uuuu", Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter MONTH = DateTimeFormatter
            .ofPattern("MMM uuuu", Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);

    /** Sort partial dates by the first day of their period; keep original text. */
    public static LocalDate sortKey(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String s = value.strip();
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            // Try the next supported date format.
        }
        try {
            return LocalDate.parse(s, DAY);
        } catch (DateTimeParseException e) {
            // Try the next supported date format.
        }
        try {
            return YearMonth.parse(s).atDay(1);
        } catch (DateTimeParseException e) {
            // Try the next supported date format.
        }
        try {
            return YearMonth.parse(s, MONTH).atDay(1);
        } catch (DateTimeParseException e) {
            // Try the next supported date format.
        }
        try {
            return Year.parse(s).atDay(1);
        } catch (DateTimeParseException e) {
            // Try the next supported date format.
        }
        return null;
    }
}

package comix.model;

import java.util.List;
import java.util.Locale;

/** Immutable comic information; personal markings belong to PersonalComic. */
public final class Comix {
    private final String publisher;
    private final String series;
    private final String issueNumber;
    private final String publicationDate;
    private final String title;
    private final String description;
    private final String variantDescription;
    private final String format;
    private final String addedDate;
    private final int volumeNumber;
    private final List<String> creators;
    private final List<String> principalCharacters;

    public Comix(String publisher, String series, int volumeNumber,
                 String issueNumber, String publicationDate, List<String> creators,
                 List<String> principalCharacters, String description, String title,
                 String variantDescription, String format, String addedDate) {
        if (volumeNumber < 1) {
            throw new IllegalArgumentException("Volume must be at least 1.");
        }
        this.publisher = text(publisher);
        this.series = text(series);
        this.volumeNumber = volumeNumber;
        this.issueNumber = text(issueNumber);
        this.publicationDate = text(publicationDate);
        this.creators = List.copyOf(creators);
        this.principalCharacters = List.copyOf(principalCharacters);
        this.description = text(description);
        this.title = text(title);
        this.variantDescription = text(variantDescription);
        this.format = text(format);
        this.addedDate = text(addedDate);
    }

    private static String text(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }
    // The catalog sometimes ends a series name with ", Vol. 2".
    // Return the comma position only when the suffix is a valid volume.
    private static int volumeSuffixStart(String series) {
        int comma = series.lastIndexOf(',');
        if (comma < 0) {
            return -1;
        }
        String suffix = series.substring(comma + 1).trim().toLowerCase(Locale.ROOT);
        if (!suffix.startsWith("vol")) {
            return -1;
        }
        String number = suffix.substring(3).trim();
        if (number.startsWith(".")) {
            number = number.substring(1).trim();
        }
        if (number.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < number.length(); i++) {
            if (!Character.isDigit(number.charAt(i))) {
                return -1;
            }
        }
        try {
            if (Integer.parseInt(number) < 1) {
                return -1;
            }
            return comma;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static int volumeFromSeries(String series) {
        series = text(series);
        int comma = volumeSuffixStart(series);
        if (comma < 0) {
            return 1;
        }
        String number = series.substring(comma + 1).trim().substring(3).trim();
        if (number.startsWith(".")) {
            number = number.substring(1).trim();
        }
        return Integer.parseInt(number);
    }

    public String getSeriesTitle() {
        int comma = volumeSuffixStart(series);
        if (comma < 0) {
            return series.trim();
        }
        return series.substring(0, comma).trim();
    }
    public String getPublisher() {
        return publisher;
    }
    public String getSeries() {
        return series;
    }
    public int getVolumeNumber() {
        return volumeNumber;
    }
    public String getIssueNumber() {
        return issueNumber;
    }
    public String getPublicationDate() {
        return publicationDate;
    }
    public List<String> getCreators() {
        return creators;
    }
    public List<String> getPrincipalCharacters() {
        return principalCharacters;
    }
    public String getDescription() {
        return description;
    }
    public String getTitle() {
        return title;
    }
    public String getVariantDescription() {
        return variantDescription;
    }
    public String getFormat() {
        return format;
    }
    public String getAddedDate() {
        return addedDate;
    }

    /** User-entered records require these fields; sparse source records are retained. */
    public void validateForManualEntry() {
        if (publisher.isBlank() || series.isBlank() || issueNumber.isBlank()
                || publicationDate.isBlank()) {
            throw new IllegalArgumentException("Publisher, series, issue and date are required.");
        }
        if (PublicationDates.sortKey(publicationDate) == null) {
            throw new IllegalArgumentException("Use a full date, month/year, or year.");
        }
    }
}

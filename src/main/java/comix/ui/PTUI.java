package comix.ui;

import comix.collection.PersonalCollection;
import comix.collection.masterDatabase;
import comix.command.*;
import comix.model.Comix;
import comix.model.PersonalComic;
import comix.persistence.CollectionStore;
import comix.search.*;
import comix.sort.sortByPublication;
import comix.sort.sortBySeries;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

public final class PTUI {
    private final masterDatabase catalog;
    private final PersonalCollection collection;
    private final CollectionStore store;
    private final Scanner input;
    private final PrintStream output;
    private final CommandManager commands = new CommandManager();
    private boolean dirty;

    public PTUI(masterDatabase catalog, PersonalCollection collection,
                CollectionStore store, Scanner input, PrintStream output) {
        this.catalog = catalog;
        this.collection = collection;
        this.store = store;
        this.input = input;
        this.output = output;
    }

    public void run() throws IOException {
        output.printf("Loaded %d catalog records and %d personal copies.%n",
                catalog.getComix().size(), collection.getComix().size());

        while (true) {
            output.println("\nCOMIX"
                    + "\n1. Search catalog / add a selected comic"
                    + "\n2. Add a comic manually"
                    + "\n3. Browse collection hierarchy"
                    + "\n4. Search / sort personal collection"
                    + "\n5. Edit a personal comic"
                    + "\n6. Remove a personal comic"
                    + "\n7. Set or remove grade"
                    + "\n8. Set or remove slab marking"
                    + "\n9. Save now"
                    + "\n0. Save and exit");

            try {
                String choice = line("Choice: ");

                if (choice.equals("1")) {
                    searchCatalog();
                } else if (choice.equals("2")) {
                    addManual();
                } else if (choice.equals("3")) {
                    new CollectionBrowser().browse(
                            collection.getComix(), input, output);
                } else if (choice.equals("4")) {
                    searchPersonal();
                } else if (choice.equals("5")) {
                    edit();
                } else if (choice.equals("6")) {
                    remove();
                } else if (choice.equals("7")) {
                    grade();
                } else if (choice.equals("8")) {
                    slab();
                } else if (choice.equals("9")) {
                    save();
                } else if (choice.equals("0")) {
                    save();
                    output.println("Goodbye.");
                    return;
                } else {
                    output.println("Choose a menu number from 0 through 9.");
                }
            } catch (NoSuchElementException endOfInput) {
                save();
                output.println("End of input; collection saved.");
                return;
            } catch (IllegalArgumentException e) {
                output.println("Cannot complete that action: " + e.getMessage());
            } catch (IOException e) {
                output.println("SAVE FAILED: " + e.getMessage());
                output.println(
                        "Changes remain in memory. Fix the file problem "
                        + "and choose 9 before exiting.");
            }
        }
    }

    private String line(String prompt) {
        output.print(prompt);
        String answer = input.nextLine().strip();
        output.println();
        return answer;
    }

    private boolean exact() {
        output.println("Search match mode");
        output.println("e. Exact match");
        output.println("p. Partial match");
        output.println();

        String answer = line("Choose match mode: ");

        if (!answer.equalsIgnoreCase("e")
                && !answer.equalsIgnoreCase("p")) {
            throw new IllegalArgumentException("Use e or p for match mode.");
        }

        return answer.equalsIgnoreCase("e");
    }

    private void searchCatalog() throws IOException {
        output.println("Search catalog by");
        output.println("1. Series");
        output.println("2. Issue number");
        output.println("3. Story title");
        output.println("4. Publisher");
        output.println("5. Release date");
        output.println("6. Creator");
        output.println("7. Any field");
        output.println();

        int field = Integer.parseInt(line("Search field: "));

        if (field < 1 || field > 7) {
            throw new IllegalArgumentException("Field must be 1 through 7.");
        }

        boolean exact = exact();
        String term = line("Search term (blank partial search lists all): ");

        comixSearch[] strategies = {
            new searchBySeries(),
            new searchByIssue(),
            new searchByTitle(),
            new searchbyPublisher(),
            new searchByPublicationDate(),
            new searchByCreator()
        };

        List<Comix> results;

        if (field == 7) {
            LinkedHashSet<Comix> combined = new LinkedHashSet<>();

            for (comixSearch strategy : strategies) {
                catalog.setComixSearcher(strategy);
                combined.addAll(catalog.doSearch(term, exact));
            }

            results = List.copyOf(combined);
        } else {
            catalog.setComixSearcher(strategies[field - 1]);
            results = catalog.doSearch(term, exact);
        }

        Comix selected = selectComic(results);

        if (selected != null) {
            showComic(output, selected);

            if (line("Add an owned copy? y/n: ").equalsIgnoreCase("y")) {
                Double value = value(null, false);
                mutate(new AddComicCommand(
                        collection, new PersonalComic(selected, value)));
            }
        }
    }

    private void addManual() throws IOException {
        output.println("Required fields cannot be blank.");
        output.println("Optional lists use | between names.");
        output.println();

        Comix comic = readComic(null);
        Double value = value(null, false);

        mutate(new AddComicCommand(
                collection, new PersonalComic(comic, value)));
    }

    private void searchPersonal() {
        chooseSort();
        boolean exact = exact();

        output.println("Search series, characters, creators or description.");
        String term = line("Search term: ");

        PersonalComic selected = selectPersonal(
                collection.search(term, exact));

        if (selected != null) {
            showPersonal(output, selected);
        }
    }

    private void chooseSort() {
        output.println("Sort personal collection");
        output.println("1. Series, volume and issue");
        output.println("2. Publication date");
        output.println();

        String order = line("Choose sort option: ");

        if (order.equals("1")) {
            collection.setSorter(new sortBySeries());
        } else if (order.equals("2")) {
            collection.setSorter(new sortByPublication());
        } else {
            throw new IllegalArgumentException("Choose sort 1 or 2.");
        }
    }

    private PersonalComic choosePersonal() {
        return selectPersonal(collection.sort(collection.getComix()));
    }

    private void edit() throws IOException {
        PersonalComic target = choosePersonal();

        if (target == null) {
            return;
        }

        output.println("Blank keeps the current field.");
        output.println("Use - to clear an optional field.");
        output.println();

        Comix replacement = readComic(target.getComic());
        Double base = value(target.getBaseValue(), true);

        mutate(new EditComicCommand(target, replacement, base));
    }

    private void remove() throws IOException {
        PersonalComic target = choosePersonal();

        if (target != null
                && line("Remove this owned copy? y/n: ")
                        .equalsIgnoreCase("y")) {
            mutate(new RemoveComicCommand(collection, target.getId()));
        }
    }

    private void grade() throws IOException {
        PersonalComic target = choosePersonal();

        if (target == null) {
            return;
        }

        output.println("Enter a grade from 1 through 10.");
        output.println("Enter 0 to remove the grade.");
        output.println();

        int grade = Integer.parseInt(line("Grade: "));
        mutate(new GradeComicCommand(target, grade == 0 ? null : grade));
    }

    private void slab() throws IOException {
        PersonalComic target = choosePersonal();

        if (target == null) {
            return;
        }

        output.println("Slab marking");
        output.println("y. Slabbed");
        output.println("n. Not slabbed");
        output.println();

        String answer = line("Choose slab status: ");

        if (!answer.equalsIgnoreCase("y")
                && !answer.equalsIgnoreCase("n")) {
            throw new IllegalArgumentException("Use y or n.");
        }

        mutate(new SlabComicCommand(
                target, answer.equalsIgnoreCase("y")));
    }

    private void mutate(CollectionCommand command) throws IOException {
        commands.executeCommand(command);
        dirty = true;
        save();
    }

    private void save() throws IOException {
        store.save(collection);
        dirty = false;
        output.println("Collection saved.");
    }

    public boolean hasUnsavedChanges() {
        return dirty;
    }

    private String field(String label, String current, boolean required) {
        String prompt = label;

        if (current != null) {
            prompt += " [" + current + "]";
        }

        String entered = line(prompt + ": ");
        String result = entered;

        if (entered.isEmpty() && current != null) {
            result = current;
        }

        if (!required && result.equals("-")) {
            result = "";
        }

        if (required && result.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }

        return result;
    }

    private List<String> names(String label, List<String> current) {
        String currentText = null;

        if (current != null) {
            currentText = String.join(" | ", current);
        }

        String text = field(label, currentText, false);
        List<String> result = new ArrayList<>();
        StringTokenizer names = new StringTokenizer(text, "|");

        while (names.hasMoreTokens()) {
            String name = names.nextToken().trim();

            if (!name.isEmpty()) {
                result.add(name);
            }
        }

        return result;
    }

    private Double value(Double current, boolean editing) {
        output.println("Base value in dollars");

        if (editing) {
            output.println("Current value: " + current);
            output.println("Leave blank to keep the current value.");
            output.println("Enter - for an unknown value.");
        } else {
            output.println("Leave blank for an unknown value.");
        }

        output.println();
        String answer = line("Base value: ");

        if (answer.equals("-")) {
            return null;
        }

        if (answer.isEmpty()) {
            if (editing) {
                return current;
            }
            return null;
        }

        double value = Double.parseDouble(answer);

        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException("Invalid value.");
        }

        return value;
    }

    private Comix readComic(Comix old) {
        String publisher = field(
                "Publisher", old == null ? null : old.getPublisher(), true);

        String series = field(
                "Series title", old == null ? null : old.getSeries(), true);

        int defaultVolume;

        if (old == null || !series.equals(old.getSeries())) {
            defaultVolume = Comix.volumeFromSeries(series);
        } else {
            defaultVolume = old.getVolumeNumber();
        }

        String volumeText = line("Volume [" + defaultVolume + "]: ");
        int volume = defaultVolume;

        if (!volumeText.isEmpty()) {
            volume = Integer.parseInt(volumeText);
        }

        String issue = field(
                "Issue number", old == null ? null : old.getIssueNumber(), true);

        output.println("Date examples: 2020-06-10, 2020-06, 2020 or Jan 1986.");

        String date = field(
                "Publication date",
                old == null ? null : old.getPublicationDate(), true);

        List<String> creators = names(
                "Creators", old == null ? null : old.getCreators());

        List<String> characters = names(
                "Principal characters",
                old == null ? null : old.getPrincipalCharacters());

        String description = field(
                "Description", old == null ? null : old.getDescription(), false);

        String title = field(
                "Story title", old == null ? null : old.getTitle(), false);

        Comix comic = new Comix(
                publisher, series, volume, issue, date,
                creators, characters, description, title,
                old == null ? "" : old.getVariantDescription(),
                old == null ? "" : old.getFormat(),
                old == null ? "" : old.getAddedDate());

        comic.validateForManualEntry();
        return comic;
    }

    private Comix selectComic(List<Comix> comics) {
        List<String> labels = new ArrayList<>();

        for (Comix comic : comics) {
            labels.add(summary(comic));
        }

        int index = selectNumber(labels);

        if (index < 0) {
            return null;
        }

        return comics.get(index);
    }

    private PersonalComic selectPersonal(List<PersonalComic> comics) {
        List<String> labels = new ArrayList<>();

        for (PersonalComic comic : comics) {
            labels.add(personalSummary(comic));
        }

        int index = selectNumber(labels);

        if (index < 0) {
            return null;
        }

        return comics.get(index);
    }

    private int selectNumber(List<String> items) {
        if (items.isEmpty()) {
            output.println("No results.");
            return -1;
        }

        int page = 0;

        while (true) {
            int start = page * 20;
            int end = Math.min(start + 20, items.size());

            output.printf("Results %d-%d of %d%n",
                    start + 1, end, items.size());
            output.println();

            for (int i = start; i < end; i++) {
                output.printf("%d. %s%n", i + 1, items.get(i));
                output.println();
            }

            output.println("Enter a displayed number to select a comic.");
            output.println("n. Next page");
            output.println("p. Previous page");
            output.println("0. Cancel");
            output.println();

            String answer = line("Selection: ");

            if (answer.equals("0")) {
                return -1;
            }

            if (answer.equalsIgnoreCase("n")) {
                if (end < items.size()) {
                    page++;
                } else {
                    output.println("Last page.");
                    output.println();
                }
                continue;
            }

            if (answer.equalsIgnoreCase("p")) {
                page = Math.max(0, page - 1);
                continue;
            }

            try {
                int index = Integer.parseInt(answer) - 1;

                if (index < start || index >= end) {
                    throw new NumberFormatException();
                }

                return index;
            } catch (NumberFormatException e) {
                output.println("Choose a number on this page.");
                output.println();
            }
        }
    }

    public static String summary(Comix comic) {
        return comic.getPublisher()
                + " | " + comic.getSeriesTitle()
                + " | vol " + comic.getVolumeNumber()
                + " | issue " + comic.getIssueNumber()
                + " | " + comic.getPublicationDate()
                + " | " + comic.getTitle()
                + " | " + comic.getVariantDescription();
    }

    private static String personalSummary(PersonalComic copy) {
        String text = summary(copy.getComic());
        text += String.format(Locale.ROOT, " | value $%.2f", copy.getValue());

        if (copy.getBaseValue() == null) {
            text += " (unknown base value)";
        }

        text += " | grade " + copy.getGrade();
        text += " | slabbed " + copy.isSlabbed();

        return text;
    }

    public static void showComic(PrintStream out, Comix comic) {
        out.println("Comic details");
        out.println();

        out.println("Publisher: " + comic.getPublisher());
        out.println("Series: " + comic.getSeriesTitle());
        out.println("Volume: " + comic.getVolumeNumber());
        out.println("Issue: " + comic.getIssueNumber());
        out.println("Publication date: " + comic.getPublicationDate());
        out.println();

        out.println("Story title: " + comic.getTitle());
        out.println("Creators: " + String.join(" | ", comic.getCreators()));
        out.println("Characters: "
                + String.join(" | ", comic.getPrincipalCharacters()));
        out.println("Description: " + comic.getDescription());
        out.println();

        out.println("Variant: " + comic.getVariantDescription());
        out.println("Original series: " + comic.getSeries());
        out.println("Format: " + comic.getFormat());
        out.println("Added date: " + comic.getAddedDate());
        out.println();
    }

    public static void showPersonal(PrintStream out, PersonalComic copy) {
        showComic(out, copy.getComic());

        out.println("Owned copy");
        out.println();

        out.println("Copy ID: " + copy.getId());
        out.println("Base value: " + copy.getBaseValue());
        out.println("Grade: " + copy.getGrade());
        out.println("Slabbed: " + copy.isSlabbed());
        out.printf("Effective value: $%.2f%n", copy.getValue());
        out.println();
    }
}
package comix.ui;

import comix.model.Comix;
import comix.model.PersonalComic;
import comix.sort.sortBySeries;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public final class CollectionBrowser {
    public static class Node {
        private final String label;
        private final List<PersonalComic> copies;
        private final List<Node> children;

        public Node(String label, List<PersonalComic> copies,
                    List<Node> children) {
            this.label = label;
            this.copies = new ArrayList<>(copies);
            this.children = new ArrayList<>(children);
        }

        public String label() {
            return label;
        }

        public List<PersonalComic> copies() {
            return new ArrayList<>(copies);
        }

        public List<Node> children() {
            return new ArrayList<>(children);
        }

        public int count() {
            return copies.size();
        }

        public double value() {
            double total = 0;

            for (PersonalComic copy : copies) {
                total += copy.getValue();
            }

            return total;
        }

        public long unknownValues() {
            long count = 0;

            for (PersonalComic copy : copies) {
                if (copy.getBaseValue() == null) {
                    count++;
                }
            }

            return count;
        }
    }

    public static Node tree(List<PersonalComic> comics) {
        return build(
                "Entire collection", new sortBySeries().doSort(comics), 0);
    }

    private static Node build(String label, List<PersonalComic> copies,
                              int depth) {
        if (depth == 4) {
            return new Node(label, copies, List.of());
        }

        Map<String, List<PersonalComic>> groups = new LinkedHashMap<>();

        for (PersonalComic copy : copies) {
            Comix comic = copy.getComic();
            String key;

            if (depth == 0) {
                key = "Publisher: " + comic.getPublisher();
            } else if (depth == 1) {
                key = "Series: " + comic.getSeriesTitle();
            } else if (depth == 2) {
                key = "Volume: " + comic.getVolumeNumber();
            } else {
                key = "Issue: " + comic.getIssueNumber();
            }

            if (!groups.containsKey(key)) {
                groups.put(key, new ArrayList<PersonalComic>());
            }

            groups.get(key).add(copy);
        }

        List<Node> children = new ArrayList<>();

        for (String key : groups.keySet()) {
            children.add(build(key, groups.get(key), depth + 1));
        }

        return new Node(label, copies, children);
    }

    public void browse(List<PersonalComic> comics, Scanner input,
                       PrintStream output) {
        List<Node> trail = new ArrayList<>();
        trail.add(tree(comics));

        while (true) {
            Node current = trail.get(trail.size() - 1);
            List<Node> children = current.children();

            output.println(current.label());
            output.println();
            output.println("Issues / owned copies: " + current.count());
            output.printf("Total known value: $%.2f%n", current.value());
            output.println("Copies without a value: "
                    + current.unknownValues());
            output.println();

            if (children.isEmpty()) {
                for (PersonalComic copy : current.copies()) {
                    PTUI.showPersonal(output, copy);
                }
            }

            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);

                output.printf("%d. %s%n", i + 1, child.label());
                output.printf(
                        "   %d copies | $%.2f | %d without value%n",
                        child.count(), child.value(), child.unknownValues());
                output.println();
            }

            output.println("Enter a displayed number to open that level.");
            output.println("b. Back");
            output.println("0. Main menu");
            output.println();

            output.print("Selection: ");
            String choice = input.nextLine().strip();
            output.println();

            if (choice.equals("0")) {
                return;
            }

            if (choice.equalsIgnoreCase("b")) {
                if (trail.size() == 1) {
                    return;
                }

                trail.remove(trail.size() - 1);
                continue;
            }

            try {
                int index = Integer.parseInt(choice) - 1;

                if (index < 0 || index >= children.size()) {
                    throw new NumberFormatException();
                }

                trail.add(children.get(index));
            } catch (NumberFormatException e) {
                output.println("Choose a displayed level, b, or 0.");
                output.println();
            }
        }
    }
}
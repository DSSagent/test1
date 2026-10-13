package comix.model;

import java.util.Objects;
import java.util.UUID;

/** One owned copy with its own ID, base value and markings. */
public final class PersonalComic {
    private final String id;
    private Comix comic;
    private Double baseValue;
    private Integer grade;
    private boolean slabbed;

    public PersonalComic(Comix comic, Double baseValue) {
        this(UUID.randomUUID().toString(), comic, baseValue, null, false);
    }
    public PersonalComic(String id, Comix comic, Double baseValue,
                         Integer grade, boolean slabbed) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Missing copy ID.");
        }
        this.id = id;
        this.comic = Objects.requireNonNull(comic);
        setBaseValue(baseValue);
        setGrade(grade);
        setSlabbed(slabbed);
    }
    public String getId() {
        return id;
    }
    public Comix getComic() {
        return comic;
    }
    public Double getBaseValue() {
        return baseValue;
    }
    public Integer getGrade() {
        return grade;
    }
    public boolean isSlabbed() {
        return slabbed;
    }
    public void setComic(Comix comic) {
        this.comic = Objects.requireNonNull(comic);
    }
    public void setBaseValue(Double value) {
        if (value != null && (!Double.isFinite(value) || value < 0)) {
            throw new IllegalArgumentException("Value must be a finite nonnegative number.");
        }
        baseValue = value;
    }
    public void setGrade(Integer grade) {
        if (grade != null && (grade < 1 || grade > 10)) {
            throw new IllegalArgumentException("Grade must be 1 through 10.");
        }
        if (grade == null && slabbed) {
            throw new IllegalArgumentException("Unslab the comic before removing its grade.");
        }
        this.grade = grade;
    }
    public void setSlabbed(boolean slabbed) {
        if (slabbed && grade == null) {
            throw new IllegalArgumentException("A comic must be graded before it is slabbed.");
        }
        this.slabbed = slabbed;
    }
    // Start from the base value each time, then wrap it in the selected markings.
    public double getValue() {
        double amount = 0;
        if (baseValue != null) {
            amount = baseValue;
        }
        ComicValue value = new BaseValue(amount);
        if (grade != null) {
            value = new GradedValue(value, grade);
        }
        if (slabbed) {
            value = new SlabbedValue(value);
        }
        return value.amount();
    }

    // These small classes implement the Decorator pattern for value markings.
    private interface ComicValue {
        double amount();
    }

    private static class BaseValue implements ComicValue {
        private final double value;

        BaseValue(double value) {
            this.value = value;
        }

        public double amount() {
            return value;
        }
    }

    private static class GradedValue implements ComicValue {
        private final ComicValue wrapped;
        private final int grade;

        GradedValue(ComicValue wrapped, int grade) {
            this.wrapped = wrapped;
            this.grade = grade;
        }

        public double amount() {
            if (grade == 1) {
                return wrapped.amount() * 0.10;
            }
            return wrapped.amount() * Math.log10(grade);
        }
    }

    private static class SlabbedValue implements ComicValue {
        private final ComicValue wrapped;

        SlabbedValue(ComicValue wrapped) {
            this.wrapped = wrapped;
        }

        public double amount() {
            return wrapped.amount() * 2;
        }
    }
}

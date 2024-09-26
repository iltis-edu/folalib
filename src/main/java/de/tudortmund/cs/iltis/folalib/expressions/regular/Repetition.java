package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import java.io.Serializable;
import java.util.Objects;

public class Repetition<S extends Serializable> extends RegularExpression<S> {
    protected int lower;
    protected int upper;

    // For serialization
    @SuppressWarnings("unused")
    private Repetition() {}

    /**
     * Build a new Repetition expression over the given alphabet which matches the given regex an
     * exact number of times
     *
     * @param numberOfRepetitions how often the subexpression must be repeated
     * @param subexpression the subexpression to match
     * @throws IllegalArgumentException if {@code numberOfRepetitions} is negative
     */
    public Repetition(RegularExpression<S> subexpression, int numberOfRepetitions) {
        this(subexpression, numberOfRepetitions, numberOfRepetitions);
    }

    /**
     * Build a new Repetition expression over the given alphabet which matches the given regex
     * between {@code lower} and {@code upper} times
     *
     * @param lower how often the subexpression must be repeated at least
     * @param upper how often the subexpression must be repeated at most
     * @param subexpression the subexpression to match
     * @throws IllegalArgumentException if {@code lower} is negative or if {@code lower > upper}
     */
    public Repetition(RegularExpression<S> subexpression, int lower, int upper) {
        super(subexpression.getAlphabet(), true, subexpression);
        if (lower < 0)
            throw new IllegalArgumentException("Lower bound of Repetition must not be negative.");
        if (lower > upper)
            throw new IllegalArgumentException(
                    "Lower bound of Repetition must not be greater than upper bound.");
        this.lower = lower;
        this.upper = upper;
    }

    public int getLower() {
        return lower;
    }

    public int getUpper() {
        return upper;
    }

    @Override
    public Repetition<S> withAlphabet(Alphabet<S> alphabet) {
        return getAlphabet().equals(alphabet)
                ? this
                : new Repetition<>(getChild(0).withAlphabet(alphabet), lower, upper);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && lower == ((Repetition<?>) obj).lower
                && upper == ((Repetition<?>) obj).upper;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lower, upper);
    }
}

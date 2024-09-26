package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.util.NullCheck;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Range<S extends Serializable> extends RegularExpression<S> {

    // store lower and upper explicitly since elements is empty for lower > upper
    private S lower;
    private S upper;
    private List<S> elements;

    // For serialization
    @SuppressWarnings("unused")
    private Range() {}

    // This constructor is for internal use only: it is required s.t. {@link withAlphabet) can be
    // implemented properly:
    // In particular, the elements must be passed explicitly, since otherwise changing the alphabet
    // could change the elements
    // between lower and upper and therefore two equivalent regexes could be "different" after
    // changing the alphabet.
    // Also, it nicely solves the problem that Comparators are *not* Serializable and can thus not
    // be stored as a class member.
    private Range(Alphabet<S> alphabet, S lower, S upper, List<S> elements) {
        super(alphabet, true);
        Objects.requireNonNull(lower);
        Objects.requireNonNull(upper);
        NullCheck.requireAllNonNull(elements);

        // Testing that lower and upper are in the alphabet is *NOT* implicitly included in
        // Data.forall check if lower > upper
        if (!alphabet.contains(lower))
            throw new IllegalArgumentException(
                    "Alphabet of RegularExpression.Range must contain the lower bound symbol.");
        if (!alphabet.contains(upper))
            throw new IllegalArgumentException(
                    "Alphabet of RegularExpression.Range must contain the upper bound symbol.");
        if (!alphabet.toUnmodifiableSet().containsAll(elements))
            throw new IllegalArgumentException(
                    "Alphabet of RegularExpression.Range must contain all symbols between lower and upper");

        this.lower = lower;
        this.upper = upper;
        this.elements = new LinkedList<>(elements);
    }

    /**
     * Build a new Range expression over the given alphabet which represents all symbols between the
     * lower and the upper bound (inclusive). The order of the symbols is based on {@code
     * Comparator.naturalOrder()}.
     *
     * @param alphabet The alphabet to use for this regular expression
     * @param lower The lower bound of the Range (inclusive)
     * @param upper The upper bound of the Range (inclusive)
     * @throws IllegalArgumentException if {@code lower} or {@code upper} are not included in the
     *     {@code alphabet}.
     * @throws NullPointerException if {@code lower}, {@code upper} or {@code alphabet} are {@code
     *     null}
     */
    public static <S extends Serializable & Comparable<? super S>> Range<S> from(
            Alphabet<S> alphabet, S lower, S upper) {
        return from(alphabet, lower, upper, Comparator.naturalOrder());
    }

    /**
     * Build a new Range expression over the given alphabet which represents all symbols between the
     * lower and the upper bound (inclusive). The order of the symbols is based on the given
     * comparator.
     *
     * @param alphabet The alphabet to use for this regular expression
     * @param lower The lower bound of the Range (inclusive)
     * @param upper The upper bound of the Range (inclusive)
     * @param comparator The comparator used to determine all symbols between {@code lower} and
     *     {@code upper}
     * @throws IllegalArgumentException if {@code lower} or {@code upper} are not included in the
     *     {@code alphabet}.
     * @throws NullPointerException if {@code lower}, {@code upper}, {@code alphabet} or {@code
     *     comparator} are {@code null}
     */
    public static <S extends Serializable> Range<S> from(
            Alphabet<S> alphabet, S lower, S upper, Comparator<S> comparator) {
        return new Range<>(
                alphabet, lower, upper, enumerateRange(alphabet, lower, upper, comparator));
    }

    @Override
    public RegularExpression<S> withAlphabet(Alphabet<S> alphabet) {
        return getAlphabet().equals(alphabet)
                ? this
                : new Range<>(alphabet, lower, upper, elements);
    }

    public S getLower() {
        return lower;
    }

    public S getUpper() {
        return upper;
    }

    public Alphabet<S> getIncludedSymbols() {
        return new Alphabet<>(elements);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && ((Range<?>) obj).lower.equals(lower)
                && ((Range<?>) obj).upper.equals(upper)
                && ((Range<?>) obj).elements.equals(elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lower, upper, elements);
    }

    private static <S extends Serializable> List<S> enumerateRange(
            Alphabet<S> domain, S lower, S upper, Comparator<S> comparator) {
        return domain.stream()
                .filter(s -> comparator.compare(s, lower) >= 0 && comparator.compare(s, upper) <= 0)
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}

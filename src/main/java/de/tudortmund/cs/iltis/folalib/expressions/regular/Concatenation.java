package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.general.Data;
import java.io.Serializable;
import java.util.Arrays;

public class Concatenation<S extends Serializable> extends RegularExpression<S> {

    // For serialization
    @SuppressWarnings("unused")
    private Concatenation() {}

    /**
     * Build a new Concatenation expression over the given alphabet from the given subexpressions
     *
     * @param subexpressions the subexpressions to concatenate
     * @throws IllegalArgumentException if no subexpressions are specified or if not all
     *     subexpression have an identical alphabet
     */
    @SafeVarargs
    public Concatenation(RegularExpression<S>... subexpressions) {
        this(Arrays.asList(subexpressions));
    }

    /**
     * Build a new Concatenation expression over the given alphabet from the given subexpressions
     *
     * @param subexpressions the subexpressions to concatenate
     * @throws IllegalArgumentException if no subexpressions are specified or if not all
     *     subexpression have an identical alphabet
     */
    public Concatenation(Iterable<? extends RegularExpression<S>> subexpressions) {
        super(inferAlphabetFromChildren(subexpressions), false, subexpressions);
    }

    @Override
    public Concatenation<S> withAlphabet(Alphabet<S> alphabet) {
        return getAlphabet().equals(alphabet)
                ? this
                : new Concatenation<>(Data.map(getChildren(), c -> c.withAlphabet(alphabet)));
    }
}

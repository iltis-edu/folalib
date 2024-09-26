package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.general.Data;
import java.io.Serializable;
import java.util.Arrays;

public class Alternative<S extends Serializable> extends RegularExpression<S> {

    // For serialization
    @SuppressWarnings("unused")
    private Alternative() {}

    /**
     * Build a new Alternative expression over the given alphabet from the given subexpressions
     *
     * @param subexpressions the subexpressions to choose from
     * @throws IllegalArgumentException if no subexpressions are specified or if not all
     *     subexpression have an identical alphabet
     */
    @SafeVarargs
    public Alternative(RegularExpression<S>... subexpressions) {
        this(Arrays.asList(subexpressions));
    }

    /**
     * Build a new Alternative expression over the given alphabet from the given subexpressions
     *
     * @param subexpressions the subexpressions to choose from
     * @throws IllegalArgumentException if no subexpressions are specified or if not all
     *     subexpression have an identical alphabet
     */
    public Alternative(Iterable<? extends RegularExpression<S>> subexpressions) {
        super(inferAlphabetFromChildren(subexpressions), false, subexpressions);
    }

    @Override
    public Alternative<S> withAlphabet(Alphabet<S> alphabet) {
        return getAlphabet().equals(alphabet)
                ? this
                : new Alternative<>(Data.map(getChildren(), c -> c.withAlphabet(alphabet)));
    }
}

package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import java.io.Serializable;

public class Option<S extends Serializable> extends RegularExpression<S> {

    // For serialization
    @SuppressWarnings("unused")
    private Option() {}

    /**
     * Build a new Option expression over the given alphabet which matches the given regex zero or
     * one times
     *
     * @param subexpression the subexpression to match
     */
    public Option(RegularExpression<S> subexpression) {
        super(subexpression.getAlphabet(), true, subexpression);
    }

    @Override
    public Option<S> withAlphabet(Alphabet<S> alphabet) {
        return getAlphabet().equals(alphabet)
                ? this
                : new Option<>(getChild(0).withAlphabet(alphabet));
    }
}

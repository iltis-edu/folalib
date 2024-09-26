package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import java.io.Serializable;

public class KleenePlus<S extends Serializable> extends RegularExpression<S> {

    // For serialization
    @SuppressWarnings("unused")
    private KleenePlus() {}

    /**
     * Build a new KleenePlus expression over the given alphabet which matches the given regex one
     * or more times
     *
     * @param subexpression the subexpression to match
     */
    public KleenePlus(RegularExpression<S> subexpression) {
        super(subexpression.getAlphabet(), true, subexpression);
    }

    @Override
    public KleenePlus<S> withAlphabet(Alphabet<S> alphabet) {
        return getAlphabet().equals(alphabet)
                ? this
                : new KleenePlus<>(getChild(0).withAlphabet(alphabet));
    }
}

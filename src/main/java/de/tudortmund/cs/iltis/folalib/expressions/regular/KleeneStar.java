package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import java.io.Serializable;

public class KleeneStar<S extends Serializable> extends RegularExpression<S> {

    // For serialization
    @SuppressWarnings("unused")
    private KleeneStar() {}

    /**
     * Build a new KleeneStar expression over the given alphabet which matches the given regex zero
     * or more times
     *
     * @param subexpression the subexpression to match
     */
    public KleeneStar(RegularExpression<S> subexpression) {
        super(subexpression.getAlphabet(), true, subexpression);
    }

    @Override
    public KleeneStar<S> withAlphabet(Alphabet<S> alphabet) {
        return getAlphabet().equals(alphabet)
                ? this
                : new KleeneStar<>(getChild(0).withAlphabet(alphabet));
    }
}

package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import java.io.Serializable;

public class EmptyLanguage<S extends Serializable> extends RegularExpression<S> {

    /** Build a new EmptyLanguage expression with empty alphabet */
    public EmptyLanguage() {
        this(new Alphabet<>());
    }

    /**
     * Build a new EmptyLanguage expression over the given alphabet
     *
     * @param alphabet the alphabet, over which this expression is defined
     */
    public EmptyLanguage(Alphabet<S> alphabet) {
        super(alphabet, true);
    }

    @Override
    public EmptyLanguage<S> withAlphabet(Alphabet<S> alphabet) {
        return getAlphabet().equals(alphabet) ? this : new EmptyLanguage<>(alphabet);
    }
}

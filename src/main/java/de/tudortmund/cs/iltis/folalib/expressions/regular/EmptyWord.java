package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import java.io.Serializable;

public class EmptyWord<S extends Serializable> extends RegularExpression<S> {

    /** Build a new EmptyWord expression with empty alphabet */
    public EmptyWord() {
        this(new Alphabet<>());
    }

    /**
     * Build a new EmptyWord expression over the given alphabet
     *
     * @param alphabet the alphabet, over which this expression is defined
     */
    public EmptyWord(Alphabet<S> alphabet) {
        super(alphabet, true);
    }

    @Override
    public EmptyWord<S> withAlphabet(Alphabet<S> alphabet) {
        return getAlphabet().equals(alphabet) ? this : new EmptyWord<>(alphabet);
    }
}

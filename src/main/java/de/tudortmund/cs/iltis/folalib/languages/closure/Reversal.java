package de.tudortmund.cs.iltis.folalib.languages.closure;

import de.tudortmund.cs.iltis.folalib.languages.Language;
import java.io.Serializable;

/**
 * Represents the abstract concept of reversing all words of a language.
 *
 * <p>Example: L = {a, babc, cca} ==> resulting language = {a, cbab, acc}
 *
 * @param <L> the language of which all words are reversed
 */
public class Reversal<L extends Language<? extends Serializable>> implements Serializable {

    private L language;

    /* For serialization */
    @SuppressWarnings("unused")
    private Reversal() {}

    public Reversal(L language) {
        this.language = language;
    }

    public L getLanguage() {
        return language;
    }
}

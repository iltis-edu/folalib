package de.tudortmund.cs.iltis.folalib.languages.closure;

import de.tudortmund.cs.iltis.folalib.languages.Language;
import java.io.Serializable;

/**
 * Represents the abstract concept of applying the KleeneStar operation to a language.
 *
 * <p>Example: L = {b} ==> resulting language = {ε, b, bb, bbb, ...}
 *
 * @param <L> the language to which the KleeneStar is applied
 */
public class KleeneStar<L extends Language<? extends Serializable>> implements Serializable {

    private L language;

    /* For serialization */
    @SuppressWarnings("unused")
    private KleeneStar() {}

    public KleeneStar(L language) {
        this.language = language;
    }

    public L getLanguage() {
        return language;
    }
}

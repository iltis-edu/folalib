package de.tudortmund.cs.iltis.folalib.languages.closure;

import de.tudortmund.cs.iltis.folalib.languages.Language;
import java.io.Serializable;

/**
 * Represents the abstract concept of applying the KleenePlus operation to a language.
 *
 * <p>Example: L = {a, b} ==> resulting language = {a, b, aa, ab, ba, bb, aaa, aab, aba, ...}
 *
 * @param <L> the language to which the KleenePlus is applied
 */
public class KleenePlus<L extends Language<? extends Serializable>> implements Serializable {

    private L language;

    /* For serialization */
    @SuppressWarnings("unused")
    private KleenePlus() {}

    public KleenePlus(L language) {
        this.language = language;
    }

    public L getLanguage() {
        return language;
    }
}

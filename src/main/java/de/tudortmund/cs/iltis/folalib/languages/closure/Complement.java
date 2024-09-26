package de.tudortmund.cs.iltis.folalib.languages.closure;

import de.tudortmund.cs.iltis.folalib.languages.Language;
import java.io.Serializable;

/**
 * Represents the abstract concept of complementing a language.
 *
 * <p>The complement of L is identical to `difference(Σ*, L)`.
 *
 * <p>Example: L = {a, b}, alphabet = {a, b} ==> resulting language = {ε, aa, ab, ba, bb, aaa}
 *
 * @param <L> the language which is complemented
 */
public class Complement<L extends Language<? extends Serializable>> implements Serializable {

    private L language;

    /* For serialization */
    @SuppressWarnings("unused")
    private Complement() {}

    public Complement(L language) {
        this.language = language;
    }

    public L getLanguage() {
        return language;
    }
}

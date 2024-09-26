package de.tudortmund.cs.iltis.folalib.languages.closure;

import de.tudortmund.cs.iltis.folalib.languages.Language;
import java.io.Serializable;

/**
 * Represents the abstract concept of computing the intersection of two languages.
 *
 * <p>Example: L1 = {a, ab, abc}, L2 = {b, bc, abc} ==> resulting language = {abc}
 *
 * @param <L1> the first language of the intersection
 * @param <L2> the second language of the intersection
 */
public class Intersection<
                L1 extends Language<? extends Serializable>,
                L2 extends Language<? extends Serializable>>
        implements Serializable {

    private L1 language1;
    private L2 language2;

    /* For serialization */
    @SuppressWarnings("unused")
    private Intersection() {}

    public Intersection(L1 language1, L2 language2) {
        this.language1 = language1;
        this.language2 = language2;
    }

    public L1 getFirstLanguage() {
        return language1;
    }

    public L2 getSecondLanguage() {
        return language2;
    }
}

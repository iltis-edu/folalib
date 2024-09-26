package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Complement;
import de.tudortmund.cs.iltis.folalib.languages.closure.Difference;
import de.tudortmund.cs.iltis.folalib.languages.closure.Intersection;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute an NFA for all in L1, that are not in L2.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularDifferenceToEpsilonNFA<S extends Serializable>
        implements SerializableFunction<
                Difference<RegularLanguage<S>, RegularLanguage<S>>,
                NFA<? extends Serializable, S>> {

    @Override
    public NFA<? extends Serializable, S> apply(
            Difference<RegularLanguage<S>, RegularLanguage<S>> difference) {
        Complement<RegularLanguage<S>> complement =
                new Complement<>(difference.getSecondLanguage());
        NFA<? extends Serializable, S> complementNFA =
                new RegularComplementToDFA<S>().apply(complement);
        RegularLanguage<S> complementLanguage = new RegularLanguage<>(complementNFA);
        Intersection<RegularLanguage<S>, RegularLanguage<S>> intersection =
                new Intersection<>(difference.getFirstLanguage(), complementLanguage);
        return new RegularIntersectionToEpsilonNFA<S>().apply(intersection);
    }
}

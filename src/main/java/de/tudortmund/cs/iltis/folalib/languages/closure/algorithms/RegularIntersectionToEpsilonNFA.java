package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Intersection;
import de.tudortmund.cs.iltis.folalib.util.BinaryFunctions;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute an NFA for all words which are in L1 *and* L2.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularIntersectionToEpsilonNFA<S extends Serializable>
        implements SerializableFunction<
                Intersection<RegularLanguage<S>, RegularLanguage<S>>,
                NFA<? extends Serializable, S>> {

    @Override
    public final NFA<? extends Serializable, S> apply(
            Intersection<RegularLanguage<S>, RegularLanguage<S>> intersection) {
        NFA<Serializable, S> nfa1 =
                intersection.getFirstLanguage().getDFA().mapStates(state -> (Serializable) state);
        NFA<Serializable, S> nfa2 =
                intersection.getSecondLanguage().getDFA().mapStates(state -> (Serializable) state);

        Alphabet<S> unionAlphabet = Alphabets.unionOf(nfa1.getAlphabet(), nfa2.getAlphabet());

        return nfa1.totalifyWithRegardTo(unionAlphabet)
                .product(nfa2.totalifyWithRegardTo(unionAlphabet), BinaryFunctions.AND);
    }
}

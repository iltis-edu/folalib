package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.SymmetricDifference;
import de.tudortmund.cs.iltis.folalib.util.BinaryFunctions;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute a RegularExpression for the symmetric difference of two regular
 * languages.
 *
 * @param <S> the type of symbols of the languages
 */
public class RegularSymmetricDifferenceToDFA<S extends Serializable>
        implements SerializableFunction<
                SymmetricDifference<RegularLanguage<S>, RegularLanguage<S>>,
                NFA<? extends Serializable, S>> {

    /**
     * We take the product automaton and set the accepting states if one component of the new state
     * was an accepting state before, but now both. Hence, we use {@link BinaryFunctions#XOR} as
     * combinator.
     */
    @Override
    public NFA<? extends Serializable, S> apply(
            SymmetricDifference<RegularLanguage<S>, RegularLanguage<S>> symmetricDifference) {
        NFA<? extends Serializable, S> firstDFA = symmetricDifference.getFirstLanguage().getDFA();
        NFA<? extends Serializable, S> secondDFA = symmetricDifference.getSecondLanguage().getDFA();

        // without DFA transform because we already know that the two NFAs are deterministic (DFA)
        return firstDFA.productWithoutDFATransform(secondDFA, BinaryFunctions.XOR);
    }
}

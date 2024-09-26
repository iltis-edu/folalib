package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.KleenePlus;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute an NFA for all words in language^+.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularKleenePlusToEpsilonNFA<S extends Serializable>
        implements SerializableFunction<
                KleenePlus<RegularLanguage<S>>, NFA<? extends Serializable, S>> {

    @Override
    public NFA<? extends Serializable, S> apply(KleenePlus<RegularLanguage<S>> kleenePlus) {
        NFA<Serializable, S> nfa =
                kleenePlus.getLanguage().getNFA().mapStates(t -> (Serializable) t);
        NFABuilder<Serializable, S> builder = new NFABuilder<>(nfa);
        for (Serializable initialState : nfa.getInitialStates()) {
            for (Serializable acceptingState : nfa.getAcceptingStates()) {
                builder.withEpsilonTransition(acceptingState, initialState);
            }
        }
        return builder.build().unwrap();
    }
}

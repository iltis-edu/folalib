package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.KleeneStar;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute an NFA for all words in language^*.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularKleeneStarToEpsilonNFA<S extends Serializable>
        implements SerializableFunction<
                KleeneStar<RegularLanguage<S>>, NFA<? extends Serializable, S>> {

    @Override
    public NFA<? extends Serializable, S> apply(KleeneStar<RegularLanguage<S>> kleeneStar) {
        NFA<MaybeGenerated<? extends Serializable, String>, S> nfa =
                kleeneStar.getLanguage().getNFA().mapStates(MaybeGenerated.Input::new);
        NFABuilder<MaybeGenerated<? extends Serializable, String>, S> builder =
                new NFABuilder<>(nfa);
        MaybeGenerated<? extends Serializable, String> newInitialState =
                new MaybeGenerated.Generated<>("newInitialState");
        builder.overrideInitial(newInitialState);
        builder.overrideAccepting(newInitialState);
        nfa.getInitialStates().forEach(t -> builder.withEpsilonTransition(newInitialState, t));
        nfa.getAcceptingStates().forEach(t -> builder.withEpsilonTransition(t, newInitialState));
        return builder.build().unwrap();
    }
}

package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFATransition;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Reversal;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute an NFA for all reversed words of a language.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularReversalToEpsilonNFA<S extends Serializable>
        implements SerializableFunction<
                Reversal<RegularLanguage<S>>, NFA<? extends Serializable, S>> {

    @Override
    public NFA<? extends Serializable, S> apply(Reversal<RegularLanguage<S>> reversal) {
        NFA<MaybeGenerated<Serializable, String>, S> dfa =
                reversal.getLanguage().getDFA().mapStates(MaybeGenerated.Input::new);

        NFABuilder<MaybeGenerated<Serializable, String>, S> builder =
                new NFABuilder<>(dfa.getAlphabet());
        builder.withStates(dfa.getStates());
        if (dfa.getAcceptingStates().isEmpty()) {
            builder.withInitial(new MaybeGenerated.Generated<>("newInitialState"));
        } else {
            builder.withInitial(dfa.getAcceptingStates());
        }
        builder.withAccepting(dfa.getInitialStates());
        for (MaybeGenerated<Serializable, String> source : dfa.getStates()) {
            for (NFATransition<MaybeGenerated<Serializable, String>, S> transition :
                    dfa.getTransitions().in(source)) {
                MaybeGenerated<Serializable, String> target = transition.getState();
                S symbol = transition.getSymbol();
                builder.withTransition(target, symbol, source);
            }
        }
        return builder.build().unwrap();
    }
}

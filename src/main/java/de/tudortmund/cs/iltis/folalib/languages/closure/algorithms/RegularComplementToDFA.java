package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.automata.finite.DFABuilder;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFATransition;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Complement;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute a DFA for all words which are not in the language.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularComplementToDFA<S extends Serializable>
        implements SerializableFunction<
                Complement<RegularLanguage<S>>, NFA<? extends Serializable, S>> {

    @Override
    public NFA<? extends Serializable, S> apply(Complement<RegularLanguage<S>> complement) {
        NFA<Serializable, S> dfa =
                complement.getLanguage().getDFA().mapStates(t -> (Serializable) t);

        DFABuilder<Serializable, S> builder = new DFABuilder<>(dfa.getAlphabet());
        builder.withInitial(dfa.getInitialStates());
        for (Serializable state : dfa.getStates()) {
            builder.withStates(state);
            for (NFATransition<Serializable, S> transition : dfa.getTransitions().in(state)) {
                builder.withTransition(state, transition);
            }
            if (!dfa.getAcceptingStates().contains(state)) {
                builder.withAccepting(state);
            }
        }
        return builder.build().unwrap();
    }
}

package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFATransition;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Union;
import de.tudortmund.cs.iltis.utils.collections.SerializablePair;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute an NFA for the union of two regular languages.
 *
 * @param <S> the type of symbols of the languages
 */
public class RegularUnionToEpsilonNFA<S extends Serializable>
        implements SerializableFunction<
                Union<RegularLanguage<S>, RegularLanguage<S>>, NFA<? extends Serializable, S>> {

    @Override
    public NFA<? extends Serializable, S> apply(
            Union<RegularLanguage<S>, RegularLanguage<S>> union) {
        NFA<? extends Serializable, S> nfa1 = union.getFirstLanguage().getNFA();
        NFA<? extends Serializable, S> nfa2 = union.getSecondLanguage().getNFA();

        NFA<SerializablePair<? extends Serializable, String>, S> nfa1Mapped =
                nfa1.mapStates(t -> new SerializablePair<>(t, "1"));
        NFA<SerializablePair<? extends Serializable, String>, S> nfa2Mapped =
                nfa2.mapStates(t -> new SerializablePair<>(t, "2"));

        Alphabet<S> unionAlphabet = nfa1Mapped.getAlphabet().unionWith(nfa2Mapped.getAlphabet());

        NFABuilder<SerializablePair<? extends Serializable, String>, S> builder =
                new NFABuilder<>(unionAlphabet);
        builder.withStates(nfa1Mapped.getStates());
        builder.withStates(nfa2Mapped.getStates());
        builder.withInitial(nfa1Mapped.getInitialStates());
        builder.withInitial(nfa2Mapped.getInitialStates());
        for (SerializablePair<? extends Serializable, String> state : nfa1Mapped.getStates()) {
            for (NFATransition<SerializablePair<? extends Serializable, String>, S> transition :
                    nfa1Mapped.getTransitions().in(state)) {
                builder.withTransition(state, transition);
            }
        }
        for (SerializablePair<? extends Serializable, String> state : nfa2Mapped.getStates()) {
            for (NFATransition<SerializablePair<? extends Serializable, String>, S> transition :
                    nfa2Mapped.getTransitions().in(state)) {
                builder.withTransition(state, transition);
            }
        }
        builder.withAccepting(nfa1Mapped.getAcceptingStates());
        builder.withAccepting(nfa2Mapped.getAcceptingStates());
        return builder.build().unwrap();
    }
}

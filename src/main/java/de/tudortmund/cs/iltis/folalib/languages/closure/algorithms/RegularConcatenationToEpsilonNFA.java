package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Concatenation;
import de.tudortmund.cs.iltis.utils.collections.SerializablePair;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute an NFA for all possible words uv, s.t. u in L1 and v in L2.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularConcatenationToEpsilonNFA<S extends Serializable>
        implements SerializableFunction<
                Concatenation<RegularLanguage<S>, RegularLanguage<S>>,
                NFA<? extends Serializable, S>> {

    @Override
    public NFA<? extends Serializable, S> apply(
            Concatenation<RegularLanguage<S>, RegularLanguage<S>> concatenation) {
        NFA<SerializablePair<Serializable, String>, S> nfa1 =
                concatenation
                        .getFirstLanguage()
                        .getNFA()
                        .mapStates(t -> new SerializablePair<>(t, "1"));
        NFA<SerializablePair<Serializable, String>, S> nfa2 =
                concatenation
                        .getSecondLanguage()
                        .getNFA()
                        .mapStates(t -> new SerializablePair<>(t, "2"));

        NFABuilder<SerializablePair<Serializable, String>, S> builder = new NFABuilder<>(nfa1);
        builder.mergeWith(false, new NFABuilder<>(nfa2));
        builder.overrideAccepting(nfa2.getAcceptingStates());

        for (SerializablePair<Serializable, String> acceptingInNfa1 : nfa1.getAcceptingStates()) {
            for (SerializablePair<Serializable, String> initialInNfa2 : nfa2.getInitialStates()) {
                builder.withEpsilonTransition(acceptingInNfa1, initialInNfa2);
            }
        }
        return builder.build().unwrap();
    }
}

package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFATransition;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.folalib.languages.closure.InverseHomomorphism;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute a NFA for all words such that their image under a homomorphism is in the
 * language.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularInverseHomomorphismToEpsilonNFA<S extends Serializable, T extends Serializable>
        implements SerializableFunction<
                InverseHomomorphism<S, T, RegularLanguage<T>>, NFA<? extends Serializable, S>> {

    @Override
    public NFA<? extends Serializable, S> apply(
            InverseHomomorphism<S, T, RegularLanguage<T>> homomorphism) {
        NFA<Serializable, T> dfa =
                homomorphism.getLanguage().getDFA().mapStates(t -> (Serializable) t);
        SerializableFunction<S, Word<T>> homo = homomorphism.getHomomorphism();
        Alphabet<S> alphabet = new Alphabet<>(homomorphism.getDomain());

        NFABuilder<Serializable, S> builder = new NFABuilder<>(alphabet);
        builder.withStates(dfa.getStates());
        builder.withInitial(dfa.getInitialStates());
        builder.withAccepting(dfa.getAcceptingStates());
        for (Serializable source : dfa.getStates()) {
            for (S symbol : homomorphism.getDomain()) {
                Word<T> word = homo.apply(symbol);
                Serializable target = computeTargetState(dfa, source, word);
                builder.withTransition(source, symbol, target);
            }
        }
        return builder.build().unwrap();
    }

    private Serializable computeTargetState(
            NFA<Serializable, T> dfa, Serializable source, Word<T> word) {
        Serializable current = source;
        for (T symbol : word) {
            NFATransition<Serializable, T> transition =
                    dfa.getTransitions(current, symbol).stream()
                            .findFirst()
                            .get(); // this should always be safe because it is a DFA
            current = transition.getState();
        }
        return current;
    }
}

package de.tudortmund.cs.iltis.folalib.automata.finite.conversion;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converts the given NFA to a NFA with only reachable states.
 *
 * @param <V> The type of the states of the NFA
 * @param <E> The type of the input symbols
 */
public class NFAOnlyReachableStatesConversion<V extends Serializable, E extends Serializable>
        extends NFAConversion<V, V, E> {

    @Override
    protected NFA<V, E> convert(NFA<V, E> nfa) {
        // Cached variable to avoid computing multiple times
        Set<V> reachableStates = nfa.getReachableStates();

        NFABuilder<V, E> builder =
                new NFABuilder<V, E>(nfa.getAlphabet())
                        .withStates(reachableStates)
                        .withInitial(nfa.getInitialStates())
                        // All accepting states need to be reachable as well
                        .withAccepting(
                                nfa.getAcceptingStates().stream()
                                        .filter(reachableStates::contains)
                                        .collect(Collectors.toSet()));

        nfa.getTransitions()
                .getTransitions()
                .forEach(
                        (origin, transitions) -> {
                            // Only transitions originating in states that are reachable need to get
                            // transferred to the new NFA
                            if (reachableStates.contains(origin)) {
                                transitions.forEach(
                                        transition -> builder.withTransition(origin, transition));
                            }
                        });

        return builder.build().unwrap();
    }

    @Override
    protected NFA<V, E> identity(NFA<V, E> nfa) {
        return nfa;
    }

    @Override
    protected boolean isRedundant(NFA<V, E> nfa) {
        return nfa.getReachableStates().size() == nfa.getStates().size();
    }
}

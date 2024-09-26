package de.tudortmund.cs.iltis.folalib.automata.finite.conversion;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFATransition;
import de.tudortmund.cs.iltis.utils.collections.Tuple;
import de.tudortmund.cs.iltis.utils.collections.relations.FiniteBinaryRelation;
import de.tudortmund.cs.iltis.utils.graph.Edge;
import de.tudortmund.cs.iltis.utils.graph.bisimulation.IntersectionBisimulation;
import de.tudortmund.cs.iltis.utils.graph.hashgraph.HashGraph;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Converts the given DFA to its minimal DFA. Can only be applied to NFAs that are total and
 * deterministic.
 *
 * @param <V> The type of the states of the NFA
 * @param <E> The type of the input symbols
 */
public class DFAMinimizationConversion<V extends Serializable, E extends Serializable>
        extends NFAConversion<V, LinkedHashSet<V>, E> {

    private NFA<V, E> dfa;

    @Override
    protected NFA<LinkedHashSet<V>, E> convert(NFA<V, E> nfa) {
        setDFA(nfa);
        return buildMinimizedDFA(makeStateSetMap(calculateStateBisimulation()));
    }

    @Override
    protected NFA<LinkedHashSet<V>, E> identity(NFA<V, E> nfa) {
        return nfa.mapStates(state -> new LinkedHashSet<>(Collections.singletonList(state)));
    }

    @Override
    protected boolean isRedundant(NFA<V, E> nfa) {
        setDFA(nfa);
        return calculateStateBisimulation().isEmpty();
    }

    /**
     * Calculates a map of old states to new set states according to the given bisimulation. All
     * bisimilar states are combined into one set.
     *
     * @param bisimulation the bisimulation
     * @return states of the minimized NFA
     */
    private Map<V, LinkedHashSet<V>> makeStateSetMap(FiniteBinaryRelation<V> bisimulation) {
        Map<V, LinkedHashSet<V>> stateToSetState = makeSingletonStateSetMap();

        for (Tuple<V> tuple : bisimulation) {
            Set<V> leftStates = stateToSetState.get(tuple.first());
            Set<V> rightStates = stateToSetState.get(tuple.second());
            LinkedHashSet<V> unionState = new LinkedHashSet<>(leftStates);
            unionState.addAll(rightStates);
            stateToSetState.put(tuple.first(), unionState);
            stateToSetState.put(tuple.second(), unionState);
        }

        return stateToSetState;
    }

    /**
     * Calculates a map of old states to new set states where each set is a singleton only
     * containing the old state.
     *
     * @return the map of old states to new setStates
     */
    private Map<V, LinkedHashSet<V>> makeSingletonStateSetMap() {
        return dfa.getStates().stream()
                .collect(
                        Collectors.toMap(
                                s -> s, s -> new LinkedHashSet<>(Collections.singletonList(s))));
    }

    /**
     * Constructs the minimized DFA by handing the given mapping to other helping methods.
     *
     * @param stateToSetState the map of old states to new setStates
     * @return the minimized DFA
     */
    private NFA<LinkedHashSet<V>, E> buildMinimizedDFA(Map<V, LinkedHashSet<V>> stateToSetState) {
        NFABuilder<LinkedHashSet<V>, E> builder =
                new NFABuilder<LinkedHashSet<V>, E>(dfa.getAlphabet())
                        .withStates(stateToSetState.values())
                        .withAccepting(getMinimizedAcceptingStates(stateToSetState))
                        .withInitial(getMinimizedInitialStates(stateToSetState));
        addTransitionsOfMinimizedDFA(builder, stateToSetState);
        return builder.build().unwrap();
    }

    /**
     * Computes the accepting states as of the minimized DFA
     *
     * @param stateToSetState the map of old states to new setStates
     * @return the accepting states
     */
    private Set<LinkedHashSet<V>> getMinimizedAcceptingStates(
            Map<V, LinkedHashSet<V>> stateToSetState) {
        return stateToSetState.values().stream()
                .filter(
                        setState ->
                                setState.stream()
                                        .anyMatch(
                                                state -> dfa.getAcceptingStates().contains(state)))
                .collect(Collectors.toSet());
    }

    /**
     * Computes the initial states of the minimized DFA
     *
     * @param stateToSetState the map of old states to new setStates
     * @return the initial states
     */
    private Set<LinkedHashSet<V>> getMinimizedInitialStates(
            Map<V, LinkedHashSet<V>> stateToSetState) {
        return stateToSetState.values().stream()
                .filter(
                        setState ->
                                setState.stream()
                                        .anyMatch(state -> dfa.getInitialStates().contains(state)))
                .collect(Collectors.toSet());
    }

    /**
     * Adds the transitions to the NFA builder for the minimized DFA
     *
     * @param builder the builder for the minimized DFA
     * @param stateToSetState the map of old states to new setStates
     */
    @SuppressWarnings("all")
    private void addTransitionsOfMinimizedDFA(
            NFABuilder<LinkedHashSet<V>, E> builder, Map<V, LinkedHashSet<V>> stateToSetState) {
        for (LinkedHashSet<V> sourceSetState : stateToSetState.values()) {
            for (E symbol : dfa.getAlphabet()) {
                V sourceRepresentative = sourceSetState.stream().findAny().get(); // cannot be empty
                V targetRepresentative =
                        dfa.reachableWith(sourceRepresentative, symbol).stream()
                                .findAny()
                                .get(); // cannot be empty
                LinkedHashSet<V> targetSetState = stateToSetState.get(targetRepresentative);
                builder.withTransition(sourceSetState, symbol, targetSetState);
            }
        }
    }

    /**
     * Sets the given NFA as attribute of this object if the NFA is deterministic and total. In
     * addition, the conversion {@link NFAOnlyReachableStatesConversion} is applied.
     *
     * @param nfa the DFA to minimize
     * @throws IllegalArgumentException if the DFA is non-deterministic (NFA) or not total
     */
    private void setDFA(NFA<V, E> nfa) {
        if (!nfa.isDeterministic())
            throw new IllegalArgumentException("NFA has to be deterministic");
        if (!nfa.isTotal()) throw new IllegalArgumentException("NFA has to be total");
        dfa = new NFAOnlyReachableStatesConversion<V, E>().apply(nfa);
    }

    /**
     * Computes tuples of states which can be combined.
     *
     * @return the bisimulation relation
     */
    private FiniteBinaryRelation<V> calculateStateBisimulation() {
        HashGraph<V, NFATransition<V, E>> graph = dfa.asGraph();

        // vertex comparison: same accepting behavior
        BiFunction<V, V, Boolean> vertexComparator =
                (p, q) ->
                        dfa.getAcceptingStates().contains(p)
                                == dfa.getAcceptingStates().contains(q);
        // edge comparison: same symbol
        BiFunction<Edge<V, NFATransition<V, E>>, Edge<V, NFATransition<V, E>>, Boolean>
                edgeComparator = (s, t) -> (s.get().getSymbol().equals(t.get().getSymbol()));

        IntersectionBisimulation<V, NFATransition<V, E>> calculator =
                new IntersectionBisimulation<>(graph, graph, vertexComparator, edgeComparator);

        return calculator.compute();
    }
}

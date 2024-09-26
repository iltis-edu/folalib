package de.tudortmund.cs.iltis.folalib.automata.finite.transformation;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFATransition;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFATransitions;
import de.tudortmund.cs.iltis.folalib.expressions.regular.*;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.folalib.util.CachedSerializableFunction;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import de.tudortmund.cs.iltis.utils.graph.Edge;
import de.tudortmund.cs.iltis.utils.graph.Graph;
import de.tudortmund.cs.iltis.utils.graph.Vertex;
import de.tudortmund.cs.iltis.utils.graph.hashgraph.HashGraph;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A transformation to transform an (epsilon)-{@link NFA} to a {@link RegularExpression}
 *
 * <p>The general approach is as follows: 1) convert NFA to a standard form (precisely 1 initial and
 * 1 accepting state) 2) map each symbol to a regex and each epsilon (on the transitions) to an
 * empty word 3) merge multiple edges, s.t. there is exactly one edge between two states, missing
 * edges are labelled with empty language 4) remove all states except the initial and accepting ones
 * 5) extract the final regex from the remaining four sub-regexes on the transitions
 *
 * <p>The algorithm is taken from "Introduction to Automata Theory, Languages and Computation" by
 * Hopcroft, Motwani and Ullman (3rd edition, pages 98-101).
 */
public class EpsilonNFAToRegularExpressionTransformation<S extends Serializable>
        implements SerializableFunction<NFA<? extends Serializable, S>, RegularExpression<S>> {

    /**
     * Transforms the given {@link NFA} to a regular expression over the same symbols
     *
     * @param nfa the NFA to transform
     * @return a regex which describes the same language as the NFA
     */
    @Override
    public RegularExpression<S> apply(NFA<? extends Serializable, S> nfa) {
        Alphabet<S> baseAlphabet = nfa.getAlphabet();
        NFA<? extends Serializable, S> standardizedNFA = getStandardizedNFA(nfa);
        NFA<? extends Serializable, RegularExpression<S>> embeddedNFA =
                getNFAWithEmbeddedSymbols(baseAlphabet, standardizedNFA);
        NFA<? extends Serializable, RegularExpression<S>> singleEdgedNFA =
                getNFAWithoutMultiEdges(baseAlphabet, embeddedNFA);
        NFA<? extends Serializable, RegularExpression<S>> reducedNFA =
                getReducedNFA(baseAlphabet, singleEdgedNFA);
        RegularExpression<S> regex = extractRegularExpression(reducedNFA);
        return RegularExpressionSimplifier.simplify(regex);
    }

    /**
     * Prepares the NFA, such that it has precisely one initial and one accepting state
     *
     * @param nfa the NFA to prepared
     * @param <S> the type of symbols of the NFA
     * @return a new, equivalent NFA
     */
    private static <S extends Serializable> NFA<? extends Serializable, S> getStandardizedNFA(
            NFA<? extends Serializable, S> nfa) {
        CachedSerializableFunction<Serializable, Serializable> embedding =
                new CachedSerializableFunction<>(MaybeGenerated.Input::new);
        NFABuilder<Serializable, S> builder = new NFABuilder<>(nfa.mapStates(embedding::apply));

        MaybeGenerated<Serializable, String> newInitialState =
                new MaybeGenerated.Generated<>("initialState");
        builder.overrideInitial(newInitialState);
        for (Serializable initialState : nfa.getInitialStates()) {
            builder.withEpsilonTransition(newInitialState, embedding.apply(initialState));
        }

        MaybeGenerated<Serializable, String> newAcceptingState =
                new MaybeGenerated.Generated<>("acceptingState");
        builder.overrideAccepting(newAcceptingState);
        for (Serializable acceptingState : nfa.getAcceptingStates()) {
            builder.withEpsilonTransition(embedding.apply(acceptingState), newAcceptingState);
        }
        return builder.build().unwrap();
    }

    /**
     * Embeds the symbols of the {@link NFA} in {@link RegularExpression} objects
     *
     * <p>The resulting NFA does not have any epsilon transitions, because each epsilon is replaced
     * with an empty word regex
     *
     * @param nfa the NFA of which the symbols should be embedded into regular expressions
     * @param <S> the type of symbols in the NFA
     * @param <K> the type of states in the NFA
     * @return a new, equivalent NFA with regexes as symbols
     */
    private static <S extends Serializable, K extends Serializable>
            NFA<K, RegularExpression<S>> getNFAWithEmbeddedSymbols(
                    Alphabet<S> baseAlphabet, NFA<K, S> nfa) {
        CachedSerializableFunction<S, RegularExpression<S>> embedding =
                new CachedSerializableFunction<>(s -> new Symbol<>(baseAlphabet, s));
        LinkedHashSet<RegularExpression<S>> alphabet =
                nfa.getAlphabet().stream()
                        .map(embedding)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
        alphabet.add(new EmptyWord<>(baseAlphabet));
        NFABuilder<K, RegularExpression<S>> builder = new NFABuilder<>(new Alphabet<>(alphabet));
        builder.withInitial(nfa.getInitialStates());
        builder.withStates(nfa.getStates());
        nfa.getAcceptingStates().forEach(builder::withAccepting);
        for (Map.Entry<K, Set<NFATransition<K, S>>> entry :
                nfa.getTransitions().getTransitions().entrySet()) {
            K sourceState = entry.getKey();
            for (NFATransition<K, S> transition : entry.getValue()) {
                K targetState = transition.getState();
                RegularExpression<S> regex =
                        transition.isEpsilon()
                                ? new EmptyWord<>(baseAlphabet)
                                : embedding.apply(transition.getSymbol());
                builder.withTransition(sourceState, regex, targetState);
            }
        }
        return builder.build().unwrap().mapAlphabet(RegularExpressionSimplifier::simplify);
    }

    /**
     * Merges multiple edges in an {@link NFA} into a single edge
     *
     * <p>As a side effect, this method also adds all missing edges with a EmptyLanguage label. As a
     * result, the returned NFA is a complete graph.
     *
     * <p>This is possible, because the symbols are regular expressions and we can merge two
     * alternative transitions between the same states into one transition using the "alternative"
     * construct of regular expressions.
     *
     * @param nfa the NFA in which the edges should be merged
     * @param <S> the type of symbols in the NFA
     * @param <K> the type of states in the NFA
     * @return a new, equivalent NFA with no duplicate edges between any two states
     */
    private static <S extends Serializable, K extends Serializable>
            NFA<? extends Serializable, RegularExpression<S>> getNFAWithoutMultiEdges(
                    Alphabet<S> baseAlphabet, NFA<K, RegularExpression<S>> nfa) {
        NFATransitions<K, RegularExpression<S>> transitions = new NFATransitions<>();
        LinkedHashSet<RegularExpression<S>> alphabet = new LinkedHashSet<>();

        HashGraph<K, NFATransition<K, RegularExpression<S>>> graph = nfa.asGraph();
        for (Vertex<K, NFATransition<K, RegularExpression<S>>> source : graph.getVertices()) {
            for (Vertex<K, NFATransition<K, RegularExpression<S>>> target : graph.getVertices()) {
                Stream<RegularExpression<S>> edges =
                        getEdgesBetween(source, target, graph).stream()
                                .map(edge -> edge.get().getSymbol()); // by construction
                // `getSymbol` never returns
                // `null`, otherwise
                // `embedSymbolsInRegularExpressions` is buggy
                RegularExpression<S> regex =
                        edges.reduce(new EmptyLanguage<>(baseAlphabet), RegularExpression::or);

                transitions.addTransition(source.get(), new NFATransition<>(regex, target.get()));
                alphabet.add(regex);
            }
        }

        NFABuilder<K, RegularExpression<S>> builder = new NFABuilder<>(new Alphabet<>(alphabet));
        builder.withStates(nfa.getStates());
        builder.withInitial(nfa.getInitialStates());
        nfa.getAcceptingStates().forEach(builder::withAccepting);
        transitions
                .getTransitions()
                .forEach(
                        (source, allTrans) ->
                                allTrans.forEach(trans -> builder.withTransition(source, trans)));

        return builder.build().unwrap().mapAlphabet(RegularExpressionSimplifier::simplify);
    }

    private static <V extends Serializable, E extends Serializable> Set<Edge<V, E>> getEdgesBetween(
            Vertex<V, E> source, Vertex<V, E> target, Graph<V, E> graph) {
        return graph.getEdges().stream()
                .filter(edge -> edge.getSource().equals(source) && edge.getTarget().equals(target))
                .collect(Collectors.toSet());
    }

    /**
     * Iteratively removes all states from the given NFA except for the initial and the accepting
     * state
     *
     * @param nfa the NFA from which all intermediate states should be removed
     * @param <S> the type of the symbols of the NFA
     * @param <K> the type of the states of the NFA
     * @return a new, equivalent NFA with exactly two states
     */
    private static <S extends Serializable, K extends Serializable>
            NFA<? extends Serializable, RegularExpression<S>> getReducedNFA(
                    Alphabet<S> baseAlphabet, NFA<K, RegularExpression<S>> nfa) {
        NFA<K, RegularExpression<S>> tmp = nfa;
        for (K state : getIntermediateStates(nfa)) {
            tmp = removeState(state, baseAlphabet, tmp);
        }
        return tmp;
    }

    private static <K extends Serializable> Set<K> getIntermediateStates(
            NFA<K, ? extends Serializable> nfa) {
        return nfa.getStates().stream()
                .filter(
                        state ->
                                !nfa.getInitialStates().contains(state)
                                        && !nfa.getAcceptingStates().contains(state))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Remove the given state from the NFA
     *
     * <p>To obtain an equivalent NFA all pairs of edges (x, state) and (state, y) (for all
     * combinations of x and y) are removed and the edge (x, y) is adjusted accordingly.
     *
     * @param state the state to remove
     * @param nfa the NFA from which the state should be removed
     * @param <S> the type of symbols of the NFA
     * @param <K> the type of states of the NFA
     * @return a new, equivalent NFA where `state` has been removed
     */
    private static <S extends Serializable, K extends Serializable>
            NFA<K, RegularExpression<S>> removeState(
                    K state, Alphabet<S> baseAlphabet, NFA<K, RegularExpression<S>> nfa) {
        HashGraph<K, NFATransition<K, RegularExpression<S>>> graph = nfa.asGraph();
        Set<K> predecessors =
                graph.getInNeighborValues(state).stream()
                        .filter(s -> !s.equals(state))
                        .collect(Collectors.toSet());
        Set<K> successors =
                graph.getOutNeighborValues(state).stream()
                        .filter(s -> !s.equals(state))
                        .collect(Collectors.toSet());
        RegularExpression<S> selfLoopRegex =
                graph.getEdge(state, state)
                        .get()
                        .getSymbol(); // we know for sure there can be no multi-edges at this point
        for (K pred : predecessors) {
            for (K succ : successors) {
                RegularExpression<S> predRegex = graph.getEdge(pred, state).get().getSymbol();
                RegularExpression<S> succRegex = graph.getEdge(state, succ).get().getSymbol();
                RegularExpression<S> directRegex =
                        graph.hasEdge(pred, succ)
                                ? graph.getEdge(pred, succ).get().getSymbol()
                                : new EmptyLanguage<>(baseAlphabet);
                directRegex = directRegex.or(predRegex.concat(selfLoopRegex.star(), succRegex));
                // update the edge in the graph
                graph.removeEdge(pred, succ);
                graph.addEdge(
                        pred,
                        succ,
                        new NFATransition<>(
                                RegularExpressionSimplifier.simplify(directRegex), succ));
            }
        }
        for (K pred : predecessors) {
            for (K succ : successors) {
                graph.removeEdge(pred, state);
                graph.removeEdge(state, succ);
            }
        }
        graph.removeVertex(state);

        LinkedHashSet<RegularExpression<S>> alphabet =
                graph.getEdgeValues().stream()
                        .map(NFATransition::getSymbol)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
        NFABuilder<K, RegularExpression<S>> builder = new NFABuilder<>(new Alphabet<>(alphabet));
        builder.withStates(nfa.getStates());
        nfa.getAcceptingStates().forEach(builder::withAccepting);
        builder.withInitial(nfa.getInitialStates());
        graph.getEdges().forEach(edge -> builder.withTransition(edge.getSourceValue(), edge.get()));
        return builder.build().unwrap();
    }

    /**
     * Extract a regular expression from the given {@link NFA} <br>
     * <b>Important:</b> this function is intended to be called as part of an algorithm and the
     * provided NFA is expected to have very specific properties.
     *
     * @param nfa the NFA from which the regex should be extracted
     * @param <S> the type of symbols of the NFA
     * @param <K> the type of states of the NFA
     * @return a regular expression for the language recognised by the NFA
     */
    private static <S extends Serializable, K extends Serializable>
            RegularExpression<S> extractRegularExpression(NFA<K, RegularExpression<S>> nfa) {
        K initialState = nfa.getInitialStates().stream().findFirst().get();
        K acceptingState = nfa.getAcceptingStates().stream().findFirst().get();

        HashGraph<K, NFATransition<K, RegularExpression<S>>> graph = nfa.asGraph();

        RegularExpression<S> r = graph.getEdge(initialState, initialState).get().getSymbol();
        RegularExpression<S> s = graph.getEdge(initialState, acceptingState).get().getSymbol();
        RegularExpression<S> t = graph.getEdge(acceptingState, acceptingState).get().getSymbol();
        RegularExpression<S> u = graph.getEdge(acceptingState, initialState).get().getSymbol();

        return r.or(s.concat(u.star(), t)).star().concat(s, u.star());
    }
}

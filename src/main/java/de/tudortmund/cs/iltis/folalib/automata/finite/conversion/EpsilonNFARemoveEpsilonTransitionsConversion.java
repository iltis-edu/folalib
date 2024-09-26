package de.tudortmund.cs.iltis.folalib.automata.finite.conversion;

import de.tudortmund.cs.iltis.folalib.automata.finite.*;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Removes all epsilon transitions from an {@link EpsilonNFA} by applying the steps given <a
 * href="https://www.geeksforgeeks.org/conversion-of-epsilon-nfa-to-nfa/">here</a>.
 *
 * <p>There is slight difference to the algorithm mentioned above: Every epsilon transition that
 * has/will have the same origin and final state will simply be removed to avoid endless loops in
 * the algorithm.
 */
public class EpsilonNFARemoveEpsilonTransitionsConversion<
                State extends Serializable, Sym extends Serializable>
        extends NFAConversion<State, State, Sym> {

    @Override
    protected NFA<State, Sym> convert(NFA<State, Sym> nfa) {
        NFABuilder<State, Sym> builder = new NFABuilder<>(nfa);

        // Add all epsilon transitions to the queue
        Queue<Pair<State, NFATransition<State, Sym>>> epsTransToProcess = new LinkedList<>();
        builder.getTransitions()
                .getTransitions()
                .forEach(
                        (origin, transSet) ->
                                transSet.forEach(
                                        trans -> { // Iterates over all transitions in the NFA
                                            if (trans.isEpsilon()) {
                                                // If there's an epsilon transition from a state to
                                                // itself, remove it. Otherwise, add it to the queue
                                                if (Objects.equals(origin, trans.getState()))
                                                    builder.removeTransition(origin, trans);
                                                else
                                                    epsTransToProcess.add(
                                                            new Pair<>(origin, trans));
                                            }
                                        }));

        while (!epsTransToProcess.isEmpty()) {
            State currentOriginState = epsTransToProcess.element().first();
            NFATransition<State, Sym> epsTrans = epsTransToProcess.element().second();
            epsTransToProcess.remove();

            // Take all transitions from the state the eps transition results in and duplicate them
            // into the origin state of the eps transition
            Set<NFATransition<State, Sym>> transitionsToDuplicate =
                    builder.getTransitions().in(epsTrans.getState());
            // Do not duplicate epsilon transitions which will have the same origin and final state
            // after duplicating
            // This is important because otherwise endless loops could occur
            transitionsToDuplicate =
                    transitionsToDuplicate.stream()
                            .filter(
                                    trans ->
                                            !(trans.isEpsilon()
                                                    && Objects.equals(
                                                            currentOriginState, trans.getState())))
                            .collect(Collectors.toSet());
            transitionsToDuplicate.forEach(
                    trans -> builder.withTransition(currentOriginState, trans));
            builder.removeTransition(currentOriginState, epsTrans);

            // If an epsilon transition gets copied to another state it must be processed again, but
            // with its new origin state
            transitionsToDuplicate.stream()
                    .filter(NFATransition::isEpsilon)
                    .forEach(
                            copiedEpsTrans ->
                                    epsTransToProcess.add(
                                            new Pair<>(currentOriginState, copiedEpsTrans)));

            if (builder.getInitialStates().contains(currentOriginState))
                builder.withInitial(epsTrans.getState());

            if (builder.getAcceptingStates().contains(epsTrans.getState()))
                builder.withAccepting(currentOriginState);
        }

        return builder.build().unwrap();
    }

    @Override
    protected NFA<State, Sym> identity(NFA<State, Sym> nfa) {
        return new NFABuilder<>(nfa).build().unwrap();
    }

    @Override
    protected boolean isRedundant(NFA<State, Sym> nfa) {
        return nfa.getTransitions().getTransitions().values().stream()
                .flatMap(Collection::stream)
                .noneMatch(NFATransition::isEpsilon);
    }
}

package de.tudortmund.cs.iltis.folalib.automata.finite;

import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultCollection;
import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultReason;
import de.tudortmund.cs.iltis.folalib.automata.Transitions;
import de.tudortmund.cs.iltis.folalib.automata.finite.fault.NFADeterminacyFault;
import de.tudortmund.cs.iltis.folalib.util.NullCheck;
import de.tudortmund.cs.iltis.utils.collections.DefaultMap;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class NFATransitions<State extends Serializable, Sym extends Serializable>
        extends Transitions<State, NFATransition<State, Sym>> implements Serializable {
    // Copy-Constructor
    public NFATransitions(Transitions<State, NFATransition<State, Sym>> toClone) {
        super(toClone);
    }

    public NFATransitions() {
        super();
    }

    public Set<NFATransition<State, Sym>> getTransitions(State state, Sym symbol) {
        return Collections.unmodifiableSet(
                transitionsByState.get(state).stream()
                        .filter(t -> symbol.equals(t.getSymbol()))
                        .collect(Collectors.toSet()));
    }

    public Set<NFATransition<State, Sym>> getEpsilonTransitions(State state) {
        return Collections.unmodifiableSet(
                transitionsByState.get(state).stream()
                        .filter(NFATransition::isEpsilon)
                        .collect(Collectors.toSet()));
    }

    public DeterminacyFaultCollection<
                    State, NFADeterminacyFault<State, Sym, DeterminacyFaultReason>>
            checkDeterminacy(
                    Collection<State> states,
                    Collection<State> initialStates,
                    Collection<Sym> alphabet) {
        List<NFADeterminacyFault<State, Sym, DeterminacyFaultReason>> faults = new ArrayList<>();

        if (initialStates.size() > 1) {
            faults.add(NFADeterminacyFault.multipleInitialStates(initialStates));
        }

        for (State state : states) {
            // eps transitions are always non-deterministic
            Set<NFATransition<State, Sym>> transitions = getEpsilonTransitions(state);
            if (!transitions.isEmpty())
                faults.add(
                        NFADeterminacyFault.ambiguousTransition(
                                state,
                                null,
                                transitions.stream()
                                        .map(NFATransition::getState)
                                        .collect(Collectors.toList())));
            for (Sym symbol : alphabet) {
                transitions = getTransitions(state, symbol);

                if (transitions.size() >= 2)
                    faults.add(
                            NFADeterminacyFault.ambiguousTransition(
                                    state,
                                    symbol,
                                    transitions.stream()
                                            .map(NFATransition::getState)
                                            .collect(Collectors.toList())));
                else if (transitions.isEmpty())
                    faults.add(NFADeterminacyFault.missingTransition(state, symbol));
            }
        }
        return new DeterminacyFaultCollection<>(faults);
    }

    /**
     * Returns a new instance of this {@link Transitions}-object containing all previous transitions
     * minus the given transition.
     *
     * @param state The origin state of the transition which shall be removed
     * @param transition The transition which shall be removed
     * @return A new {@link Transitions}-object
     */
    public NFATransitions<State, Sym> removeTransition(
            State state, NFATransition<State, Sym> transition) {
        Objects.requireNonNull(state);
        Objects.requireNonNull(transition);

        NFATransitions<State, Sym> clone = new NFATransitions<State, Sym>(this);
        removeAndCheckEmptyMapEntry(
                clone.getTransitions(), state, Collections.singleton(transition));
        return clone;
    }

    /**
     * Returns a new instance of this {@link Transitions}-object containing all previous transitions
     * minus the given transitions.
     *
     * @param state The origin state of the transitions which shall be removed
     * @param transitions The transitions which shall be removed
     * @return A new {@link Transitions}-object
     */
    public NFATransitions<State, Sym> removeTransitions(
            State state, Set<NFATransition<State, Sym>> transitions) {
        Objects.requireNonNull(state);
        NullCheck.requireAllNonNull(transitions);

        NFATransitions<State, Sym> clone = new NFATransitions<State, Sym>(this);
        removeAndCheckEmptyMapEntry(clone.getTransitions(), state, transitions);
        return clone;
    }

    private void removeAndCheckEmptyMapEntry(
            DefaultMap<State, Set<NFATransition<State, Sym>>> map,
            State state,
            Set<NFATransition<State, Sym>> transitions) {
        map.get(state).removeAll(transitions);
        if (map.get(state).isEmpty()) map.remove(state);
    }
}

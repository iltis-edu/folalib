package de.tudortmund.cs.iltis.folalib.automata.finite.fault;

import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFault;
import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultReason;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import java.io.Serializable;
import java.util.*;

/**
 * Class modelling determinacy faults that can occur in a {@link NFA}.
 *
 * <p>A NFA is deterministic if there are no epsilon transitions, if for every state S and alphabet
 * symbol A there exists exactly one transition (S, A, T) for some state T and if there is only one
 * initial state.
 *
 * @param <Sym>
 * @param <State>
 */
public class NFADeterminacyFault<State, Sym, Reason extends Serializable>
        extends DeterminacyFault<State, Reason> {
    private final Sym symbol;
    private final List<State> to;
    private final List<State> multipleInitialStates;

    // For GWT serialization
    private NFADeterminacyFault() {
        symbol = null;
        to = new ArrayList<>();
        multipleInitialStates = new ArrayList<>();
    }

    /**
     * Constructs a new {@link NFADeterminacyFault} instance for faults of kind {@link
     * DeterminacyFaultReason#AMBIGUOUS_TRANSITION}.
     *
     * @param where The state in which the fault occurs
     * @param symbol The symbol which, after reading in state {@code where}, leads to an ambiguous
     *     successor state. Should be {@code null} for epsilon transitions
     * @param to The set of states reachable from {@code where} after reading {@code symbol}
     */
    public static <Sym, State>
            NFADeterminacyFault<State, Sym, DeterminacyFaultReason> ambiguousTransition(
                    State where, Sym symbol, Collection<State> to) {
        return new NFADeterminacyFault<>(
                where, symbol, to, DeterminacyFaultReason.AMBIGUOUS_TRANSITION);
    }

    private NFADeterminacyFault(
            State where, Sym symbol, Collection<State> to, Reason ambiguousTransitionReason) {
        super(where, ambiguousTransitionReason);

        this.symbol = symbol;
        this.to = new ArrayList<>(to);
        this.multipleInitialStates = new ArrayList<>();
    }

    /**
     * Constructs a new {@link NFADeterminacyFault} instance for faults of kind {@link
     * DeterminacyFaultReason#MISSING_TRANSITION}.
     *
     * @param where The state in which the fault occurs
     * @param missingTransition The symbol for which no transition exists
     */
    public static <Sym, State>
            NFADeterminacyFault<State, Sym, DeterminacyFaultReason> missingTransition(
                    State where, Sym missingTransition) {
        return new NFADeterminacyFault<>(
                where, missingTransition, DeterminacyFaultReason.MISSING_TRANSITION);
    }

    private NFADeterminacyFault(
            State where, Sym missingTransition, Reason missingTransitionReason) {
        super(where, missingTransitionReason);

        this.symbol = missingTransition;
        this.to = new ArrayList<>();
        this.multipleInitialStates = new ArrayList<>();
    }

    /**
     * Constructs a new {@link NFADeterminacyFault} instance for faults of kind {@link
     * DeterminacyFaultReason#MULTIPLE_INITIAL_STATES}.
     *
     * @param states The states which are set as initial states
     */
    public static <Sym, State>
            NFADeterminacyFault<State, Sym, DeterminacyFaultReason> multipleInitialStates(
                    Collection<State> states) {
        return new NFADeterminacyFault<>(states, DeterminacyFaultReason.MULTIPLE_INITIAL_STATES);
    }

    private NFADeterminacyFault(Collection<State> states, Reason multipleInitialStatesReason) {
        super(null, multipleInitialStatesReason);

        this.symbol = null;
        this.to = new ArrayList<>();
        this.multipleInitialStates = new ArrayList<>(states);
    }

    public NFADeterminacyFault<State, Sym, NFAConstructionFaultReason> asConstructionFault() {
        if (!multipleInitialStates.isEmpty())
            return new NFADeterminacyFault<>(
                    multipleInitialStates, NFAConstructionFaultReason.MULTIPLE_INITIAL_STATES);
        else if (to.isEmpty())
            return new NFADeterminacyFault<>(
                    where, symbol, NFAConstructionFaultReason.MISSING_TRANSITION);
        else
            return new NFADeterminacyFault<>(
                    where, symbol, to, NFAConstructionFaultReason.AMBIGUOUS_TRANSITION);
    }

    /**
     * Gets the symbol for which a transition is missing or ambiguous.
     *
     * @return The symbol causing this {@link NFADeterminacyFault}. Returns {@code null} if an
     *     epsilon transition or multiple initial states caused this fault.
     */
    public Sym getSymbol() {
        return symbol;
    }

    /**
     * Gets the list of states reachable via an {@link NFADeterminacyFault#getSymbol()}-Transition.
     *
     * <p>Empty for a totality fault or for multiple initial states.
     *
     * <p>This list is unmodifiable.
     *
     * @return The list of states reachable
     */
    public List<State> getTo() {
        return Collections.unmodifiableList(to);
    }

    /**
     * Gets the list of all initial states.
     *
     * <p>Empty if {@code this.getReason()} is not a {@link
     * DeterminacyFaultReason#MULTIPLE_INITIAL_STATES} fault.
     *
     * @return The list of all initial states.
     */
    public List<State> getMultipleInitialStates() {
        return multipleInitialStates;
    }

    @Override
    protected Object clone() {
        if (to.isEmpty()) {
            return new NFADeterminacyFault<>(where, symbol, getReason());
        } else {
            return new NFADeterminacyFault<>(where, symbol, to, getReason());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NFADeterminacyFault<?, ?, ?> that = (NFADeterminacyFault<?, ?, ?>) o;
        return Objects.equals(symbol, that.symbol) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), symbol, to);
    }
}

package de.tudortmund.cs.iltis.folalib.automata.pushdown.fault;

import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.util.Objects;

/**
 * A PDATransitionFault is a fault which prevented the PDABuilder to construct a valid PDA
 *
 * <p>A typical example is a transition which contains input symbols that are not part of the input
 * alphabet. The intended use of this class is to call the public static convenience methods to
 * construct new instances of PDATransitionFault, such as {@code
 * TransitionFault.unknownDestination(state, transition)}.
 *
 * @param <T> The type of the states where the Trans originate from
 * @param <Trans> The type of the transitions which are faulty
 */
public class PDATransitionFault<T, Trans> extends Fault<PDAConstructionFaultReason> {
    private T state;
    private Trans transition;

    private PDATransitionFault(T state, Trans transition, PDAConstructionFaultReason reason) {
        super(reason);
        this.state = state;
        this.transition = transition;
    }

    /* For serialization */
    @SuppressWarnings("unused")
    public PDATransitionFault() {}

    /**
     * Construct a new PDATransitionFault indicating that the transition contains an unknown input
     * symbol
     *
     * @param state The state where the transition originates
     * @param transition The faulty transition
     * @param <T> The type of the state where the transition originates
     * @param <Trans> The type of the faulty transition
     * @return a new PDATransitionFault
     */
    public static <T, Trans> PDATransitionFault<T, Trans> unknownInputSymbol(
            T state, Trans transition) {
        return new PDATransitionFault<>(
                state, transition, PDAConstructionFaultReason.TRANSITION_UNKNOWN_INPUT_SYMBOL);
    }

    /**
     * Construct a new PDATransitionFault indicating that the transition contains an unknown origin
     *
     * @param state The state where the transition originates
     * @param transition The faulty transition
     * @param <T> The type of the state where the transition originates
     * @param <Trans> The type of the faulty transition
     * @return a new PDATransitionFault
     */
    public static <T, Trans> PDATransitionFault<T, Trans> unknownOrigin(T state, Trans transition) {
        return new PDATransitionFault<>(
                state, transition, PDAConstructionFaultReason.TRANSITION_UNKNOWN_ORIGIN);
    }

    /**
     * Construct a new PDATransitionFault indicating that the transition contains an unknown
     * destination
     *
     * @param state The state where the transition originates
     * @param transition The faulty transition
     * @param <T> The type of the state where the transition originates
     * @param <Trans> The type of the faulty transition
     * @return a new PDATransitionFault
     */
    public static <T, Trans> PDATransitionFault<T, Trans> unknownDestination(
            T state, Trans transition) {
        return new PDATransitionFault<>(
                state, transition, PDAConstructionFaultReason.TRANSITION_UNKNOWN_DESTINATION);
    }

    /**
     * Construct a new PDATransitionFault indicating that the transition contains an unknown stack
     * symbol
     *
     * @param state The state where the transition originates
     * @param transition The faulty transition
     * @param <T> The type of the state where the transition originates
     * @param <Trans> The type of the faulty transition
     * @return a new PDATransitionFault
     */
    public static <T, Trans> PDATransitionFault<T, Trans> unknownStackSymbol(
            T state, Trans transition) {
        return new PDATransitionFault<>(
                state, transition, PDAConstructionFaultReason.TRANSITION_UNKNOWN_STACK_SYMBOL);
    }

    /**
     * Construct a new PDATransitionFault indicating that the transition adds an unknown stack
     * symbol to the stack
     *
     * @param state The state where the transition originates
     * @param transition The faulty transition
     * @param <T> The type of the state where the transition originates
     * @param <Trans> The type of the faulty transition
     * @return a new PDATransitionFault
     */
    public static <T, Trans> PDATransitionFault<T, Trans> invalidNewTopOfStack(
            T state, Trans transition) {
        return new PDATransitionFault<>(
                state, transition, PDAConstructionFaultReason.TRANSITION_INVALID_NEW_TOP_OF_STACK);
    }

    @Override
    protected Object clone() {
        return new PDATransitionFault<>(state, transition, getReason());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PDATransitionFault<?, ?> that = (PDATransitionFault<?, ?>) o;
        return Objects.equals(state, that.state) && Objects.equals(transition, that.transition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), state, transition);
    }
}

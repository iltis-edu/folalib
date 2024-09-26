package de.tudortmund.cs.iltis.folalib.automata.finite.fault;

import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.util.Objects;

public class TransitionFault<State, Trans> extends Fault<NFAConstructionFaultReason> {
    private final State origin;
    private final Trans transition;

    public TransitionFault(NFAConstructionFaultReason reason, State origin, Trans transition) {
        super(reason);

        this.origin = origin;
        this.transition = transition;
    }

    // For GWT serialization
    private TransitionFault() {
        origin = null;
        transition = null;
    }

    public Trans getTransition() {
        return transition;
    }

    @Override
    protected Object clone() {
        return new TransitionFault<>(getReason(), origin, transition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TransitionFault<?, ?> that = (TransitionFault<?, ?>) o;
        return Objects.equals(origin, that.origin) && Objects.equals(transition, that.transition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), origin, transition);
    }
}

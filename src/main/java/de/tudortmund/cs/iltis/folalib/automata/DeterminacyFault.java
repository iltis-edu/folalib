package de.tudortmund.cs.iltis.folalib.automata;

import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;
import java.util.Objects;

/**
 * ABC for faults that violate automaton determinacy
 *
 * @param <State>
 */
public abstract class DeterminacyFault<State, Reason extends Serializable> extends Fault<Reason> {
    protected State where;

    // For GWT Serialization
    protected DeterminacyFault() {}

    protected DeterminacyFault(State where, Reason reason) {
        super(reason);

        this.where = where;
    }

    /**
     * Gets the state in which a transition (or absence thereof) violates determinacy
     *
     * @return the state in which determinacy is violated
     */
    public State getWhere() {
        return where;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DeterminacyFault<?, ?> that = (DeterminacyFault<?, ?>) o;
        return Objects.equals(where, that.where);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), where);
    }
}

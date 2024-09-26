package de.tudortmund.cs.iltis.folalib.automata;

import de.tudortmund.cs.iltis.utils.collections.FaultCollection;
import java.util.List;

public class DeterminacyFaultCollection<
                State, TFault extends DeterminacyFault<State, DeterminacyFaultReason>>
        extends FaultCollection<DeterminacyFaultReason, TFault> {
    /**
     * Constructs an empty {@link DeterminacyFaultCollection}, indicating the automaton in question
     * is deterministic and total
     */
    public DeterminacyFaultCollection() {
        super();
    }

    /**
     * Constructs a {@link DeterminacyFaultCollection} containing the given faults
     *
     * @param faults The set of faults the automaton exhibits, which prevent it from being
     *     classified as deterministic and/or total
     */
    public DeterminacyFaultCollection(List<TFault> faults) {
        super(faults);
    }

    /**
     * Gets all faults that are caused by <b>non-determinism</b>
     *
     * @return The set of faults
     */
    public List<TFault> getDeterminismFaults() {
        List<TFault> faults = getFaults(DeterminacyFaultReason.AMBIGUOUS_TRANSITION);
        faults.addAll(getFaults(DeterminacyFaultReason.MULTIPLE_INITIAL_STATES));
        return faults;
    }

    /**
     * Gets all faults that are caused by <b>non-totality</b>
     *
     * @return The set of faults
     */
    public List<TFault> getTotalityFaults() {
        return getFaults(DeterminacyFaultReason.MISSING_TRANSITION);
    }

    /**
     * Checks whether this {@link DeterminacyFaultCollection} contains any determinism faults.
     *
     * @return {@code true} iff determinism faults exist
     */
    public boolean hasDeterminismFaults() {
        return !getDeterminismFaults().isEmpty();
    }

    /**
     * Checks whether this {@link DeterminacyFaultCollection} contains any totality faults.
     *
     * @return {@code true} iff totality faults exist
     */
    public boolean hasTotalityFaults() {
        return !getTotalityFaults().isEmpty();
    }

    @Override
    public FaultCollection<DeterminacyFaultReason, TFault> clone() {
        return new DeterminacyFaultCollection<>(faults);
    }
}

package de.tudortmund.cs.iltis.folalib.automata;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;

/** Enum modelling the different reasons that might cause an {@link DeterminacyFault} */
public enum DeterminacyFaultReason {
    /**
     * Enum variant indicating that in some state two or more transitions might coincide
     *
     * <p>For {@link NFA}s, epsilon transitions are always considered ambiguous.
     */
    AMBIGUOUS_TRANSITION,

    /** Enum variant indicating that a transition is missing in some state. */
    MISSING_TRANSITION,

    /** Enum variant indicating that there are more than one initial state. */
    MULTIPLE_INITIAL_STATES
}

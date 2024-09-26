package de.tudortmund.cs.iltis.folalib.automata.finite.fault;

/**
 * This enum declares all possible construction faults that can occur while validating a
 * NFA-builder. Please note that this validation only checks the syntax, not the semantics. It is
 * <b>not</b> a construction fault if no accepting states exist.
 */
public enum NFAConstructionFaultReason {
    /** No initial state has been declared */
    MISSING_INITIAL_STATE,

    /**
     * One of the transitions is set to trigger on a symbol which doesn't lie inside the declared
     * alphabet
     */
    TRANSITION_UNKNOWN_SYMBOL,

    /**
     * One of the transitions is set to originate in a state which doesn't lie inside the declared
     * state set
     */
    TRANSITION_UNKNOWN_ORIGIN,

    /**
     * One of the transitions is set to lead to a state which doesn't lie inside the declared state
     * set
     */
    TRANSITION_UNKNOWN_DESTINATION,

    /**
     * @see de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultReason#AMBIGUOUS_TRANSITION
     */
    AMBIGUOUS_TRANSITION,

    /**
     * @see de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultReason#MISSING_TRANSITION
     */
    MISSING_TRANSITION,

    /**
     * @see de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultReason#MULTIPLE_INITIAL_STATES
     */
    MULTIPLE_INITIAL_STATES
}

package de.tudortmund.cs.iltis.folalib.automata.pushdown.fault;

/**
 * This enum declares all possible construction faults that can occur while validating a
 * PDA-builder. Please note that this validation only checks the syntax, not the semantics. It is
 * <b>not</b> a construction fault if no accepting states exist.
 */
public enum PDAConstructionFaultReason {

    /** The PDA has no initial state */
    MISSING_INITIAL_STATE,

    /** The PDA has no initial symbol on the stack */
    MISSING_INITIAL_STACK_SYMBOL,

    /** The PDA does not have a defined acceptance strategy */
    MISSING_ACCEPTANCE_STRATEGY,

    /**
     * One of the transitions is set to trigger on a symbol which is not included in the declared
     * input alphabet
     */
    TRANSITION_UNKNOWN_INPUT_SYMBOL,

    /**
     * One of the transitions originates from a state which is not included in the declared set of
     * states
     */
    TRANSITION_UNKNOWN_ORIGIN,

    /**
     * One of the transitions leads to a state which is not included in the declared set of states
     */
    TRANSITION_UNKNOWN_DESTINATION,

    /**
     * One of the transitions is set to trigger on a stack symbol which is not included in the
     * declared stack alphabet
     */
    TRANSITION_UNKNOWN_STACK_SYMBOL,

    /**
     * One of the transitions pushes a symbol onto the stack which is not included in the declared
     * stack alphabet
     */
    TRANSITION_INVALID_NEW_TOP_OF_STACK,
}

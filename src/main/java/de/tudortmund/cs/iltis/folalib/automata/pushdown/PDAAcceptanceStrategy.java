package de.tudortmund.cs.iltis.folalib.automata.pushdown;

/** This enum encodes the different acceptance modes of a {@link PDA} */
public enum PDAAcceptanceStrategy {

    /** The PDA accepts iff the entire input is consumed and the stack is empty */
    EMPTY_STACK,

    /** The PDA accepts iff the entire input is consumed and an accepting state is reached */
    ACCEPTING_STATES,
}

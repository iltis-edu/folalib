package de.tudortmund.cs.iltis.folalib.grammar.construction.fault;

public enum GrammarConstructionFaultReason {

    /*------------------------------------------*\
     | For C0-grammars and below                |
    \*------------------------------------------*/

    /** The LHS of some production does not contain any symbols */
    EMPTY_LHS,

    /** The specified alphabets for terminals and non-terminals are not disjoint */
    ALPHABETS_NOT_DISJOINT,

    /** No non-terminal has been designated as start symbol */
    MISSING_START_SYMBOL,

    /**
     * One or more productions contain a terminal symbol which is not contained in the declared
     * alphabet
     */
    UNKNOWN_TERMINAL,

    /**
     * One or more productions contain a non-terminal symbol which is not contained in the declared
     * alphabet
     */
    UNKNOWN_NONTERMINAL,

    /*------------------------------------------*\
     | For context-sensitive grammars and below |
    \*------------------------------------------*/

    /**
     * The RHS of some production is shorter than its LHS.
     *
     * <p><b>This is not relevant for regular and context-free grammars!</b>
     */
    NON_MONOTONIC,

    /**
     * The start symbol of a regular or context-sensitive grammar occurs in the RHS of some
     * production, yet {@code S -> epsilon} exists as a production. This combination is forbidden in
     * regular and context-sensitive grammars!
     *
     * <p>Also, only {@code S -> epsilon} is allowed as an epsilon-production.
     *
     * <p><b>This is not relevant for context-free grammars!</b>
     */
    THE_EPSILON_RULE,

    /*------------------------------------------*\
     | For context-free grammars and below      |
    \*------------------------------------------*/

    /** The LHS does not only consist of exactly one non-terminal. */
    LHS_NOT_ONLY_ONE_NONTERMINAL,

    /*------------------------------------------*\
     | For regular grammars                     |
    \*------------------------------------------*/

    /**
     * The RHS does not consist of a combination of one terminal and one non-terminal, or only one
     * terminal.
     */
    INVALID_REGULAR_RHS,

    /**
     * Indicates a left-regular RHS in a right-regular grammar and vice versa.
     *
     * <p>Left-regular and right-regular productions cannot occur both in a regular grammar because
     * the resulting grammar is then no longer necessarily regular. This is an example for this
     * case:
     *
     * <p>Σ = {a, b}, V = {S, X}, S = S, (start symbol) P = {S -> aX | Ɛ, X -> Sb}
     *
     * <p>This grammar produces the language L = {a^n b^n | n >= 0} which is not regular!
     */
    RIGHT_AND_LEFT_REGULAR_RHS_MIX

    // THE_EPSILON_RULE

}

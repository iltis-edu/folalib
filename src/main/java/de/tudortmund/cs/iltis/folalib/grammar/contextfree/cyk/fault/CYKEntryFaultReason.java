package de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk.fault;

public enum CYKEntryFaultReason {
    /**
     * A CYK cell is missing an entry it should contain, as determined by executing the
     * CYK-algorithm for the cell in question, taking into account all other student-input values.
     * Additionally, if the CYK-algorithm were to be executed on an empty tableau, the cell in
     * question would contain the missing value.
     */
    MISSING_ENTRY,

    /**
     * A CYK cell is missing an entry it should contain, as determined by executing the complete
     * CYK-algorithm on an empty tableau, yet an fault of kind {@link
     * CYKEntryFaultReason#MISSING_ENTRY} was not generated.
     *
     * <p>This kind of fault means that the current step of the algorithm was executed correctly
     * based on all previous student-input values, yet a mistake was done in an earlier step, which
     * causes aftereffect ("Folgefehler") now.
     */
    MISSING_ENTRY_AFTEREFFECT,

    /**
     * Same as {@link CYKEntryFaultReason#MISSING_ENTRY}, but the "missing entry" is not actually
     * part of a correct solution.
     *
     * <p>This is a different kind of aftereffect error, where the student did two mistakes which
     * cancel each other out. Lucky them!
     */
    MISSING_ENTRY_GHOST,

    /**
     * Same as {@link CYKEntryFaultReason#MISSING_ENTRY}, but instead of missing an entry, the cell
     * contains an entry too many
     */
    ABUNDANT_ENTRY,

    /**
     * Same as {@link CYKEntryFaultReason#MISSING_ENTRY_AFTEREFFECT}, but instead of missing an
     * entry, the cell contains an entry too many
     */
    ABUNDANT_ENTRY_AFTEREFFECT,

    /**
     * Same as {@link CYKEntryFaultReason#MISSING_ENTRY_GHOST}, but instead of missing an entry, the
     * cell contains an entry too many
     */
    ABUNDANT_ENTRY_GHOST,

    /** An entry's non-terminal is correct, but its k-value is wrong */
    WRONG_K_VALUE,
}

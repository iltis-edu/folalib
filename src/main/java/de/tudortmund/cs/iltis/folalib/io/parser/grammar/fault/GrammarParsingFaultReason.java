package de.tudortmund.cs.iltis.folalib.io.parser.grammar.fault;

import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultReason;

/** A collection of all specific faults that can occur while parsing a grammar. */
public enum GrammarParsingFaultReason implements ParsingFaultReason {
    UNKNOWN_SENTENTIAL_FORM, // If a sentential form only consists of ambiguous symbols. This causes
    // the parser to bail out.
    EMPTY_GRAMMAR, // If no production was found
    ABUNDANT_RIGHT_SIDE_SEPARATORS, // If multiple consecutive right side separators were read
    ABUNDANT_EPSILONS, // If a sentential form consists of multiple consecutive epsilons but nothing
    // else
    LINE_ENDS_WITH_RIGHT_SIDE_SEPARATOR, // If a production line ends with (multiple) right side
    // separator(s)
    LINE_STARTS_WITH_RIGHT_SIDE_SEPARATOR, // If a production line starts with (multiple) right side
    // separator(s)
    INCOMPLETE_PRODUCTION, // If a production line has no right side, no left side or both
    MISSING_PRODUCTION_ARROW, // If a production line has no production arrow, but anything else
    RIGHT_SIDE_SEPARATOR_ON_LHS, // If the separator symbol that is usually used to separate
    // different productions on the RHS occurs on an LHS
    MULTIPLE_PRODUCTION_ARROWS, // If a productions line contains multiple production arrows
    AMBIGUOUS_SYMBOL, // If the input to grammar symbol converter cannot decide if a symbol is a
    // terminal or a non-terminal unambiguously
    SYMBOL_EPSILON_MIX, // If a sentential form consists of a mix of symbol(s) and epsilon(s)
    START_SYMBOL_NOT_DERIVABLE, // If the start symbol could not be derived from the input

    BLANK_INPUT; // Only relevant for SententialFormReader and WordReader! If an input contains no

    // symbols

    @Override
    public int getGroup() {
        switch (this) {
            case START_SYMBOL_NOT_DERIVABLE:
            case EMPTY_GRAMMAR:
            case BLANK_INPUT:
                return 600;
            case INCOMPLETE_PRODUCTION:
            case MISSING_PRODUCTION_ARROW:
            case MULTIPLE_PRODUCTION_ARROWS:
                return 800;
            case RIGHT_SIDE_SEPARATOR_ON_LHS:
            case SYMBOL_EPSILON_MIX:
            case LINE_ENDS_WITH_RIGHT_SIDE_SEPARATOR:
            case LINE_STARTS_WITH_RIGHT_SIDE_SEPARATOR:
            case UNKNOWN_SENTENTIAL_FORM:
            case AMBIGUOUS_SYMBOL:
                return 1000;
            case ABUNDANT_EPSILONS:
            case ABUNDANT_RIGHT_SIDE_SEPARATORS:
                return 1200;
            default:
                return 0;
        }
    }
}

package de.tudortmund.cs.iltis.folalib.io.parser.regex.fault;

import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultReason;

public enum RegexParsingFaultReason implements ParsingFaultReason {
    NO_ALTERNATIVE,
    CARDINALITY_OPERATOR_AFTER_ALTERNATION_SYMBOL,
    EMPTY_REGEX,
    START_WITH_CARDINALITY_OPERATOR,
    REPETITION_OPERAND_NOT_NUMERIC,
    AMBIGUOUS_REPETITION_DEFINITION,
    NO_DOMAIN_FOR_RANGE_DEFINED // Gets thrown if a Range expression is parsed but no domain is
// specified in the
// reader properties
;

    @Override
    public int getGroup() {
        switch (this) {
            case REPETITION_OPERAND_NOT_NUMERIC:
                return 100; // Low number because this is a lexer-fault
            case NO_DOMAIN_FOR_RANGE_DEFINED:
                return 600;
            case NO_ALTERNATIVE:
            case CARDINALITY_OPERATOR_AFTER_ALTERNATION_SYMBOL:
            case START_WITH_CARDINALITY_OPERATOR:
                return 800;
            case AMBIGUOUS_REPETITION_DEFINITION:
                return 1000;
            case EMPTY_REGEX:
                return 1200;
            default:
                return 0;
        }
    }
}

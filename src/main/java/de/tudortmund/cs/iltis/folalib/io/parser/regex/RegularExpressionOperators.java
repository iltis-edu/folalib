package de.tudortmund.cs.iltis.folalib.io.parser.regex;

import de.tudortmund.cs.iltis.utils.io.parser.general.ParsableSymbol;

public enum RegularExpressionOperators implements ParsableSymbol {
    OPENING_PARENTHESIS,
    CLOSING_PARENTHESIS,
    OPENING_REPETITION,
    CLOSING_REPETITION,
    FIXED_REPETITION_OPENER,
    OPENING_RANGE,
    CLOSING_RANGE,
    REPETITION_SEPARATOR,
    EPSILON,
    SYMBOL,
    EMPTY_SET,
    WORD,
    REPETITION_OPERAND,
    ALTERNATION,
    KLEENE_STAR,
    KLEENE_PLUS,
    OPTIONAL,
    RANGE_SEPARATOR,
    WHITESPACE;

    @Override
    public int getTokenType() {
        switch (this) {
            case KLEENE_STAR:
                return RegularExpressionParser.KLEENE_STAR;
            case ALTERNATION:
                return RegularExpressionParser.ALTERNATION;
            case SYMBOL:
                return RegularExpressionParser.SYMBOL;
            case EPSILON:
                return RegularExpressionParser.EPSILON;
            case OPENING_PARENTHESIS:
                return RegularExpressionParser.OPENING_PARENTHESIS;
            case CLOSING_PARENTHESIS:
                return RegularExpressionParser.CLOSING_PARENTHESIS;
            case OPENING_REPETITION:
                return RegularExpressionParser.OPENING_REPETITION;
            case CLOSING_REPETITION:
                return RegularExpressionParser.CLOSING_REPETITION;
            case REPETITION_SEPARATOR:
                return RegularExpressionParser.REPETITION_SEPARATOR;
            case FIXED_REPETITION_OPENER:
                return RegularExpressionParser.FIXED_REPETITION_OPENER;
            case RANGE_SEPARATOR:
                return RegularExpressionParser.RANGE_SEPARATOR;
            case OPENING_RANGE:
                return RegularExpressionParser.OPENING_RANGE;
            case CLOSING_RANGE:
                return RegularExpressionParser.CLOSING_RANGE;
            case KLEENE_PLUS:
                return RegularExpressionParser.KLEENE_PLUS;
            case OPTIONAL:
                return RegularExpressionParser.OPTIONAL;
            case WHITESPACE:
                return RegularExpressionParser.WHITESPACE;
            case EMPTY_SET:
                return RegularExpressionParser.EMPTY_SET;
            case WORD:
                return RegularExpressionParser.WORD;
            case REPETITION_OPERAND:
                return RegularExpressionParser.REPETITION_OPERAND;
        }
        throw new RuntimeException("literally unreachable, but java's static analysis sucks");
    }
}

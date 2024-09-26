package de.tudortmund.cs.iltis.folalib.io.parser.grammar;

import de.tudortmund.cs.iltis.utils.io.parser.general.ParsableSymbol;

public enum GrammarParserOperators implements ParsableSymbol {
    WHITESPACE,
    PRODUCTION_ARROW,
    SYMBOL,
    SYMBOL_CONCATENATION,
    LINE_SEPARATOR,
    RIGHT_SIDE_SEPARATOR,
    EPSILON;

    @Override
    public int getTokenType() {
        switch (this) {
            case WHITESPACE:
                return GrammarParser.WHITESPACE;
            case PRODUCTION_ARROW:
                return GrammarParser.PRODUCTION_ARROW;
            case SYMBOL:
                return GrammarParser.SYMBOL;
            case SYMBOL_CONCATENATION:
                return GrammarParser.SYMBOL_CONCATENATION;
            case LINE_SEPARATOR:
                return GrammarParser.LINE_SEPARATOR;
            case RIGHT_SIDE_SEPARATOR:
                return GrammarParser.RIGHT_SIDE_SEPARATOR;
            case EPSILON:
                return GrammarParser.EPSILON;
        }
        throw new RuntimeException("literally unreachable, but java's static analysis sucks");
    }
}

package de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter;

import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.util.Objects;

/**
 * This error will be thrown by a {@link InputToGrammarSymbolConverter} if an input is ambiguous.
 */
public class InputConversionException extends Exception {

    private IndexedSymbol symbol;

    public InputConversionException(IndexedSymbol symbol, String errorMsg) {
        super(errorMsg);

        Objects.requireNonNull(symbol);
        this.symbol = symbol;
    }

    public IndexedSymbol getSymbol() {
        return symbol;
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private InputConversionException() {
        this.symbol = new IndexedSymbol("");
    }
}

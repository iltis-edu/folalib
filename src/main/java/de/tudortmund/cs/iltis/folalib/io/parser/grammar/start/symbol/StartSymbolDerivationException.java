package de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol;

/**
 * This error will be thrown by a {@link StartSymbolDerivationStrategy} if no start symbol can be
 * derived.
 */
public class StartSymbolDerivationException extends Exception {

    public StartSymbolDerivationException(String errorMsg) {
        super(errorMsg);
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private StartSymbolDerivationException() {}
}

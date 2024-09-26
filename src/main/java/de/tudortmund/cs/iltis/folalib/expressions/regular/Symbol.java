package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import java.io.Serializable;
import java.util.Objects;

public class Symbol<S extends Serializable> extends RegularExpression<S> {
    protected S symbol;

    // For serialization
    @SuppressWarnings("unused")
    private Symbol() {}

    /**
     * Build a new Symbol expression which matches the given symbol. The alphabet will only contain
     * the given symbol.
     *
     * @throws NullPointerException if {@code symbol} is {@code null}
     */
    public Symbol(S symbol) {
        this(new Alphabet<>(symbol), symbol);
    }

    /**
     * Build a new Symbol expression over the given alphabet which matches the given symbol
     *
     * @param alphabet the alphabet, over which this expression is defined
     * @throws IllegalArgumentException if the given Alphabet does not contain {@code symbol}
     * @throws NullPointerException if {@code symbol} is {@code null}
     */
    public Symbol(Alphabet<S> alphabet, S symbol) {
        super(alphabet, true);
        Objects.requireNonNull(symbol);
        this.symbol = symbol;
        if (!alphabet.contains(symbol))
            throw new IllegalArgumentException(
                    "Alphabet of RegularExpression.Symbol must contain symbol.");
    }

    public S getSymbol() {
        return this.symbol;
    }

    @Override
    public Symbol<S> withAlphabet(Alphabet<S> alphabet) {
        return getAlphabet().equals(alphabet) ? this : new Symbol<>(alphabet, symbol);
    }

    @Override
    public String toString() {
        return symbol.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && Objects.equals(symbol, ((Symbol<?>) obj).symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), symbol);
    }
}

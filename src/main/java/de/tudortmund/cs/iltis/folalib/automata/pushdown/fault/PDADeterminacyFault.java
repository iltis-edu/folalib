package de.tudortmund.cs.iltis.folalib.automata.pushdown.fault;

import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFault;
import java.io.Serializable;
import java.util.Objects;

/**
 * A fault why a PDA is not considered deterministic, e.g. because for a given state, input symbol
 * and stack symbol there exist more than one possible subsequent configuration.
 *
 * @param <T> The type of the states of the PDA
 * @param <S> The type of the input symbols of the PDA
 * @param <K> The type of the stack symbols of the PDA
 * @param <Reason> The type of reason of this fault
 */
public class PDADeterminacyFault<T, S, K, Reason extends Serializable>
        extends DeterminacyFault<T, Reason> {
    private S symbol;
    private K stackSymbol;

    private int possibleNextConfigurations;

    public PDADeterminacyFault(
            T state, S symbol, K stackSymbol, Reason reason, int possibleNextConfigurations) {
        super(state, reason);
        this.symbol = symbol;
        this.stackSymbol = stackSymbol;
        this.possibleNextConfigurations = possibleNextConfigurations;
    }

    public PDADeterminacyFault(T state, S symbol, K stackSymbol, Reason reason) {
        this(state, symbol, stackSymbol, reason, 0);
    }

    /* For serialization */
    @SuppressWarnings("unused")
    private PDADeterminacyFault() {}

    @Override
    protected Object clone() {
        return new PDADeterminacyFault<>(getWhere(), symbol, stackSymbol, getReason());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PDADeterminacyFault<?, ?, ?, ?> that = (PDADeterminacyFault<?, ?, ?, ?>) o;
        return Objects.equals(symbol, that.symbol)
                && Objects.equals(stackSymbol, that.stackSymbol)
                && Objects.equals(possibleNextConfigurations, that.possibleNextConfigurations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), symbol, stackSymbol);
    }

    public S getSymbol() {
        return symbol;
    }

    public K getStackSymbol() {
        return stackSymbol;
    }

    public int getPossibleNextConfigurations() {
        return possibleNextConfigurations;
    }
}

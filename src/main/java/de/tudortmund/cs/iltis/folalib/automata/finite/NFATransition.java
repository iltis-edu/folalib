package de.tudortmund.cs.iltis.folalib.automata.finite;

import de.tudortmund.cs.iltis.folalib.automata.Configuration;
import de.tudortmund.cs.iltis.folalib.automata.ITransition;
import java.io.Serializable;
import java.util.Objects;

public class NFATransition<T extends Serializable, S extends Serializable>
        implements ITransition<T, S, Configuration<T, S>> {
    private S symbol;
    private T state;

    public NFATransition(T state) {
        Objects.requireNonNull(state);

        this.state = state;
    }

    public NFATransition(S symbol, T state) {
        this(state);

        this.symbol = symbol;
    }

    // For GWT serialization
    private NFATransition() {}

    public S getSymbol() {
        return symbol;
    }

    public T getState() {
        return state;
    }

    @Override
    public String toString() {
        return "--" + (symbol == null ? "ε" : symbol) + "-> " + state;
    }

    /*@Override
    public NFATransition<T, S> clone() {
    	return new NFATransition<T, S>((S)symbol.clone(), states.clone());
    }*/

    @Override
    public boolean isApplicable(Configuration<T, S> config) {
        return isEpsilon() || config.hasSymbol() && config.getCurrentSymbol().equals(symbol);
    }

    @Override
    public Configuration<T, S> fire(Configuration<T, S> config) {
        if (isEpsilon()) return new Configuration<>(state, config.getWord(), config.getPosition());
        else return new Configuration<>(state, config.getWord(), config.getPosition() + 1);
    }

    @Override
    public boolean isEpsilon() {
        return symbol == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NFATransition<?, ?> that = (NFATransition<?, ?>) o;
        return Objects.equals(symbol, that.symbol) && state.equals(that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, state);
    }
}

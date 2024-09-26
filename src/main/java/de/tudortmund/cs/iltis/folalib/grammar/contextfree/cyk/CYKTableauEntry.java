package de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class CYKTableauEntry<N extends Serializable> implements ICYKTableauEntry<N> {
    private N nonTerminal;

    public CYKTableauEntry(N nonTerminal) {
        this.nonTerminal = nonTerminal;
    }

    /* For serialization */
    @SuppressWarnings("unused")
    protected CYKTableauEntry() {}

    public N getNonTerminal() {
        return nonTerminal;
    }

    @Override
    public Optional<Integer> getSplittingPoint() {
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CYKTableauEntry<?> that = (CYKTableauEntry<?>) o;
        return nonTerminal.equals(that.nonTerminal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nonTerminal);
    }
}

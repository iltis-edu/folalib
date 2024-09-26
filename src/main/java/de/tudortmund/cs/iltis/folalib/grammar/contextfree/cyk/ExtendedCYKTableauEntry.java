package de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class ExtendedCYKTableauEntry<N extends Serializable> extends CYKTableauEntry<N> {
    private int k;

    public ExtendedCYKTableauEntry(N nonTerminal, int k) {
        super(nonTerminal);

        this.k = k;
    }

    /* For serialization */
    @SuppressWarnings("unused")
    private ExtendedCYKTableauEntry() {}

    public int getK() {
        return k;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExtendedCYKTableauEntry<?> that = (ExtendedCYKTableauEntry<?>) o;
        return k == that.k;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), k);
    }

    @Override
    public Optional<Integer> getSplittingPoint() {
        return Optional.of(k);
    }
}

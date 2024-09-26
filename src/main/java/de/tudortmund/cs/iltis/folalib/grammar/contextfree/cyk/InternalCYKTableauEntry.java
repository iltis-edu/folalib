package de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk;

import de.tudortmund.cs.iltis.folalib.grammar.production.ChomskyNormalformProduction;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class InternalCYKTableauEntry<T extends Serializable, N extends Serializable>
        implements ICYKTableauEntry<N> {
    private ChomskyNormalformProduction<T, N> production;
    private int k;

    public InternalCYKTableauEntry(ChomskyNormalformProduction<T, N> production, int k) {
        this.production = production;
        this.k = k;
    }

    public N getNonTerminal() {
        return production.getLhsNonTerminal();
    }

    @Override
    public Optional<Integer> getSplittingPoint() {
        return Optional.of(k);
    }

    public ChomskyNormalformProduction<T, N> getProduction() {
        return production;
    }

    public int getK() {
        return k;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalCYKTableauEntry<?, ?> that = (InternalCYKTableauEntry<?, ?>) o;
        return k == that.k && Objects.equals(production, that.production);
    }

    @Override
    public int hashCode() {
        return Objects.hash(production, k);
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private InternalCYKTableauEntry() {}
}

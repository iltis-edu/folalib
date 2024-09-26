package de.tudortmund.cs.iltis.folalib.grammar.contextfree;

import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.utils.tree.Tree;
import java.io.Serializable;
import java.util.Objects;

/** Basic class for derivation trees. */
public class DerivationTree<T extends Serializable, N extends Serializable>
        extends Tree<DerivationTree<T, N>> {
    private GrammarSymbol<T, N> symbol;

    @SafeVarargs
    public DerivationTree(N nonTerminal, DerivationTree<T, N>... children) {
        super(children);

        symbol = new GrammarSymbol.NonTerminal<>(nonTerminal);
    }

    public DerivationTree(T terminal) {
        super();

        symbol = new GrammarSymbol.Terminal<>(terminal);
    }

    /* For serialization */
    @SuppressWarnings("unused")
    private DerivationTree() {}

    public GrammarSymbol<T, N> getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && Objects.equals(symbol, ((DerivationTree<?, ?>) o).symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), symbol);
    }
}

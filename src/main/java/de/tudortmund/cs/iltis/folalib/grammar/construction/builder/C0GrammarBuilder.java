package de.tudortmund.cs.iltis.folalib.grammar.construction.builder;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import java.io.Serializable;

/**
 * A C0-grammar is defined as follows: {w -> w' | w ∈ (Σ ∪ V)⁺, w' ∈ (Σ ∪ V)*}. Epsilon-rules are
 * explicitly allowed.
 */
public class C0GrammarBuilder<T extends Serializable, N extends Serializable>
        extends AbstractGrammarBuilder<
                T, N, Production<T, N>, Grammar<T, N, Production<T, N>>, C0GrammarBuilder<T, N>> {

    public C0GrammarBuilder(Alphabet<? extends T> terminals, Alphabet<? extends N> nonTerminals) {
        super(terminals, nonTerminals, Grammar::new);
    }

    public IntegratedGenericProductionBuilder<T, N, C0GrammarBuilder<T, N>> withProduction() {
        return new IntegratedGenericProductionBuilder<>(
                this, production -> productions.add(production));
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private C0GrammarBuilder() {}
}

package de.tudortmund.cs.iltis.folalib.grammar.contextfree.conversion;

import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import java.io.Serializable;

/**
 * Eliminates unreachable non-terminals from the given grammar
 *
 * @param <T> the type of terminals of the grammar
 * @param <N> the type of non-terminals of the grammar
 */
public class CFGEliminateUnreachableNonTerminalsConversion<
                T extends Serializable, N extends Serializable>
        extends CFGConversion<T, N, ContextFreeProduction<T, N>> {

    @Override
    protected ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> convert(
            ContextFreeGrammar<T, N, ? extends ContextFreeProduction<T, N>> cfg) {
        ContextFreeGrammarBuilder<T, N> builder = new ContextFreeGrammarBuilder<>(cfg);
        for (N nonTerminal : cfg.unreachableNonTerminals())
            builder.eliminateNonTerminal(nonTerminal);
        return builder.build().unwrap();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> identity(
            ContextFreeGrammar<T, N, ? extends ContextFreeProduction<T, N>> cfg) {
        return (ContextFreeGrammar<T, N, ContextFreeProduction<T, N>>) cfg; // Cannot fail
    }

    @Override
    protected boolean isRedundant(
            ContextFreeGrammar<T, N, ? extends ContextFreeProduction<T, N>> cfg) {
        return cfg.unreachableNonTerminals().isEmpty();
    }
}

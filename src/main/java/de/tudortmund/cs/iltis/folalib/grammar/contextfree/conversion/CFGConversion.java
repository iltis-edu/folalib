package de.tudortmund.cs.iltis.folalib.grammar.contextfree.conversion;

import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.transform.AbstractConversion;
import java.io.Serializable;

/**
 * Converts a given {@link ContextFreeGrammar} to a CFG with specific properties such that both CFGs
 * decide the same language.
 *
 * @param <T> The type of the terminal symbols
 * @param <N> The type of the non-terminal symbols
 * @param <Prod> The type of the productions in the resulting CFG
 */
public abstract class CFGConversion<
                T extends Serializable,
                N extends Serializable,
                Prod extends ContextFreeProduction<T, N>>
        extends AbstractConversion<
                ContextFreeGrammar<T, N, ? extends ContextFreeProduction<T, N>>,
                ContextFreeGrammar<T, N, Prod>> {}

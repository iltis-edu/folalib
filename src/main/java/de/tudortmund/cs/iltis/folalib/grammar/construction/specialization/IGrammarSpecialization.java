package de.tudortmund.cs.iltis.folalib.grammar.construction.specialization;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.util.Result;
import java.io.Serializable;

/**
 * An interface for all specialization classes. Every class must be able to (try to) convert any
 * grammar with any productions to the desired type in the chomsky hierarchy.
 *
 * @param <T> The type of the terminals used
 * @param <N> The type of the non-terminals used
 * @param <Prod> The type of {@link Production} the specialization will return
 * @param <Gram> The type of {@link Grammar} the specialization will return
 */
public interface IGrammarSpecialization<
        T extends Serializable,
        N extends Serializable,
        Prod extends Production<T, N>,
        Gram extends Grammar<T, N, Prod>> {

    Result<Gram, GrammarConstructionFaultCollection> specialize(
            Grammar<T, N, ? extends Production<T, N>> grammarToSpecialize);
}

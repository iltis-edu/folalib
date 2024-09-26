package de.tudortmund.cs.iltis.folalib.grammar.construction.specialization;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextSensitiveGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.util.Result;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;
import java.util.Objects;

/**
 * A specialization class for type 1 grammars in the chomsky hierarchy. See the JavaDoc of {@link
 * ContextSensitiveGrammarBuilder} to view the exact definition of this type of grammar. This class
 * takes a grammar of any kind (even with obvious faults as e.g. using symbols that are not defined
 * in the alphabets) and tries to convert it into a proper type 1 grammar. If that is not possible,
 * a {@link GrammarConstructionFaultCollection} will be returned which contains a separate {@link
 * Fault} for every fault that occurred while converting.
 *
 * <p>The following faults can occur: {@link GrammarConstructionFaultReason} (see docs to identify
 * applicable faults)
 *
 * @param <T> The type of the terminals used
 * @param <N> The type of the non-terminals used
 */
public class GrammarToContextSensitiveGrammarSpecialization<
                T extends Serializable, N extends Serializable>
        implements IGrammarSpecialization<T, N, Production<T, N>, Grammar<T, N, Production<T, N>>> {

    @Override
    public Result<Grammar<T, N, Production<T, N>>, GrammarConstructionFaultCollection> specialize(
            Grammar<T, N, ? extends Production<T, N>> grammarToSpecialize) {
        Objects.requireNonNull(grammarToSpecialize);

        // Simply use the grammar builder for context-sensitive grammars as this builder accepts all
        // types of
        // productions and can return a fault collection.
        // This includes all faults that can occur for C0-grammars as well.
        ContextSensitiveGrammarBuilder<T, N> contextSensitiveGrammarBuilder =
                new ContextSensitiveGrammarBuilder<>(
                        grammarToSpecialize.getTerminals(), grammarToSpecialize.getNonTerminals());
        contextSensitiveGrammarBuilder.withStartSymbol(grammarToSpecialize.getStartSymbol());
        grammarToSpecialize
                .getProductions()
                .forEach(contextSensitiveGrammarBuilder::withProduction);

        return contextSensitiveGrammarBuilder.build();
    }
}

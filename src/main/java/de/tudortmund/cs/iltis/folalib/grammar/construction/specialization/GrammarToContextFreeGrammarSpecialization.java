package de.tudortmund.cs.iltis.folalib.grammar.construction.specialization;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.ProductionFault;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.util.Result;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A specialization class for type 2 grammars in the chomsky hierarchy. See the JavaDoc of {@link
 * ContextFreeGrammarBuilder} to view the exact definition of this type of grammar. This class takes
 * a grammar of any kind (even with obvious faults as e.g. using symbols that are not defined in the
 * alphabets) and tries to convert it into a proper type 2 grammar. If that is not possible, a
 * {@link GrammarConstructionFaultCollection} will be returned which contains a separate {@link
 * Fault} for every fault that occurred while converting.
 *
 * <p>The following faults can occur: {@link GrammarConstructionFaultReason} (see docs to identify
 * applicable faults)
 *
 * @param <T> The type of the terminals used
 * @param <N> The type of the non-terminals used
 */
public class GrammarToContextFreeGrammarSpecialization<
                T extends Serializable, N extends Serializable>
        implements IGrammarSpecialization<
                T,
                N,
                ContextFreeProduction<T, N>,
                ContextFreeGrammar<T, N, ContextFreeProduction<T, N>>> {

    @Override
    public Result<
                    ContextFreeGrammar<T, N, ContextFreeProduction<T, N>>,
                    GrammarConstructionFaultCollection>
            specialize(Grammar<T, N, ? extends Production<T, N>> grammarToSpecialize) {
        Objects.requireNonNull(grammarToSpecialize);

        List<Fault<GrammarConstructionFaultReason>> faults;
        List<ContextFreeProduction<T, N>> cachedConvertedContextFreeProductions =
                new LinkedList<>();

        // Use the C0 specialization class to get all faults that already occurred on that grammar
        // type.
        // We do not use the context-sensitive specialization class here because the only additional
        // faults that can
        // occur there are the "epsilon-rule" and the non-monotonic rule, which are not needed for
        // context free
        // grammars. The latter rule will be replaced here by a check if every LHS only consists of
        // one non-terminal.
        GrammarToC0GrammarSpecialization<T, N> c0Specializer =
                new GrammarToC0GrammarSpecialization<>();
        Result<Grammar<T, N, Production<T, N>>, GrammarConstructionFaultCollection> resultC0 =
                c0Specializer.specialize(grammarToSpecialize);
        faults =
                resultC0.match(
                        valid -> new LinkedList<>(),
                        faultCollection -> new LinkedList<>(faultCollection.getFaults()));

        for (Production<T, N> production : grammarToSpecialize.getProductions()) {
            // Check if lhs is correct
            Optional<ContextFreeProduction<T, N>> optionalContextFree =
                    ContextFreeProduction.contextFreeProductionFromProduction(production);
            if (optionalContextFree.isPresent()) {
                cachedConvertedContextFreeProductions.add(optionalContextFree.get());
            } else {
                faults.add(
                        new ProductionFault<>(
                                GrammarConstructionFaultReason.LHS_NOT_ONLY_ONE_NONTERMINAL,
                                production));
            }
        }

        if (!faults.isEmpty())
            return new Result.Err<>(new GrammarConstructionFaultCollection(faults));

        return new Result.Ok<>(
                new ContextFreeGrammar<>(
                        grammarToSpecialize.getTerminals(),
                        grammarToSpecialize.getNonTerminals(),
                        grammarToSpecialize.getStartSymbol(),
                        cachedConvertedContextFreeProductions));
    }
}

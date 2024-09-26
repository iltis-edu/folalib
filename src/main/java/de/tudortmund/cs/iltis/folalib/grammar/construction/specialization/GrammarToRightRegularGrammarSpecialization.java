package de.tudortmund.cs.iltis.folalib.grammar.construction.specialization;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.AbstractGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.RightRegularGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.ProductionFault;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.grammar.production.RightRegularProduction;
import de.tudortmund.cs.iltis.folalib.util.Result;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A specialization class for type 3 grammars in the chomsky hierarchy. See the JavaDoc of {@link
 * RightRegularGrammarBuilder} to view the exact definition of this type of grammar. This class
 * takes a grammar of any kind (even with obvious faults as e.g. using symbols that are not defined
 * in the alphabets) and tries to convert it into a proper type 3 grammar. If that is not possible,
 * a {@link GrammarConstructionFaultCollection} will be returned which contains a separate {@link
 * Fault} for every fault that occurred while converting.
 *
 * <p>The following faults can occur: {@link GrammarConstructionFaultReason} (see docs to identify
 * applicable faults)
 *
 * @param <T> The type of the terminals used
 * @param <N> The type of the non-terminals used
 */
public class GrammarToRightRegularGrammarSpecialization<
                T extends Serializable, N extends Serializable>
        implements IGrammarSpecialization<
                T, N, RightRegularProduction<T, N>, Grammar<T, N, RightRegularProduction<T, N>>> {

    @Override
    public Result<Grammar<T, N, RightRegularProduction<T, N>>, GrammarConstructionFaultCollection>
            specialize(Grammar<T, N, ? extends Production<T, N>> grammarToSpecialize) {
        Objects.requireNonNull(grammarToSpecialize);

        List<Fault<GrammarConstructionFaultReason>> faults =
                getContextFreeFaults(grammarToSpecialize);

        faults.addAll(getRhsFaults(grammarToSpecialize));

        // Check epsilon rule
        faults.addAll(
                AbstractGrammarBuilder.validateEpsilonRule(
                                grammarToSpecialize.getProductions(),
                                grammarToSpecialize.getStartSymbol())
                        .getFaults());

        if (!faults.isEmpty())
            return new Result.Err<>(new GrammarConstructionFaultCollection(faults));

        return new Result.Ok<>(convertGrammar(grammarToSpecialize));
    }

    /**
     * Uses the context-free specialization class to get all faults that already occurred on that
     * grammar type.
     */
    private List<Fault<GrammarConstructionFaultReason>> getContextFreeFaults(
            Grammar<T, N, ? extends Production<T, N>> grammarToSpecialize) {
        GrammarToContextFreeGrammarSpecialization<T, N> contextFreeSpecializer =
                new GrammarToContextFreeGrammarSpecialization<>();
        Result<
                        ContextFreeGrammar<T, N, ContextFreeProduction<T, N>>,
                        GrammarConstructionFaultCollection>
                resultContextFree = contextFreeSpecializer.specialize(grammarToSpecialize);
        return resultContextFree.match(
                valid -> new LinkedList<>(),
                faultCollection -> new LinkedList<>(faultCollection.getFaults()));
    }

    /**
     * Checks every production's RHS for correctness. If a production's RHS is not regular or a
     * left-regular RHS, a respective fault gets added.
     */
    private List<Fault<GrammarConstructionFaultReason>> getRhsFaults(
            Grammar<T, N, ? extends Production<T, N>> grammarToSpecialize) {
        List<Fault<GrammarConstructionFaultReason>> faults = new LinkedList<>();

        // Check for wrong Rhss
        for (Production<T, N> production : grammarToSpecialize.getProductions()) {
            // Check if rhs is incorrect
            // Epsilon rules are allowed here, this will be checked later

            if (production.getRhs().size() == 2
                    && production.getRhs().get(0).isNonTerminal()
                    && production.getRhs().get(1).isTerminal()) {
                // Left regular production
                faults.add(
                        new ProductionFault<>(
                                GrammarConstructionFaultReason.RIGHT_AND_LEFT_REGULAR_RHS_MIX,
                                production));
            } else if (!(production.getRhs().isEmpty()
                    || (production.getRhs().size() == 1 && production.getRhs().get(0).isTerminal())
                    || (production.getRhs().size() == 2
                            && production.getRhs().get(0).isTerminal()
                            && production.getRhs().get(1).isNonTerminal()))) {

                // Any arbitrary non-right-regular production
                faults.add(
                        new ProductionFault<>(
                                GrammarConstructionFaultReason.INVALID_REGULAR_RHS, production));
            }
        }

        return faults;
    }

    /** Converts the grammar to a real right-regular grammar. */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private Grammar<T, N, RightRegularProduction<T, N>> convertGrammar(
            Grammar<T, N, ? extends Production<T, N>> grammarToSpecialize) {
        // Cast to regular production grammar if no faults were found
        List<RightRegularProduction<T, N>> regularProductions =
                grammarToSpecialize.getProductions().stream()
                        .map(RightRegularProduction::rightRegularProductionFromProduction)
                        .map(Optional::get) // This cannot fail because we already checked if the
                        // productions are faulty
                        .collect(Collectors.toList());

        return new Grammar<>(
                grammarToSpecialize.getTerminals(),
                grammarToSpecialize.getNonTerminals(),
                grammarToSpecialize.getStartSymbol(),
                regularProductions);
    }
}

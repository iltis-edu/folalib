package de.tudortmund.cs.iltis.folalib.grammar.construction.builder;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.ProductionFault;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;
import java.util.List;

/**
 * A context-sensitive grammar is defined as follows: {w -> w' | w,w' ∈ (Σ ∪ V)⁺, |w| <= |w'|}.
 * Additionally, the production {S -> ɛ | S is start symbol} is allowed iff S occurs in no RHS of
 * any production.
 */
public class ContextSensitiveGrammarBuilder<T extends Serializable, N extends Serializable>
        extends AbstractGrammarBuilder<
                T,
                N,
                Production<T, N>,
                Grammar<T, N, Production<T, N>>,
                ContextSensitiveGrammarBuilder<T, N>> {

    public ContextSensitiveGrammarBuilder(
            Alphabet<? extends T> terminals, Alphabet<? extends N> nonTerminals) {
        super(terminals, nonTerminals, Grammar::new);
    }

    public IntegratedGenericProductionBuilder<T, N, ContextSensitiveGrammarBuilder<T, N>>
            withProduction() {
        return new IntegratedGenericProductionBuilder<>(
                this, production -> productions.add(production));
    }

    /**
     * Additionally to the faults generated by {@link AbstractGrammarBuilder#validate()} the
     * following checks will be performed: It will check... ... if every production's LHS is smaller
     * or equal in length than its RHS (epsilon-rules are ignored here) {@link
     * GrammarConstructionFaultReason#NON_MONOTONIC}, ... if S -> ɛ is the only epsilon-rule (if it
     * exists) and if S does not occur in any RHS then {@link
     * GrammarConstructionFaultReason#THE_EPSILON_RULE}.
     *
     * @return A collection of all faults that were found.
     */
    @Override
    public GrammarConstructionFaultCollection validate() {
        List<Fault<GrammarConstructionFaultReason>> superFaults = super.validate().getFaults();

        for (Production<T, N> production : productions) {
            if (production.getRhs().isEmpty()) continue;
            if (production.getLhs().size() > production.getRhs().size())
                superFaults.add(
                        new ProductionFault<>(
                                GrammarConstructionFaultReason.NON_MONOTONIC, production));
        }

        superFaults.addAll(super.validateEpsilonRule().getFaults());

        return new GrammarConstructionFaultCollection(superFaults);
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private ContextSensitiveGrammarBuilder() {}
}

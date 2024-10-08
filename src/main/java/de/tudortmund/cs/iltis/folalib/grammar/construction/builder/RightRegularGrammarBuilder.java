package de.tudortmund.cs.iltis.folalib.grammar.construction.builder;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.production.RightRegularProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;
import java.util.List;

/**
 * A right regular grammar is defined as follows: {X -> aY, X -> a | X,Y ∈ V, a ∈ Σ} Additionally,
 * the production {S -> ɛ | S is start symbol} is allowed iff S occurs in no RHS of any production.
 */
public class RightRegularGrammarBuilder<T extends Serializable, N extends Serializable>
        extends AbstractGrammarBuilder<
                T,
                N,
                RightRegularProduction<T, N>,
                Grammar<T, N, RightRegularProduction<T, N>>,
                RightRegularGrammarBuilder<T, N>> {

    public RightRegularGrammarBuilder(
            Alphabet<? extends T> terminals, Alphabet<? extends N> nonTerminals) {
        super(terminals, nonTerminals, Grammar::new);
    }

    public RightRegularGrammarBuilder<T, N> withProduction(N lhs, T rhs) {
        return withProduction(new RightRegularProduction<>(lhs, rhs));
    }

    public RightRegularGrammarBuilder<T, N> withProduction(N lhs, T rhsTerminal, N rhsNonTerminal) {
        return withProduction(new RightRegularProduction<>(lhs, rhsTerminal, rhsNonTerminal));
    }

    /**
     * Please note that a regular grammar only allows the epsilon-rule {S -> ɛ | S is start symbol}
     * iff S occurs in no RHS of any production. If this restriction is violated {@link
     * #validate()}, {@link #build()} and {@link #buildAndReset()} will fail.
     */
    public RightRegularGrammarBuilder<T, N> withEpsProduction(N lhsNonTerminal) {
        return withProduction(new RightRegularProduction<>(lhsNonTerminal));
    }

    /**
     * Additionally to the faults generated by {@link AbstractGrammarBuilder#validate()} the
     * following checks will be performed: It will check... ... if S -> ɛ is the only epsilon-rule
     * (if it exists) and if S does not occur in any RHS then {@link
     * GrammarConstructionFaultReason#THE_EPSILON_RULE}.
     *
     * @return A collection of all faults that were found.
     */
    @Override
    public GrammarConstructionFaultCollection validate() {
        List<Fault<GrammarConstructionFaultReason>> superFaults = super.validate().getFaults();

        superFaults.addAll(super.validateEpsilonRule().getFaults());

        return new GrammarConstructionFaultCollection(superFaults);
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private RightRegularGrammarBuilder() {}
}

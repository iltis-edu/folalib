package de.tudortmund.cs.iltis.folalib.grammar.construction.builder;

import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * A context-free grammar is defined as follows: {X -> w | X ∈ V, w ∈ (Σ ∪ V)*} Epsilon-rules are
 * explicitly allowed.
 */
public class ContextFreeGrammarBuilder<T extends Serializable, N extends Serializable>
        extends AbstractGrammarBuilder<
                T,
                N,
                ContextFreeProduction<T, N>,
                ContextFreeGrammar<T, N, ContextFreeProduction<T, N>>,
                ContextFreeGrammarBuilder<T, N>> {

    public ContextFreeGrammarBuilder(
            Alphabet<? extends T> terminals, Alphabet<? extends N> nonTerminals) {
        super(terminals, nonTerminals, ContextFreeGrammar::new);
    }

    /**
     * Constructs a new builder from the given grammar
     *
     * @param grammar The {@link ContextFreeGrammar} to base this builder off of
     */
    public ContextFreeGrammarBuilder(
            ContextFreeGrammar<T, N, ? extends ContextFreeProduction<T, N>> grammar) {
        super(grammar.getTerminals(), grammar.getNonTerminals(), ContextFreeGrammar::new);

        startSymbol = grammar.getStartSymbol();

        for (ContextFreeProduction<T, N> prod : grammar) productions.add(prod);
    }

    public ContextFreeGrammarBuilder<T, N> replaceTerminal(T terminal, N nonTerminal) {
        productions =
                productions.stream()
                        .map(
                                production ->
                                        new ContextFreeProduction<>(
                                                production.getLhsNonTerminal(),
                                                production
                                                        .getRhs()
                                                        .replaceTerminal(
                                                                terminal,
                                                                new GrammarSymbol.NonTerminal<>(
                                                                        nonTerminal))))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
        return this;
    }

    public IntegratedSententialFormBuilder<T, N, ContextFreeGrammarBuilder<T, N>> withProduction(
            N lhsVariable) {
        return new IntegratedSententialFormBuilder<>(
                rhs -> productions.add(new ContextFreeProduction<>(lhsVariable, rhs)), this);
    }

    public ContextFreeGrammarBuilder<T, N> withEpsProduction(N lhsNonTerminal) {
        return withProduction(new ContextFreeProduction<>(lhsNonTerminal));
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private ContextFreeGrammarBuilder() {}
}

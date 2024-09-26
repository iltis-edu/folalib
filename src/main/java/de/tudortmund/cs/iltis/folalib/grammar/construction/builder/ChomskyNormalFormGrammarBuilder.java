package de.tudortmund.cs.iltis.folalib.grammar.construction.builder;

import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ChomskyNormalformProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class ChomskyNormalFormGrammarBuilder<T extends Serializable, N extends Serializable>
        extends AbstractGrammarBuilder<
                T,
                N,
                ChomskyNormalformProduction<T, N>,
                ContextFreeGrammar<T, N, ChomskyNormalformProduction<T, N>>,
                ChomskyNormalFormGrammarBuilder<T, N>> {

    public ChomskyNormalFormGrammarBuilder(
            Alphabet<? extends T> terminals, Alphabet<? extends N> nonTerminals) {
        super(terminals, nonTerminals, ContextFreeGrammar::new);
    }

    public ChomskyNormalFormGrammarBuilder<T, N> withProduction(N nonTerminal, T terminal) {
        return withProduction(
                new ChomskyNormalformProduction.TerminalProduction<>(nonTerminal, terminal));
    }

    public ChomskyNormalFormGrammarBuilder<T, N> withProduction(N nonTerminal, N rhsnt1, N rhsnt2) {
        return withProduction(
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>(
                        nonTerminal, rhsnt1, rhsnt2));
    }

    public ChomskyNormalFormGrammarBuilder<T, N> replaceNonTerminal(N original, N replacement) {
        productions =
                productions.stream()
                        .map(p -> p.replaceNonTerminal(original, replacement))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
        return this;
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private ChomskyNormalFormGrammarBuilder() {}
}

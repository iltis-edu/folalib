package de.tudortmund.cs.iltis.folalib.automata.pushdown.transformation;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDA;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDAAcceptanceStrategy;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDABuilder;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDAStackWord;
import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.folalib.util.CachedSerializableFunction;
import java.io.Serializable;
import java.util.stream.Collectors;

/**
 * Transform a CFG into an equivalent PDA
 *
 * <p>The algorithm is taken from slide 354 of GTI lectures, SS 18
 *
 * @param <T> The type of the terminals of the CFG *and* the type of the input symbols of the PDA
 * @param <N> The type of the non-terminals of the CFG
 */
public class CFGToPDATransformation<T extends Serializable, N extends Serializable> {

    private ContextFreeGrammar<T, N, ? extends ContextFreeProduction<T, N>> grammar;
    private PDABuilder<String, T, MaybeGenerated<T, N>> builder;

    private Alphabet<T> inputAlphabet;
    private Alphabet<MaybeGenerated<T, N>> stackAlphabet;

    private final CachedSerializableFunction<T, MaybeGenerated<T, N>> terminalToStackSymbol =
            new CachedSerializableFunction<>(MaybeGenerated.Input::new);
    private final CachedSerializableFunction<N, MaybeGenerated<T, N>> nonTerminalToStackSymbol =
            new CachedSerializableFunction<>(MaybeGenerated.Generated::new);

    /**
     * Transform a CFG into an equivalent PDA
     *
     * @param grammar The grammar to transform
     * @return A new, equivalent PDA
     */
    public PDA<String, T, MaybeGenerated<T, N>> transform(
            ContextFreeGrammar<T, N, ? extends ContextFreeProduction<T, N>> grammar) {
        this.grammar = grammar;
        setupAlphabets();
        setupBuilder();
        addTransitions(grammar);
        return builder.build().unwrap();
    }

    private void setupAlphabets() {
        inputAlphabet = grammar.getTerminals();
        stackAlphabet =
                grammar.getTerminals()
                        .map(terminalToStackSymbol)
                        .unionWith(grammar.getNonTerminals().map(nonTerminalToStackSymbol));
    }

    private void setupBuilder() {
        builder = new PDABuilder<>(inputAlphabet);
        builder.withStackSymbols(stackAlphabet.toUnmodifiableSet())
                .withInitialStackSymbol(nonTerminalToStackSymbol.apply(grammar.getStartSymbol()))
                .withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK);
    }

    private void addTransitions(
            ContextFreeGrammar<T, N, ? extends ContextFreeProduction<T, N>> grammar) {
        String state = "q0";
        builder.withInitial(state);

        for (T symbol : inputAlphabet) {
            builder.withTransition(
                    state,
                    symbol,
                    terminalToStackSymbol.apply(symbol),
                    state,
                    new PDAStackWord<>());
        }

        for (ContextFreeProduction<T, N> production : grammar.getProductions()) {
            MaybeGenerated<T, N> X = nonTerminalToStackSymbol.apply(production.getLhsNonTerminal());
            PDAStackWord<MaybeGenerated<T, N>> alpha =
                    new PDAStackWord<>(
                            production.getRhs().stream()
                                    .map(this::grammarSymbolToStackSymbol)
                                    .collect(Collectors.toList()));
            builder.withEpsilonTransition(state, X, state, alpha);
        }
    }

    private MaybeGenerated<T, N> grammarSymbolToStackSymbol(GrammarSymbol<T, N> grammarSymbol) {
        return grammarSymbol.match(terminalToStackSymbol, nonTerminalToStackSymbol);
    }
}

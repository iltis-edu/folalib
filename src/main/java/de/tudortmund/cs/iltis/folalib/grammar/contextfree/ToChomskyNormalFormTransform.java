package de.tudortmund.cs.iltis.folalib.grammar.contextfree;

import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ChomskyNormalFormGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.production.ChomskyNormalformProduction;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.transform.ConstrainedSupplier;
import de.tudortmund.cs.iltis.utils.graph.algorithms.ShrinkSCCs;
import de.tudortmund.cs.iltis.utils.graph.hashgraph.DefaultHashGraph;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A class which groups several algorithms/methods related to transforming any {@link
 * ContextFreeGrammar} into a CFG in CNF (Chomsky-Normal-Form).
 *
 * <p>This class offers a method to perform the complete transformation as well as methods for each
 * individual step, such as removing epsilon-productions or separating terminals from non-terminals.
 */
public class ToChomskyNormalFormTransform {

    /**
     * Eliminates useless (i.e. non generating or unreachable) non-terminals from the given grammar
     *
     * @param <T> the type of terminals of the grammar
     * @param <N> the type of non-terminals of the grammar
     * @return a new grammar with all useless non-terminals removed
     */
    public static <T extends Serializable, N extends Serializable>
            ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> eliminateUselessNonTerminals(
                    ContextFreeGrammar<T, N, ? extends ContextFreeProduction<T, N>> grammar) {
        return eliminateUnreachableNonTerminals(eliminateNonGeneratingNonTerminals(grammar));
    }

    /**
     * Eliminates non-generating non-terminals from the given grammar
     *
     * @param <T> the type of terminals of the grammar
     * @param <N> the type of non-terminals of the grammar
     * @return a new grammar with all non-generating non-terminals removed
     */
    public static <T extends Serializable, N extends Serializable>
            ContextFreeGrammar<T, N, ContextFreeProduction<T, N>>
                    eliminateNonGeneratingNonTerminals(
                            ContextFreeGrammar<T, N, ? extends ContextFreeProduction<T, N>>
                                    grammar) {
        ContextFreeGrammarBuilder<T, N> builder = new ContextFreeGrammarBuilder<>(grammar);
        Set<N> generating = grammar.generatingNonTerminals();
        for (N nonTerminal : grammar.getNonTerminals())
            if (!generating.contains(nonTerminal)) builder.eliminateNonTerminal(nonTerminal);

        /* In some cases (e.g. for grammars with no productions) the start symbol is removed from the set of non-terminals,
         * so we have to re-add it. */
        builder.withNonTerminal(builder.getStartSymbol());
        return builder.build().unwrap();
    }

    /**
     * Eliminates unreachable non-terminals from the given grammar
     *
     * @param <T> the type of terminals of the grammar
     * @param <N> the type of non-terminals of the grammar
     * @return a new grammar with all unreachable non-terminals removed
     */
    public static <T extends Serializable, N extends Serializable>
            ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> eliminateUnreachableNonTerminals(
                    ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> grammar) {
        ContextFreeGrammarBuilder<T, N> builder = new ContextFreeGrammarBuilder<>(grammar);
        for (N nonTerminal : grammar.unreachableNonTerminals())
            builder.eliminateNonTerminal(nonTerminal);
        return builder.build().unwrap();
    }

    /**
     * Separates terminals from non-terminals in the given grammar
     *
     * @param <T> the type of terminals of the grammar
     * @param <N> the type of non-terminals of the grammar
     * @return a new grammar with all terminals and non-terminals separated
     */
    public static <T extends Serializable, N extends Serializable>
            ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> separateTerminalsFromNonTerminals(
                    ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> grammar,
                    ConstrainedSupplier<N> nonTerminalSupplier) {
        ContextFreeGrammarBuilder<T, N> builder = new ContextFreeGrammarBuilder<>(grammar);
        Set<T> usedTerminals =
                grammar.getProductions().stream()
                        .flatMap(prod -> prod.getTerminals().stream())
                        .collect(Collectors.toSet());
        for (T terminal : usedTerminals) {
            N nonTerminal = nonTerminalSupplier.get();
            builder.withNonTerminal(nonTerminal)
                    .replaceTerminal(terminal, nonTerminal)
                    .withProduction(nonTerminal)
                    .t(terminal)
                    .finish();
        }
        return builder.build().unwrap();
    }

    /**
     * Shorten the right-hand sides of productions to have a length of at most 2
     *
     * <p><b>Important:</b> this method is intended to be used during the CNF-transformation and it
     * is expected that the terminals and non-terminals are separated already.
     *
     * @param <T> the type of terminals of the grammar
     * @param <N> the type of non-terminals of the grammar
     * @return a new grammar such that all productions have at most length 2
     */
    public static <T extends Serializable, N extends Serializable>
            ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> shortenRightHandSideOfProductions(
                    ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> grammar,
                    ConstrainedSupplier<N> nonTerminalSupplier) {
        // TODO: make this safe, e.g. return Optional/Result to handle cases where rhs is too long
        // and not exclusively non-terminals
        ContextFreeGrammarBuilder<T, N> builder =
                new ContextFreeGrammarBuilder<>(grammar.getTerminals(), grammar.getNonTerminals());
        builder.withStartSymbol(grammar.getStartSymbol());

        for (ContextFreeProduction<T, N> prod : grammar.getProductions()) {
            SententialForm<T, N> rhs = prod.getRhs();
            // TODO: move body of this loop into separate function
            // TODO: investigate in how far this is the same as `PDATransitionChain` and perhaps
            // reuse/abstract code
            if (rhs.size() <= 2) {
                builder.withProduction(prod);
            } else {
                // Break Production A -> B1 ... Bm into
                //   A      -> B1 C1
                //   Ci     -> B(i+1)C(i+1)  for i = 1,...,m-3
                //   C(m-2) -> B(m-1)Bm
                // for new non-terminals C1 ... C(m-2)
                N lastNonTerminal = nonTerminalSupplier.get();
                builder.withNonTerminal(lastNonTerminal);

                builder.withProduction(prod.getLhsNonTerminal())
                        .nt(rhs.get(0).unwrapNonTerminal())
                        .nt(lastNonTerminal)
                        .finish();

                for (int i = 1; i <= rhs.size() - 3; ++i) {
                    N nonTerminal = nonTerminalSupplier.get();
                    builder.withNonTerminal(nonTerminal);
                    builder.withProduction(lastNonTerminal)
                            .nt(rhs.get(i).unwrapNonTerminal())
                            .nt(nonTerminal)
                            .finish();
                    lastNonTerminal = nonTerminal;
                }

                int m = rhs.size();
                builder.withProduction(lastNonTerminal)
                        .nt(rhs.get(m - 2).unwrapNonTerminal())
                        .nt(rhs.get(m - 1).unwrapNonTerminal())
                        .finish();
            }
        }
        return builder.build().unwrap();
    }

    /**
     * Removes all epsilon productions from the given grammar
     *
     * <p><b>Important:</b> this method is intended to be used during the CNF-transformation and it
     * is expected that the terminals and non-terminals are separated already and the right-hand
     * sides of the productions are of length at most 2.
     *
     * @param <T> the type of terminals of the grammar
     * @param <N> the type of non-terminals of the grammar
     * @return a new grammar where all epsilon productions have been removed
     */
    public static <T extends Serializable, N extends Serializable>
            ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> removeEpsilonProductions(
                    ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> grammar) {
        ContextFreeGrammarBuilder<T, N> builder =
                new ContextFreeGrammarBuilder<>(grammar.getTerminals(), grammar.getNonTerminals());
        builder.withStartSymbol(grammar.getStartSymbol());

        Map<N, SetWithEpsilon<T>> firstSets = grammar.firstSets();
        for (ContextFreeProduction<T, N> prod : grammar) {
            SententialForm<T, N> rhs = prod.getRhs();
            // for rhs.size() == 0 do nothing because we want to remove all epsilon productions
            if (rhs.size() == 1) {
                builder.withProduction(prod);
            } else if (rhs.size() == 2) {
                builder.withProduction(prod);
                N a = prod.getLhsNonTerminal();
                N b = rhs.get(0).unwrapNonTerminal();
                N c = rhs.get(1).unwrapNonTerminal();
                if (firstSets.get(b).containsEpsilon() && !a.equals(c))
                    builder.withProduction(prod.getLhsNonTerminal()).nt(c).finish();
                if (firstSets.get(c).containsEpsilon() && !a.equals(b))
                    builder.withProduction(prod.getLhsNonTerminal()).nt(b).finish();
            }
            // rhs.size() > 2 is impossible as per precondition of calling this function
        }
        ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> result = builder.build().unwrap();
        return eliminateUselessNonTerminals(result);
    }

    /**
     * Remove all single-variable productions like `P -> Q` where P, Q are non-terminals
     *
     * <p><b>Important:</b> this method is intended to be used during the CNF-transformation and it
     * is expected that the terminals and non-terminals are separated already, the right-hand sides
     * of the productions are of length at most 2 and there are no more epsilon productions.
     *
     * @param <T> the type of terminals of the grammar
     * @param <N> the type of non-terminals of the grammar
     * @return a new grammar such that all productions have at most length 2
     */
    public static <T extends Serializable, N extends Serializable>
            ContextFreeGrammar<T, N, ChomskyNormalformProduction<T, N>>
                    removeSingleVariableProductions(
                            ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> grammar) {
        DefaultHashGraph<N> chainProductionGraph = buildChainProductionGraph(grammar);
        ChomskyNormalFormGrammarBuilder<T, N> cnfBuilder = initializeCNFBuilderFromGrammar(grammar);
        eliminateChainRuleCycles(chainProductionGraph, grammar.getStartSymbol(), cnfBuilder);
        eliminateChainRules(chainProductionGraph, cnfBuilder);
        return cnfBuilder.build().unwrap();
    }

    /**
     * Convert the given grammar to Chomsky-Normal-Form (CNF)
     *
     * <p>The algorithm is taken from the GTI lecture (SS 18) slides, page 293 and following.
     *
     * <p><b>Important:</b> the algorithm slightly differs from the algorithm in the lecture: the
     * resulting grammar does <b>not</b> contain the empty word. Make sure to test for this
     * separately.
     *
     * @param grammar the grammar to convert
     * @param nonTerminalSupplier a (sufficiently large) supply of non-terminals
     * @param <T> the type of terminals of the grammar
     * @param <N> the type of non-terminals of the grammar
     * @return a new grammar (almost) equivalent grammar such that all productions are in CNF
     */
    public static <T extends Serializable, N extends Serializable>
            ContextFreeGrammar<T, N, ChomskyNormalformProduction<T, N>> convertToCnf(
                    ContextFreeGrammar<T, N, ? extends ContextFreeProduction<T, N>> grammar,
                    ConstrainedSupplier<N> nonTerminalSupplier) {
        // Ensure that the supplier never supplies an already existing non-terminal
        nonTerminalSupplier.constrain(grammar.getNonTerminals().toUnmodifiableSet());

        ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> grammar1 =
                eliminateUselessNonTerminals(grammar);
        ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> grammar2 =
                separateTerminalsFromNonTerminals(grammar1, nonTerminalSupplier);
        ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> grammar3 =
                shortenRightHandSideOfProductions(grammar2, nonTerminalSupplier);
        ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> grammar4 =
                removeEpsilonProductions(grammar3);
        return removeSingleVariableProductions(grammar4);
    }

    private static <T extends Serializable, N extends Serializable>
            DefaultHashGraph<N> buildChainProductionGraph(
                    ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> grammar) {
        DefaultHashGraph<N> graph = new DefaultHashGraph<>();
        graph.addVertices(grammar.getNonTerminals().toUnmodifiableSet());
        for (ContextFreeProduction<T, N> prod : grammar.getProductions()) {
            if (prod.getRhs().size() == 1 && prod.getRhs().get(0).isNonTerminal()) {
                graph.addEdge(prod.getLhsNonTerminal(), prod.getRhs().get(0).unwrapNonTerminal());
            }
        }
        return graph;
    }

    private static <T extends Serializable, N extends Serializable>
            ChomskyNormalFormGrammarBuilder<T, N> initializeCNFBuilderFromGrammar(
                    ContextFreeGrammar<T, N, ContextFreeProduction<T, N>> grammar) {
        ChomskyNormalFormGrammarBuilder<T, N> cnfBuilder =
                new ChomskyNormalFormGrammarBuilder<>(
                        grammar.getTerminals(), grammar.getNonTerminals());
        cnfBuilder.withStartSymbol(grammar.getStartSymbol());
        for (ContextFreeProduction<T, N> prod : grammar) {
            N lhs = prod.getLhsNonTerminal();
            SententialForm<T, N> rhs = prod.getRhs();
            if (rhs.size() == 1 && rhs.get(0).isTerminal()) {
                cnfBuilder.withProduction(lhs, rhs.get(0).unwrapTerminal());
            } else if (rhs.size() == 2) {
                cnfBuilder.withProduction(
                        lhs, rhs.get(0).unwrapNonTerminal(), rhs.get(1).unwrapNonTerminal());
            }
        }
        return cnfBuilder;
    }

    private static <T extends Serializable, N extends Serializable> void eliminateChainRuleCycles(
            DefaultHashGraph<N> chainProductionGraph,
            N startPoint,
            ChomskyNormalFormGrammarBuilder<T, N> cnfBuilder) {

        ShrinkSCCs.shrinkByValues(
                chainProductionGraph,
                (g, vs) -> {
                    N representative =
                            vs.contains(startPoint) ? startPoint : vs.stream().findFirst().get();
                    for (N nonTerminal : vs) {
                        cnfBuilder.replaceNonTerminal(
                                nonTerminal,
                                representative); // this is a no-op if nonTerminal equals
                        // representative
                    }
                    return representative;
                });
    }

    private static <T extends Serializable, N extends Serializable> void eliminateChainRules(
            DefaultHashGraph<N> chainProductionGraph,
            ChomskyNormalFormGrammarBuilder<T, N> cnfBuilder) {
        List<N> topologicalOrdering = chainProductionGraph.getTopologicalOrdering().get();

        for (int i = topologicalOrdering.size() - 1; i >= 0; --i) {
            for (int j = 0; j < i; ++j) {
                N ai = topologicalOrdering.get(i);
                N aj = topologicalOrdering.get(j);

                if (chainProductionGraph.hasEdge(aj, ai)) {
                    Set<ChomskyNormalformProduction<T, N>> newProductions = new HashSet<>();

                    for (ChomskyNormalformProduction<T, N> prod : cnfBuilder.getProductions()) {
                        if (prod.getLhsNonTerminal().equals(ai)) {
                            prod.consumeRhs(
                                    t ->
                                            newProductions.add(
                                                    new ChomskyNormalformProduction
                                                            .TerminalProduction<>(aj, t)),
                                    (n1, n2) ->
                                            newProductions.add(
                                                    new ChomskyNormalformProduction
                                                            .TwoNonTerminalsProduction<>(
                                                            aj, n1, n2)));
                        }
                    }

                    for (ChomskyNormalformProduction<T, N> prod : newProductions)
                        cnfBuilder.withProduction(prod);
                }
            }
        }
    }
}

package de.tudortmund.cs.iltis.folalib.grammar.contextfree;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ChomskyNormalFormGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.specialization.GrammarToContextFreeGrammarSpecialization;
import de.tudortmund.cs.iltis.folalib.grammar.production.ChomskyNormalformProduction;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.transform.ConstrainedSupplier;
import de.tudortmund.cs.iltis.folalib.util.Result;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import de.tudortmund.cs.iltis.utils.graph.EmptyEdgeLabel;
import de.tudortmund.cs.iltis.utils.graph.Graph;
import de.tudortmund.cs.iltis.utils.graph.hashgraph.HashGraph;
import java.io.Serializable;
import java.util.*;

public class ContextFreeGrammar<
                T extends Serializable,
                N extends Serializable,
                Prod extends ContextFreeProduction<T, N>>
        extends Grammar<T, N, Prod> {

    public ContextFreeGrammar(
            Alphabet<T> terminals,
            Alphabet<N> nonTerminals,
            N startSymbol,
            Collection<? extends Prod> productions) {
        super(terminals, nonTerminals, startSymbol, productions);
    }

    public ContextFreeGrammar(
            Collection<? extends T> terminals,
            Collection<? extends N> nonTerminals,
            N startSymbol,
            Collection<? extends Prod> productions) {
        super(terminals, nonTerminals, startSymbol, productions);
    }

    /* For serialization */
    @SuppressWarnings("unused")
    private ContextFreeGrammar() {
        super();
    }

    // TODO: once the CNF-transform exists, we can implement public boolean contains(Word<T> word);
    // via CYK (though maybe not here?)

    /**
     * Checks where this grammar can be used to derive the empty word.
     *
     * @return {@code true} iff the empty word can be derived.
     */
    public boolean containsEmptyWord() {
        return firstSets().get(getStartSymbol()).containsEpsilon();
    }

    @Override
    public <S extends Serializable, M extends Serializable>
            ContextFreeGrammar<S, M, ? extends ContextFreeProduction<S, M>> map(
                    SerializableFunction<T, S> terminalMap,
                    SerializableFunction<N, M> nonTerminalMap) {
        return contextFreeGrammarFromGrammar(super.map(terminalMap, nonTerminalMap)).unwrap();
    }

    @Override
    public <M extends Serializable>
            ContextFreeGrammar<T, M, ? extends ContextFreeProduction<T, M>> mapNonTerminals(
                    SerializableFunction<N, M> f) {
        // Implementation identical to `super` but this is required to "specialise" the (covariant)
        // return type
        return map(t -> t, f);
    }

    @Override
    public <S extends Serializable>
            ContextFreeGrammar<S, N, ? extends ContextFreeProduction<S, N>> mapTerminals(
                    SerializableFunction<T, S> f) {
        // Implementation identical to `super` but this is required to "specialise" the (covariant)
        // return type
        return map(f, n -> n);
    }

    public static <S extends Serializable, M extends Serializable>
            Result<
                            ContextFreeGrammar<S, M, ContextFreeProduction<S, M>>,
                            GrammarConstructionFaultCollection>
                    contextFreeGrammarFromGrammar(
                            Grammar<S, M, ? extends Production<S, M>> grammar) {
        return new GrammarToContextFreeGrammarSpecialization<S, M>().specialize(grammar);
    }

    public static <S extends Serializable, M extends Serializable>
            Optional<ContextFreeGrammar<S, M, ? extends ChomskyNormalformProduction<S, M>>>
                    chomskyNormalFormGrammarFromGrammar(
                            Grammar<S, M, ? extends Production<S, M>> grammar) {
        ChomskyNormalFormGrammarBuilder<S, M> builder =
                new ChomskyNormalFormGrammarBuilder<>(
                        grammar.getTerminals(), grammar.getNonTerminals());
        builder.withStartSymbol(grammar.getStartSymbol());
        for (Production<S, M> production : grammar.getProductions()) {
            Optional<ChomskyNormalformProduction<S, M>> cfp =
                    ChomskyNormalformProduction.chomskyNormalformProductionFromProduction(
                            production);
            if (cfp.isPresent()) {
                builder.withProduction(cfp.get());
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(builder.build().unwrap());
    }

    /**
     * Computes the graph of non terminals induces by this {@link Grammar}.
     *
     * <p>For two non terminals {@code A, B} the returned graph contains the edge {@code (A, B)} iff
     * there exists a production {@code A -> aBb} for sentential forms {@code a,b}.
     *
     * @return The induced graph of non terminals.
     */
    public Graph<N, EmptyEdgeLabel> nonTerminalsGraph() {
        Graph<N, EmptyEdgeLabel> graph = new HashGraph<>();

        for (N nonTerminal : getNonTerminals()) graph.addVertex(nonTerminal);

        for (Prod production : this) {
            for (GrammarSymbol<T, N> symbol : production.getRhs()) {
                symbol.consume(
                        t -> {},
                        n ->
                                graph.addEdge(
                                        production.getLhsNonTerminal(), n, new EmptyEdgeLabel()));
            }
        }

        return graph;
    }

    // TODO: unit test

    /**
     * Computes the set of non terminals that are not reachable.
     *
     * <p>A non terminal {@code N} is <i>reachable</i> if there exist sentential forms {@code a,b}
     * such that {@code S ->* aNb} where {@code S} is the start symbol.
     *
     * @return The set of unreachable terminals in this {@link Grammar}.
     */
    public Set<N> unreachableNonTerminals() {
        Graph<N, EmptyEdgeLabel> nonTerminalGraph = nonTerminalsGraph();
        Set<N> reachable =
                new LinkedHashSet<>(
                        nonTerminalGraph.getReachableValues(
                                nonTerminalGraph.getVertex(getStartSymbol())));

        Set<N> nonTerminals = new LinkedHashSet<>(getNonTerminals().toUnmodifiableSet());
        nonTerminals.removeAll(reachable);
        return nonTerminals;
    }

    // TODO: unit test

    /**
     * Computes the set of non terminals that are generating.
     *
     * <p>A non terminal {@code N} is <i>generating</i> if there exists a word {@code w} such that
     * {@code S ->* w} where {@code S} is the start symbol
     *
     * @return The set of generating non-terminals in this {@link Grammar}
     */
    public Set<N> generatingNonTerminals() {
        Set<N> generating = new LinkedHashSet<>();

        boolean changed;

        // I am fairly certain that this can be implemented more efficiently as a graph algorithm
        // (think topological ordering) instead of a fixed-point-iteration.
        do {
            changed = false;

            for (Prod production : this) {
                boolean generatingProduction =
                        production.getRhs().stream()
                                .allMatch(s -> s.match(t -> true, generating::contains));

                if (generatingProduction) {
                    changed |= generating.add(production.getLhsNonTerminal());
                }
            }
        } while (changed);

        return generating;
    }

    // TODO: Keep track of iteration in which a terminal was added to the first/follow set (via
    // modification to SetWithEpsilon)
    // TODO: cache these results

    /**
     * Computes the {@code FIRST} sets of this {@link Grammar}.
     *
     * <p>Implemented according to lecture "Übersetzerbau", Chapter "Parsing Top-Down", slide 50
     *
     * @return A mapping from non-terminals to their {@code FIRST} set
     */
    public Map<N, SetWithEpsilon<T>> firstSets() {
        Map<N, SetWithEpsilon<T>> firstSets = new HashMap<>();

        for (N nonTerminal : getNonTerminals()) firstSets.put(nonTerminal, new SetWithEpsilon<>());

        boolean changed;

        do {
            changed = false;

            for (Prod production : this) {
                // non-short-circuiting OR to ensure we don't stop this iteration after the first
                // modification.
                changed |=
                        firstSets
                                .get(production.getLhsNonTerminal())
                                .addAll(firstOfSententialForm(production.getRhs(), firstSets));
            }
        } while (changed);

        return firstSets;
    }

    private SetWithEpsilon<T> firstOfSententialForm(
            SententialForm<T, N> form, Map<N, SetWithEpsilon<T>> firstInformation) {
        SetWithEpsilon<T> firstSet = new SetWithEpsilon<>();

        for (GrammarSymbol<T, N> symbol : form) {
            symbol.match(firstSet::add, n -> firstSet.addAllTerminals(firstInformation.get(n)));

            if (symbol.match(t -> true, n -> !firstInformation.get(n).containsEpsilon()))
                return firstSet;
        }

        // The word only consists of non-terminals, and each of those non-terminals can be derive to
        // epsilon. That means the entire form can be derived to epsilon too.
        firstSet.setContainsEpsilon(true);

        return firstSet;
    }

    /**
     * Computes if this contextFreeGrammar describes a finite language
     *
     * <p>A ContextFreeGrammar produces a finite language iff the nonTerminalsGraph() of the CNF
     * contains no directed circle Definition taken from GTI lecture slides (SS18), page 398
     *
     * <p>
     *
     * @return If this contextFreeGrammar describes a finite language
     */
    public boolean isFinite() {
        // map nonTerminals to increasing integers
        ArrayList<N> nts = new ArrayList<>();
        getNonTerminals().forEach(nts::add);
        ContextFreeGrammar<T, Integer, ?> integerNonTerminals = this.mapNonTerminals(nts::indexOf);

        ContextFreeGrammar<T, Integer, ChomskyNormalformProduction<T, Integer>> cnf =
                ToChomskyNormalFormTransform.convertToCnf(
                        integerNonTerminals,
                        ConstrainedSupplier.constrainedSupplierFromIntegers(i -> i + nts.size()));

        return cnf.nonTerminalsGraph().isDirectedAcyclic();
    }

    /**
     * Computes the {@code FOLLOW} sets of this {@link Grammar}
     *
     * <p>Implemented according to lecture "Übersetzerbau", Chapter "Parsing Top-Down", slide 58
     *
     * @return A mapping from non-terminals to their {@code FOLLOW} set
     */
    public Map<N, SetWithEpsilon<T>> followSets() {
        Map<N, SetWithEpsilon<T>> firstSets = firstSets();
        Map<N, SetWithEpsilon<T>> followSets = new HashMap<>();

        for (N nonTerminal : getNonTerminals()) followSets.put(nonTerminal, new SetWithEpsilon<>());

        followSets.get(getStartSymbol()).setContainsEpsilon(true);

        boolean changed;

        do {
            changed = false;

            for (Prod production : this) {
                SententialForm<T, N> rhs = production.getRhs();

                for (int i = 0; i < rhs.size(); ++i) {
                    final int finalI = i; // java restriction

                    changed =
                            changed
                                    | rhs.get(i)
                                            .match(
                                                    t -> false,
                                                    n -> {
                                                        boolean changedd = false;
                                                        SetWithEpsilon<T> firstOfRemainder =
                                                                firstOfSententialForm(
                                                                        rhs.drop(finalI + 1),
                                                                        firstSets);

                                                        if (firstOfRemainder.containsEpsilon())
                                                            changedd =
                                                                    followSets
                                                                            .get(n)
                                                                            .addAll(
                                                                                    followSets.get(
                                                                                            production
                                                                                                    .getLhsNonTerminal()));

                                                        firstOfRemainder.setContainsEpsilon(false);

                                                        return changedd
                                                                | followSets
                                                                        .get(n)
                                                                        .addAll(firstOfRemainder);
                                                    });
                }
            }
        } while (changed);

        return followSets;
    }
}

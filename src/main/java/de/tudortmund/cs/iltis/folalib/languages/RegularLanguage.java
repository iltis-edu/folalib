package de.tudortmund.cs.iltis.folalib.languages;

import de.tudortmund.cs.iltis.folalib.automata.StateSupplier;
import de.tudortmund.cs.iltis.folalib.automata.finite.*;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFAExecutor;
import de.tudortmund.cs.iltis.folalib.automata.finite.conversion.DFAMinimizationConversion;
import de.tudortmund.cs.iltis.folalib.automata.finite.conversion.NFAOnlyReachableStatesConversion;
import de.tudortmund.cs.iltis.folalib.automata.finite.transformation.EpsilonNFAToRegularExpressionTransformation;
import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.expressions.regular.ToStandardRegexTransform;
import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.RightRegularGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.production.LeftRegularProduction;
import de.tudortmund.cs.iltis.folalib.grammar.production.RightRegularProduction;
import de.tudortmund.cs.iltis.folalib.languages.closure.*;
import de.tudortmund.cs.iltis.folalib.languages.closure.algorithms.*;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.folalib.transform.TransformGraph;
import de.tudortmund.cs.iltis.folalib.util.BinaryFunctions;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class RegularLanguage<S extends Serializable> implements Language<S> {
    private TransformGraph graph;

    /** For serialization */
    @SuppressWarnings("unused")
    private RegularLanguage() {}

    public RegularLanguage(RegularExpression<S> ere) {
        this(new TransformGraph(new Labels.RegularExpressionLabel<>(), ere));
    }

    public RegularLanguage(NFA<?, S> nfa) {
        this(new TransformGraph(new Labels.EpsilonNFALabel<>(), nfa));
    }

    /** For internal use only */
    private RegularLanguage(TransformGraph graph) {
        this.graph = graph;
        setupTransformGraph();
    }

    public RegularLanguage<S> complement() {
        return new RegularLanguage<>(
                new TransformGraph(new Labels.RegularComplementLabel<>(), new Complement<>(this)));
    }

    public RegularLanguage<S> concat(RegularLanguage<S> other) {
        return new RegularLanguage<>(
                new TransformGraph(
                        new Labels.RegularConcatenation<>(), new Concatenation<>(this, other)));
    }

    public RegularLanguage<S> difference(RegularLanguage<S> other) {
        return new RegularLanguage<>(
                new TransformGraph(
                        new Labels.RegularDifferenceLabel<>(), new Difference<>(this, other)));
    }

    public <T extends Serializable> RegularLanguage<T> homomorphism(
            SerializableFunction<S, Word<T>> homomorphism) {
        return new RegularLanguage<>(
                new TransformGraph(
                        new Labels.RegularHomomorphismLabel<>(),
                        new Homomorphism<>(this, homomorphism)));
    }

    public RegularLanguage<S> intersect(RegularLanguage<S> other) {
        return new RegularLanguage<>(
                new TransformGraph(
                        new Labels.RegularIntersectionLabel<>(), new Intersection<>(this, other)));
    }

    public <T extends Serializable> RegularLanguage<T> inverseHomomorphism(
            SerializableFunction<T, Word<S>> inverseHomomorphism, Alphabet<T> domain) {
        return new RegularLanguage<>(
                new TransformGraph(
                        new Labels.RegularInverseHomomorphismLabel<>(),
                        new InverseHomomorphism<>(
                                inverseHomomorphism, domain.toUnmodifiableSet(), this)));
    }

    public RegularLanguage<S> kleenePlus() {
        return new RegularLanguage<>(
                new TransformGraph(new Labels.RegularKleenePlusLabel<>(), new KleenePlus<>(this)));
    }

    public RegularLanguage<S> kleeneStar() {
        return new RegularLanguage<>(
                new TransformGraph(new Labels.RegularKleeneStarLabel<>(), new KleeneStar<>(this)));
    }

    public RegularLanguage<S> reverse() {
        return new RegularLanguage<>(
                new TransformGraph(new Labels.RegularReversalLabel<>(), new Reversal<>(this)));
    }

    public RegularLanguage<S> union(RegularLanguage<S> other) {
        return new RegularLanguage<>(
                new TransformGraph(new Labels.RegularUnionLabel<>(), new Union<>(this, other)));
    }

    public RegularLanguage<S> symmetricDifference(RegularLanguage<S> other) {
        return new RegularLanguage<>(
                new TransformGraph(
                        new Labels.RegularSymmetricDifferenceLabel<>(),
                        new SymmetricDifference<>(this, other)));
    }

    /**
     * Returns one random shortest word of all shortest words of this language.
     *
     * <p>If you need the actual smallest word of this language (according to a given {@link
     * Comparator}), consider using {@link #getSmallestWord(Comparator)} instead. Be aware that this
     * method may have a worse runtime.
     *
     * @return A random {@link Word} of all the shortest words of this language
     */
    public Word<S> getRandomShortestWord() {

        // Use NFA to avoid any epsilon-transitions which would intervene with the traversal
        NFA<? extends Serializable, S> nfa = this.getNFA().removeEpsilonTransitions();
        // Only one initial state to execute the traversal correctly
        // This conversion introduces epsilon-transitions only from the initial state to other
        // states, i.e. this is no problem for the traversal-logic
        NFA<Serializable, S> preparedNfa =
                nfa.onlyOneInitialState()
                        .mapStates(s -> (Serializable) s); // Mapping to Serializable
        // Only reachable states to avoid failure in backtracking later
        preparedNfa = new NFAOnlyReachableStatesConversion<Serializable, S>().apply(preparedNfa);

        Serializable initialState = preparedNfa.getInitialStates().iterator().next();

        Map<Serializable, Pair<Serializable, S>> predecessorsMap =
                preparedNfa
                        .asGraph(state -> state, NFATransition::getSymbol)
                        .breadthFirstTraversal(
                                initialState,
                                v -> true,
                                e -> true,
                                null); // No edge prioritization because it makes no sense on NFAs
        // and can fail if the NFA contains epsilon-transitions

        List<Word<S>> shortestWords =
                getShortestWordsFromTraversalTree(
                        predecessorsMap, preparedNfa.getAcceptingStates(), initialState);
        if (shortestWords.isEmpty()) return null; // The language is empty

        return Collections.min(
                shortestWords, Word.getWordComparator(null)); // All symbols are considered equal
    }

    /**
     * Returns the smallest word of this language.
     *
     * <p>The returned word is the smallest word of all possible shortest words. To determine this,
     * the given {@link Comparator} is used, which induces an order onto the alphabet used in this
     * language.
     *
     * <p>To determine the smallest word, we have to get this language as a {@link DFA}. An {@link
     * NFA} is not sufficient! If you only need a random shortest word of this language, which is
     * not necessarily the smallest, use {@link #getRandomShortestWord()} as this can use an {@link
     * NFA} (performance improvement).
     *
     * <p>For a full explanation why an NFA does not suffice see <a
     * href="https://ls1-gitlab.cs.tu-dortmund.de/ILTIS/FoLaLib/-/merge_requests/67#note_16263">here</a>.
     *
     * @param symComparator Used to induce an order onto the alphabet to determine the actual
     *     smallest word of the language. If {@code <Sym>} implements {@link Comparable} you can use
     *     {@link Comparator#naturalOrder()}. If the symbols do not have any order, use {@link
     *     #getRandomShortestWord()}.
     * @return The smallest {@link Word} of this language
     * @throws NullPointerException if {@code symComparator} is {@code null}
     */
    public Word<S> getSmallestWord(final Comparator<S> symComparator) {
        Objects.requireNonNull(symComparator);

        // NFA does not suffice!
        NFA<MaybeGenerated<Serializable, Integer>, S> dfa =
                getDFA().mapStates(s -> (Serializable) s).totalify();
        // Only reachable states to avoid failure in backtracking later
        dfa =
                new NFAOnlyReachableStatesConversion<MaybeGenerated<Serializable, Integer>, S>()
                        .apply(dfa);

        Serializable initialState = dfa.getInitialStates().iterator().next();

        Map<Serializable, Pair<Serializable, S>> predecessorsMap =
                dfa.asGraph(s -> (Serializable) s, NFATransition::getSymbol)
                        .breadthFirstTraversal(initialState, v -> true, e -> true, symComparator);

        List<Word<S>> shortestWords =
                getShortestWordsFromTraversalTree(
                        predecessorsMap,
                        dfa.getAcceptingStates().stream()
                                .map(m -> (Serializable) m)
                                .collect(Collectors.toSet()),
                        initialState);
        if (shortestWords.isEmpty()) return null; // The language is empty

        return Collections.min(shortestWords, Word.getWordComparator(symComparator));
    }

    /**
     * A helper method for {@link #getRandomShortestWord()} and {@link
     * #getSmallestWord(Comparator)}.
     */
    private List<Word<S>> getShortestWordsFromTraversalTree(
            Map<Serializable, Pair<Serializable, S>> predecessorsMap,
            Set<Serializable> acceptingStates,
            Serializable initialState) {
        List<Word<S>> shortestWords = new LinkedList<>();
        for (Serializable currentAccepting : acceptingStates) {
            ArrayList<S> currentWord = new ArrayList<>();
            Serializable currentBacktrackingVertex = currentAccepting;

            while (currentBacktrackingVertex != initialState) {
                S edgeSymbol = predecessorsMap.get(currentBacktrackingVertex).second();
                if (edgeSymbol
                        != null) // There can be one epsilon transition from the initial state (only
                    // in #getRandomShortestWord). We can ignore this
                    currentWord.add(0, edgeSymbol);
                currentBacktrackingVertex = predecessorsMap.get(currentBacktrackingVertex).first();
            }

            shortestWords.add(new Word<>(currentWord));
        }

        return shortestWords;
    }

    private static <T extends Serializable, N extends Serializable>
            NFA<MaybeGenerated<N, Integer>, T> rightLinearGrammarToNFA(
                    Grammar<T, N, RightRegularProduction<T, N>> grammar) {
        NFABuilder<MaybeGenerated<N, Integer>, T> builder =
                new NFABuilder<MaybeGenerated<N, Integer>, T>(grammar.getTerminals())
                        .withInitial(new MaybeGenerated.Input<>(grammar.getStartSymbol()))
                        .withAccepting(new MaybeGenerated.Generated<>(0));

        for (N nonTerminal : grammar.getNonTerminals())
            builder.withStates(new MaybeGenerated.Input<>(nonTerminal));

        for (RightRegularProduction<T, N> production : grammar) {
            SententialForm<T, N> rhs = production.getRhs();

            if (rhs.isEmpty())
                builder.withAccepting(new MaybeGenerated.Input<>(production.getLhsNonTerminal()));

            if (rhs.size() == 1) {
                builder.withTransition(
                        new MaybeGenerated.Input<>(production.getLhsNonTerminal()),
                        production.getRhsTerminal().orElse(null),
                        new MaybeGenerated.Generated<>(0));
            }

            if (rhs.size() == 2) {
                builder.withTransition(
                        new MaybeGenerated.Input<>(production.getLhsNonTerminal()),
                        production.getRhsTerminal().orElse(null),
                        new MaybeGenerated.Input<>(production.getRhsNonTerminal().orElse(null)));
            }
        }

        return builder.buildAndReset().unwrap();
    }

    private static <T extends Serializable, N extends Serializable>
            NFA<MaybeGenerated<N, Integer>, T> leftLinearGrammarToNFA(
                    Grammar<T, N, LeftRegularProduction<T, N>> grammar) {
        MaybeGenerated<N, Integer> initial = new MaybeGenerated.Generated<>(0);
        MaybeGenerated<N, Integer> accepting = new MaybeGenerated.Input<>(grammar.getStartSymbol());

        NFABuilder<MaybeGenerated<N, Integer>, T> builder =
                new NFABuilder<MaybeGenerated<N, Integer>, T>(grammar.getTerminals())
                        .withInitial(initial)
                        .withAccepting(accepting);

        for (N nonTerminal : grammar.getNonTerminals())
            builder.withStates(new MaybeGenerated.Input<>(nonTerminal));

        for (LeftRegularProduction<T, N> production : grammar) {
            SententialForm<T, N> rhs = production.getRhs();

            if (rhs.isEmpty()) {
                builder.withEpsilonTransition(
                        initial, accepting); // S -> eps is the only allowed epsilon production!
            }

            if (rhs.size() == 1) {
                builder.withTransition(
                        initial,
                        production.getRhsTerminal().orElse(null),
                        new MaybeGenerated.Input<>(production.getLhsNonTerminal()));
            }

            if (rhs.size() == 2) {
                builder.withTransition(
                        new MaybeGenerated.Input<>(production.getRhsNonTerminal().orElse(null)),
                        production.getRhsTerminal().orElse(null),
                        new MaybeGenerated.Input<>(production.getLhsNonTerminal()));
            }
        }

        return builder.buildAndReset().unwrap();
    }

    /**
     * This method transforms a given NFA in an equivalent regular grammar. The NFA gets
     * determinized first (using {@link NFA#determinize()}), hence the return type {@code
     * LinkedHashSet<T>}.
     *
     * <p>This algorithm is taken from TI lecture (Buchin), slide 57
     *
     * @param nfa The NFA to be transformed
     * @param <T> The type of states used in {@param nfa}
     * @param <S> The type of the alphabet used in {@param nfa}
     * @return The computed regular grammar
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static <T extends Serializable, S extends Serializable>
            Grammar<S, LinkedHashSet<T>, RightRegularProduction<S, LinkedHashSet<T>>>
                    computeRegularGrammarFromNFA(NFA<T, S> nfa) {

        // Determinize first because a new initial state must possibly be generated.
        NFA<LinkedHashSet<T>, S> deterministicNfa = nfa.determinize();

        RightRegularGrammarBuilder<S, LinkedHashSet<T>> builder =
                new RightRegularGrammarBuilder<>(
                        deterministicNfa.getAlphabet(),
                        new Alphabet<>(deterministicNfa.getStates()));
        builder.withStartSymbol(deterministicNfa.getInitialStates().stream().findFirst().get());

        // Side note on slide 57
        if (deterministicNfa
                .getAcceptingStates()
                .contains(
                        deterministicNfa.getInitialStates().stream()
                                .findFirst()
                                .get())) { // .get() is fine because we determinized previously
            builder.withProduction(new RightRegularProduction<>(builder.getStartSymbol()));
        }

        for (LinkedHashSet<T> state : deterministicNfa.getStates()) {
            for (S symbol : deterministicNfa.getAlphabet()) {
                // Because we determinized previously, the set only contains one entry. This loop
                // below ALWAYS iterates only once.
                // It is solved with a for loop to avoid a hideous extraction from the set.
                for (LinkedHashSet<T> reachable : deterministicNfa.reachableWith(state, symbol)) {
                    builder.withProduction(state, symbol, reachable);

                    if (deterministicNfa.getAcceptingStates().contains(reachable))
                        builder.withProduction(state, symbol);
                }
            }
        }

        return builder.buildAndReset().unwrap();
    }

    private void setupTransformGraph() {
        /* !!! IMPORTANT !!!
         * DO NOT replace these references to static transformation classes by method references (as ::method).
         * The GWT compiler will stop understanding the code (even though the standard Java compiler still accepts it).
         * Also, the operations need to be serializable. Thus, either use a lengthy conversion of a lambda expression
         * into a SerializableFunction, e.g.
         * (SerializableFunction<NFA<? extends Serializable,S>, NFA<? extends Serializable,S>>) (nfa -> nfa.determinize()))
         * or use a wrapper transformation class (as done here).
         * ### IMPORTANT ###
         */
        graph.registerTransform(
                new Labels.RegularExpressionLabel<>(),
                new Labels.StandardRegularExpressionLabel<>(),
                new RegularExpressionToStandardRegularExpressionTransform<>());
        graph.registerTransform(
                new Labels.StandardRegularExpressionLabel<S>(),
                new Labels.EpsilonNFALabel<>(),
                new StandardRegularExpressionToEpsilonNFATransform<>());
        graph.registerTransform(
                new Labels.EpsilonNFALabel<S>(),
                new Labels.DeterministicNFALabel<>(),
                new EpsilonNFAToDFATransform<>());
        graph.registerTransform(
                new Labels.EpsilonNFALabel<>(),
                new Labels.RegularExpressionLabel<>(),
                new EpsilonNFAToRegularExpressionTransformation<>());
        graph.registerTransform(
                new Labels.DeterministicNFALabel<>(),
                new Labels.EpsilonNFALabel<>(),
                new DFAToEpsilonNFATransform<>());
        //      graph.registerTransform(new Labels.RightRegularGrammarLabel<S>(), new
        // Labels.EpsilonNFALabel<>(),
        //								grammar -> rightLinearGrammarToNFA(grammar));
        //		graph.registerTransform(new Labels.EpsilonNFALabel<S>(), new
        // Labels.RightRegularGrammarLabel<>(),
        //								nfa -> computeRegularGrammarFromNFA(nfa));
        graph.registerTransform(
                new Labels.RegularComplementLabel<>(),
                new Labels.DeterministicNFALabel<>(),
                new RegularComplementToDFA<>());
        graph.registerTransform(
                new Labels.RegularConcatenation<>(),
                new Labels.EpsilonNFALabel<>(),
                new RegularConcatenationToEpsilonNFA<>());
        graph.registerTransform(
                new Labels.RegularConcatenation<>(),
                new Labels.RegularExpressionLabel<>(),
                new RegularConcatenationToRegularExpression<>());
        graph.registerTransform(
                new Labels.RegularDifferenceLabel<>(),
                new Labels.EpsilonNFALabel<>(),
                new RegularDifferenceToEpsilonNFA<>());
        graph.registerTransform(
                new Labels.RegularHomomorphismLabel<>(),
                new Labels.RegularExpressionLabel<>(),
                new RegularHomomorphismToRegularExpression<>());
        graph.registerTransform(
                new Labels.RegularIntersectionLabel<>(),
                new Labels.EpsilonNFALabel<>(),
                new RegularIntersectionToEpsilonNFA<>());
        graph.registerTransform(
                new Labels.RegularInverseHomomorphismLabel<>(),
                new Labels.EpsilonNFALabel<>(),
                new RegularInverseHomomorphismToEpsilonNFA<>());
        graph.registerTransform(
                new Labels.RegularKleenePlusLabel<>(),
                new Labels.EpsilonNFALabel<>(),
                new RegularKleenePlusToEpsilonNFA<>());
        graph.registerTransform(
                new Labels.RegularKleenePlusLabel<>(),
                new Labels.RegularExpressionLabel<>(),
                new RegularKleenePlusToRegularExpression<>());
        graph.registerTransform(
                new Labels.RegularKleeneStarLabel<>(),
                new Labels.EpsilonNFALabel<>(),
                new RegularKleeneStarToEpsilonNFA<>());
        graph.registerTransform(
                new Labels.RegularKleeneStarLabel<>(),
                new Labels.RegularExpressionLabel<>(),
                new RegularKleeneStarToRegularExpression<>());
        graph.registerTransform(
                new Labels.RegularReversalLabel<>(),
                new Labels.EpsilonNFALabel<>(),
                new RegularReversalToEpsilonNFA<>());
        graph.registerTransform(
                new Labels.RegularReversalLabel<>(),
                new Labels.RegularExpressionLabel<>(),
                new RegularReversalToRegularExpression<>());
        graph.registerTransform(
                new Labels.RegularUnionLabel<>(),
                new Labels.EpsilonNFALabel<>(),
                new RegularUnionToEpsilonNFA<>());
        graph.registerTransform(
                new Labels.RegularUnionLabel<>(),
                new Labels.RegularExpressionLabel<>(),
                new RegularUnionToRegularExpression<>());
        graph.registerTransform(
                new Labels.RegularSymmetricDifferenceLabel<>(),
                new Labels.DeterministicNFALabel<>(),
                new RegularSymmetricDifferenceToDFA<>());
    }

    /**
     * Creates a set of languages in which each language represents one state of the minimized
     * Nerode-DFA constructed from {@code this} language.
     *
     * @return A set of all equivalence classes of {@code this} language
     */
    @SuppressWarnings("unchecked")
    public Set<RegularLanguage<S>> getEquivalenceClasses() {
        // Minimize DFA
        NFA<LinkedHashSet<Serializable>, S> minimizedDfa =
                new DFAMinimizationConversion<Serializable, S>()
                        .apply(
                                (NFA<Serializable, S>)
                                        getDFA()); // Every state of an NFA is always serializable

        // Create a DFA for each state of the minimized DFA and sets the respective state as the
        // only accepting state
        Set<RegularLanguage<S>> equivalenceClasses = new LinkedHashSet<>();
        for (LinkedHashSet<Serializable> state : minimizedDfa.getReachableStates()) {
            NFA<LinkedHashSet<Serializable>, S> equivalenceClassDFA =
                    new NFABuilder<>(minimizedDfa).overrideAccepting(state).build().unwrap();
            equivalenceClasses.add(new RegularLanguage<>(equivalenceClassDFA));
        }
        return equivalenceClasses;
    }

    /**
     * Retrieves the alphabet this language is defined over.
     *
     * @return The underlying alphabet
     */
    @Override
    public Alphabet<S> getAlphabet() {
        return getNFA().getAlphabet();
    }

    public RegularExpression<S> getRegularExpression() {
        return graph.get(new Labels.RegularExpressionLabel<>());
    }

    public NFA<? extends Serializable, S> getNFA() {
        return graph.get(new Labels.EpsilonNFALabel<>());
    }

    public NFA<? extends Serializable, S> getDFA() {
        return graph.get(new Labels.DeterministicNFALabel<>());
    }

    @Override
    public boolean contains(Word<S> word) {
        NFA<?, S> nfa = this.getNFA();
        NFAExecutor<?, S> exec = new NFAExecutor<>(nfa, word);
        return exec.run();
    }

    public boolean isEqualTo(RegularLanguage<S> other) {
        NFA<?, S> lhs = getDFA();
        NFA<?, S> rhs = other.getDFA();

        Alphabet<S> unionAlphabet = Alphabets.unionOf(lhs.getAlphabet(), rhs.getAlphabet());

        return lhs.totalifyWithRegardTo(unionAlphabet)
                .product(rhs.totalifyWithRegardTo(unionAlphabet), BinaryFunctions.XOR)
                .isEmpty();
    }

    public String toString() {
        return getRegularExpression().toString();
    }

    public static class RegularExpressionToStandardRegularExpressionTransform<
                    S extends Serializable>
            implements SerializableFunction<RegularExpression<S>, RegularExpression<S>> {

        public RegularExpressionToStandardRegularExpressionTransform() {}

        @Override
        public RegularExpression<S> apply(RegularExpression<S> sRegularExpression) {
            return ToStandardRegexTransform.toStandard(sRegularExpression);
        }
    }

    public static class StandardRegularExpressionToEpsilonNFATransform<S extends Serializable>
            implements SerializableFunction<RegularExpression<S>, NFA<? extends Serializable, S>> {
        @Override
        public NFA<Integer, S> apply(RegularExpression<S> sRegularExpression) {
            return sRegularExpression.toNFA(StateSupplier.integerStateSupplier());
        }
    }

    public static class EpsilonNFAToDFATransform<S extends Serializable>
            implements SerializableFunction<
                    NFA<? extends Serializable, S>, NFA<? extends Serializable, S>> {

        @Override
        public NFA<? extends LinkedHashSet<?>, S> apply(NFA<?, S> tsnfa) {
            return tsnfa.determinize();
        }
    }

    public static class DFAToEpsilonNFATransform<S extends Serializable>
            implements SerializableFunction<
                    NFA<? extends Serializable, S>, NFA<? extends Serializable, S>> {

        @Override
        public NFA<? extends Serializable, S> apply(NFA<?, S> tsnfa) {
            return tsnfa;
        }
    }
}

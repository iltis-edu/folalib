package de.tudortmund.cs.iltis.folalib.languages;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDA;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.transformation.CFGToPDATransformation;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.transformation.PDAToCFGTransformation;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ToChomskyNormalFormTransform;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk.CorrectCYKTableau;
import de.tudortmund.cs.iltis.folalib.grammar.production.ChomskyNormalformProduction;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.io.writer.cfg.JSONStyleGrammarWriter;
import de.tudortmund.cs.iltis.folalib.transform.ConstrainedSupplier;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.folalib.transform.TransformGraph;
import de.tudortmund.cs.iltis.folalib.util.ToIntegersHomomorphism;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * Represents a ContextFreeLanguage abstractly and independently of a concrete representation such
 * as a PDA or a CFG.
 *
 * @param <S> The type of the alphabet of the words in this language
 */
public class ContextFreeLanguage<S extends Serializable> implements Language<S> {
    private TransformGraph graph;

    /**
     * Create a new ContextFreeLanguage based on the given PDA
     *
     * @param pda the PDA which determines the language
     */
    public <T extends Serializable, K extends Serializable> ContextFreeLanguage(PDA<T, S, K> pda) {
        ToIntegersHomomorphism<T> stateHomomorphism = new ToIntegersHomomorphism<>(pda.getStates());
        ToIntegersHomomorphism<K> stackAlphabetHomomorphism =
                new ToIntegersHomomorphism<>(pda.getStackAlphabet().toUnmodifiableSet());
        PDA<Integer, S, Integer> mappedPda =
                pda.mapStates(stateHomomorphism).mapStackAlphabet(stackAlphabetHomomorphism);
        graph = new TransformGraph(new Labels.PDALabel<>(), mappedPda);
        setupGraph();
    }

    /**
     * Create a new ContextFreeLanguage based on the given CFG
     *
     * @param cfg the CFG which determines the language
     * @param <N> The type of the non-terminals of the given CFG. We need this type parameter,
     *     because `N` appears twice in the Signature of ContextFreeGrammar. If we used `?` instead,
     *     the compiler would not know at declaration site (aka here) that the first `?` and the
     *     second `?` are indeed the same. However, all algorithms we implemented
     *     (PDATransform.CFGToPDA, ToChomskyNormalFormTransform, ...) require both arguments to be
     *     equal. `N` is a method local type parameter for now because we only use it here and I
     *     don't like ContextFreeLanguage objects which represent a language independently of a
     *     CFG/PDA to depend on the type of non-terminals of some CFG.
     */
    public <N extends Serializable> ContextFreeLanguage(
            ContextFreeGrammar<S, N, ContextFreeProduction<S, N>> cfg) {
        ToIntegersHomomorphism<N> nonTerminalsHomomorphism =
                new ToIntegersHomomorphism<>(cfg.getNonTerminals().toUnmodifiableSet());
        ContextFreeGrammar<S, Integer, ? extends ContextFreeProduction<S, Integer>> mappedCfg =
                cfg.mapNonTerminals(nonTerminalsHomomorphism);
        ContextFreeGrammar<S, String, ? extends ContextFreeProduction<S, String>> stringCfg =
                mappedCfg.mapNonTerminals(String::valueOf);
        graph = new TransformGraph(new Labels.CFGLabel<>(), stringCfg);
        setupGraph();
    }

    /**
     * Return a PDA which recognizes this ContextFreeLanguage
     *
     * @return a new PDA for this language
     */
    public PDA<Integer, S, Integer> getPDA() {
        return graph.get(new Labels.PDALabel<>());
    }

    /**
     * Return a CFG which recognizes this ContextFreeLanguage
     *
     * @return a new CFG for this language
     */
    public ContextFreeGrammar<S, String, ? extends ContextFreeProduction<S, String>> getCFG() {
        return graph.get(new Labels.CFGLabel<>());
    }

    /**
     * Return a CFG in ChomskyNormalForm which recognizes this ContextFreeLanguage
     *
     * @return a new CFG for this language
     */
    public ContextFreeGrammar<S, String, ? extends ChomskyNormalformProduction<S, String>>
            getCNF() {
        return graph.get(new Labels.CNFLabel<>());
    }

    /* Initialise the internal transformation graph */
    private void setupGraph() {
        graph.registerTransform(new Labels.CFGLabel<>(), new Labels.PDALabel<>(), new CFGToPDA<>());
        graph.registerTransform(new Labels.CFGLabel<>(), new Labels.CNFLabel<>(), new CFGToCNF<>());
        graph.registerTransform(new Labels.PDALabel<>(), new Labels.CFGLabel<>(), new PDAToCFG<>());
    }

    /* For serialization */
    @SuppressWarnings("unused")
    private ContextFreeLanguage() {}

    /**
     * Return the underlying alphabet of this language
     *
     * @return the alphabet
     */
    @Override
    public Alphabet<S> getAlphabet() {
        return graph.get(new Labels.PDALabel<Integer, S, Integer>()).getAlphabet();
    }

    @Override
    public boolean contains(Word<S> word) {
        if (word.isEmpty()) return getCFG().containsEmptyWord();
        ContextFreeGrammar<S, String, ChomskyNormalformProduction<S, String>> cnf =
                (ContextFreeGrammar<S, String, ChomskyNormalformProduction<S, String>>) getCNF();
        return CorrectCYKTableau.compute(cnf, word).accepts();
    }

    /**
     * Hopcroft, J. E., Motwani, R., & Ullman, J. D. (2001). Introduction to automata theory,
     * languages, and computation. Acm Sigact News, 32(1), 137.
     *
     * <p>A CFG describes a finite language iff the start symbol does not generate any string of
     * terminals.
     */
    public boolean isEmpty() {
        ContextFreeGrammar<S, String, ?> cfg = this.getCFG();
        return !cfg.generatingNonTerminals().contains(cfg.getStartSymbol());
    }

    public boolean isFinite() {
        return graph.get(new Labels.CFGLabel<>()).isFinite();
    }

    @Override
    public String toString() {
        return new JSONStyleGrammarWriter().write(getCFG());
    }

    /* Helper classes to prevent serialization issues with lambda expressions */
    public static class PDAToCFG<S extends Serializable>
            implements SerializableFunction<
                    PDA<Integer, S, Integer>,
                    ContextFreeGrammar<S, String, ? extends ContextFreeProduction<S, String>>> {

        @Override
        public ContextFreeGrammar<S, String, ? extends ContextFreeProduction<S, String>> apply(
                PDA<Integer, S, Integer> pda) {
            PDAToCFGTransformation<Integer, S, Integer> transformation =
                    new PDAToCFGTransformation<>();
            return transformation.transform(pda);
        }
    }

    public static class CFGToPDA<S extends Serializable>
            implements SerializableFunction<
                    ContextFreeGrammar<S, String, ? extends ContextFreeProduction<S, String>>,
                    PDA<Integer, S, Integer>> {

        @Override
        public PDA<Integer, S, Integer> apply(
                ContextFreeGrammar<S, String, ? extends ContextFreeProduction<S, String>> cfg) {
            CFGToPDATransformation<S, String> transformation = new CFGToPDATransformation<>();
            PDA<String, S, MaybeGenerated<S, String>> pda = transformation.transform(cfg);
            ToIntegersHomomorphism<String> stateHomomorphism =
                    new ToIntegersHomomorphism<>(pda.getStates());
            ToIntegersHomomorphism<MaybeGenerated<S, String>> stackSymbolHomomorphism =
                    new ToIntegersHomomorphism<>(pda.getStackAlphabet().toUnmodifiableSet());
            return pda.mapStates(stateHomomorphism).mapStackAlphabet(stackSymbolHomomorphism);
        }
    }

    public static class CFGToCNF<S extends Serializable>
            implements SerializableFunction<
                    ContextFreeGrammar<S, String, ? extends ContextFreeProduction<S, String>>,
                    ContextFreeGrammar<
                            S, String, ? extends ChomskyNormalformProduction<S, String>>> {

        @Override
        public ContextFreeGrammar<S, String, ? extends ChomskyNormalformProduction<S, String>>
                apply(
                        ContextFreeGrammar<S, String, ? extends ContextFreeProduction<S, String>>
                                cfg) {
            ConstrainedSupplier<String> supplier = ConstrainedSupplier.constrainedStringSupplier();
            return ToChomskyNormalFormTransform.convertToCnf(cfg, supplier);
        }
    }
}

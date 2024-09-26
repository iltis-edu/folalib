package de.tudortmund.cs.iltis.folalib.automata.pushdown.transformation;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.*;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.conversion.*;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGeneratedDeepFold;
import java.io.Serializable;

/**
 * This class encapsulates the transformation from a PDA to a CFG
 *
 * <p>The algorithm is taken from GTI lectures, SS 18, slide 359
 *
 * @param <T> The type of the states of the PDA
 * @param <S> The type of the input alphabet of the PDA and the resulting CFG
 * @param <K> The type of the stack alphabet of the PDA
 */
public class PDAToCFGTransformation<
        T extends Serializable, S extends Serializable, K extends Serializable> {

    private final String startSymbol = "S";

    private PDA<MaybeGenerated<T, String>, S, MaybeGenerated<K, String>> pda;
    private ContextFreeGrammarBuilder<S, String> builder;

    /**
     * Transform a PDA into an equivalent CFG.
     *
     * @param pda The PDA to transform
     * @return a new, equivalent CFG
     */
    public ContextFreeGrammar<S, String, ContextFreeProduction<S, String>> transform(
            PDA<T, S, K> pda) {

        preparePDA(pda);
        setupBuilder();
        addStartProduction();
        addProductions();
        return builder.build().unwrap();
    }

    private void preparePDA(PDA<T, S, K> pda) {
        PDAConversion<T, MaybeGenerated<T, String>, S, K, K> conversion0 =
                new PDAMultipleInitialStatesToOnlyOneInitialStateConversion<>();
        PDAConversion<
                        MaybeGenerated<T, String>,
                        MaybeGenerated<MaybeGenerated<T, String>, String>,
                        S,
                        K,
                        MaybeGenerated<K, String>>
                conversion1 = new PDAAcceptingStatesToEmptyStackConversion<>();
        PDAConversion<
                        MaybeGenerated<MaybeGenerated<T, String>, String>,
                        MaybeGenerated<MaybeGenerated<MaybeGenerated<T, String>, String>, String>,
                        S,
                        MaybeGenerated<K, String>,
                        MaybeGenerated<K, String>>
                conversion2 = new PDAWithMaximalTwoWritesToStackPerTransitionConversion<>();
        PDAConversion<
                        MaybeGenerated<MaybeGenerated<MaybeGenerated<T, String>, String>, String>,
                        MaybeGenerated<MaybeGenerated<MaybeGenerated<T, String>, String>, String>,
                        S,
                        MaybeGenerated<K, String>,
                        MaybeGenerated<K, String>>
                conversion3 = new PDANoStackSymbolWildcardsOnTransitionsConversion<>();
        PDA<
                        MaybeGenerated<MaybeGenerated<MaybeGenerated<T, String>, String>, String>,
                        S,
                        MaybeGenerated<K, String>>
                tempPda =
                        conversion0
                                .andThen(conversion1)
                                .andThen(conversion2)
                                .andThen(conversion3)
                                .apply(pda);

        // Remove MaybeGenerated nesting
        this.pda =
                tempPda.mapStates((state) -> MaybeGeneratedDeepFold.deepFold(state, this::helper));

        /* These conditions must hold, otherwise our conversions are broken */
        if (this.pda
                .getTransitions()
                .exists((state, trans) -> trans.getNewTopOfStack().size() > 2)) {
            throw new RuntimeException(
                    "Fatal internal error: the constructed PDA does not satisfy the precondition: |newTopOfStack| <= 2 for all transitions");
        } else if (this.pda.getTransitions().exists((state, trans) -> trans.containsWildcard())) {
            throw new RuntimeException(
                    "Fatal internal error: the constructed PDA does not satisfy the precondition: every transition is concrete (i.e. no wildcards)");
        } else if (this.pda
                .getTransitions()
                .exists((state, trans) -> trans.getStackSymbol().isVariable())) {
            throw new RuntimeException(
                    "Fatal internal error: the constructed PDA does not satisfy the precondition: no stack symbol may be a variable (e.g. wildcard)");
        } else if (this.pda.getInitialStates().size() > 1) {
            throw new RuntimeException(
                    "Fatal internal error: the constructed PDA does not satisfy the precondition: no multiple initial states.");
        }
    }

    // Helper method for MaybeGenerated unfold
    private MaybeGenerated<T, String> helper(String path, Object result) {
        if (path.matches(
                "0+")) { // path of only zero's indicates result was found at In{In{In{... x ...}}}
            return new MaybeGenerated.Input<>((T) result); // We know that this must result in T
        } else {
            return new MaybeGenerated.Generated<>(
                    "MGLayer" + (path.length() - 1) + "_" + result.toString());
        }
    }

    private void setupBuilder() {
        Alphabet<S> terminals = pda.getAlphabet();
        Alphabet<String> nonTerminals = new Alphabet<>(startSymbol);

        builder = new ContextFreeGrammarBuilder<>(terminals, nonTerminals);
    }

    private void addStartProduction() {
        builder.withStartSymbol(startSymbol);
        for (MaybeGenerated<T, String> state : pda.getStates()) {
            String nonTerminal =
                    productionName(
                            pda.getInitialStates().stream().findFirst().get(),
                            pda.getInitialStackSymbol(),
                            state); // The input PDA does not have multiple initial states, so this
            // is fine.
            builder.withNonTerminal(nonTerminal);
            builder.withProduction(startSymbol).nt(nonTerminal).finish();
        }
    }

    private void addProductions() {
        MaybeGenerated<T, String> q;
        S alpha;
        MaybeGenerated<K, String> tau;
        PDAStackWord<MaybeGenerated<K, String>> word;
        PDAStackSymbol<MaybeGenerated<K, String>> tmp;

        for (MaybeGenerated<T, String> p : pda.getStates()) {
            for (PDATransition<MaybeGenerated<T, String>, S, MaybeGenerated<K, String>> transition :
                    pda.getTransitions().in(p)) {
                q = transition.getState();
                alpha = transition.getInputSymbol();
                tmp = transition.getStackSymbol();
                /* The cast must always succeed, otherwise `preparePDA` is broken */
                tau = ((PDAStackSymbol.Exactly<MaybeGenerated<K, String>>) tmp).getSymbol();
                word = transition.getNewTopOfStack();

                addProduction(p, alpha, tau, q, word);
            }
        }
    }

    private void addProduction(
            MaybeGenerated<T, String> p,
            S alpha,
            MaybeGenerated<K, String> tau,
            MaybeGenerated<T, String> q,
            PDAStackWord<MaybeGenerated<K, String>> taus) {

        if (taus.isEmpty()) {
            addProductionCase0(p, alpha, tau, q, taus);
        } else if (taus.size() == 1) {
            addProductionCase1(p, alpha, tau, q, taus);
        } else if (taus.size() == 2) {
            addProductionCase2(p, alpha, tau, q, taus);
        } else {
            /* This must never be executed, otherwise `preparePDA` is broken */
            throw new RuntimeException(
                    "Unreachable: |newTopOfStack| <= 2 must be satisfied by all transitions");
        }
    }

    /** Implementation of the first row in the table in slide 360 of GTI SS 18 lecture slides */
    private void addProductionCase0(
            MaybeGenerated<T, String> p,
            S alpha,
            MaybeGenerated<K, String> tau,
            MaybeGenerated<T, String> q,
            PDAStackWord<MaybeGenerated<K, String>> taus) {
        String nonTerminal = productionName(p, tau, q);
        builder.withNonTerminal(nonTerminal);
        if (alpha == null) {
            /* transition is an epsilon transition */
            builder.withEpsProduction(nonTerminal);
        } else {
            builder.withProduction(nonTerminal).t(alpha).finish();
        }
    }

    /** Implementation of the middle row in the table in slide 360 of GTI SS 18 lecture slides */
    private void addProductionCase1(
            MaybeGenerated<T, String> p,
            S alpha,
            MaybeGenerated<K, String> tau,
            MaybeGenerated<T, String> q,
            PDAStackWord<MaybeGenerated<K, String>> taus) {
        for (MaybeGenerated<T, String> p1 : pda.getStates()) {
            String nonTerminal = productionName(p, tau, p1);
            builder.withNonTerminal(nonTerminal);
            if (alpha == null) {
                /* transition is an epsilon transition */
                builder.withNonTerminal(productionName(q, taus.get(0), p1));
                builder.withProduction(nonTerminal).nt(productionName(q, taus.get(0), p1)).finish();
            } else {
                builder.withNonTerminal(productionName(q, taus.get(0), p1));
                builder.withProduction(nonTerminal)
                        .t(alpha)
                        .nt(productionName(q, taus.get(0), p1))
                        .finish();
            }
        }
    }

    /** Implementation of the last row in the table in slide 360 of GTI SS 18 lecture slides */
    private void addProductionCase2(
            MaybeGenerated<T, String> p,
            S alpha,
            MaybeGenerated<K, String> tau,
            MaybeGenerated<T, String> q,
            PDAStackWord<MaybeGenerated<K, String>> taus) {
        for (MaybeGenerated<T, String> p1 : pda.getStates()) {
            for (MaybeGenerated<T, String> p2 : pda.getStates()) {
                String nonTerminal = productionName(p, tau, p2);
                builder.withNonTerminal(nonTerminal);
                if (alpha == null) {
                    /* transition is an epsilon transition */
                    builder.withNonTerminal(productionName(q, taus.get(0), p1));
                    builder.withNonTerminal(productionName(p1, taus.get(1), p2));
                    builder.withProduction(nonTerminal)
                            .nt(productionName(q, taus.get(0), p1))
                            .nt(productionName(p1, taus.get(1), p2))
                            .finish();
                } else {
                    builder.withNonTerminal(productionName(q, taus.get(0), p1));
                    builder.withNonTerminal(productionName(p1, taus.get(1), p2));
                    builder.withProduction(nonTerminal)
                            .t(alpha)
                            .nt(productionName(q, taus.get(0), p1))
                            .nt(productionName(p1, taus.get(1), p2))
                            .finish();
                }
            }
        }
    }

    private String productionName(
            MaybeGenerated<T, String> p, MaybeGenerated<K, String> k, MaybeGenerated<T, String> q) {
        return "X_" + p + "," + k + "," + q;
    }
}

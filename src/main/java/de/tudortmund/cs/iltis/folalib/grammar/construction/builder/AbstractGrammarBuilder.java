package de.tudortmund.cs.iltis.folalib.grammar.construction.builder;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.*;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.util.Result;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractGrammarBuilder<
        T extends Serializable,
        N extends Serializable,
        Prod extends Production<T, N>,
        G extends Grammar<T, N, Prod>,
        Self> {

    protected Set<T> terminals;
    protected Set<N> nonTerminals;
    protected N startSymbol;
    protected Set<Prod> productions;
    protected MakeGrammar<
                    Collection<? extends T>,
                    Collection<? extends N>,
                    N,
                    Collection<? extends Prod>,
                    G>
            make;

    protected AbstractGrammarBuilder(
            Alphabet<? extends T> terminals,
            Alphabet<? extends N> nonTerminals,
            MakeGrammar<
                            Collection<? extends T>,
                            Collection<? extends N>,
                            N,
                            Collection<? extends Prod>,
                            G>
                    make) {
        this.terminals = new LinkedHashSet<>(terminals.toUnmodifiableSet());
        this.nonTerminals = new LinkedHashSet<>(nonTerminals.toUnmodifiableSet());
        this.productions = new LinkedHashSet<>();
        this.make = make;
    }

    public Self withProduction(Prod production) {
        productions.add(production);
        return (Self) this;
    }

    /**
     * Adds a new non-terminal to the grammar to be constructed.
     *
     * @param newNonTerminal The new non-terminal
     * @return {@code this} for method chaining
     */
    public Self withNonTerminal(N newNonTerminal) {
        nonTerminals.add(newNonTerminal);
        return (Self) this;
    }

    /**
     * Removes all productions that use the given non-terminal as well as the non-terminal itself
     *
     * @param nonTerminal The non-terminal whose occurrences to eliminate
     * @return {@code this} for method chaining
     */
    public Self eliminateNonTerminal(N nonTerminal) {
        productions.removeIf(
                p ->
                        p.getLhs().containsNonTerminal(nonTerminal)
                                || p.getRhs().containsNonTerminal(nonTerminal));
        nonTerminals.remove(nonTerminal);
        return (Self) this;
    }

    public Self withStartSymbol(N startSymbol) {
        this.startSymbol = startSymbol;
        return (Self) this;
    }

    public final N getStartSymbol() {
        return startSymbol;
    }

    protected void reset() {
        startSymbol = null;
        productions.clear();
    }

    /**
     * This is the basis for every correct grammar. It will check ... ... if a start symbol is set
     * {@link GrammarConstructionFaultReason#MISSING_START_SYMBOL}, ... if the start symbol is
     * included in the non-terminal alphabet {@link
     * GrammarConstructionFaultReason#UNKNOWN_NONTERMINAL}, ... if all used terminals occur in the
     * terminals alphabet {@link GrammarConstructionFaultReason#UNKNOWN_TERMINAL}, ... if all used
     * non-terminals occur in the non-terminals alphabet {@link
     * GrammarConstructionFaultReason#UNKNOWN_NONTERMINAL}, ... if both alphabets are disjoint
     * {@link GrammarConstructionFaultReason#ALPHABETS_NOT_DISJOINT}, ... if every production's LHS
     * contains at least one symbol (terminal or non-terminal) {@link
     * GrammarConstructionFaultReason#EMPTY_LHS}.
     *
     * @return A collection of all faults that were found.
     */
    public GrammarConstructionFaultCollection validate() {
        List<Fault<GrammarConstructionFaultReason>> faults = new ArrayList<>();

        if (startSymbol == null) faults.add(new NoStartSymbolFault());

        if (!nonTerminals.contains(startSymbol)) {
            faults.add(SyntaxFault.unknownNonTerminalSymbol(startSymbol));
        }

        // Obviously, these kinds of faults can only be generated if T == N or if one is a subtype
        // of the other
        for (T terminal : terminals)
            for (N nonTerminal : nonTerminals)
                if (terminal.equals(nonTerminal)) faults.add(new AlphabetFault<>(terminal));

        for (Prod production : productions)
            if (production.getLhs().isEmpty())
                faults.add(
                        new ProductionFault<>(
                                GrammarConstructionFaultReason.EMPTY_LHS, production));

        validateAllUsedTerminals(faults);
        validateAllUsedNonTerminals(faults);

        return new GrammarConstructionFaultCollection(faults);
    }

    private void validateAllUsedNonTerminals(List<Fault<GrammarConstructionFaultReason>> faults) {
        for (Prod production : productions) {
            for (N nonTerminal : production.getNonTerminals()) {
                if (!this.nonTerminals.contains(nonTerminal)) {
                    faults.add(SyntaxFault.unknownNonTerminalSymbol(nonTerminal));
                }
            }
        }
    }

    private void validateAllUsedTerminals(List<Fault<GrammarConstructionFaultReason>> faults) {
        for (Prod production : productions) {
            for (T terminal : production.getTerminals()) {
                if (!this.terminals.contains(terminal)) {
                    faults.add(SyntaxFault.unknownTerminalSymbol(terminal));
                }
            }
        }
    }

    protected GrammarConstructionFaultCollection validateEpsilonRule() {
        return validateEpsilonRule(productions, startSymbol);
    }

    /**
     * Checks if the start symbol occurs in the RHS of some production, yet {@code S -> epsilon}
     * exists as a production. It also checks if there are wrong epsilon rules (LHS is not the start
     * symbol). If no epsilon-production exists there will be no errors.
     *
     * <p>This method can be used by the {@link AbstractGrammarBuilder#validate()}-method of the
     * children classes if the specific grammar has this restriction.
     *
     * @return A collection of all faults found that are related to the epsilon-rule.
     */
    public static <T extends Serializable, N extends Serializable>
            GrammarConstructionFaultCollection validateEpsilonRule(
                    Collection<? extends Production<T, N>> productions, N startSymbol) {
        List<Fault<GrammarConstructionFaultReason>> faults = new LinkedList<>();
        AtomicBoolean correctEpsilonRuleExists =
                new AtomicBoolean(
                        false); // Needed because a stream can potentially be multithreaded

        // Check if correct epsilon-rule exists and if other wrong epsilon-rules exist (LHS is not
        // the start symbol)
        productions.stream()
                .filter(prod -> prod.getRhs().isEmpty()) // Only epsilon rules
                .forEach(
                        prod -> {
                            if (prod.getLhs().isOnlyNonTerminal(startSymbol))
                                correctEpsilonRuleExists.set(true);
                            else
                                faults.add(
                                        new ProductionFault<>(
                                                GrammarConstructionFaultReason.THE_EPSILON_RULE,
                                                prod));
                        });

        // If a correct epsilon rule exists, we need to check if the start symbol occurs on any RHS.
        // This results in a fault
        if (correctEpsilonRuleExists.get())
            productions.stream()
                    .filter(prod -> prod.getRhs().containsNonTerminal(startSymbol))
                    .forEach(
                            production ->
                                    faults.add(
                                            new ProductionFault<>(
                                                    GrammarConstructionFaultReason.THE_EPSILON_RULE,
                                                    production)));

        return new GrammarConstructionFaultCollection(faults);
    }

    public final Result<G, GrammarConstructionFaultCollection> build() {
        GrammarConstructionFaultCollection faults = validate();

        if (faults.containsAnyFault()) return new Result.Err<>(faults);

        return new Result.Ok<>(make.apply(terminals, nonTerminals, startSymbol, productions));
    }

    public final Result<G, GrammarConstructionFaultCollection> buildAndReset() {
        Result<G, GrammarConstructionFaultCollection> result = build();
        reset();
        return result;
    }

    public final Alphabet<T> getTerminals() {
        return new Alphabet<>(terminals);
    }

    public final Alphabet<N> getNonTerminals() {
        return new Alphabet<>(nonTerminals);
    }

    public final Set<Prod> getProductions() {
        return Collections.unmodifiableSet(productions);
    }

    @FunctionalInterface
    public interface MakeGrammar<A, B, C, D, G> extends Serializable {
        G apply(A a, B b, C c, D d);
    }

    /** For serialization */
    @SuppressWarnings("unused")
    protected AbstractGrammarBuilder() {}
}

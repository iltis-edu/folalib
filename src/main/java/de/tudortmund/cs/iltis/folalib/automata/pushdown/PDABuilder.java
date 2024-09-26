package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import static de.tudortmund.cs.iltis.folalib.util.NullCheck.requireAllNonNull;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.fault.PDAConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.util.Result;
import java.io.Serializable;
import java.util.*;

/**
 * A builder to construct instances of {@link PDA}s.
 *
 * <p>The builder keeps its internal state after calling {@link PDABuilder#build} and can thus be
 * reused to create multiple instances of the same PDA. Each constructed PDA is completely
 * independent of the others and this builder.
 *
 * @param <T> The type of states in this automaton
 * @param <S> The type of the input alphabet
 * @param <K> The type of the stack alphabet
 */
public class PDABuilder<T extends Serializable, S extends Serializable, K extends Serializable>
        implements Serializable {

    LinkedHashSet<S> inputAlphabet;
    LinkedHashSet<K> stackAlphabet;
    LinkedHashSet<T> states;
    LinkedHashSet<T> acceptingStates;
    LinkedHashSet<T> initialStates;
    K initialTopOfStack;
    PDATransitions<T, S, K> transitions;
    PDAAcceptanceStrategy acceptanceStrategy;

    private PDABuilderValidator<T, S, K> validator = new PDABuilderValidator<>();

    /**
     * Initialize a new PDABuilder to construct {@link PDA}s over the given alphabet.
     *
     * @param inputAlphabet The alphabet of the input symbols of the resulting PDA
     * @throws NullPointerException if {@param inputAlphabet} is {@code null}
     */
    public PDABuilder(Alphabet<? extends S> inputAlphabet) {
        Objects.requireNonNull(inputAlphabet);
        this.inputAlphabet = new LinkedHashSet<>(inputAlphabet.toUnmodifiableSet());
        this.stackAlphabet = new LinkedHashSet<>();
        this.states = new LinkedHashSet<>();
        this.acceptingStates = new LinkedHashSet<>();
        this.initialStates = new LinkedHashSet<>();
        this.initialTopOfStack = null;
        this.acceptanceStrategy = null;
        this.transitions = new PDATransitions<>();
    }

    /**
     * Initialize a new PDABuilder to construct {@link PDA}s over the given alphabet. Also provides
     * an {@param validator}.
     *
     * @param inputAlphabet The alphabet of the input symbols of the resulting PDA
     * @param validator The validator which verifies that the PDA can be built
     * @throws NullPointerException if {@param inputAlphabet} or {@param validator} are {@code null}
     */
    public PDABuilder(Alphabet<? extends S> inputAlphabet, PDABuilderValidator<T, S, K> validator) {
        this(inputAlphabet);
        Objects.requireNonNull(validator);
        this.validator = validator;
    }

    /**
     * Construct a new PDABuilder with the exact same settings as the given {@link PDA}.
     *
     * @param automaton The automaton for which to construct a builder
     * @throws NullPointerException if {@param automaton} is {@code null}
     */
    public PDABuilder(PDA<T, S, K> automaton) {
        Objects.requireNonNull(automaton);
        this.inputAlphabet = new LinkedHashSet<>(automaton.getAlphabet().toUnmodifiableSet());
        this.stackAlphabet = new LinkedHashSet<>(automaton.getStackAlphabet().toUnmodifiableSet());
        this.states = new LinkedHashSet<>(automaton.getStates());
        this.acceptingStates = new LinkedHashSet<>(automaton.getAcceptingStates());
        this.initialStates = new LinkedHashSet<>(automaton.getInitialStates());
        this.initialTopOfStack = automaton.getInitialStackSymbol();
        this.acceptanceStrategy = automaton.getAcceptanceStrategy();
        this.transitions = new PDATransitions<>(automaton.getTransitions());
    }

    /* For serialization */
    @SuppressWarnings("unused")
    private PDABuilder() {}

    /**
     * Add the given states to the resulting PDA
     *
     * @param states The states to add to the PDA
     * @return {@code this}
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    @SafeVarargs
    public final PDABuilder<T, S, K> withStates(T... states) {
        Objects.requireNonNull(states);
        return withStates(Arrays.asList(states));
    }

    /**
     * Add all states in the given collection to the resulting PDA
     *
     * @param states The states to add to the PDA
     * @return {@code this}
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    public final PDABuilder<T, S, K> withStates(Collection<? extends T> states) {
        requireAllNonNull(states);
        this.states.addAll(states);
        return this;
    }

    /**
     * Add the given states to the resulting PDA and mark them as initial states
     *
     * <p>If this method is called multiple times, the states are added to the already existing
     * initial states.
     *
     * @param states The states to add to the PDA as the initial states
     * @return {@code this}
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    @SafeVarargs
    public final PDABuilder<T, S, K> withInitial(T... states) {
        Objects.requireNonNull(states);
        return withInitial(Arrays.asList(states));
    }

    /**
     * Add the given states to the resulting PDA and mark them as initial states
     *
     * <p>If this method is called multiple times, the states are added to the already existing
     * initial states.
     *
     * @param states The states to add to the PDA as the initial states as a set
     * @return {@code this}
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    public final PDABuilder<T, S, K> withInitial(Collection<? extends T> states) {
        requireAllNonNull(states);
        initialStates.addAll(states);
        return withStates(states);
    }

    /**
     * Add the given states to the resulting PDA and mark all of them as accepting
     *
     * <p>If this method is called multiple times, the states are added to the already existing
     * accepting states.
     *
     * @param states The states to add to the PDA as accepting states
     * @return {@code this}
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    @SafeVarargs
    public final PDABuilder<T, S, K> withAccepting(T... states) {
        Objects.requireNonNull(states);
        return withAccepting(Arrays.asList(states));
    }

    /**
     * Add the given states to the resulting PDA and mark all of them as accepting
     *
     * <p>If this method is called multiple times, the states are added to the already existing
     * accepting states.
     *
     * @param states The states to add to the PDA as accepting states
     * @return {@code this}
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    public final PDABuilder<T, S, K> withAccepting(Collection<? extends T> states) {
        requireAllNonNull(states);
        acceptingStates.addAll(states);
        return withStates(states);
    }

    /**
     * Add the given states to the resulting PDA and set them (and only them!) as the accepting
     * states
     *
     * <p>If this method is called multiple times, previously added accepting states are no longer
     * considered accepting, but the states are *not* removed from the PDA.
     *
     * @param states The states to set as the accepting states
     * @return {@code this}
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    @SafeVarargs
    public final PDABuilder<T, S, K> overrideAccepting(T... states) {
        acceptingStates.clear();
        return withAccepting(states);
    }

    /**
     * Add the given states to the resulting PDA and set them (and only them!) as the initial states
     *
     * <p>If this method is called multiple times, previously added initial states are no longer
     * considered initial, but the states are *not* removed from the PDA.
     *
     * @param states The states to set as the initial states
     * @return {@code this}
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    @SafeVarargs
    public final PDABuilder<T, S, K> overrideInitial(T... states) {
        initialStates.clear();
        return withInitial(states);
    }

    /**
     * Add the given symbols to the stack alphabet
     *
     * @param symbols The symbols to add to the stack alphabet
     * @return {@code this}
     * @throws NullPointerException if {@param symbols} is {@code null} or if one of the contents is
     *     {@code null}
     */
    @SafeVarargs
    public final PDABuilder<T, S, K> withStackSymbols(K... symbols) {
        Objects.requireNonNull(symbols);
        return withStackSymbols(Arrays.asList(symbols));
    }

    /**
     * Add all symbols of the given collection to the stack alphabet of the resulting PDA
     *
     * @param stackAlphabet The collection whose elements should be added to the stack alphabet
     * @return {@code this}
     * @throws NullPointerException if {@param stackAlphabet} is {@code null} or if one of the
     *     contents is {@code null}
     */
    public final PDABuilder<T, S, K> withStackSymbols(Collection<? extends K> stackAlphabet) {
        requireAllNonNull(stackAlphabet);
        this.stackAlphabet.addAll(stackAlphabet);
        return this;
    }

    /**
     * Add all symbols of the given alphabet to the stack alphabet of the resulting PDA
     *
     * @param stackAlphabet The collection whose elements should be added to the stack alphabet
     * @return {@code this}
     * @throws NullPointerException if {@param stackAlphabet} is {@code null} or if one of the
     *     contents is {@code null}
     */
    public final PDABuilder<T, S, K> withStackSymbols(Alphabet<? extends K> stackAlphabet) {
        Objects.requireNonNull(stackAlphabet);
        return withStackSymbols(stackAlphabet.toUnmodifiableSet());
    }

    /**
     * Set the bottommost symbol of the stack for the resulting PDA
     *
     * <p>If this method is called multiple times, previously set initial stack values are
     * overwritten silently.
     *
     * @param symbol The symbol to set at the very bottom of the stack
     * @return {@code this}
     * @throws NullPointerException if {@param symbol} is {@code null}
     */
    public PDABuilder<T, S, K> withInitialStackSymbol(K symbol) {
        Objects.requireNonNull(symbol);
        initialTopOfStack = symbol;
        return withStackSymbols(symbol);
    }

    /**
     * Set the given alphabet as the stack alphabet and discard all previously added symbols
     *
     * @param stackAlphabet The new stack alphabet to use
     * @return {@code this}
     * @throws NullPointerException if {@param stackAlphabet} is {@code null} or if one of the
     *     contents is {@code null}
     */
    public final PDABuilder<T, S, K> overrideStackAlphabet(Collection<? extends K> stackAlphabet) {
        requireAllNonNull(stackAlphabet);
        this.stackAlphabet.clear();
        return withStackSymbols(stackAlphabet);
    }

    /**
     * Add a given transition as an outgoing transition of the given state
     *
     * @param from The state from which the newly added transition originates
     * @param transition The new transition to add to the PDA
     * @return {@code this}
     * @throws NullPointerException if {@param from} is {@code null} or {@param transition} are
     *     {@code null}
     */
    public PDABuilder<T, S, K> withTransition(T from, PDATransition<T, S, K> transition) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(transition);
        transitions.addTransition(from, transition);
        return this;
    }

    /**
     * Add a new transition between two states with the given input symbol, topmost stack symbol and
     * the new top of the stack
     *
     * <p>The first symbol of newTopOfStack becomes the new topmost symbol of the stack.
     *
     * @param from The state from which the new transition originates
     * @param symbol The input symbol which may trigger this transition
     * @param topOfStack The topmost symbol of the stack (including ranges and wildcards) for this
     *     transition
     * @param to The destination state of the new transition
     * @param newTopOfStack The new top of the stack
     * @return {@code this}
     * @throws NullPointerException if any parameter is {@code null}
     */
    public final PDABuilder<T, S, K> withTransition(
            T from, S symbol, PDAStackSymbol<K> topOfStack, T to, PDAStackWord<K> newTopOfStack) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(symbol);
        Objects.requireNonNull(topOfStack);
        Objects.requireNonNull(to);
        Objects.requireNonNull(newTopOfStack);
        PDATransition<T, S, K> transition =
                new PDATransition<>(to, symbol, topOfStack, newTopOfStack);
        return withTransition(from, transition);
    }

    /**
     * Convenience method to add a new transition with a fixed (no ranges or wildcard) topmost stack
     * symbol
     *
     * @param from The state from which the new transition originates
     * @param symbol The input symbol which may trigger this transition
     * @param topOfStack The topmost symbol of the stack for this transition
     * @param to The destination state of the new transition
     * @param newTopOfStack The new top of the stack
     * @return {@code this}
     * @throws NullPointerException if any parameter is {@code null}
     */
    public final PDABuilder<T, S, K> withTransition(
            T from, S symbol, K topOfStack, T to, PDAStackWord<K> newTopOfStack) {
        Objects.requireNonNull(topOfStack);
        // The other params get tested in the method below.
        return withTransition(from, symbol, PDAStackSymbol.exactly(topOfStack), to, newTopOfStack);
    }

    /**
     * Add a new epsilon transition between two states for the topmost stack symbol and with the new
     * top of the stack
     *
     * <p>The first symbol of newTopOfStack becomes the new topmost symbol of the stack.
     *
     * @param from The state from which the new transition originates
     * @param topOfStack The topmost symbol of the stack (including ranges and wildcards) for this
     *     transition
     * @param to The destination state of the new transition
     * @param newTopOfStack The new top of the stack
     * @return {@code this}
     * @throws NullPointerException if any parameter is {@code null}
     */
    public final PDABuilder<T, S, K> withEpsilonTransition(
            T from, PDAStackSymbol<K> topOfStack, T to, PDAStackWord<K> newTopOfStack) {
        Objects.requireNonNull(topOfStack);
        Objects.requireNonNull(to);
        Objects.requireNonNull(newTopOfStack);
        PDATransition<T, S, K> transition = new PDATransition<>(to, topOfStack, newTopOfStack);
        return withTransition(from, transition);
    }

    /**
     * Convenience method to add a new transition with a fixed (no ranges or wildcard) topmost stack
     * symbol
     *
     * @param from The state from which the new transition originates
     * @param topOfStack The topmost symbol of the stack for this transition
     * @param to The destination state of the new transition
     * @param newTopOfStack The new top of the stack
     * @return {@code this}
     * @throws NullPointerException if any parameter is {@code null}
     */
    public final PDABuilder<T, S, K> withEpsilonTransition(
            T from, K topOfStack, T to, PDAStackWord<K> newTopOfStack) {
        Objects.requireNonNull(topOfStack);
        // The other params get tested in the method below.
        return withEpsilonTransition(from, PDAStackSymbol.exactly(topOfStack), to, newTopOfStack);
    }

    /**
     * Remove all previously added transitions from the resulting PDA
     *
     * @return {@code this}
     */
    public PDABuilder<T, S, K> clearTransitions() {
        transitions = new PDATransitions<>();
        return this;
    }

    /**
     * Remove all transitions which originate from the given state
     *
     * @param from The state from which all transitions which should be removed originate
     * @return {@code this}
     * @throws NullPointerException if {@code from} is {@code null}
     */
    public PDABuilder<T, S, K> removeTransitions(T from) {
        Objects.requireNonNull(from);
        transitions.in(from).clear();
        return this;
    }

    /**
     * Set the acceptance strategy for the resulting PDA
     *
     * <p>If this method is called multiple times, the previously set strategy is silently replaced.
     *
     * @param strategy The strategy to set
     * @return {@code this}
     * @throws NullPointerException if {@code strategy} is {@code null}
     */
    public PDABuilder<T, S, K> withAcceptanceStrategy(PDAAcceptanceStrategy strategy) {
        Objects.requireNonNull(strategy);
        this.acceptanceStrategy = strategy;
        return this;
    }

    /**
     * Build a PDA from the current specification and settings
     *
     * <p>The settings are not altered and thus the builder can be used to construct multiple
     * instances of an identical PDA.
     *
     * @return {@code Result.Ok} with the constructed PDA or {@code Result.Err} with a collection of
     *     faults why the PDA could not be constructed
     */
    public Result<PDA<T, S, K>, PDAConstructionFaultCollection> build() {
        PDAConstructionFaultCollection validationResult = validate();

        if (validationResult.containsAnyFault()) {
            return new Result.Err<>(validationResult);
        }

        return new Result.Ok<>(
                new PDA<>(
                        states,
                        new Alphabet<>(inputAlphabet),
                        new Alphabet<>(stackAlphabet),
                        initialStates,
                        initialTopOfStack,
                        acceptanceStrategy,
                        acceptingStates,
                        new PDATransitions<>(transitions)));
    }

    /**
     * Build a PDA from the current specification and settings and reset the builder to its initial
     * state
     *
     * @return {@code Result.Ok} with the constructed PDA or {@code Result.Err} with a collection of
     *     faults why the PDA could not be constructed
     */
    public Result<PDA<T, S, K>, PDAConstructionFaultCollection> buildAndReset() {
        Result<PDA<T, S, K>, PDAConstructionFaultCollection> result = build();
        reset();
        return result;
    }

    /**
     * Reset the builder to its initial state
     *
     * <p>Equivalent to constructing a new PDABuilder with the same underlying alphabets.
     */
    public void reset() {
        states = new LinkedHashSet<>();
        acceptingStates = new LinkedHashSet<>();
        initialStates = new LinkedHashSet<>();
        initialTopOfStack = null;
        transitions = new PDATransitions<>();
        acceptanceStrategy = null;
        stackAlphabet = new LinkedHashSet<>();
    }

    private PDAConstructionFaultCollection validate() {
        return validator.validate(this);
    }
}

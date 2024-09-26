package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import de.tudortmund.cs.iltis.folalib.automata.*;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.fault.PDADeterminacyFault;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.folalib.util.CachedSerializableFunction;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A possibly non-deterministic push down automaton
 *
 * @param <T> The type of states in this automaton
 * @param <S> The type of the input alphabet
 * @param <K> The type of the stack alphabet
 */
public class PDA<T extends Serializable, S extends Serializable, K extends Serializable>
        extends Automaton<T, S, PDAConfiguration<T, S, K>, PDATransition<T, S, K>> {
    private Alphabet<K> stackAlphabet;
    private Set<T> acceptingStates;
    private PDAAcceptanceStrategy acceptanceStrategy;
    private K initialStackSymbol;

    protected PDA(
            Set<T> states,
            Alphabet<S> inputAlphabet,
            Alphabet<K> stackAlphabet,
            Set<T> initialStates,
            K initialStackSymbol,
            PDAAcceptanceStrategy strategy,
            Set<T> acceptingStates,
            PDATransitions<T, S, K> transitions) {
        super(states, inputAlphabet, initialStates, transitions);

        this.stackAlphabet = stackAlphabet;
        this.acceptingStates = new LinkedHashSet<>(acceptingStates);
        this.acceptanceStrategy = strategy;
        this.initialStackSymbol = initialStackSymbol;
    }

    /* For serialization */
    @SuppressWarnings("unused")
    private PDA() {}

    @Override
    public DeterminacyFaultCollection<T, PDADeterminacyFault<T, S, K, DeterminacyFaultReason>>
            checkDeterminacy() {
        return new PDADeterminacyCheck<T, S, K>().checkDeterminacy(this);
    }

    /**
     * Apply the given homomorphisms to the states and the stack alphabet of this PDA
     *
     * @param stateHomomorphism The homomorphism to map the states
     * @param alphabetHomomorphism The homomorphism to map the stack alphabet
     * @param <U> The type of the new states
     * @param <J> The type of the new stack alphabet symbols
     * @return a new PDA
     */
    private <U extends Serializable, J extends Serializable> PDA<U, S, J> bimap(
            SerializableFunction<T, U> stateHomomorphism,
            SerializableFunction<K, J> alphabetHomomorphism) {

        SerializableFunction<T, U> stateMapping =
                new CachedSerializableFunction<>(stateHomomorphism);
        SerializableFunction<K, J> stackAlphabetMapping =
                new CachedSerializableFunction<>(alphabetHomomorphism);

        PDABuilder<U, S, J> builder = new PDABuilder<>(alphabet);
        builder.withStackSymbols(
                stackAlphabet.stream().map(stackAlphabetMapping).collect(Collectors.toList()));

        for (T initialState : initialStates) {
            builder.withInitial(stateMapping.apply(initialState));
        }
        for (T state : states) {
            builder.withStates(stateMapping.apply(state));
        }
        for (T acceptingState : acceptingStates) {
            builder.withAccepting(stateMapping.apply(acceptingState));
        }

        builder.withInitialStackSymbol(stackAlphabetMapping.apply(initialStackSymbol));
        builder.withAcceptanceStrategy(acceptanceStrategy);

        for (T state : states) {
            for (PDATransition<T, S, K> transition : transitions.in(state)) {
                PDATransition<U, S, J> newTransition =
                        new PDATransition<>(
                                stateMapping.apply(transition.getState()),
                                transition.getInputSymbol(),
                                transition.getStackSymbol().map(stackAlphabetMapping),
                                new PDAStackWord<>(
                                        transition.getNewTopOfStack().stream()
                                                .map(
                                                        k ->
                                                                k == null
                                                                        ? null
                                                                        : stackAlphabetMapping
                                                                                .apply(k))
                                                .collect(Collectors.toList())));
                builder.withTransition(stateMapping.apply(state), newTransition);
            }
        }

        return builder.build().unwrap();
    }

    @Override
    public <U extends Serializable> PDA<U, S, K> mapStates(
            SerializableFunction<T, U> homomorphism) {
        return bimap(homomorphism, k -> k);
    }

    @Override
    public <R extends Serializable> PDA<T, R, K> mapAlphabet(
            SerializableFunction<S, R> homomorphism) {

        CachedSerializableFunction<S, R> memoizedHomomorphism =
                new CachedSerializableFunction<>(homomorphism);
        Alphabet<R> mappedAlphabet =
                new Alphabet<>(
                        alphabet.stream().map(memoizedHomomorphism).collect(Collectors.toList()));
        PDABuilder<T, R, K> builder = new PDABuilder<>(mappedAlphabet);

        builder.withInitial(initialStates)
                .withInitialStackSymbol(initialStackSymbol)
                .withStackSymbols(stackAlphabet.toUnmodifiableSet())
                .withAcceptanceStrategy(acceptanceStrategy);

        builder.withStates(states);
        acceptingStates.forEach(builder::withAccepting);

        for (T state : states) {
            for (PDATransition<T, S, K> transition : getTransitions().in(state)) {
                PDATransition<T, R, K> newTransition =
                        new PDATransition<>(
                                transition.getState(),
                                transition.getInputSymbol() == null
                                        ? null
                                        : memoizedHomomorphism.apply(transition.getInputSymbol()),
                                transition.getStackSymbol(),
                                transition.getNewTopOfStack());
                builder.withTransition(state, newTransition);
            }
        }
        return builder.build().unwrap();
    }

    public <J extends Serializable> PDA<T, S, J> mapStackAlphabet(
            SerializableFunction<K, J> homomorphism) {
        return bimap(t -> t, homomorphism);
    }

    @Override
    public boolean isHaltingConfiguration(PDAConfiguration<T, S, K> configuration) {
        return !transitions.exists((s, trans) -> trans.isApplicable(configuration));
    }

    @Override
    public boolean isAcceptingConfiguration(PDAConfiguration<T, S, K> configuration) {
        switch (acceptanceStrategy) {
            case EMPTY_STACK:
                return !configuration.hasSymbol() && configuration.isStackEmpty();
            case ACCEPTING_STATES:
                return !configuration.hasSymbol()
                        && acceptingStates.contains(configuration.getState());
            default:
                throw new RuntimeException("Unknown acceptance strategy for PDA");
        }
    }

    /**
     * Return the initial stack of this PDA
     *
     * @return The initial stack of this PDA
     */
    public Deque<K> getInitialStack() {
        Deque<K> stack = new LinkedList<>();
        stack.push(initialStackSymbol);
        return stack;
    }

    /**
     * Get the acceptance strategy of this PDA
     *
     * @return the acceptance strategy
     */
    public PDAAcceptanceStrategy getAcceptanceStrategy() {
        return this.acceptanceStrategy;
    }

    /**
     * Get the stack alphabet of this PDA
     *
     * @return the stack alphabet
     */
    public Alphabet<K> getStackAlphabet() {
        return stackAlphabet;
    }

    /**
     * Get the set of accepting states of this PDA
     *
     * @return the set of accepting states
     */
    public Set<T> getAcceptingStates() {
        return Collections.unmodifiableSet(acceptingStates);
    }

    /**
     * Get the bottommost stack symbol of this PDA
     *
     * @return the bottommost stack symbol
     */
    public K getInitialStackSymbol() {
        return initialStackSymbol;
    }

    @Override
    public Set<PDAConfiguration<T, S, K>> getAllStartConfigurations(Word<S> word) {
        return getInitialStates().stream()
                .map(start -> new PDAConfiguration<>(start, word, getInitialStack()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PDA<?, ?, ?> pda = (PDA<?, ?, ?>) o;
        return Objects.equals(stackAlphabet, pda.stackAlphabet)
                && Objects.equals(acceptingStates, pda.acceptingStates)
                && acceptanceStrategy == pda.acceptanceStrategy
                && Objects.equals(initialStackSymbol, pda.initialStackSymbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                stackAlphabet,
                acceptingStates,
                acceptanceStrategy,
                initialStackSymbol);
    }
}

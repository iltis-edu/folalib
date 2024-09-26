package de.tudortmund.cs.iltis.folalib.automata.finite;

import de.tudortmund.cs.iltis.folalib.automata.*;
import de.tudortmund.cs.iltis.folalib.automata.finite.conversion.EpsilonNFAOnlyOneInitialStateConversion;
import de.tudortmund.cs.iltis.folalib.automata.finite.conversion.EpsilonNFARemoveEpsilonTransitionsConversion;
import de.tudortmund.cs.iltis.folalib.automata.finite.fault.NFADeterminacyFault;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.folalib.util.CachedSerializableFunction;
import de.tudortmund.cs.iltis.utils.collections.SerializablePair;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NFA<T extends Serializable, S extends Serializable>
        extends Automaton<T, S, Configuration<T, S>, NFATransition<T, S>> implements Serializable {
    /* package-private */ DeterminacyFaultCollection<
                    T, NFADeterminacyFault<T, S, DeterminacyFaultReason>>
            determinacy;

    private Set<T> acceptingStates;

    /* For serialization */
    @SuppressWarnings("unused")
    private NFA() {
        super();
    }

    protected NFA(
            Set<T> states,
            Alphabet<S> alphabet,
            Set<T> initialStates,
            Set<T> acceptingStates,
            NFATransitions<T, S> transitions) {
        super(states, alphabet, initialStates, transitions);

        this.acceptingStates = new LinkedHashSet<>(acceptingStates);
    }

    protected NFA(
            Set<T> states, Alphabet<S> alphabet, Set<T> initialStates, Set<T> acceptingStates) {
        super(states, alphabet, initialStates, new NFATransitions<>());

        Objects.requireNonNull(acceptingStates);

        if (!states.containsAll(acceptingStates))
            throw new IllegalArgumentException(
                    "Set of accepting states not contained in state set!");

        this.acceptingStates = new LinkedHashSet<>(acceptingStates);
    }

    /**
     * Construct a copy of this {@link NFA} in which all missing transitions are added as leading a
     * newly constructed "discard" state
     *
     * <p>The discard state loops an all alphabet symbols.
     *
     * <p>The resulting automaton is guaranteed to be total
     *
     * @param supplier A {@link StateSupplier} used to generate the discard state, in case the
     *     automaton wasn't total yet.
     * @return The "totalified" version of this automaton.
     */
    public NFA<T, S> totalify(StateSupplier<T> supplier) {
        return totalify(t -> t, supplier, alphabet);
    }

    public NFA<T, S> totalifyWithRegardTo(StateSupplier<T> supplier, Alphabet<S> alphabet) {
        return totalify(t -> t, supplier, alphabet);
    }

    public NFA<MaybeGenerated<T, Integer>, S> totalify() {
        return totalify(
                MaybeGenerated.Input::new, () -> new MaybeGenerated.Generated<>(0), alphabet);
    }

    public NFA<MaybeGenerated<T, Integer>, S> totalifyWithRegardTo(Alphabet<S> alphabet) {
        return totalify(
                MaybeGenerated.Input::new, () -> new MaybeGenerated.Generated<>(0), alphabet);
    }

    /**
     * Totalifies this {@link NFA}.
     *
     * @param embedding An injective map T -> State
     * @param supplier Supplier that is used to create exactly one new state
     * @param alphabet The alphabet regarding which to totalify
     * @param <State> The type of states of the resulting automaton
     * @return A total automaton
     */
    private <State extends Serializable> NFA<State, S> totalify(
            SerializableFunction<T, State> embedding,
            StateSupplier<State> supplier,
            Alphabet<S> alphabet) {
        NFA<State, S> mapped = mapStates(embedding);
        // temporary automaton that is local to this method. Modification is O.K.!
        mapped.alphabet = alphabet;

        DeterminacyFaultCollection<State, NFADeterminacyFault<State, S, DeterminacyFaultReason>>
                faults = mapped.checkDeterminacy();

        if (!faults.hasTotalityFaults()) return mapped;

        State discardState = supplier.getUnused(mapped);
        NFABuilder<State, S> builder = new NFABuilder<>(mapped);
        builder.withStates(discardState);

        for (S symbol : alphabet) {
            builder.withTransition(discardState, symbol, discardState);
        }

        for (NFADeterminacyFault<State, S, DeterminacyFaultReason> fault :
                faults.getTotalityFaults()) {
            builder.withTransition(fault.getWhere(), fault.getSymbol(), discardState);
        }

        return builder.buildAndReset().unwrap(); // we know that we did it correctly!
    }

    @Override
    public <U extends Serializable> NFA<U, S> mapStates(SerializableFunction<T, U> homomorphism) {
        CachedSerializableFunction<T, U> memoizedHomomorphism =
                new CachedSerializableFunction<>(homomorphism);

        NFABuilder<U, S> builder = new NFABuilder<>(alphabet);
        initialStates.forEach(t -> builder.withInitial(memoizedHomomorphism.apply(t)));
        states.forEach(t -> builder.withStates(memoizedHomomorphism.apply(t)));
        acceptingStates.forEach(t -> builder.withAccepting(memoizedHomomorphism.apply(t)));

        for (Map.Entry<T, Set<NFATransition<T, S>>> entry :
                transitions.getTransitions().entrySet()) {
            for (NFATransition<T, S> trans : entry.getValue()) {
                S symbol = trans.getSymbol();
                U origin = memoizedHomomorphism.apply(entry.getKey());
                U destination = memoizedHomomorphism.apply(trans.getState());
                NFATransition<U, S> newTransition = new NFATransition<>(symbol, destination);
                builder.withTransition(origin, newTransition);
            }
        }
        return builder.buildAndReset().unwrap();
    }

    @Override
    public <R extends Serializable> NFA<T, R> mapAlphabet(SerializableFunction<S, R> homomorphism) {
        CachedSerializableFunction<S, R> memoizedHomomorphism =
                new CachedSerializableFunction<>(homomorphism);
        Alphabet<R> mappedAlphabet =
                new Alphabet<>(
                        alphabet.stream().map(memoizedHomomorphism).collect(Collectors.toList()));

        NFABuilder<T, R> builder = new NFABuilder<>(mappedAlphabet);
        initialStates.forEach(builder::withInitial);
        states.forEach(builder::withStates);
        acceptingStates.forEach(builder::withAccepting);

        for (Map.Entry<T, Set<NFATransition<T, S>>> entry :
                transitions.getTransitions().entrySet()) {
            for (NFATransition<T, S> trans : entry.getValue()) {
                R symbol =
                        trans.getSymbol() == null
                                ? null
                                : memoizedHomomorphism.apply(trans.getSymbol());
                T origin = entry.getKey();
                T destination = trans.getState();
                NFATransition<T, R> newTransition = new NFATransition<>(symbol, destination);
                builder.withTransition(origin, newTransition);
            }
        }
        return builder.build().unwrap();
    }

    /**
     * @see EpsilonNFARemoveEpsilonTransitionsConversion
     */
    public NFA<T, S> removeEpsilonTransitions() {
        return new EpsilonNFARemoveEpsilonTransitionsConversion<T, S>().apply(this);
    }

    /**
     * @see EpsilonNFAOnlyOneInitialStateConversion
     */
    public NFA<MaybeGenerated<T, String>, S> onlyOneInitialState() {
        return new EpsilonNFAOnlyOneInitialStateConversion<T, S>().apply(this);
    }

    // Yeehaw, Covariance
    @Override
    public synchronized DeterminacyFaultCollection<
                    T, NFADeterminacyFault<T, S, DeterminacyFaultReason>>
            checkDeterminacy() {
        if (determinacy == null) {
            determinacy =
                    getTransitions()
                            .checkDeterminacy(states, initialStates, alphabet.toUnmodifiableSet());
        }

        return determinacy;
    }

    @Override
    public boolean isHaltingConfiguration(Configuration<T, S> config) {
        return !config.hasSymbol();
    }

    private final HashSet<Configuration<T, S>> cachedAcceptingConfigurations = new HashSet<>();

    @Override
    public boolean isAcceptingConfiguration(Configuration<T, S> config) {
        if (cachedAcceptingConfigurations.contains(config)) {
            return true;
        } else if (!config.hasSymbol() && acceptingStates.contains(config.getState())) {
            cachedAcceptingConfigurations.add(config);
            return true;
        }
        return false;
    }

    @Override
    public NFATransitions<T, S> getTransitions() {
        return new NFATransitions<>(transitions);
    }

    public Set<NFATransition<T, S>> getTransitions(T state, S symbol) {
        return getTransitions().getTransitions(state, symbol);
    }

    public Set<NFATransition<T, S>> getEpsilonTransitions(T state) {
        return getTransitions().getEpsilonTransitions(state);
    }

    @Deprecated
    public boolean addTransition(T from, S symbol, T to) {
        return addTransition(from, new NFATransition<>(symbol, to));
    }

    public Set<T> getAcceptingStates() {
        return Collections.unmodifiableSet(acceptingStates);
    }

    public boolean isEmpty() {
        return getReachableStates().stream().noneMatch(acceptingStates::contains);
    }

    private final HashMap<T, Set<T>> cachedClosure = new HashMap<>();

    /**
     * Computes the set of states reachable from the given state via epsilon transitions,
     * <i>including</i> {@code state} itself
     *
     * @param state The state
     * @return The set of reachable states
     */
    public Set<T> epsilonClosureOf(T state) {
        Function<T, Set<T>> computeHull =
                s -> {
                    Set<T> hull =
                            new HashSet<>(
                                    asGraph()
                                            .breadthFirstTraversal(
                                                    s, t -> true, NFATransition::isEpsilon)
                                            .keySet());
                    hull.add(s);
                    return hull;
                };
        return cachedClosure.computeIfAbsent(state, computeHull);
    }

    public Set<T> reachableWith(T from, S with) {
        return epsilonClosureOf(from).stream()
                .flatMap(t -> getTransitions(t, with).stream())
                .map(NFATransition::getState)
                .collect(Collectors.toSet());
    }

    /**
     * Performs the product automaton construction for {@code this} and {@code other}. The
     * automatons can be non-deterministic. They will get determinized by using {@link
     * NFA::determinize()} beforehand. If you do not want that to happen, use {@link
     * NFA::productWithoutDFATransform}.
     *
     * <p>Only reachable states are constructed. Accepting states are set as determined by the given
     * 2-ary boolean function. <b>The two automata are assumed to have the same alphabet!</b>
     *
     * @param other The automaton with which to perform the product construction
     * @param acceptingCombinator A function determining the configuration of the product
     *     automaton's accepting states
     * @param <U> The type of states the other automaton uses
     * @return The product automaton
     */
    public <U extends Serializable>
            NFA<SerializablePair<LinkedHashSet<T>, LinkedHashSet<U>>, S> product(
                    NFA<U, S> other, BiFunction<Boolean, Boolean, Boolean> acceptingCombinator) {
        NFA<LinkedHashSet<T>, S> determinizedThis = this.determinize();
        NFA<LinkedHashSet<U>, S> determinizedOther = other.determinize();

        return determinizedThis.productWithoutDFATransform(determinizedOther, acceptingCombinator);
    }

    /**
     * Performs the product automaton construction for {@code this} and {@code other} on a purely
     * <b>syntactical</b> level.
     *
     * <p>{@code this} and {@param other} need to be <b>deterministic</b>!
     *
     * <p>Only reachable states are constructed. Accepting states are set as determined by the given
     * 2-ary boolean function. <b>Note that no semantic checks are performed!</b>. If you give this
     * method two (actually) non-deterministic automata and set {@code acceptingCombinator} to e.g.
     * {@link de.tudortmund.cs.iltis.folalib.util.BinaryFunctions#XOR} this method will happily
     * construct an automaton for you, but that automaton <i>will not recognize the symmetric
     * different of the languages recognized by the input automata</i> (as that would obviously
     * imply P = NP). Furthermore, both automatons are assumed to have only one initial state.
     * Otherwise, an error will be thrown. There is no check in place to prevent such fallacies. You
     * have been warned.
     *
     * <p><b>The two automata are assumed to have the same alphabet!</b>
     *
     * @param other The automaton with which to perform the product construction
     * @param acceptingCombinator A function determining the configuration of the product
     *     automaton's accepting states
     * @param <U> The type of states the other automaton uses
     * @return The product automaton
     */
    public <U extends Serializable> NFA<SerializablePair<T, U>, S> productWithoutDFATransform(
            NFA<U, S> other, BiFunction<Boolean, Boolean, Boolean> acceptingCombinator) {
        Queue<SerializablePair<T, U>> toProcess = new LinkedList<>();
        SerializablePair<T, U> initial =
                new SerializablePair<>(
                        initialStates.stream().findFirst().get(),
                        other.initialStates.stream().findFirst().get());

        toProcess.offer(initial);

        NFABuilder<SerializablePair<T, U>, S> builder =
                new NFABuilder<SerializablePair<T, U>, S>(alphabet).withInitial(initial);

        while (!toProcess.isEmpty()) {
            SerializablePair<T, U> current = toProcess.poll();

            if (acceptingCombinator.apply(
                    epsilonClosureOf(current.first()).stream()
                            .anyMatch(s -> acceptingStates.contains(s)),
                    other.epsilonClosureOf(current.second()).stream()
                            .anyMatch(v -> other.acceptingStates.contains(v))))
                builder.withAccepting(current);

            for (S symbol : alphabet) {
                for (T t : reachableWith(current.first(), symbol)) {
                    for (U u : other.reachableWith(current.second(), symbol)) {
                        SerializablePair<T, U> newState = new SerializablePair<>(t, u);

                        if (!builder.hasState(newState)) {
                            toProcess.offer(newState);
                            builder.withStates(newState);
                        }

                        builder.withTransition(current, symbol, newState);
                    }
                }
            }
        }

        return builder.build().unwrap();
    }

    /**
     * Compute a DFA recognizing the complement language of {@code this}. {@code this} gets
     * determinized beforehand, it does not matter if {@code this} is already deterministic or not.
     *
     * <p>If {@code this} is already deterministic, consider using {@link
     * NFA::complementWithoutDFATransform()}. This method will not turn the states into a
     * LinkedHashSet.
     *
     * @return A (deterministic) {@link NFA} recognizing the complement of {@code this} automaton.
     */
    public NFA<LinkedHashSet<T>, S> complement() {
        // Automatically returns deterministic and total automaton.
        NFA<LinkedHashSet<T>, S> determinized = this.determinize();

        NFABuilder<LinkedHashSet<T>, S> bob =
                new NFABuilder<>(determinized).overrideAccepting(); // unset accepting states

        // set them to complement of current accepting states
        for (LinkedHashSet<T> state : determinized.getStates())
            if (!determinized.getAcceptingStates().contains(state)) bob.withAccepting(state);

        return bob.buildAndReset().unwrap();
    }

    /**
     * If this {@link NFA} is deterministic, compute a DFA recognizing the complement language of
     * {@code this}, potentially totalifying the automaton using the given discard state first.
     *
     * <p>If you want {@code this} to get determinized automatically beforehand, use {@link
     * NFA::complement()}. That method will always return a valid value.
     *
     * @return A (deterministic) {@link NFA} recognizing the complement, or {@link Optional#empty()}
     *     if {@code this} is not deterministic
     * @see NFA#totalify(StateSupplier)
     */
    public Optional<NFA<T, S>> complementWithoutDFATransform(T discardState) {
        if (!isDeterministic()) return Optional.empty();

        NFABuilder<T, S> bob =
                new NFABuilder<>(this.totalify(() -> discardState))
                        .overrideAccepting(); // unset accepting states

        // set them to complement of current accepting states
        for (T state : states) if (!acceptingStates.contains(state)) bob.withAccepting(state);

        return Optional.of(bob.buildAndReset().unwrap());
    }

    /**
     * Determizes this {@link NFA} using the power set method.
     *
     * <p>Only reachable states are generated
     *
     * @return A deterministic and total version of this automaton.
     */
    public NFA<LinkedHashSet<T>, S> determinize() {
        DFABuilder<LinkedHashSet<T>, S> dfaBuilder = new DFABuilder<>(alphabet);

        LinkedHashSet<T> initial = new LinkedHashSet<>();

        // Add all with epsilon transition reachable states from all initial states (including
        // themselves)
        for (T start : initialStates) {
            initial.addAll(epsilonClosureOf(start));
        }

        dfaBuilder.withInitial(initial);

        if (initial.stream().anyMatch(acceptingStates::contains)) dfaBuilder.withAccepting(initial);

        Queue<LinkedHashSet<T>> toProcess = new LinkedList<>();
        toProcess.offer(initial);

        while (!toProcess.isEmpty()) {
            LinkedHashSet<T> current = toProcess.poll();

            for (S symbol : alphabet) {
                LinkedHashSet<T> successor = new LinkedHashSet<>();

                for (T state : current) {
                    for (NFATransition<T, S> transition : getTransitions(state, symbol)) {
                        successor.addAll(epsilonClosureOf(transition.getState()));
                    }
                }

                if (!dfaBuilder.hasState(successor)) toProcess.offer(successor);

                dfaBuilder.withStates(successor).withTransition(current, symbol, successor);

                if (successor.stream().anyMatch(acceptingStates::contains))
                    dfaBuilder.withAccepting(successor);
            }
        }

        return dfaBuilder.buildAndReset().unwrap();
    }

    public NFA<T, S> clone() {
        return new NFABuilder<>(this).buildAndReset().unwrap();
    }

    @Override
    public Set<Configuration<T, S>> getAllStartConfigurations(Word<S> word) {
        return getInitialStates().stream()
                .map(start -> new Configuration<>(start, word))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NFA<?, ?> nfa = (NFA<?, ?>) o;
        return acceptingStates.equals(nfa.acceptingStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), acceptingStates);
    }
}

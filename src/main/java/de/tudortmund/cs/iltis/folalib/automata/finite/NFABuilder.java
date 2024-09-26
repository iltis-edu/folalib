package de.tudortmund.cs.iltis.folalib.automata.finite;

import static de.tudortmund.cs.iltis.folalib.util.NullCheck.requireAllNonNull;

import de.tudortmund.cs.iltis.folalib.automata.finite.fault.*;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.util.Result;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Builder class for constructing an (eps-){@link NFA}.
 *
 * <p>The builder object can be reused even after {@link NFABuilder#build()} was called without
 * modifying the constructed NFA.
 *
 * @param <State> The type of the NFA's states
 * @param <Symbol> The type of the NFA's input symbols
 */
// FIXME: once we have more automata model, we can probably abstract some of these method into some
// sort of `abstract class AutomatonBuilder`
public class NFABuilder<State extends Serializable, Symbol extends Serializable>
        implements Serializable {

    protected LinkedHashSet<Symbol> alphabet;
    protected LinkedHashSet<State> states;
    protected LinkedHashSet<State> acceptingStates;
    protected LinkedHashSet<State> initialStates;
    protected NFATransitions<State, Symbol> transitions;

    // For GWT Serialization
    protected NFABuilder() {}

    /**
     * Initializes an {@link NFABuilder} for constructing an NFA with the given alphabet
     *
     * @param alphabet The NFA's input alphabet
     * @throws NullPointerException if {@param alphabet} is {@code null}
     */
    public NFABuilder(Alphabet<? extends Symbol> alphabet) {
        Objects.requireNonNull(alphabet);
        this.alphabet = new LinkedHashSet<>(alphabet.toUnmodifiableSet());
        states = new LinkedHashSet<>();
        acceptingStates = new LinkedHashSet<>();
        initialStates = new LinkedHashSet<>();
        transitions = new NFATransitions<>();
    }

    /**
     * Constructs a builder initialized to a given NFA's data
     *
     * <p>This does not allow the modification of the given NFA but rather the construction of a
     * derived NFA
     *
     * @param nfa The NFA to base construction off.
     * @throws NullPointerException if {@param state} is {@code null}
     */
    public NFABuilder(NFA<State, Symbol> nfa) {
        Objects.requireNonNull(nfa);
        this.alphabet = new LinkedHashSet<>(nfa.getAlphabet().toUnmodifiableSet());
        this.states = new LinkedHashSet<>(nfa.getStates());
        this.acceptingStates = new LinkedHashSet<>(nfa.getAcceptingStates());
        this.initialStates = new LinkedHashSet<>(nfa.getInitialStates());
        this.transitions = nfa.getTransitions();
    }

    /**
     * Merges the given builders states and transitions into this one. Does not ensure state
     * disjointness. Accepting states are merged. Initial states are merged if {@param
     * mergeInitialStates} is set to true, otherwise the initial states of {@code this} are kept
     * unchanged. Alphabets are merged, although the actual alphabet objects remain untouched.
     *
     * @param mergeInitialStates Determines whether the initial states are intended to be merged.
     * @param builders The builders to merge with
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param builders} is {@code null} or if one of the contents
     *     is {@code null}
     */
    @SafeVarargs
    public final NFABuilder<State, Symbol> mergeWith(
            boolean mergeInitialStates, NFABuilder<State, Symbol>... builders) {
        Objects.requireNonNull(builders);
        requireAllNonNull(builders);

        for (NFABuilder<State, Symbol> other : builders) {
            alphabet.addAll(other.alphabet);
            states.addAll(other.states);
            acceptingStates.addAll(other.acceptingStates);
            if (mergeInitialStates) {
                initialStates.addAll(other.initialStates);
            }
            for (Map.Entry<State, Set<NFATransition<State, Symbol>>> entry :
                    other.transitions.getTransitions().entrySet())
                transitions.in(entry.getKey()).addAll(entry.getValue());
        }

        return this;
    }

    /**
     * Adds multiple states to the NFA
     *
     * @param states The states to add
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    @SafeVarargs
    public final NFABuilder<State, Symbol> withStates(State... states) {
        Objects.requireNonNull(states);
        return withStates(Arrays.asList(states));
    }

    /**
     * Adds multiple states to the NFA
     *
     * @param states The states to add
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    public final NFABuilder<State, Symbol> withStates(Collection<? extends State> states) {
        requireAllNonNull(states);
        this.states.addAll(states);
        return this;
    }

    /**
     * Adds the given states to the NFA and additionally marks them as initial states.
     *
     * <p>Calling this method multiple times will not override the previously set values and instead
     * add to the set of initial states.
     *
     * @param states The initial states to add
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    @SafeVarargs
    public final NFABuilder<State, Symbol> withInitial(State... states) {
        Objects.requireNonNull(states);
        return withInitial(Arrays.asList(states));
    }

    /**
     * Adds the given states to the NFA and additionally marks them as initial states.
     *
     * <p>Calling this method multiple times will not override the previously set values and instead
     * add to the set of initial states.
     *
     * @param states The initial states to add as a set
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    public final NFABuilder<State, Symbol> withInitial(Collection<State> states) {
        requireAllNonNull(states);
        initialStates.addAll(states);
        return withStates(states);
    }

    /**
     * Adds the given states to the NFA and additionally marks them to be accepting.
     *
     * <p>Calling this method multiple times will not override the previously set values and instead
     * add to the set of accepting states.
     *
     * @param states The accepting states to add
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    @SafeVarargs
    public final NFABuilder<State, Symbol> withAccepting(State... states) {
        Objects.requireNonNull(states);
        return withAccepting(Arrays.asList(states));
    }

    /**
     * Adds the given states to the NFA and additionally marks them to be accepting.
     *
     * <p>Calling this method multiple times will not override the previously set values and instead
     * add to the set of accepting states.
     *
     * @param states The accepting states to add
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    public final NFABuilder<State, Symbol> withAccepting(Collection<State> states) {
        requireAllNonNull(states);
        acceptingStates.addAll(states);
        return withStates(states);
    }

    /**
     * Adds the given states to the NFA and additionally marks them to be accepting, overriding
     * whatever states were previously set to be accepting.
     *
     * <p>Does <i>not</i> remove the old accepting states from this builder, they are simply
     * 'normal' states now without any special behaviour.
     *
     * @param states The new set of accepting states
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    @SafeVarargs
    public final NFABuilder<State, Symbol> overrideAccepting(State... states) {
        Objects.requireNonNull(states);
        return overrideAccepting(Arrays.asList(states));
    }

    /**
     * Adds the given states to the NFA and additionally marks them to be accepting, overriding
     * whatever states were previously set to be accepting.
     *
     * <p>Does <i>not</i> remove the old accepting states from this builder
     *
     * @param states The new collection of accepting states
     * @return {@code this} for method chaining
     */
    public final NFABuilder<State, Symbol> overrideAccepting(Collection<State> states) {
        acceptingStates.clear();
        return withAccepting(states);
    }

    /**
     * Adds the given states to the NFA and additionally marks them as initial states, overriding
     * whatever states were previously set to be initial.
     *
     * <p>Does <i>not</i> remove the old initial states from this builder, they are simply 'normal'
     * states now without any special behaviour.
     *
     * @param states The new set of initial states
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param states} is {@code null} or if one of the contents is
     *     {@code null}
     */
    @SafeVarargs
    public final NFABuilder<State, Symbol> overrideInitial(State... states) {
        initialStates.clear();
        return withInitial(states);
    }

    /**
     * Adds the specified transition to the NFA
     *
     * @param from The state to which the transition shall be added
     * @param trans The transition
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param from} or {@param trans} are {@code null}
     */
    public NFABuilder<State, Symbol> withTransition(
            State from, NFATransition<State, Symbol> trans) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(trans);

        transitions.addTransition(from, trans);
        return this;
    }

    /**
     * Adds a {@param symbol}-transition to the NFA
     *
     * @param from The state to which the transition shall be added
     * @param symbol The alphabet symbol on which the transition should trigger
     * @param to The state the transition should lead to
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param from},{@param symbol} or {@param to} are {@code null}
     */
    public NFABuilder<State, Symbol> withTransition(State from, Symbol symbol, State to) {
        Objects.requireNonNull(symbol);
        Objects.requireNonNull(to);

        return withTransition(from, new NFATransition<>(symbol, to));
    }

    /**
     * Adds an epsilon-transition to the NFA
     *
     * @param from The state to which the transition shall be added
     * @param to The state the transition should lead to
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param from} or {@param to} are {@code null}
     */
    public NFABuilder<State, Symbol> withEpsilonTransition(State from, State to) {
        return withTransition(from, new NFATransition<>(to));
    }

    /**
     * Adds multiple epsilon-transitions to the NFA
     *
     * @param from The state to which the transition shall be added
     * @param to The states the transitions should lead to
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param from}, {@param states} or one of the contents is
     *     {@code null}
     */
    public NFABuilder<State, Symbol> withEpsilonTransition(State from, Collection<State> to) {
        Objects.requireNonNull(to);
        requireAllNonNull(to);

        for (State toElement : to) {
            this.withTransition(from, new NFATransition<>(toElement));
        }

        return this;
    }

    /**
     * Validates whether this {@link NFABuilder} instance, as currently configured, would produce a
     * valid {@link NFA}. Please note that this validation only checks the syntax, not the
     * semantics. It is <b>not</b> a construction fault if no accepting states exist.
     *
     * @return A set of faults indicating why the current builder state does not represent a valid
     *     NFA, or an empty set in case of a valid construction
     */
    public NFAConstructionFaultCollection validate() {
        List<Fault<NFAConstructionFaultReason>> faults = new ArrayList<>();

        if (initialStates.isEmpty()) faults.add(new SyntaxFault());

        for (Map.Entry<State, Set<NFATransition<State, Symbol>>> entry :
                transitions.getTransitions().entrySet()) {
            for (NFATransition<State, Symbol> trans : entry.getValue()) {
                if (!states.contains(entry.getKey()))
                    faults.add(
                            new TransitionFault<>(
                                    NFAConstructionFaultReason.TRANSITION_UNKNOWN_ORIGIN,
                                    entry.getKey(),
                                    trans));
                if (!trans.isEpsilon() && !alphabet.contains(trans.getSymbol()))
                    faults.add(
                            new TransitionFault<>(
                                    NFAConstructionFaultReason.TRANSITION_UNKNOWN_SYMBOL,
                                    entry.getKey(),
                                    trans));
                if (!states.contains(trans.getState()))
                    faults.add(
                            new TransitionFault<>(
                                    NFAConstructionFaultReason.TRANSITION_UNKNOWN_DESTINATION,
                                    entry.getKey(),
                                    trans));
            }
        }

        return new NFAConstructionFaultCollection(faults);
    }

    /**
     * Constructs an {@link NFA} from the data stored in this builder.
     *
     * @return The constructed NFA
     */
    public Result<NFA<State, Symbol>, NFAConstructionFaultCollection> build() {
        NFAConstructionFaultCollection validationResult = validate();

        if (validationResult.containsAnyFault()) return new Result.Err<>(validationResult);

        return new Result.Ok<>(
                new NFA<>(
                        states,
                        new Alphabet<>(alphabet),
                        initialStates,
                        acceptingStates,
                        new NFATransitions<>(transitions)));
    }

    private void reset() {
        this.states = new LinkedHashSet<>();
        this.initialStates = new LinkedHashSet<>();
        this.acceptingStates = new LinkedHashSet<>();
        this.transitions = new NFATransitions<>();
    }

    /**
     * Constructs an {@link NFA} from the data store in this builder, resetting the builder's
     * internal state alongside.
     *
     * @return The constructed NFA
     */
    public Result<NFA<State, Symbol>, NFAConstructionFaultCollection> buildAndReset() {
        Result<NFA<State, Symbol>, NFAConstructionFaultCollection> result = build();
        reset();
        return result;
    }

    /**
     * Removes all transitions from the NFA, but keeps all states and their properties (e.g.
     * acceptance)
     *
     * @return {@code this} for method chaining
     */
    public NFABuilder<State, Symbol> clearTransitions() {
        transitions = new NFATransitions<>();
        return this;
    }

    /**
     * Removes all transitions from the NFA which start in {@param from}
     *
     * @param from The state whose outgoing transitions to delete
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param from} is {@code null}
     */
    public NFABuilder<State, Symbol> removeTransitions(State from) {
        Objects.requireNonNull(from);
        transitions.in(from).clear();
        return this;
    }

    /**
     * Removes all transitions from the NFA which start in {@param from} and which trigger on
     * {@param symbol}
     *
     * @param from The state whose outgoing transitions to delete
     * @param symbol The symbol whose transitions should be deleted
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param from} or {@param symbol} are {@code null}
     */
    public NFABuilder<State, Symbol> removeTransitions(State from, Symbol symbol) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(symbol);

        Set<NFATransition<State, Symbol>> outgoing = transitions.in(from);

        outgoing.removeAll(
                outgoing.stream()
                        .filter(t -> symbol.equals(t.getSymbol()))
                        .collect(Collectors.toSet()));

        return this;
    }

    /**
     * Returns a copy of the currently configured accepting states.
     *
     * @return the accepting states.
     */
    public Set<State> getAccepting() {
        return Collections.unmodifiableSet(acceptingStates);
    }

    /**
     * Returns a copy of the currently configured initial states.
     *
     * @return the initial states.
     */
    public Set<State> getInitial() {
        return Collections.unmodifiableSet(initialStates);
    }

    /**
     * Checks whether the given object is registered as a state
     *
     * @param state The object to check
     * @return {@code true} iff the given object is registered as a state.
     */
    public boolean hasState(State state) {
        return states.contains(state);
    }

    // The GWT compiler does not like this Override annotation
    // @Override
    public NFABuilder<State, Symbol> clone() {
        NFABuilder<State, Symbol> clone = new NFABuilder<>(new Alphabet<>(alphabet));
        clone.states = new LinkedHashSet<>(states);
        clone.acceptingStates = new LinkedHashSet<>(acceptingStates);
        clone.initialStates = new LinkedHashSet<>(initialStates);
        clone.transitions = new NFATransitions<>(transitions);
        return clone;
    }

    public NFATransitions<State, Symbol> getTransitions() {
        return transitions;
    }

    public LinkedHashSet<State> getInitialStates() {
        return initialStates;
    }

    public LinkedHashSet<State> getAcceptingStates() {
        return acceptingStates;
    }

    /**
     * Removes the given transition from the NFA which starts in {@param from}.
     *
     * @param from The origin state of the transition
     * @param transition The transition to remove
     * @return {@code this} for method chaining
     * @throws NullPointerException if {@param from} or {@param transition} are {@code null}
     */
    @SuppressWarnings("unchecked")
    public NFABuilder<State, Symbol> removeTransition(
            State from, NFATransition<State, Symbol> transition) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(transition);

        transitions = transitions.removeTransition(from, transition);
        return this;
    }
}

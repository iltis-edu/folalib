package de.tudortmund.cs.iltis.folalib.automata;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import de.tudortmund.cs.iltis.utils.graph.Graph;
import de.tudortmund.cs.iltis.utils.graph.hashgraph.HashGraph;
import java.io.Serializable;
import java.util.*;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class modelling an abstract automaton.
 *
 * <p>See the repository wiki for more architectural information
 *
 * @param <S> The type of symbols read by this automaton
 * @param <T> The type of states in this automaton
 * @param <Config> The type of configuration an automaton subclass uses to store its computatoin
 *     state
 * @param <Trans> The type of transition an automaton subclass uses
 */
public abstract class Automaton<
                T extends Serializable,
                S extends Serializable,
                Config extends Configuration<T, S>,
                Trans extends ITransition<T, S, Config>>
        implements Serializable {
    protected Set<T> states;
    protected Alphabet<S> alphabet;
    protected Set<T> initialStates;
    protected Transitions<T, Trans> transitions;

    protected Automaton(
            Collection<T> states,
            Alphabet<S> alphabet,
            Collection<T> initialStates,
            Transitions<T, Trans> transitions) {
        Objects.requireNonNull(states);
        Objects.requireNonNull(alphabet);
        Objects.requireNonNull(initialStates);

        if (!states.containsAll(initialStates))
            throw new IllegalArgumentException("Start states are not part of state set!");

        this.states = new LinkedHashSet<>(states);
        this.alphabet = alphabet;
        this.initialStates = new LinkedHashSet<>(initialStates);
        this.transitions = transitions;
    }

    protected Automaton() {}

    public abstract DeterminacyFaultCollection<
                    T, ? extends DeterminacyFault<T, DeterminacyFaultReason>>
            checkDeterminacy();

    public abstract <U extends Serializable>
            Automaton<U, S, ? extends Configuration<U, S>, ? extends ITransition<U, S, ?>>
                    mapStates(SerializableFunction<T, U> homomorphism);

    public abstract <R extends Serializable>
            Automaton<T, R, ? extends Configuration<T, R>, ? extends ITransition<T, R, ?>>
                    mapAlphabet(SerializableFunction<S, R> homomorphism);

    public boolean isTotal() {
        return !checkDeterminacy().hasTotalityFaults();
    }

    public boolean isDeterministic() {
        return !checkDeterminacy().hasDeterminismFaults();
    }

    public abstract boolean isHaltingConfiguration(Config config);

    public abstract boolean isAcceptingConfiguration(Config config);

    /**
     * Returns all possible start configurations of {@code this} NFA with the given {@code word},
     * i.e. a separate configuration for every initial state with the original {@code word}.
     *
     * @param word The word the configuration is supposed to be created with.
     * @return A Set which contains all possible start configurations.
     */
    public abstract Set<Config> getAllStartConfigurations(Word<S> word);

    public Set<T> getStates() {
        return Collections.unmodifiableSet(states);
    }

    public Alphabet<S> getAlphabet() {
        return alphabet;
    }

    public Set<T> getInitialStates() {
        return Collections.unmodifiableSet(initialStates);
    }

    /**
     * Gets a copy of this automaton's {@link Transitions} object
     *
     * @return The automaton's transitions
     */
    public Transitions<T, Trans> getTransitions() {
        return new Transitions<>(transitions);
    }

    /**
     * Adds a new transition to this {@link Automaton}, returning {@code true} on success.
     *
     * @param from The state to which to add the new transition
     * @param trans The transition to add
     * @return {@code true} if the transition was added, {@code false} if the transition already
     *     existed
     */
    @Deprecated
    public boolean addTransition(T from, Trans trans) {
        transitions.addTransition(from, trans);
        return true;
    }

    private final HashMap<Config, Set<Trans>> cachedApplicableTransitions = new HashMap<>();

    /**
     * Computes the set of transitions that are applicable for the given configuration.
     *
     * <p>Calls {@link ITransition#isApplicable(Configuration)} for each transition of the
     * automaton.
     *
     * @param config The configuration
     * @return A set of transitions applicable to the automaton state stored in the given
     *     configuratoin
     */
    public Set<Trans> getApplicableTransitions(Config config) {
        Function<Config, Set<Trans>> computeApplicableTransitions =
                c ->
                        transitions.in(c.state).stream()
                                .filter(trans -> trans.isApplicable(c))
                                .collect(Collectors.toSet());
        return cachedApplicableTransitions.computeIfAbsent(config, computeApplicableTransitions);
    }

    @SafeVarargs
    public static <T extends Serializable> boolean stateDisjoint(
            Automaton<T, ?, ?, ?>... automata) {
        Set<T> states = new LinkedHashSet<>();
        for (Automaton<T, ?, ?, ?> automaton : automata)
            for (T state : automaton.states) {
                if (states.contains(state)) return false;
                states.add(state);
            }
        return true;
    }

    /**
     * Turns this {@link Automaton} into an {@link HashGraph} with states as vertices and edges
     * labeled with transitions
     *
     * @return A graph representation of this automaton
     */
    // TODO: Once the Automaton class is immutable, this graph can be cached!
    //  A: Not really, since graphs are mutable and we need to copy it anyway to ensure the user
    // does not manipulate our cached graph.
    public HashGraph<T, Trans> asGraph() {
        return asGraph(vertex -> vertex, edge -> edge);
    }

    /**
     * Turns this {@link Automaton} into an {@link HashGraph} with states as vertices and edges
     * labeled with transitions according to the two functions
     *
     * @return A graph representation of this automaton
     */
    public <VertexT extends Serializable, EdgeT extends Serializable>
            HashGraph<VertexT, EdgeT> asGraph(
                    Function<T, VertexT> stateToVertexValue,
                    Function<Trans, EdgeT> transitionToEdgeValue) {
        HashGraph<VertexT, EdgeT> graph = new HashGraph<>();

        for (T state : states) {
            graph.addVertex(stateToVertexValue.apply(state));
        }

        for (T state : states) {
            for (Trans trans : transitions.in(state))
                graph.addEdge(
                        stateToVertexValue.apply(state),
                        stateToVertexValue.apply(trans.getState()),
                        transitionToEdgeValue.apply(trans));
        }

        return graph;
    }

    /**
     * Computes the set of states reachable from this {@link Automaton}'s start states
     *
     * @return The set of reachable states.
     */
    public Set<T> getReachableStates() {
        Graph<T, Trans> graph = asGraph();

        Set<T> reachable = new LinkedHashSet<>();
        int numberOfStates = states.size();

        for (T start : initialStates) {
            reachable.addAll(graph.getReachableValues(graph.getVertex(start)));

            // Break the loop if all states are reachable
            if (reachable.size() == numberOfStates) {
                break;
            }
        }

        return reachable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Automaton<?, ?, ?, ?> automaton = (Automaton<?, ?, ?, ?>) o;
        return states.equals(automaton.states)
                && alphabet.equals(automaton.alphabet)
                && initialStates.equals(automaton.initialStates)
                && transitions.equals(automaton.transitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(states, alphabet, initialStates, transitions);
    }
}

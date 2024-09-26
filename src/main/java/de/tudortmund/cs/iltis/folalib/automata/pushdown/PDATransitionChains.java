package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.transform.ConstrainedSupplier;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A set of chains required to substitute a given PDATransition.
 *
 * <p>Note: Wildcards are only made concrete if necessary.
 *
 * @param <T> The type of the states of the PDA
 * @param <S> The type of the input symbols of the PDA
 * @param <K> The type of the stack symbols of the PDA
 */
public final class PDATransitionChains<
                T extends Serializable, S extends Serializable, K extends Serializable>
        implements Iterable<PDATransitionChains.PDATransitionChain<T, S, K>> {

    private final List<PDATransitionChain<T, S, K>> chains = new ArrayList<>();
    private final ConstrainedSupplier<T> stateSupplier;

    private final int MAX_LENGTH = 2;

    public PDATransitionChains(
            T source,
            PDATransition<T, S, K> transition,
            Alphabet<K> stackAlphabet,
            ConstrainedSupplier<T> stateSupplier) {
        this.stateSupplier = stateSupplier;
        stateSupplier.constrain(source);
        stateSupplier.constrain(transition.getState());
        /* If the transition is transformed into a chain of exactly one step potentially contained wildcards can remain:
         * the reason is that the wildcard will be replaced with the correct stack symbol when this single transition is
         * triggered.
         * If, however, we need to transform a transition into multiple steps we CANNOT keep the wildcards, because the
         * wildcard in later steps would be replaced with the topmost stack symbol of the previous step and NOT the
         * topmost stack symbol at the time the first step was triggered.
         */
        if (transition.getNewTopOfStack().size() <= MAX_LENGTH) {
            PDATransitionChain<T, S, K> chain = new PDATransitionChain<>(source);
            chain.addStep(transition);
            chains.add(chain);
        } else {
            for (PDATransition<T, S, K> concreteTransition :
                    transition.substituteWildcards(stackAlphabet)) {
                buildChain(source, concreteTransition);
            }
        }
    }

    private void buildChain(T source, PDATransition<T, S, K> transition) {
        PDAStackWord<K> word = transition.getNewTopOfStack();

        List<PDAStackWord<K>> chunks = chunks(word);
        List<T> states = generateStateChain(source, transition.getState(), chunks.size() - 1);

        PDATransitionChain<T, S, K> chain = new PDATransitionChain<>(source);

        T nextState;
        PDAStackWord<K> chunk;
        K lastSymbol;
        PDATransition<T, S, K> trans;

        for (int i = 0; i < chunks.size(); ++i) {
            nextState = states.get(i + 1);
            chunk = chunks.get(i);
            if (i == 0) {
                trans =
                        new PDATransition<>(
                                nextState,
                                transition.getInputSymbol(),
                                transition.getStackSymbol(),
                                chunk);
            } else {
                // At this point we know for sure that |chunk| == 2
                // Proof: |chunk| < 2
                // => |chunks| == 1
                // => 0 <= i < 1
                // => i == 0
                // => we never enter this branch
                lastSymbol = chunk.get(1);
                trans = new PDATransition<>(nextState, PDAStackSymbol.exactly(lastSymbol), chunk);
            }
            chain.addStep(trans);
        }

        chains.add(chain);
    }

    private List<PDAStackWord<K>> chunks(PDAStackWord<K> word) {
        // word = "" => chunks = {""}
        // word = "123" => chunks = {"23", "12"}
        // word = "14523143" => chunks = {"43", "14", "31", "23", "52", "45", "14"}
        List<PDAStackWord<K>> chunks = new ArrayList<>();

        if (word.size() <= MAX_LENGTH) {
            chunks.add(word);
            return chunks;
        }
        int length = word.size();
        for (int i = length - 2; i >= 0; --i) {
            PDAStackWord<K> subWord = word.drop(i).take(2);
            chunks.add(subWord);
        }
        return chunks;
    }

    private List<T> generateStateChain(T source, T destination, int count) {
        List<T> states = new ArrayList<>();
        states.add(source);
        for (int i = 0; i < count; ++i) {
            states.add(stateSupplier.get());
        }
        states.add(destination);
        return states;
    }

    /** Get a list of all contained chains */
    public List<PDATransitionChain<T, S, K>> getChains() {
        return new ArrayList<>(chains);
    }

    /**
     * Get the chain at the i-th index
     *
     * @throws IndexOutOfBoundsException iff {@code index < 0} or {@code index >=
     *     getNumberOfChains() }
     */
    public PDATransitionChain<T, S, K> getChainAt(int index) {
        return new PDATransitionChain<>(chains.get(index));
    }

    /** Get the number of chains */
    public int getNumberOfChains() {
        return chains.size();
    }

    @Override
    public Iterator<PDATransitionChain<T, S, K>> iterator() {
        return chains.iterator();
    }

    /**
     * A chain of transitions in a PDA such that the destination state of each step is the starting
     * state of the next step
     *
     * @param <T> The type of states of the PDA
     * @param <S> The type of input symbols of the PDA
     * @param <K> The type of stack symbols of the PDA
     */
    public static class PDATransitionChain<
                    T extends Serializable, S extends Serializable, K extends Serializable>
            implements Iterable<Pair<T, PDATransition<T, S, K>>> {

        private final LinkedList<Pair<T, PDATransition<T, S, K>>> steps;
        private T current;

        /**
         * Initialize a new chain starting at the given state
         *
         * @param source the source of the new chain
         */
        public PDATransitionChain(T source) {
            current = source;
            steps = new LinkedList<>();
        }

        /** Create a new transition chain from an existing one */
        public PDATransitionChain(PDATransitionChain<T, S, K> chain) {
            this(chain.getSource());
            for (Pair<T, PDATransition<T, S, K>> step : chain) {
                addStep(step.second());
            }
        }

        /**
         * Add a single step to this chain
         *
         * @param transition the transition of the step
         */
        public void addStep(PDATransition<T, S, K> transition) {
            steps.add(new Pair<>(current, transition));
            current = transition.getState();
        }

        /**
         * Get the source of this chain
         *
         * @return the first state of this chain
         */
        public T getSource() {
            return steps.isEmpty() ? current : steps.getFirst().first();
        }

        /**
         * Get the last state of this chain
         *
         * @return the currently last state of this chain
         */
        public T getCurrent() {
            return current;
        }

        /** Return the number of steps in this chain */
        public int size() {
            return steps.size();
        }

        /**
         * Get the i-th step in this chain
         *
         * @throws IndexOutOfBoundsException iff {@code i < 0} or {@code i >= size()}
         */
        public Pair<T, PDATransition<T, S, K>> get(int index) {
            return steps.get(index);
        }

        @Override
        public Iterator<Pair<T, PDATransition<T, S, K>>> iterator() {
            return steps.iterator();
        }
    }
}

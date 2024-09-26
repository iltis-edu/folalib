package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import de.tudortmund.cs.iltis.folalib.automata.ITransition;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A transition in a PDA.
 *
 * @param <T> The type of states
 * @param <S> The type of input symbols
 * @param <K> The type of the stack symbols
 */
public class PDATransition<T extends Serializable, S extends Serializable, K extends Serializable>
        implements ITransition<T, S, PDAConfiguration<T, S, K>> {
    private T state;
    private S symbol;
    private PDAStackSymbol<K> topOfStack;
    private PDAStackWord<K>
            newTopOfStack; // leftmost symbol becomes topmost symbol of the stack, null represents

    // wildcards

    public PDATransition(
            T state, S symbol, PDAStackSymbol<K> topOfStack, PDAStackWord<K> newTopOfStack) {
        this.state = state;
        this.symbol = symbol;
        this.topOfStack = topOfStack;
        this.newTopOfStack = newTopOfStack;
    }

    public PDATransition(T state, PDAStackSymbol<K> topOfStack, PDAStackWord<K> newTopOfStack) {
        this(state, null, topOfStack, newTopOfStack);
    }

    /* For serialization */
    @SuppressWarnings("unused")
    private PDATransition() {}

    @Override
    public boolean isApplicable(PDAConfiguration<T, S, K> configuration) {
        boolean stackIncompatible =
                configuration.isStackEmpty()
                        || !configuration.getTopOfStack().isPresent()
                        || !topOfStack.matches(configuration.getTopOfStack().get());
        if (stackIncompatible) {
            return false;
        }
        return isEpsilon()
                || configuration.hasSymbol() && configuration.getCurrentSymbol().equals(symbol);
    }

    @Override
    public PDAConfiguration<T, S, K> fire(PDAConfiguration<T, S, K> configuration) {
        Deque<K> stack = getNextStack(configuration);
        return getNextConfiguration(configuration, configuration.getWord(), stack);
    }

    private PDAConfiguration<T, S, K> getNextConfiguration(
            PDAConfiguration<T, S, K> configuration, Word<S> word, Deque<K> stack) {
        if (isEpsilon())
            return new PDAConfiguration<>(state, word, stack, configuration.getPosition());
        else return new PDAConfiguration<>(state, word, stack, configuration.getPosition() + 1);
    }

    private Deque<K> getNextStack(PDAConfiguration<T, S, K> configuration) {
        if (!configuration.getTopOfStack().isPresent()) {
            throw new RuntimeException(
                    "Cannot compute new stack after transition for a configuration with empty stack.");
        }
        Deque<K> stack = new LinkedList<>(configuration.getStack());
        stack.pop(); /* remove old topmost symbol because newTopOfStack contains it again */
        PDAStackWord<K> concreteNewTopOfStack =
                newTopOfStack.substituteWildcards(configuration.getTopOfStack().get());
        for (K k : concreteNewTopOfStack.reverse()) {
            stack.push(k);
        }
        return stack;
    }

    @Override
    public boolean isEpsilon() {
        return symbol == null;
    }

    /**
     * Checks whether the word which replaces the top of the stack contains a wildcard
     *
     * <p>Note: this is *not* equivalent to {@code !PDAStackSymbol.isVariable()}, because even if
     * the {@code PDAStackSymbol} is variable (e.g. a wildcard), it does not necessarily mean that
     * the resulting new top of the stack references the symbol that was matched by the wildcard.
     *
     * @return true iff there is at least one occurrence of a wildcard in the new top of the stack
     */
    public boolean containsWildcard() {
        return newTopOfStack.containsWildcard();
    }

    /**
     * Get all new PDATransition which result from substituting the wildcards with each element of
     * the given alphabet
     *
     * @param alphabet The symbols to substitute the wildcards with
     * @return a list of all concrete {@code PDATransition}s which all together are equivalent to
     *     the original transition
     */
    public Set<PDATransition<T, S, K>> substituteWildcards(Alphabet<K> alphabet) {
        if (!topOfStack.isVariable() && !containsWildcard()) {
            return Collections.singleton(
                    new PDATransition<>(state, symbol, topOfStack, newTopOfStack));
        }
        return alphabet.stream()
                .filter(k -> topOfStack.matches(k))
                .map(
                        k ->
                                new PDATransition<>(
                                        state,
                                        symbol,
                                        PDAStackSymbol.exactly(k),
                                        newTopOfStack.substituteWildcards(k)))
                .collect(Collectors.toSet());
    }

    @Override
    public T getState() {
        return state;
    }

    /**
     * Return the input symbol consumed by this transitions or null iff this is an epsilon
     * transition
     *
     * @return The input symbol
     */
    public S getInputSymbol() {
        return symbol;
    }

    /**
     * Return the stack symbol that determines whether this transition matches a given symbol of the
     * stack alphabet
     *
     * @return The stack symbol
     */
    public PDAStackSymbol<K> getStackSymbol() {
        return topOfStack;
    }

    /**
     * Return the word that replaces the topmost stack symbol when this transition is triggered
     *
     * @return The new topmost stack symbols
     */
    public PDAStackWord<K> getNewTopOfStack() {
        return newTopOfStack;
    }

    @Override
    public String toString() {
        return "-- "
                + (symbol == null ? "ε" : symbol)
                + ", "
                + topOfStack
                + " : "
                + newTopOfStack
                + " -> "
                + state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PDATransition<?, ?, ?> that = (PDATransition<?, ?, ?>) o;
        return Objects.equals(state, that.state)
                && Objects.equals(symbol, that.symbol)
                && Objects.equals(topOfStack, that.topOfStack)
                && Objects.equals(newTopOfStack, that.newTopOfStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, symbol, topOfStack, newTopOfStack);
    }
}

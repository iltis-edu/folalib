package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import de.tudortmund.cs.iltis.folalib.automata.Configuration;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import java.io.Serializable;
import java.util.*;

/**
 * The configuration of a {@link PDA} including the current state, the input left to process and the
 * current stack
 *
 * @param <T> The type of the states
 * @param <S> The type of the input alphabet
 * @param <K> The type of the stack alphabet
 */
public class PDAConfiguration<T extends Serializable, S extends Serializable, K>
        extends Configuration<T, S> {
    private final Deque<K> stack;

    public PDAConfiguration(T state, Word<S> word, Deque<K> stack) {
        this(state, word, stack, 0);
    }

    public PDAConfiguration(T state, Word<S> word, Deque<K> stack, int position) {
        super(state, word, position);
        this.stack = stack;
    }

    /**
     * Return whether the stack is empty in this configuration
     *
     * @return {@code true} iff the stack is empty
     */
    public boolean isStackEmpty() {
        return stack.isEmpty();
    }

    /**
     * Return the topmost symbol of the stack
     *
     * <p>This function assumes, that {@link PDAConfiguration#isStackEmpty} returns {@code true}
     *
     * @return the topmost stack symbol
     */
    public Optional<K> getTopOfStack() {
        return Optional.ofNullable(stack.peek());
    }

    /**
     * Return the stack in this configuration
     *
     * @return the stack of this configuration
     */
    public Deque<K> getStack() {
        return new LinkedList<>(stack);
    }

    @Override
    public String toString() {
        return "(" + state + ", " + word.drop(position) + ", " + stack + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PDAConfiguration<?, ?, ?> that = (PDAConfiguration<?, ?, ?>) o;
        return Objects.equals(stack, that.stack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack);
    }
}

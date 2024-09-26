package de.tudortmund.cs.iltis.folalib.grammar.production;

import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * The base class for productions of grammars. This class provides fields for the left-hand and the
 * right-hand side of a production. Subclasses should only implement convenience getters/methods if
 * necessary and <b>should not</b> add additional fields (see also {@link
 * Production#equals(Object)}).
 *
 * @param <T> the type of terminals in the grammar
 * @param <N> the type of non-terminals in the grammar
 */
public class Production<T extends Serializable, N extends Serializable> implements Serializable {
    private SententialForm<T, N> lhs;
    private SententialForm<T, N> rhs;

    /* For serialization */
    @SuppressWarnings("unused")
    protected Production() {
        super();
    }

    /**
     * Create a new production object with the given left- and right-hand sentential forms
     *
     * @param lhs the left-hand sentential form
     * @param rhs the right-hand sentential form
     * @throws NullPointerException if any of {@code lhs} or {@code rhs} is {@code null}.
     */
    public Production(SententialForm<T, N> lhs, SententialForm<T, N> rhs) {
        Objects.requireNonNull(lhs);
        Objects.requireNonNull(rhs);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    /**
     * Get the left-hand-side of this production
     *
     * @return the left-hand-side
     */
    public SententialForm<T, N> getLhs() {
        return lhs;
    }

    /**
     * Get the right-hand-side of this production
     *
     * @return the right-hand-side
     */
    public SententialForm<T, N> getRhs() {
        return rhs;
    }

    @Override
    public String toString() {
        return getLhs() + " -> " + getRhs();
    }

    /**
     * Tests whether the given object is equal to {@code this}
     *
     * <p>This method tests for inter-type equality, which means that different subclasses of {@link
     * Production} can be equal to one another or even to an instance of {@code Production} itself.
     * The idea is that all productions have a left-hand and a right-hand side and subclasses only
     * add additional getters for convenience but no fields.
     *
     * <p>This is also the reason that {@code equals} is final. If this is too restrictive for your
     * subclass, e.g. because it has a field, it is likely that this implementation of {@code
     * equals} is no longer correct wrt. symmetry and/or transitivity.
     *
     * @param o the object to compare {@code this} to
     * @return {@code true} if {@code o} is equal to this, {@code false} otherwise
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Production)) return false;
        Production<?, ?> that = (Production<?, ?>) o;
        return Objects.equals(lhs, that.lhs) && Objects.equals(rhs, that.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLhs(), getRhs());
    }

    /**
     * Get a set of all terminals that occur anywhere within this production
     *
     * @return a set of all terminals
     */
    public Set<T> getTerminals() {
        Set<T> terminals = getLhs().getTerminals();
        terminals.addAll(getRhs().getTerminals());
        return terminals;
    }

    /**
     * Get a set of all non-terminals that occur anywhere within this production
     *
     * @return a set off all non-terminals
     */
    public Set<N> getNonTerminals() {
        Set<N> nonTerminals = getLhs().getNonTerminals();
        nonTerminals.addAll(getRhs().getNonTerminals());
        return nonTerminals;
    }

    /**
     * Map functions `terminalMap` and `nonTerminalMap` over the terminals and non-terminals of this
     * production
     *
     * @param terminalMap the function to apply to each terminal
     * @param nonTerminalMap the function to apply to each non-terminal
     * @param <S> the type of the terminals in the new production
     * @param <M> the type of the non-terminals in the new production
     * @return a new, isomorphic production with mapped terminals and non-terminals
     */
    public <S extends Serializable, M extends Serializable> Production<S, M> map(
            SerializableFunction<T, S> terminalMap, SerializableFunction<N, M> nonTerminalMap) {
        SententialForm<S, M> mappedLhs =
                getLhs().mapNonTerminals(nonTerminalMap).mapTerminals(terminalMap);
        SententialForm<S, M> mappedRhs =
                getRhs().mapNonTerminals(nonTerminalMap).mapTerminals(terminalMap);
        return new Production<>(mappedLhs, mappedRhs);
    }

    public <M extends Serializable> Production<T, M> mapNonTerminals(SerializableFunction<N, M> f) {
        return map(t -> t, f);
    }

    public <S extends Serializable> Production<S, N> mapTerminals(SerializableFunction<T, S> f) {
        return map(f, n -> n);
    }
}

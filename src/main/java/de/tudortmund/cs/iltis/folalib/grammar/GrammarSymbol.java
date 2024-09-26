package de.tudortmund.cs.iltis.folalib.grammar;

import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A fake algebraic data types modelling terminal and non-terminal symbols
 *
 * @param <T> The type of the terminal symbols
 * @param <N> The type of the non-terminal symbols
 */
public interface GrammarSymbol<T extends Serializable, N extends Serializable>
        extends Serializable {
    /**
     * Java function mimicing Rusts {@code match} expressions.
     *
     * <p>The java expression {@code U u = symbol.match(f, g)} is equivalent to the following Rust
     * code:
     *
     * <pre>
     * let u: U = match symbol {
     *     GrammarSymbol::Terminal(t) => f(t),
     *     GrammarSymbol::NonTerminal(n) => g(n)
     * };
     * </pre>
     *
     * provided {@code Grammar} symbol is implemented as the following algebraic data type:
     *
     * <pre>
     * enum GrammarSymbol&#60;T, N&#62; {
     *     Terminal(T),
     *     NonTerminal(N)
     * }
     * </pre>
     *
     * @param terminalMatch The function to execute if {@code this} is a terminal
     * @param nonTerminalMatch The function to execute if {@code this} is a non-terminal
     * @param <U> The result type
     * @return The result of evaluating either {@code terminalMatch} or {@code nonTerminalMatch}.
     */
    <U> U match(Function<T, U> terminalMatch, Function<N, U> nonTerminalMatch);

    /**
     * The same as {@link GrammarSymbol#match(Function, Function)}, just without return value
     *
     * @param terminalConsumer The consumer to call if {@code this} is a {@link Terminal}
     * @param nonTerminalConsumer The consumer to call if {@code this} is a {@link NonTerminal}
     */
    default void consume(Consumer<T> terminalConsumer, Consumer<N> nonTerminalConsumer) {
        match(
                t -> {
                    terminalConsumer.accept(t);
                    return null;
                },
                n -> {
                    nonTerminalConsumer.accept(n);
                    return null;
                });
    }

    /**
     * Checks whether this {@link GrammarSymbol} is a terminal
     *
     * @return {@code true} iff this GrammarSymbol is a terminal.
     */
    default boolean isTerminal() {
        return match(t -> true, n -> false);
    }

    /**
     * Checks whether this {@link GrammarSymbol} is a terminal
     *
     * @return {@code true} iff this GrammarSymbol is a terminal.
     */
    default boolean isNonTerminal() {
        return !isTerminal();
    }

    default T unwrapTerminal() {
        return match(
                t -> t,
                n -> {
                    throw new RuntimeException(".unwrapTerminal() called on NonTerminal variant");
                });
    }

    default N unwrapNonTerminal() {
        return match(
                t -> {
                    throw new RuntimeException(".unwrapTerminal() called on NonTerminal variant");
                },
                n -> n);
    }

    /**
     * Map a function over Grammar symbols, effectively changing the non-terminals
     *
     * <p>Terminal symbols must remain unaltered.
     *
     * @param f the function to apply
     * @param <M> The new type of non-terminals in the grammar symbol
     * @return a new GrammarSymbol
     */
    <M extends Serializable> GrammarSymbol<T, M> mapNonTerminals(SerializableFunction<N, M> f);

    <S extends Serializable> GrammarSymbol<S, N> mapTerminals(SerializableFunction<T, S> f);

    final class Terminal<T extends Serializable, N extends Serializable>
            implements GrammarSymbol<T, N> {
        private T terminal;

        public Terminal(T terminal) {
            Objects.requireNonNull(terminal);

            this.terminal = terminal;
        }

        /* For serialization */
        @SuppressWarnings("unused")
        private Terminal() {}

        @Override
        public <U> U match(Function<T, U> terminalMatch, Function<N, U> nonTerminalMatch) {
            return terminalMatch.apply(terminal);
        }

        @Override
        public String toString() {
            return terminal.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Terminal<?, ?> terminal1 = (Terminal<?, ?>) o;
            return terminal.equals(terminal1.terminal);
        }

        @Override
        public int hashCode() {
            return Objects.hash(terminal);
        }

        @Override
        public <M extends Serializable> GrammarSymbol<T, M> mapNonTerminals(
                SerializableFunction<N, M> f) {
            return new Terminal<>(terminal);
        }

        @Override
        public <S extends Serializable> GrammarSymbol<S, N> mapTerminals(
                SerializableFunction<T, S> f) {
            return new Terminal<>(f.apply(terminal));
        }
    }

    final class NonTerminal<T extends Serializable, N extends Serializable>
            implements GrammarSymbol<T, N> {
        private N nonTerminal;

        public NonTerminal(N nonTerminal) {
            Objects.requireNonNull(nonTerminal);

            this.nonTerminal = nonTerminal;
        }

        /* For serialization */
        @SuppressWarnings("unused")
        private NonTerminal() {}

        @Override
        public <U> U match(Function<T, U> terminalMatch, Function<N, U> nonTerminalMatch) {
            return nonTerminalMatch.apply(nonTerminal);
        }

        @Override
        public String toString() {
            return nonTerminal.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NonTerminal<?, ?> that = (NonTerminal<?, ?>) o;
            return nonTerminal.equals(that.nonTerminal);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nonTerminal);
        }

        @Override
        public <M extends Serializable> GrammarSymbol<T, M> mapNonTerminals(
                SerializableFunction<N, M> f) {
            return new NonTerminal<>(f.apply(nonTerminal));
        }

        @Override
        public <S extends Serializable> GrammarSymbol<S, N> mapTerminals(
                SerializableFunction<T, S> f) {
            return new NonTerminal<>(nonTerminal);
        }
    }
}

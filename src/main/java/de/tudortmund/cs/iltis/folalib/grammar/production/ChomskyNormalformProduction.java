package de.tudortmund.cs.iltis.folalib.grammar.production;

import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ChomskyNormalformProduction<T extends Serializable, N extends Serializable>
        extends ContextFreeProduction<T, N> {

    /* For serialization */
    @SuppressWarnings("unused")
    private ChomskyNormalformProduction() {
        super();
    }

    protected ChomskyNormalformProduction(N lhsNonTerminal, SententialForm<T, N> rhs) {
        super(lhsNonTerminal, rhs);
    }

    public abstract <U> U matchRhs(
            Function<T, U> terminalProduction, BiFunction<N, N, U> nonTerminalProduction);

    public void consumeRhs(Consumer<T> terminalProduction, BiConsumer<N, N> nonTerminalProduction) {
        matchRhs(
                t -> {
                    terminalProduction.accept(t);
                    return null;
                },
                (a, b) -> {
                    nonTerminalProduction.accept(a, b);
                    return null;
                });
    }

    public ChomskyNormalformProduction<T, N> replaceNonTerminal(N original, N replacement) {
        N lhs = getLhsNonTerminal().equals(original) ? replacement : getLhsNonTerminal();

        return matchRhs(
                t -> new TerminalProduction<>(lhs, t),
                (a, b) ->
                        new TwoNonTerminalsProduction<>(
                                lhs,
                                a.equals(original) ? replacement : a,
                                b.equals(original) ? replacement : b));
    }

    @Override
    public <S extends Serializable, M extends Serializable> ChomskyNormalformProduction<S, M> map(
            SerializableFunction<T, S> terminalMap, SerializableFunction<N, M> nonTerminalMap) {
        return chomskyNormalformProductionFromProduction(super.map(terminalMap, nonTerminalMap))
                .get();
    }

    @Override
    public <M extends Serializable> ChomskyNormalformProduction<T, M> mapNonTerminals(
            SerializableFunction<N, M> f) {
        // Implementation identical to `super` but this is required to "specialise" the (covariant)
        // return type
        return map(t -> t, f);
    }

    @Override
    public <S extends Serializable> ChomskyNormalformProduction<S, N> mapTerminals(
            SerializableFunction<T, S> f) {
        // Implementation identical to `super` but this is required to "specialise" the (covariant)
        // return type
        return map(f, n -> n);
    }

    public static <S extends Serializable, M extends Serializable>
            Optional<ChomskyNormalformProduction<S, M>> chomskyNormalformProductionFromProduction(
                    Production<S, M> production) {
        if (TerminalProduction.terminalProductionFromProduction(production).isPresent()) {
            return Optional.of(
                    TerminalProduction.terminalProductionFromProduction(production).get());
        } else if (TwoNonTerminalsProduction.twoNonTerminalsProductionFromProduction(production)
                .isPresent()) {
            return Optional.of(
                    TwoNonTerminalsProduction.twoNonTerminalsProductionFromProduction(production)
                            .get());
        }
        return Optional.empty();
    }

    public static final class TerminalProduction<T extends Serializable, N extends Serializable>
            extends ChomskyNormalformProduction<T, N> {

        /**
         * Creates a new production of the form {@code X -> a}
         *
         * @param lhsNonTerminal The variable on the left-hand side (the {@code 'X'} in the above
         *     example)
         * @param terminal The terminal on the right-hand side (the {@code 'a'} in the above
         *     example)
         * @throws NullPointerException if either argument is {@code null}
         */
        public TerminalProduction(N lhsNonTerminal, T terminal) {
            super(lhsNonTerminal, new SententialForm<T, N>(new GrammarSymbol.Terminal<>(terminal)));
        }

        /* For serialization */
        @SuppressWarnings("unused")
        private TerminalProduction() {
            super();
        }

        @Override
        public <U> U matchRhs(
                Function<T, U> terminalProduction, BiFunction<N, N, U> nonTerminalProduction) {
            return terminalProduction.apply(getTerminal());
        }

        public T getTerminal() {
            // This is ok, because our constructor guarantees that the right-hand side is exactly
            // one terminal
            return getRhs().get(0).unwrapTerminal();
        }

        @Override
        public <S extends Serializable, M extends Serializable> TerminalProduction<S, M> map(
                SerializableFunction<T, S> terminalMap, SerializableFunction<N, M> nonTerminalMap) {
            return terminalProductionFromProduction(super.map(terminalMap, nonTerminalMap)).get();
        }

        @Override
        public <M extends Serializable> TerminalProduction<T, M> mapNonTerminals(
                SerializableFunction<N, M> f) {
            // Implementation identical to `super` but this is required to "specialise" the
            // (covariant) return type
            return map(t -> t, f);
        }

        @Override
        public <S extends Serializable> TerminalProduction<S, N> mapTerminals(
                SerializableFunction<T, S> f) {
            // Implementation identical to `super` but this is required to "specialise" the
            // (covariant) return type
            return map(f, n -> n);
        }

        public static <S extends Serializable, M extends Serializable>
                Optional<TerminalProduction<S, M>> terminalProductionFromProduction(
                        Production<S, M> production) {
            SententialForm<S, M> lhs = production.getLhs();
            SententialForm<S, M> rhs = production.getRhs();
            if (lhs.size() == 1 && lhs.get(0).isNonTerminal()) {
                if (rhs.size() == 1 && rhs.get(0).isTerminal()) {
                    return Optional.of(
                            new TerminalProduction<>(
                                    lhs.get(0).unwrapNonTerminal(), rhs.get(0).unwrapTerminal()));
                }
            }
            return Optional.empty();
        }
    }

    public static final class TwoNonTerminalsProduction<
                    T extends Serializable, N extends Serializable>
            extends ChomskyNormalformProduction<T, N> {

        /**
         * Creates a new production of the form {@code X -> AB}
         *
         * @param lhsNonTerminal The variable on the left-hand side (the {@code 'X'} in the above
         *     example)
         * @param firstNonTerminal The non-terminal on the right-hand side (the {@code 'A'} in the
         *     above example)
         * @param secondNonTerminal The non-terminal on the right-hand side (the {@code 'B'} in the
         *     above example)
         * @throws NullPointerException if any argument is {@code null}
         */
        public TwoNonTerminalsProduction(
                N lhsNonTerminal, N firstNonTerminal, N secondNonTerminal) {
            super(
                    lhsNonTerminal,
                    new SententialForm<T, N>(
                            new GrammarSymbol.NonTerminal<>(firstNonTerminal),
                            new GrammarSymbol.NonTerminal<>(secondNonTerminal)));
        }

        /* For serialization */
        @SuppressWarnings("unused")
        private TwoNonTerminalsProduction() {
            super();
        }

        @Override
        public <U> U matchRhs(
                Function<T, U> terminalProduction, BiFunction<N, N, U> nonTerminalProduction) {
            return nonTerminalProduction.apply(getFirstNonTerminal(), getSecondNonTerminal());
        }

        public N getFirstNonTerminal() {
            // This is ok, because our constructor guarantees that the right-hand side is exactly
            // two non-terminals
            return getRhs().get(0).unwrapNonTerminal();
        }

        public N getSecondNonTerminal() {
            // This is ok, because our constructor guarantees that the right-hand side is exactly
            // two non-terminals
            return getRhs().get(1).unwrapNonTerminal();
        }

        @Override
        public <S extends Serializable, M extends Serializable> TwoNonTerminalsProduction<S, M> map(
                SerializableFunction<T, S> terminalMap, SerializableFunction<N, M> nonTerminalMap) {
            return twoNonTerminalsProductionFromProduction(super.map(terminalMap, nonTerminalMap))
                    .get();
        }

        @Override
        public <M extends Serializable> TwoNonTerminalsProduction<T, M> mapNonTerminals(
                SerializableFunction<N, M> f) {
            // Implementation identical to `super` but this is required to "specialise" the
            // (covariant) return type
            return map(t -> t, f);
        }

        @Override
        public <S extends Serializable> TwoNonTerminalsProduction<S, N> mapTerminals(
                SerializableFunction<T, S> f) {
            // Implementation identical to `super` but this is required to "specialise" the
            // (covariant) return type
            return map(f, n -> n);
        }

        public static <S extends Serializable, M extends Serializable>
                Optional<TwoNonTerminalsProduction<S, M>> twoNonTerminalsProductionFromProduction(
                        Production<S, M> production) {
            SententialForm<S, M> lhs = production.getLhs();
            SententialForm<S, M> rhs = production.getRhs();
            if (lhs.size() == 1 && lhs.get(0).isNonTerminal()) {
                if (rhs.size() == 2 && rhs.get(0).isNonTerminal() && rhs.get(1).isNonTerminal()) {
                    return Optional.of(
                            new TwoNonTerminalsProduction<>(
                                    lhs.get(0).unwrapNonTerminal(),
                                    rhs.get(0).unwrapNonTerminal(),
                                    rhs.get(1).unwrapNonTerminal()));
                }
            }
            return Optional.empty();
        }
    }
}

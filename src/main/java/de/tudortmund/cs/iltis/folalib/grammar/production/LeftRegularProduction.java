package de.tudortmund.cs.iltis.folalib.grammar.production;

import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * This class represents a production of a left regular grammar. In addition to being context free
 * (i.e. the left-hand-side can only be a single non-terminal symbol) a left regular grammar either
 * produces 1) an epsilon 2) a single terminal 3) a single non-terminal followed by a single
 * terminal
 *
 * @param <T> the type of terminals of the grammar
 * @param <N> the type of non-terminals of the grammar
 */
public class LeftRegularProduction<T extends Serializable, N extends Serializable>
        extends ContextFreeProduction<T, N> {

    /**
     * Creates a new {@link LeftRegularProduction} of the form {@code X -> eps}.
     *
     * @param lhsNonTerminal The variable on the left hand side (the {@code 'X'} in the above
     *     example)
     * @throws NullPointerException if {@code lhsNonTerminal} is {@code null}
     */
    public LeftRegularProduction(N lhsNonTerminal) {
        super(lhsNonTerminal, buildRhs(null, null));
    }

    /**
     * Creates a new {@link LeftRegularProduction} of the form {@code X -> a}
     *
     * @param lhsNonTerminal The variable on the left hand side (the {@code 'X'} in the above
     *     example)
     * @param rhsTerminal The terminal on the right hand side (the {@code 'a'} in the above example)
     * @throws NullPointerException if any of {@code lhsNonTerminal} or {@code rhsTerminal} is
     *     {@code null}
     */
    public LeftRegularProduction(N lhsNonTerminal, T rhsTerminal) {
        super(lhsNonTerminal, buildRhs(null, Objects.requireNonNull(rhsTerminal)));
    }

    /**
     * Creates a new {@link LeftRegularProduction} of the form {@code X -> Ya}
     *
     * @param lhsNonTerminal The variable on the left-hand side (the {@code 'X'} in the above
     *     example)
     * @param rhsNonTerminal The variable on the right-hand side (the {@code 'Y'} in the above
     *     example)
     * @param rhsTerminal The terminal on the right-hand side (the {@code 'a'} in the above example)
     * @throws NullPointerException if any argument is {@code null}
     */
    public LeftRegularProduction(N lhsNonTerminal, N rhsNonTerminal, T rhsTerminal) {
        super(
                lhsNonTerminal,
                buildRhs(
                        Objects.requireNonNull(rhsNonTerminal),
                        Objects.requireNonNull(rhsTerminal)));
    }

    private static <T extends Serializable, N extends Serializable> SententialForm<T, N> buildRhs(
            N rhsNonTerminal, T rhsTerminal) {
        ArrayList<GrammarSymbol<T, N>> result = new ArrayList<>();
        if (rhsNonTerminal != null) {
            result.add(new GrammarSymbol.NonTerminal<>(rhsNonTerminal));
        }
        if (rhsTerminal != null) {
            result.add(new GrammarSymbol.Terminal<>(rhsTerminal));
        }
        return new SententialForm<>(result);
    }

    /**
     * Get the terminal on the right-hand-side of this production if present
     *
     * @return the (unique) terminal on the right-hand-side or {@code Optional.empty()} if there is
     *     no terminal
     */
    public Optional<T> getRhsTerminal() {
        // This is ok, because our constructor guarantees that the terminal can only be the last
        // symbol on the right
        if (!getRhs().isEmpty()) {
            int last = getRhs().size() - 1;
            if (getRhs().get(last).isTerminal()) {
                return Optional.of(getRhs().get(last).unwrapTerminal());
            }
        }
        return Optional.empty();
    }

    /**
     * Get the non-terminal on the right-hand-side of this production if present
     *
     * @return the (unique) non-terminal on the right-hand-side or {@code Optional.empty()} if there
     *     is no non-terminal
     */
    public Optional<N> getRhsNonTerminal() {
        // This is ok, because our constructor guarantees that the non-terminal can only be the
        // first symbol on the right
        if (!getRhs().isEmpty()) {
            if (getRhs().get(0).isNonTerminal()) {
                return Optional.of(getRhs().get(0).unwrapNonTerminal());
            }
        }
        return Optional.empty();
    }

    @Override
    public <S extends Serializable, M extends Serializable> LeftRegularProduction<S, M> map(
            SerializableFunction<T, S> terminalMap, SerializableFunction<N, M> nonTerminalMap) {
        return leftRegularProductionFromProduction(super.map(terminalMap, nonTerminalMap)).get();
    }

    @Override
    public <M extends Serializable> LeftRegularProduction<T, M> mapNonTerminals(
            SerializableFunction<N, M> f) {
        // Implementation identical to `super` but this is required to "specialise" the (covariant)
        // return type
        return map(t -> t, f);
    }

    @Override
    public <S extends Serializable> LeftRegularProduction<S, N> mapTerminals(
            SerializableFunction<T, S> f) {
        // Implementation identical to `super` but this is required to "specialise" the (covariant)
        // return type
        return map(f, n -> n);
    }

    public static <S extends Serializable, M extends Serializable>
            Optional<LeftRegularProduction<S, M>> leftRegularProductionFromProduction(
                    Production<S, M> production) {
        SententialForm<S, M> lhs = production.getLhs();
        SententialForm<S, M> rhs = production.getRhs();
        if (lhs.size() == 1 && lhs.get(0).isNonTerminal()) {
            M lhsNonTerminal = lhs.get(0).unwrapNonTerminal();
            if (rhs.isEmpty()) {
                return Optional.of(new LeftRegularProduction<>(lhsNonTerminal));
            } else if (rhs.size() == 1 && rhs.get(0).isTerminal()) {
                S rhsTerminal = rhs.get(0).unwrapTerminal();
                return Optional.of(new LeftRegularProduction<>(lhsNonTerminal, rhsTerminal));
            } else if (rhs.size() == 2 && rhs.get(0).isNonTerminal() && rhs.get(1).isTerminal()) {
                M rhsNonTerminal = rhs.get(0).unwrapNonTerminal();
                S rhsTerminal = rhs.get(1).unwrapTerminal();
                return Optional.of(
                        new LeftRegularProduction<>(lhsNonTerminal, rhsNonTerminal, rhsTerminal));
            }
        }
        return Optional.empty();
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private LeftRegularProduction() {
        super();
    }
}

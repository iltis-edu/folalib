package de.tudortmund.cs.iltis.folalib.grammar.production;

import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.Optional;

/**
 * A class which represents context-free productions. This means that the left-hand-side can only be
 * a single non-terminal symbol.
 *
 * @param <T> the type of terminals in the grammar
 * @param <N> the type of non-terminals in the grammar
 */
public class ContextFreeProduction<T extends Serializable, N extends Serializable>
        extends Production<T, N> {

    /**
     * Create a new ContextFreeProduction which produces epsilon
     *
     * @param lhsNonTerminal the non-terminal on the left-hand side
     * @throws NullPointerException if {@code lhsNonTerminal} is {@code null}
     */
    public ContextFreeProduction(N lhsNonTerminal) {
        this(lhsNonTerminal, new SententialForm<>());
    }

    /**
     * Create a new ContextFreeProduction with the given non-terminal on the left and the sentential
     * form on the right
     *
     * @param lhsNonTerminal the non-terminal of the left-hand side
     * @param rhs the right-hand side of the production
     * @throws NullPointerException if any of {@code lhsNonTerminal} or {@code rhs} is {@code null}
     */
    public ContextFreeProduction(N lhsNonTerminal, SententialForm<T, N> rhs) {
        super(new SententialForm<>(new GrammarSymbol.NonTerminal<>(lhsNonTerminal)), rhs);
    }

    /**
     * Get the single non-terminal symbol on the left-hand-side of this production
     *
     * <p>This is a convenience getter.
     *
     * @return the single non-terminal symbol on the left
     */
    public N getLhsNonTerminal() {
        // This is ok, because our constructor guarantees that `lhs` is exactly one nonTerminal
        // symbol
        return getLhs().get(0).unwrapNonTerminal();
    }

    @Override
    public <S extends Serializable, M extends Serializable> ContextFreeProduction<S, M> map(
            SerializableFunction<T, S> terminalMap, SerializableFunction<N, M> nonTerminalMap) {
        return contextFreeProductionFromProduction(super.map(terminalMap, nonTerminalMap)).get();
    }

    @Override
    public <M extends Serializable> ContextFreeProduction<T, M> mapNonTerminals(
            SerializableFunction<N, M> f) {
        // Implementation identical to `super` but this is required to "specialise" the (covariant)
        // return type
        return map(t -> t, f);
    }

    @Override
    public <S extends Serializable> ContextFreeProduction<S, N> mapTerminals(
            SerializableFunction<T, S> f) {
        // Implementation identical to `super` but this is required to "specialise" the (covariant)
        // return type
        return map(f, n -> n);
    }

    public static <S extends Serializable, M extends Serializable>
            Optional<ContextFreeProduction<S, M>> contextFreeProductionFromProduction(
                    Production<S, M> production) {
        if (production.getLhs().size() == 1 && production.getLhs().get(0).isNonTerminal()) {
            return Optional.of(
                    new ContextFreeProduction<>(
                            production.getLhs().get(0).unwrapNonTerminal(), production.getRhs()));
        }
        return Optional.empty();
    }

    /* For serialization */
    @SuppressWarnings("unused")
    protected ContextFreeProduction() {
        super();
    }
}

package de.tudortmund.cs.iltis.folalib.automata.pushdown.conversion;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.*;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An ABC which offers functionality to convert a given {@link PDA} to a PDA with specific
 * properties such that both PDAs decide the same language.
 *
 * @param <T> The type of states in the original PDA
 * @param <U> The type of states in the converted PDA
 * @param <S> The type of the input symbols
 * @param <K> The type of stack symbols in the original PDA
 * @param <J> The type of stack symbols in the converted PDA
 */
public abstract class PDAConversion<
                T extends Serializable,
                U extends Serializable,
                S extends Serializable,
                K extends Serializable,
                J extends Serializable>
        implements SerializableFunction<PDA<T, S, K>, PDA<U, S, J>> {

    /**
     * The actual conversion method which each subclass needs to implement
     *
     * <p><b>Note:</b> This method must not be called directly. Instead, use {@code apply()} instead
     * which calls this method internally. Otherwise, the redundancy check is omitted and repeated
     * calls are not idempotent.
     *
     * @param pda the PDA to convert
     * @return a new PDA
     */
    protected abstract PDA<U, S, J> convert(PDA<T, S, K> pda);

    /**
     * An identity conversion which does effectively nothing but is required to satisfy Java's type
     * checker
     *
     * @param pda the PDA to convert
     * @return a new PDA
     */
    protected abstract PDA<U, S, J> identity(PDA<T, S, K> pda);

    /**
     * Checks whether a conversion is redundant for the given PDA
     *
     * @param pda the PDA which should be converted
     * @return {@code true} iff the given PDA does not need to be converted
     */
    protected abstract boolean isRedundant(PDA<T, S, K> pda);

    /**
     * Applies this conversion or the identity conversion to the given PDA, depending on whether
     * this conversion is redundant
     *
     * <p>It is guaranteed that this method always returns a _new_ PDA object.
     *
     * @param pda the PDA to convert
     * @return a new PDA
     */
    @Override
    public final PDA<U, S, J> apply(PDA<T, S, K> pda) {
        return isRedundant(pda) ? identity(pda) : convert(pda);
    }
}

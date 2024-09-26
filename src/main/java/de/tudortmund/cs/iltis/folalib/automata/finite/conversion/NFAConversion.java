package de.tudortmund.cs.iltis.folalib.automata.finite.conversion;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * Converts a given {@link NFA} to a NFA with specific properties such that both NFAs decide the
 * same language
 *
 * @param <originalStateType> The type of states in the original NFA
 * @param <convertedStateType> The type of states in the converted NFA
 * @param <inputSymbolType> The type of the input symbols
 */
public abstract class NFAConversion<
                originalStateType extends Serializable,
                convertedStateType extends Serializable,
                inputSymbolType extends Serializable>
        implements SerializableFunction<
                NFA<originalStateType, inputSymbolType>, NFA<convertedStateType, inputSymbolType>> {

    /**
     * The actual conversion method which each subclass needs to implement
     *
     * @param nfa the NFA to convert
     * @return a new NFA
     */
    protected abstract NFA<convertedStateType, inputSymbolType> convert(
            NFA<originalStateType, inputSymbolType> nfa);

    /**
     * An identity conversion which does effectively nothing but is required to satisfy Java's type
     * checker
     *
     * @param nfa the NFA to convert
     * @return a new NFA
     */
    protected abstract NFA<convertedStateType, inputSymbolType> identity(
            NFA<originalStateType, inputSymbolType> nfa);

    /**
     * Checks whether a conversion is redundant for the given NFA
     *
     * @param nfa the NFA which should be converted
     * @return {@code true} iff the given NFA does not need to be converted
     */
    protected abstract boolean isRedundant(NFA<originalStateType, inputSymbolType> nfa);

    /**
     * Applies this conversion or the identity conversion to the given PDA, depending on whether
     * this conversion is redundant
     *
     * <p>It is guaranteed that this method always returns a _new_ PDA object.
     *
     * @param nfa the NFA to convert
     * @return a new NFA
     */
    @Override
    public final NFA<convertedStateType, inputSymbolType> apply(
            NFA<originalStateType, inputSymbolType> nfa) {
        return isRedundant(nfa) ? identity(nfa) : convert(nfa);
    }
}

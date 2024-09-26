package de.tudortmund.cs.iltis.folalib.transform;

import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An abstract superclass for every conversion of any representation of a language. A conversion
 * converts a given input to an output with specific properties such that both decide the same
 * language. A conversion never changes the type of the representation, e.g. input and output both
 * need to be an {@link EpsilonNFA}.
 *
 * @param <Input> The type of the input
 * @param <Output> The type of the output
 */
public abstract class AbstractConversion<Input extends Serializable, Output extends Serializable>
        implements SerializableFunction<Input, Output> {

    /**
     * The actual conversion method which each subclass needs to implement.
     *
     * <p><b>Note:</b> This method must not be called directly. Instead, use {@code apply()} instead
     * which calls this method internally. Otherwise, the redundancy check is omitted and repeated
     * calls are not idempotent.
     *
     * @param input the input to convert
     * @return a new converted output
     */
    protected abstract Output convert(Input input);

    /**
     * An identity conversion which does effectively nothing but is required to satisfy Java's type
     * checker.
     *
     * @param input the input to convert
     * @return An object equal to the input but with adapted types
     */
    protected abstract Output identity(Input input);

    /**
     * Checks whether a conversion is redundant for the given input.
     *
     * @param input the input which should be converted
     * @return {@code true} iff the given input does not need to be converted
     */
    protected abstract boolean isRedundant(Input input);

    /**
     * Applies this conversion or the identity conversion to the given input, depending on whether
     * this conversion is redundant.
     *
     * @param input the input to convert
     * @return a possibly converted output
     */
    @Override
    public final Output apply(Input input) {
        return isRedundant(input) ? identity(input) : convert(input);
    }
}

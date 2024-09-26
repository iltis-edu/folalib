package de.tudortmund.cs.iltis.folalib.transform;

import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.folalib.languages.WordGenerator;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import de.tudortmund.cs.iltis.utils.general.Data;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A supplier of a virtually infinite stream of values of type T with the addition that the user can
 * block specific values.
 *
 * <p>This is super useful if we want to generate new states for an automaton but make sure that the
 * supplier does not unexpectedly return a value which already corresponds to an original state.
 *
 * <p>It is expected that a ConstrainedSupplier does not return duplicate values even if the
 * returned values are _not_ {@code constrain}ed explicitly.
 *
 * @param <T> The type of values which are supplier
 */
public interface ConstrainedSupplier<T extends Serializable> extends Supplier<T>, Serializable {

    /**
     * Prohibit the value t from being return by this supplier in the future
     *
     * @param t the value which must not be returned
     */
    void constrain(T t);

    /** Prohibit all given values from being returned anytime in the future */
    default void constrain(T... ts) {
        constrain(Data.newArrayList(ts));
    }

    /** Prohibit all given values from being returned anytime in the future */
    default void constrain(Iterable<T> ts) {
        for (T t : ts) {
            constrain(t);
        }
    }

    static <T extends Serializable> ConstrainedSupplier<T> constrainedSupplierFromIntegers(
            SerializableFunction<Integer, T> f) {
        return new ConstrainedSupplier<T>() {
            private Integer i = 0;
            private final Set<T> blocked = new HashSet<>();

            @Override
            public T get() {
                T next;
                do {
                    next = f.apply(i++);
                } while (blocked.contains(next));
                return next;
            }

            @Override
            public void constrain(T value) {
                blocked.add(value);
            }
        };
    }

    /** An instance of a ConstrainedSupplier which returns Integers */
    static ConstrainedSupplier<Integer> constrainedIntegerSupplier() {
        return constrainedSupplierFromIntegers(i -> i);
    }

    /** An instance of a ConstrainedSupplier which returns Strings */
    static ConstrainedSupplier<String> constrainedStringSupplier() {
        return constrainedSupplierFromIntegers(String::valueOf);
    }

    /**
     * An instance of a ConstrainedSupplier which returns Excel column names, e.g. A, B, C, ..., AA,
     * AB, AC, ...
     */
    static ConstrainedSupplier<String> constrainedExcelColumnNameSupplier() {
        WordGenerator<Character> generator =
                new WordGenerator<>(Alphabets.characterAlphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        return constrainedSupplierFromIntegers(i -> generator.getKthWord(i + 1).toString());
    }
}

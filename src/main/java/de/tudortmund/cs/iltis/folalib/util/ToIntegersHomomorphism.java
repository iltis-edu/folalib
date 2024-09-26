package de.tudortmund.cs.iltis.folalib.util;

import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A mapping from a set of values of type T to the integers
 *
 * <p>This mapping can always be created even for completely unknown types T.
 *
 * @param <T> The type of values which should be mapped to the integers.
 */
public class ToIntegersHomomorphism<T extends Serializable>
        implements SerializableFunction<T, Integer> {

    private final Map<T, Integer> mapping = new HashMap<>();

    public ToIntegersHomomorphism(Iterable<T> elements) {
        Integer i = 0;
        for (T element : elements) {
            if (!mapping.containsKey(element)) {
                mapping.put(element, i);
                ++i;
            }
        }
    }

    /* For serialization */
    @SuppressWarnings("unused")
    private ToIntegersHomomorphism() {}

    @Override
    public Integer apply(T t) {
        return mapping.get(t);
    }
}

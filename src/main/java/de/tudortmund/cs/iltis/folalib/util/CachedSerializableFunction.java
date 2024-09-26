package de.tudortmund.cs.iltis.folalib.util;

import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.util.HashMap;
import java.util.Map;

/**
 * A Serializable function whose results are cached, i.e. it is guaranteed that for multiple calls
 * with the same argument x the same result is returned.
 *
 * <p>This is useful in several places, e.g.when mapping a homomorphism over an automaton. It
 * protects against the pitfall that a type T does not overwrite {@code .equals()} and thus uses
 * object identity for comparisons.
 *
 * @param <D> The type of the domain
 * @param <C> The type of the codomain
 */
public class CachedSerializableFunction<D, C> implements SerializableFunction<D, C> {

    private SerializableFunction<D, C> f;
    private final Map<D, C> cache = new HashMap<>();

    public CachedSerializableFunction(SerializableFunction<D, C> f) {
        this.f = f;
    }

    /* For serialization */
    @SuppressWarnings("unused")
    public CachedSerializableFunction() {}

    @Override
    public C apply(D d) {
        if (!cache.containsKey(d)) {
            cache.put(d, f.apply(d));
        }
        return cache.get(d);
    }
}

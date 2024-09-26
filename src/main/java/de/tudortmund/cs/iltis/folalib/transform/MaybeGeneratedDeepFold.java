package de.tudortmund.cs.iltis.folalib.transform;

import de.tudortmund.cs.iltis.utils.collections.Pair;
import java.util.function.BiFunction;

/** A utility class to unfold MaybeGenerated instances like In{Gen{In{In{x}}}}. */
public abstract class MaybeGeneratedDeepFold {

    /**
     * Traverses a nested instance of {@code MaybeGenerated} and extracts the contained value using
     * the given function.
     *
     * <p>The path to the actual value (which must not be a {@code MaybeGenerated} object itself) is
     * recorded using "0" for {@code MaybeGenerated.Input} and "1" for {@code
     * MaybeGenerated.Generated}. The first element of the path indicates the type of the outermost
     * {@code MaybeGenerated}.
     *
     * <p>Examples:<br>
     * In{x} --> f("0", x)<br>
     * Gen{x} --> f("1", x)<br>
     * In{In{Gen{x}}} --> f("001", x)<br>
     * Gen{Gen{x}} --> f("11", x)<br>
     *
     * @param mg the {@code MaybeGenerated} object to "unfold"
     * @param f the function to extract the contained element. It is passed the path and the
     *     contained element
     * @param <T> the type of the contained value: the caller is responsible for checking and
     *     casting types correctly
     * @return the contained value of the nested {@code MaybeGenerated}s
     */
    public static <T> T deepFold(MaybeGenerated<?, ?> mg, BiFunction<String, Object, T> f) {
        Pair<String, ?> result = findValue(mg, "");
        return f.apply(result.first(), result.second());
    }

    protected static Pair<String, ?> findValue(Object mg, String path) {
        if (mg instanceof MaybeGenerated) {
            return ((MaybeGenerated<?, ?>) mg)
                    .match(in -> findValue(in, path + "0"), gen -> findValue(gen, path + "1"));
        } else {
            return new Pair<>(path, mg);
        }
    }
}

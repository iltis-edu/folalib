package de.tudortmund.cs.iltis.folalib.util;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.utils.function.SerializableBiFunction;
import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * Utility class containing common 2-ary boolean functions as {@link BiFunction}. Meant for usage
 * with {@link NFA#product(NFA, BiFunction)}.
 */
public final class BinaryFunctions implements Serializable {
    public static final SerializableBiFunction<Boolean, Boolean, Boolean> AND = (a, b) -> a & b;
    public static final SerializableBiFunction<Boolean, Boolean, Boolean> OR = (a, b) -> a | b;
    public static final SerializableBiFunction<Boolean, Boolean, Boolean> XOR = (a, b) -> a ^ b;
}

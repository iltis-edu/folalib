package de.tudortmund.cs.iltis.folalib.util;

import java.util.Collection;
import java.util.Objects;

public class NullCheck {

    /**
     * Throws an exception when the passed array is {@code null} or any of its elements is {@code
     * null}
     *
     * @param array the array to test
     * @throws NullPointerException if {@code array} is {@code null} or any contained object in
     *     {@code array} is {@code null}
     */
    public static void requireAllNonNull(Object[] array) {
        Objects.requireNonNull(array);
        for (Object a : array) {
            Objects.requireNonNull(a);
        }
    }

    /**
     * Throws an exception when the passed collection is {@code null} or any of its elements is
     * {@code null}
     *
     * @param collection the collection to test
     * @throws NullPointerException if {@code collection} is {@code null} or any contained object in
     *     {@code collection} is {@code null}
     */
    public static void requireAllNonNull(Collection<?> collection) {
        Objects.requireNonNull(collection);
        for (Object o : collection) {
            Objects.requireNonNull(o);
        }
    }
}

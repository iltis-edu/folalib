package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An ABC for the topmost symbols of the stack of {@link PDA}s
 *
 * <p>This class serves the purpose of allowing a wildcard as well as ranges to be used with a
 * single transition. If an unboxed value of type K would be used, there is no way of representing a
 * wildcard (or range) for an arbitrary choice of K. On the other hand, it is not possible to use a
 * predicate in K instead, because for an arbitrary predicate it is not possible to validate it in
 * {@link PDABuilder} before building the resulting PDA.
 *
 * <p>This class is for internal use only and users of the library should exclusively use the public
 * static convenience methods such as {@code PDAStackSymbol.wildcard()} or {@code
 * PDAStackSymbol.exactly(2)} to create instances of available subclasses.
 *
 * @param <K> The type of the elements of the underlying stack
 */
public abstract class PDAStackSymbol<K extends Serializable> implements Serializable {

    /**
     * Test whether the given (raw) symbol matches this one
     *
     * @param symbol The symbol to test the matching with
     * @return {@code true} iff the given symbol matches this one
     */
    public abstract boolean matches(K symbol);

    /**
     * Returns {@code true} iff this PDAStackSymbol is variable
     *
     * <p>For instance, wildcards are variable, whereas a fixed symbol such as {@code
     * PDAStackSymbol.exactly(...)} is not.
     *
     * @return {@code true} iff this PDAStackSymbol is variable
     */
    public abstract boolean isVariable();

    /**
     * Check whether this symbol is compatible with the given alphabet
     *
     * <p>To be compatible with the given alphabet all (concrete) values of type K in this
     * PDAStackSymbol must be contained in the given alphabet.
     *
     * @param alphabet The alphabet to test the compatibility with
     * @return {@code true} iff this symbol is compatible with the given alphabet
     */
    public abstract boolean compatibleWithAlphabet(Collection<? extends K> alphabet);

    /**
     * Map a homomorphism over the stack alphabet
     *
     * @param homomorphism The homomorphism to apply
     * @param <J> The new type of stack symbols after applying the homomorphism
     * @return A new PDAStackSymbol for the new stack alphabet
     */
    public abstract <J extends Serializable> PDAStackSymbol<J> map(
            SerializableFunction<K, J> homomorphism);

    /**
     * A subclass of PDAStackSymbol which matches with every possible raw symbol
     *
     * @param <K> The type of the raw symbols this symbol is matched with
     */
    public static final class WildCard<K extends Serializable> extends PDAStackSymbol<K> {

        @Override
        public boolean matches(K symbol) {
            return true;
        }

        @Override
        public boolean compatibleWithAlphabet(Collection<? extends K> alphabet) {
            return true;
        }

        @Override
        public String toString() {
            return "τ";
        }

        @Override
        public boolean isVariable() {
            return true;
        }

        @Override
        public <J extends Serializable> PDAStackSymbol<J> map(
                SerializableFunction<K, J> homomorphism) {
            return PDAStackSymbol.wildcard();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            return o != null && getClass() == o.getClass();
        }

        @Override
        public int hashCode() {
            return Objects.hash("wildcard");
        }
    }

    /**
     * A subclass of PDAStackSymbol which matches exactly the given raw symbol and nothing else
     *
     * @param <K> The type of the raw symbol this symbol is matched with
     */
    public static final class Exactly<K extends Serializable> extends PDAStackSymbol<K> {
        private K symbol;

        private Exactly(K symbol) {
            this.symbol = symbol;
        }

        /* For serialization */
        @SuppressWarnings("unused")
        private Exactly() {}

        @Override
        public boolean matches(K symbol) {
            return this.symbol.equals(symbol);
        }

        @Override
        public boolean compatibleWithAlphabet(Collection<? extends K> alphabet) {
            return alphabet.contains(symbol);
        }

        @Override
        public String toString() {
            return symbol.toString();
        }

        public K getSymbol() {
            return symbol;
        }

        @Override
        public boolean isVariable() {
            return false;
        }

        @Override
        public <J extends Serializable> PDAStackSymbol<J> map(
                SerializableFunction<K, J> homomorphism) {
            return PDAStackSymbol.exactly(homomorphism.apply(symbol));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Exactly<?> exactly = (Exactly<?>) o;
            return Objects.equals(symbol, exactly.symbol);
        }

        @Override
        public int hashCode() {
            return Objects.hash(symbol);
        }
    }

    /**
     * A subclass of PDAStackSymbol which matches with any of the given raw symbols and nothing else
     *
     * @param <K> The type of the raw symbols this symbol is matched with
     */
    public static final class AnyOf<K extends Serializable> extends PDAStackSymbol<K> {
        private final Set<K> possibleKs = new LinkedHashSet<>();

        private AnyOf(Iterable<? extends K> possibleKs) {
            possibleKs.forEach(this.possibleKs::add);
        }

        /* For serialization */
        @SuppressWarnings("unused")
        private AnyOf() {}

        @Override
        public boolean matches(K symbol) {
            return possibleKs.contains(symbol);
        }

        @Override
        public boolean compatibleWithAlphabet(Collection<? extends K> alphabet) {
            return alphabet.containsAll(possibleKs);
        }

        @Override
        public String toString() {
            return "τ ∈ " + possibleKs;
        }

        @Override
        public boolean isVariable() {
            return true;
        }

        @Override
        public <J extends Serializable> PDAStackSymbol<J> map(
                SerializableFunction<K, J> homomorphism) {
            return PDAStackSymbol.anyOf(
                    possibleKs.stream().map(homomorphism).collect(Collectors.toSet()));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AnyOf<?> anyOf = (AnyOf<?>) o;
            return Objects.equals(possibleKs, anyOf.possibleKs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(possibleKs);
        }

        public Set<K> getPossibleKs() {
            return Collections.unmodifiableSet(possibleKs);
        }
    }

    public static <K extends Serializable> PDAStackSymbol<K> wildcard() {
        return new WildCard<>();
    }

    public static <K extends Serializable> PDAStackSymbol<K> exactly(K symbol) {
        return new Exactly<>(symbol);
    }

    @SafeVarargs
    public static <K extends Serializable> PDAStackSymbol<K> anyOf(K head, K... tail) {
        Set<K> possibleKs = new LinkedHashSet<>();
        possibleKs.add(head);
        possibleKs.addAll(Arrays.asList(tail));
        return new AnyOf<>(possibleKs);
    }

    public static <K extends Serializable> PDAStackSymbol<K> anyOf(Iterable<? extends K> symbols) {
        return new AnyOf<>(symbols);
    }
}

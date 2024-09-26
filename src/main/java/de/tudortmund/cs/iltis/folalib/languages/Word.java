package de.tudortmund.cs.iltis.folalib.languages;

import de.tudortmund.cs.iltis.utils.collections.immutable.ImmutableList;
import de.tudortmund.cs.iltis.utils.function.SerializableComparator;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class Word<S extends Serializable> extends ImmutableList<S> {

    /* For serialization */
    @SuppressWarnings("unused")
    private Word() {}

    @SafeVarargs
    public Word(S... symbols) {
        super(symbols);
    }

    public Word(Collection<? extends S> collection) {
        super(collection);
    }

    @Override
    public <T extends Serializable> Word<T> map(Function<? super S, T> f) {
        return shareConstructInto(Word::new, super.map(f));
    }

    @Override
    public Word<S> filter(Predicate<? super S> p) {
        return shareConstructInto(Word::new, super.filter(p));
    }

    @Override
    public Word<S> drop(int prefixSize) {
        return shareConstructInto(Word::new, super.drop(prefixSize));
    }

    @Override
    public Word<S> take(int prefixSize) {
        return shareConstructInto(Word::new, super.take(prefixSize));
    }

    @Override
    public Word<S> append(ImmutableList<? extends S> other) {
        return shareConstructInto(Word::new, super.append(other));
    }

    @Override
    public Word<S> prepend(ImmutableList<? extends S> other) {
        return shareConstructInto(Word::new, super.prepend(other));
    }

    /**
     * Returns a {@link SerializableComparator} which can compare words of the same {@code <Sym>}.
     *
     * <p>If one word is shorter than the other it is considered smaller. In case both words are of
     * the same length, the symbols will be compared. The first symbol (the left-most one) has the
     * highest priority whereas the last symbol (the right-most one) has the lowest priority.
     *
     * <p>An example (assuming a lexicographic order on the symbols):
     *
     * <pre>
     *     zz < aaa,
     *     ab < ba
     * </pre>
     *
     * @param symbolComparator A comparator to compare each symbol in case both words are of the
     *     same length. If {@code <Sym>} implements {@link Comparable} you can use {@link
     *     Comparator#naturalOrder()}. If {@code symbolComparator} is {@code null}, all symbols are
     *     considered equal.
     * @return A serializable comparator to decide which of two given words is smaller
     */
    public static <Sym extends Serializable> SerializableComparator<Word<Sym>> getWordComparator(
            Comparator<Sym> symbolComparator) {
        Comparator<Sym> actualComparator;
        if (symbolComparator != null) actualComparator = symbolComparator;
        else actualComparator = (word1, word2) -> 0; // All symbols are considered equal

        return (word1, word2) -> {
            int lengthDifference = word1.size() - word2.size();
            if (lengthDifference != 0) return lengthDifference;

            for (int i = 0; i < word1.size(); i++) {
                int symbolDifference = actualComparator.compare(word1.get(i), word2.get(i));
                if (symbolDifference != 0) return symbolDifference;
            }

            return 0;
        };
    }

    @Override
    public String toString() {
        if (this.isEmpty()) return "ε";
        StringBuilder text = new StringBuilder();
        for (S character : this) text.append(character.toString());
        return text.toString();
    }
}

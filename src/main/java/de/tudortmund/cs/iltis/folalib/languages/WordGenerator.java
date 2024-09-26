package de.tudortmund.cs.iltis.folalib.languages;

import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A WordGenerator which can generate all words, random words of words of specific sizes of any
 * non-empty alphabet.
 *
 * @param <S> The type of symbols in the alphabet
 */
public class WordGenerator<S extends Serializable> implements Serializable {

    private Alphabet<S> alphabet;

    /* For serialization */
    @SuppressWarnings("unused")
    public WordGenerator() {}

    public WordGenerator(Alphabet<S> alphabet) {
        this.alphabet = alphabet;
    }

    public WordGenerator(Alphabet<S> alphabet, Comparator<S> comparator) {
        this(new Alphabet<>(alphabet.stream().sorted(comparator).collect(Collectors.toList())));
    }

    /**
     * Get the underlying alphabet for this WordGenerator
     *
     * @return the alphabet
     */
    public Alphabet<S> getAlphabet() {
        return alphabet;
    }

    /**
     * Compute the k-th word over this alphabet
     *
     * <p>The words are sorted by length (increasing) and then "lexicographically". The
     * lexicographic order is taken from the order of elements in this alphabet.
     *
     * <p>The idea of the algorithm is to construct a number in the base b, where b is the size of
     * this alphabet.
     *
     * @param k the index
     * @return the k-th word over this alphabet
     */
    public Word<S> getKthWord(int k) {
        int s = alphabet.size();
        int l = sizeOfKthWord(k);
        k = k - geometricSum(s, l - 1);
        int[] digits = new int[l];
        Arrays.fill(digits, 0);
        for (int idx = digits.length - 1; k > 0; --idx, k /= s) {
            digits[idx] = k % s;
        }
        return fromDigits(digits);
    }

    /**
     * Compute the size of the k-th word over this alphabet
     *
     * @param k the index
     * @return the size of the k-th word over this alphabet
     */
    public int sizeOfKthWord(int k) {
        int sum = 0;
        int r = 0;
        while (sum < k) {
            sum += pow(alphabet.size(), ++r);
        }
        return r;
    }

    /**
     * Compute the number of possible words of the given size over this alphabet
     *
     * @param k the size of the words
     */
    public int numberOfWordsOfSize(int k) {
        return pow(alphabet.size(), k);
    }

    /**
     * Get an infinite stream of all possible words over this alphabet
     *
     * <p>The words are guaranteed to be ordered by length and then lexicographically for all words
     * of the same length.
     *
     * @return an infinite stream
     */
    public Stream<Word<S>> allWords() {
        return allWordsFromIndex(0);
    }

    /**
     * Get an infinite stream of all words over this alphabet, starting at the given index
     *
     * @param index the index of the first generated word
     * @return an infinite stream
     */
    private Stream<Word<S>> allWordsFromIndex(int index) {
        return Stream.iterate(index, k -> k + 1).map(this::getKthWord);
    }

    /**
     * Get a finite stream of all possible words over this alphabet with the given size
     *
     * @param size the size of the words
     * @return a finite stream
     */
    public Stream<Word<S>> allWordsOfSize(int size) {
        int s = geometricSum(alphabet.size(), size - 1);
        int l = pow(alphabet.size(), size);
        return allWordsFromIndex(s).limit(l);
    }

    /**
     * Get a finite stream of all possible words over this alphabet up to (inclusive) the given
     * size.
     *
     * @param size the maximum size of the words
     * @return a finite stream
     */
    public Stream<Word<S>> allWordsUpToSize(int size) {
        int l = geometricSum(alphabet.size(), size);
        return allWords().limit(l);
    }

    /**
     * Get an infinite stream of all possible words over this alphabet that are at least as long as
     * the given size (inclusive)
     *
     * @param size the minimum size of the words
     * @return an infinite stream
     */
    public Stream<Word<S>> allWordsFromSize(int size) {
        int s = geometricSum(alphabet.size(), size - 1);
        return allWordsFromIndex(s);
    }

    /**
     * Get a finite stream of all possible words over this alphabet whose size is at least minSize
     * and at most maxSize (inclusive)
     *
     * @param minSize the minimum size of the words
     * @param maxSize the maximum size of the words
     * @return a finite stream
     */
    public Stream<Word<S>> allWordsOfSizeBetween(int minSize, int maxSize) {
        int s = geometricSum(alphabet.size(), minSize - 1);
        int l = geometricSum(alphabet.size(), maxSize) - s;
        return allWordsFromIndex(s).limit(l);
    }

    /**
     * Get an infinite stream of random words of the requested size over this alphabet
     *
     * <p>Note: this method may (and will at some point) return duplicates
     *
     * @param size the size of the words
     * @return an infinite stream
     */
    public Stream<Word<S>> randomWordsOfSize(int size) {
        Supplier<Integer> supplier =
                new Supplier<Integer>() {

                    private final Random random = new Random();
                    private final int offset = geometricSum(alphabet.size(), size - 1);
                    private final int maxIndex = numberOfWordsOfSize(size);

                    @Override
                    public Integer get() {
                        return offset + random.nextInt(maxIndex);
                    }
                };
        return Stream.generate(supplier).map(this::getKthWord);
    }

    /**
     * Get an infinite stream of random words whose size is at most the given size (inclusive)
     *
     * <p>Note: this method may (and will at some point) return duplicates
     *
     * @param size the maximum size of the words
     * @return an infinite stream
     */
    public Stream<Word<S>> randomWordsUpToSize(int size) {
        Supplier<Integer> supplier =
                new Supplier<Integer>() {

                    private final Random random = new Random();
                    private final int maxIndex = geometricSum(alphabet.size(), size);

                    @Override
                    public Integer get() {
                        return random.nextInt(maxIndex);
                    }
                };
        return Stream.generate(supplier).map(this::getKthWord);
    }

    /**
     * Get an infinite stream of random words over this alphabet whose size is at least minSize and
     * at most maxSize (inclusive)
     *
     * <p>Note: this method may (and will at some point) return duplicates
     *
     * @param minSize the minimum size of the words
     * @param maxSize the maximum size of the words
     * @return an infinite stream
     */
    public Stream<Word<S>> randomWordsOfSizeBetween(int minSize, int maxSize) {
        Supplier<Integer> supplier =
                new Supplier<Integer>() {

                    private final Random random = new Random();
                    private final int minIndex = geometricSum(alphabet.size(), minSize - 1);
                    private final int numberOfWords =
                            geometricSum(alphabet.size(), maxSize) - minIndex;

                    @Override
                    public Integer get() {
                        return minIndex + random.nextInt(numberOfWords);
                    }
                };
        return Stream.generate(supplier).map(this::getKthWord);
    }

    private Word<S> fromDigits(int[] digits) {
        List<S> symbols = new LinkedList<>();
        for (int digit : digits) {
            symbols.add(alphabet.toUnmodifiableList().get(digit));
        }
        return new Word<>(symbols);
    }

    private int pow(int base, int exp) {
        int res = 1;
        while (exp > 0) {
            if (exp % 2 == 1) {
                res *= base; // multiply
            }
            base *= base; // square
            exp /= 2;
        }
        return res;
    }

    private int geometricSum(int s, int n) {
        int sum = 0;
        for (int i = 0; i <= n; ++i) {
            sum += pow(s, i);
        }
        return sum;
    }
}

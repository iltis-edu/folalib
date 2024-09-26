package de.tudortmund.cs.iltis.folalib.languages;

import de.tudortmund.cs.iltis.folalib.util.NullCheck;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A straightforward brute-force algorithm to heuristically determine whether two {@link Language}s
 * are equivalent.
 */
public class HeuristicEquivalenceCheck implements Serializable {

    /**
     * Tests whether two languages are equivalent with regard to the given words.
     *
     * @param languageA The first language
     * @param languageB The second language
     * @param words The words to test
     * @param <S> The type of the symbols of the words
     * @throws NullPointerException if either language, the word collection or any word is {@code
     *     null}
     * @return A {@link HeuristicEquivalenceResult} is returned indicating whether the language are
     *     (not) equivalent.
     */
    public static <S extends Serializable> HeuristicEquivalenceResult<S> testGivenWords(
            Language<S> languageA, Language<S> languageB, Collection<Word<S>> words) {
        Objects.requireNonNull(languageA);
        Objects.requireNonNull(languageB);
        NullCheck.requireAllNonNull(words);

        return testWords(languageA, languageB, words);
    }

    /**
     * Tests whether two languages are equivalent with regard to the given words.
     *
     * @param languageA The first language
     * @param languageB The second language
     * @param words The words to test
     * @param <S> The type of the symbols of the words
     * @throws NullPointerException if either language, the word collection or any word is {@code
     *     null}
     * @return A {@link HeuristicEquivalenceResult} is returned indicating whether the language are
     *     (not) equivalent.
     */
    public static <S extends Serializable> HeuristicEquivalenceResult<S> testGivenWords(
            Language<S> languageA, Language<S> languageB, Stream<Word<S>> words) {
        Objects.requireNonNull(words);
        return testGivenWords(languageA, languageB, words.collect(Collectors.toSet()));
    }

    /**
     * Test whether two languages are equivalent by testing all words smaller or equal to {@param
     * length} over the intersection of the alphabets of the two given languages.
     *
     * @param languageA The first language
     * @param languageB The second language
     * @param maxLength The maximum length of the words to be tested
     * @param <S> The type of the symbols of the words
     * @throws NullPointerException if either language is {@code null}
     * @return A {@link HeuristicEquivalenceResult} is returned indicating whether the language are
     *     (not) equivalent.
     */
    public static <S extends Serializable> HeuristicEquivalenceResult<S> testAllWordsToLength(
            Language<S> languageA, Language<S> languageB, int maxLength) {
        WordGenerator<S> generator = getWordGenerator(languageA, languageB);
        Stream<Word<S>> wordStream = generator.allWordsUpToSize(maxLength);

        return testWords(languageA, languageB, wordStream.collect(Collectors.toSet()));
    }

    /**
     * Test whether two languages are equivalent by testing {@param amountOfRandomWords} random
     * words of length between {@param minLength} and {@param maxLength} (inclusive) over the
     * intersection of the alphabets of the two given languages.
     *
     * @param languageA The first language
     * @param languageB The second language
     * @param amountOfRandomWords The amount of random words to be tested
     * @param minLength The minimum length of the words to be tested
     * @param maxLength The maximum length of the words to be tested
     * @param <S> The type of the symbols of the words
     * @throws NullPointerException if either language is {@code null}
     * @return A {@link HeuristicEquivalenceResult} is returned indicating whether the language are
     *     (not) equivalent.
     */
    public static <S extends Serializable> HeuristicEquivalenceResult<S> testNRandomWordsOfLength(
            Language<S> languageA,
            Language<S> languageB,
            int amountOfRandomWords,
            int minLength,
            int maxLength) {
        WordGenerator<S> generator = getWordGenerator(languageA, languageB);
        Stream<Word<S>> wordStream = generator.randomWordsOfSizeBetween(minLength, maxLength);

        return testWords(
                languageA,
                languageB,
                wordStream.limit(amountOfRandomWords).collect(Collectors.toSet()));
    }

    // PRIVATE METHODS:

    /** The actual testing process */
    private static <S extends Serializable> HeuristicEquivalenceResult<S> testWords(
            Language<S> languageA, Language<S> languageB, Collection<Word<S>> words) {
        for (Word<S> word : words) {
            if (languageA.contains(word) != languageB.contains(word)) {
                return HeuristicEquivalenceResult.disproved(word);
            }
        }

        if (languageA.getAlphabet().equals(languageB.getAlphabet()))
            return HeuristicEquivalenceResult.possible();
        else return HeuristicEquivalenceResult.possibleIgnoringAlphabets();
    }

    /**
     * Returns a {@link WordGenerator} for words over the intersection of the alphabets of two
     * languages.
     */
    private static <S extends Serializable> WordGenerator<S> getWordGenerator(
            Language<S> languageA, Language<S> languageB) {
        Objects.requireNonNull(languageA);
        Objects.requireNonNull(languageB);

        // We will construct words over the union of the two alphabets
        Alphabet<S> unionOfAlphabets =
                Alphabets.unionOf(languageA.getAlphabet(), languageB.getAlphabet());

        return new WordGenerator<>(unionOfAlphabets);
    }
}

package de.tudortmund.cs.iltis.folalib.languages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.Utils;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;

public class WordGeneratorTest extends Utils {

    private final Alphabet<Character> alphabet = Alphabets.characterAlphabet("abc");
    private final WordGenerator<Character> stream = new WordGenerator<>(alphabet);

    /* the words over this alphabet are:
     * - eps
     * - a, b, c
     * - aa, ab, ac, ba, bb, bc, ca, cb, cc
     * - aaa, aab, aac, aba, abb, abc, aca, acb, acc, baa, bab, bac, bba, bbb, bbc, bca, ...
     * - aaaa, aaab, aaac, aaba, aabb, aabc, ...
     * - ...
     */

    @Test
    public void testAlphabetOfSizeOne() {
        Alphabet<Character> alphabet = Alphabets.characterAlphabet("a");
        WordGenerator<Character> stream = new WordGenerator<>(alphabet);
        assertEquals(Words.characterWord(""), stream.getKthWord(0));
        assertEquals(Words.characterWord("a"), stream.getKthWord(1));
        assertEquals(Words.characterWord("aa"), stream.getKthWord(2));
        assertEquals(Words.characterWord("aaa"), stream.getKthWord(3));
        assertEquals(Words.characterWord("aaaaaaaaaaaaa"), stream.getKthWord(13));
    }

    @Test
    public void testGetKthWord() {
        assertEquals(Words.characterWord(""), stream.getKthWord(0));
        assertEquals(Words.characterWord("a"), stream.getKthWord(1));
        assertEquals(Words.characterWord("b"), stream.getKthWord(2));
        assertEquals(Words.characterWord("c"), stream.getKthWord(3));
        assertEquals(Words.characterWord("aa"), stream.getKthWord(4));
        assertEquals(Words.characterWord("bc"), stream.getKthWord(9));
        assertEquals(Words.characterWord("baa"), stream.getKthWord(22));
        assertEquals(Words.characterWord("aaba"), stream.getKthWord(43));
    }

    @Test
    public void testGetKthWordWithCustomOrder() {
        WordGenerator<Character> stream =
                new WordGenerator<>(
                        alphabet, (lhs, rhs) -> rhs - lhs); // inverse order, i.e. c < b < a
        assertEquals(Words.characterWord(""), stream.getKthWord(0));
        assertEquals(Words.characterWord("c"), stream.getKthWord(1));
        assertEquals(Words.characterWord("b"), stream.getKthWord(2));
        assertEquals(Words.characterWord("a"), stream.getKthWord(3));
        assertEquals(Words.characterWord("cc"), stream.getKthWord(4));
        assertEquals(Words.characterWord("ba"), stream.getKthWord(9));
        assertEquals(Words.characterWord("bcc"), stream.getKthWord(22));
        assertEquals(Words.characterWord("ccbc"), stream.getKthWord(43));
    }

    @Test
    public void testAllWordsOfSize0() {
        Set<Word<Character>> words = stream.allWordsOfSize(0).collect(Collectors.toSet());
        assertEquals(1, words.size());
        assertContains(words, Words.characterWord(""));
    }

    @Test
    public void testAllWordsOfSize2() {
        Set<Word<Character>> words = stream.allWordsOfSize(2).collect(Collectors.toSet());
        assertEquals(9, words.size());
        /* Exhaustive list of all generated words */
        assertContains(words, Words.characterWord("aa"));
        assertContains(words, Words.characterWord("ab"));
        assertContains(words, Words.characterWord("ac"));
        assertContains(words, Words.characterWord("ba"));
        assertContains(words, Words.characterWord("bb"));
        assertContains(words, Words.characterWord("bc"));
        assertContains(words, Words.characterWord("ca"));
        assertContains(words, Words.characterWord("cb"));
        assertContains(words, Words.characterWord("cc"));
    }

    @Test
    public void testAllWordsOfSize5() {
        Set<Word<Character>> words = stream.allWordsOfSize(5).collect(Collectors.toSet());
        assertEquals(3 * 3 * 3 * 3 * 3, words.size());
        assertTrue(words.stream().allMatch(w -> w.size() == 5));
        /* Random sample to keep the unit test readable */
        assertContains(words, Words.characterWord("abcbc"));
        assertContains(words, Words.characterWord("aacbb"));
        assertContains(words, Words.characterWord("ccccc"));
    }

    @Test
    public void testSizeOfKthWord() {
        assertEquals(0, stream.sizeOfKthWord(0));

        assertEquals(1, stream.sizeOfKthWord(1));
        assertEquals(1, stream.sizeOfKthWord(2));
        assertEquals(1, stream.sizeOfKthWord(3));

        assertEquals(2, stream.sizeOfKthWord(4)); /* first word of size 2 */
        assertEquals(2, stream.sizeOfKthWord(12)); /* last word of size 2 */

        assertEquals(3, stream.sizeOfKthWord(13)); /* first word of size 3 */
        assertEquals(3, stream.sizeOfKthWord(39)); /* last word of size 3 */

        assertEquals(4, stream.sizeOfKthWord(40)); /* first word of size 4 */
        assertEquals(4, stream.sizeOfKthWord(120)); /* last word of size 4 */

        assertEquals(5, stream.sizeOfKthWord(121)); /* first word of size 5 */
        assertEquals(5, stream.sizeOfKthWord(363)); /* last word of size 5 */

        assertEquals(6, stream.sizeOfKthWord(364)); /* first word of size 6 */
    }

    @Test
    public void testAllWordsUpToSize2() {
        Set<Word<Character>> words = stream.allWordsUpToSize(2).collect(Collectors.toSet());
        assertEquals(1 + 3 + 3 * 3, words.size());
        assertTrue(words.stream().allMatch(w -> w.size() <= 2));

        assertContains(words, Words.characterWord(""));

        assertContains(words, Words.characterWord("a"));
        assertContains(words, Words.characterWord("b"));
        assertContains(words, Words.characterWord("c"));

        assertContains(words, Words.characterWord("aa"));
        assertContains(words, Words.characterWord("ab"));
        assertContains(words, Words.characterWord("ac"));
        assertContains(words, Words.characterWord("ba"));
        assertContains(words, Words.characterWord("bb"));
        assertContains(words, Words.characterWord("bc"));
        assertContains(words, Words.characterWord("ca"));
        assertContains(words, Words.characterWord("cb"));
        assertContains(words, Words.characterWord("cc"));
    }

    @Test
    public void testAllWordsUpToSize4() {
        Set<Word<Character>> words = stream.allWordsUpToSize(4).collect(Collectors.toSet());
        assertEquals(1 + 3 + 3 * 3 + 3 * 3 * 3 + 3 * 3 * 3 * 3, words.size());
        assertTrue(words.stream().allMatch(w -> w.size() <= 4));

        /* sample words */
        assertContains(words, Words.characterWord("a"));
        assertContains(words, Words.characterWord("ca"));
        assertContains(words, Words.characterWord("bca"));
        assertContains(words, Words.characterWord("abcc"));
    }

    @Test
    public void testAllWordsFromSize2() {
        Set<Word<Character>> words =
                stream.allWordsFromSize(2).limit(3 * 3 + 1).collect(Collectors.toSet());

        assertContains(words, Words.characterWord("aa"));
        assertContains(words, Words.characterWord("ab"));
        assertContains(words, Words.characterWord("ac"));
        assertContains(words, Words.characterWord("ba"));
        assertContains(words, Words.characterWord("bb"));
        assertContains(words, Words.characterWord("bc"));
        assertContains(words, Words.characterWord("ca"));
        assertContains(words, Words.characterWord("cb"));
        assertContains(words, Words.characterWord("cc"));

        assertContains(words, Words.characterWord("aaa"));
    }

    @Test
    public void testAllWordsFromSize5() {
        Set<Word<Character>> words =
                stream.allWordsFromSize(5).limit(3 * 3 * 3 * 3 * 3).collect(Collectors.toSet());
        assertTrue(words.stream().allMatch(w -> w.size() == 5));

        /* check if it contains the first and last word of all words, whose size is equal to 5 */
        assertContains(words, Words.characterWord("aaaaa"));
        assertContains(words, Words.characterWord("ccccc"));
    }

    @Test
    public void testAllWordsOfSizeBetweenOneAndTwo() {
        Set<Word<Character>> words = stream.allWordsOfSizeBetween(1, 2).collect(Collectors.toSet());
        assertTrue(words.stream().allMatch(w -> w.size() >= 1 && w.size() <= 2));
        assertEquals(3 + 3 * 3, words.size());

        assertContains(words, Words.characterWord("a"));
        assertContains(words, Words.characterWord("cc"));
    }

    @Test
    public void testRandomWordsOfSize4() {
        Set<Word<Character>> words =
                stream.randomWordsOfSize(4).limit(60).collect(Collectors.toSet());
        Set<Word<Character>> allValidWords = stream.allWordsOfSize(4).collect(Collectors.toSet());
        assertTrue(words.stream().allMatch(w -> w.size() == 4));

        for (Word<Character> word : words) {
            assertContains(allValidWords, word);
        }
    }

    @Test
    public void testRandomWordsUpToSize2() {
        Set<Word<Character>> words =
                stream.randomWordsUpToSize(2).limit(30).collect(Collectors.toSet());
        Set<Word<Character>> allValidWords = stream.allWordsUpToSize(2).collect(Collectors.toSet());
        assertTrue(words.stream().allMatch(w -> w.size() <= 2));

        for (Word<Character> word : words) {
            assertContains(allValidWords, word);
        }
    }

    @Test
    public void testRandomWordsOfSizeBetweenOneAndTwo() {
        Set<Word<Character>> words =
                stream.randomWordsOfSizeBetween(1, 2).limit(30).collect(Collectors.toSet());
        Set<Word<Character>> allValidWords =
                stream.allWordsOfSizeBetween(1, 2).collect(Collectors.toSet());
        assertTrue(words.stream().allMatch(w -> w.size() <= 2 && w.size() >= 1));

        for (Word<Character> word : words) {
            assertContains(allValidWords, word);
        }
    }
}

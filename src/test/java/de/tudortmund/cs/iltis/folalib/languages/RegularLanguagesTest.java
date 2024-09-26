package de.tudortmund.cs.iltis.folalib.languages;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.tudortmund.cs.iltis.folalib.io.reader.word.WordReader;
import de.tudortmund.cs.iltis.folalib.io.reader.word.WordReaderProperties;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import org.junit.Test;

public class RegularLanguagesTest {

    private final Alphabet<IndexedSymbol> alphabet =
            new Alphabet<>(new IndexedSymbol("a"), new IndexedSymbol("b"), new IndexedSymbol("c"));
    private final WordReader reader = new WordReader(WordReaderProperties.createDefault(alphabet));

    @Test
    public void testEmptyLanguage() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.emptyLanguage();
        assertFalse(language.contains(reader.read("ε")));
        assertFalse(language.contains(reader.read("a")));
        assertFalse(language.contains(reader.read("ac")));
        assertFalse(language.contains(reader.read("abc")));
        assertFalse(language.contains(reader.read("ccbbaba")));
    }

    @Test
    public void testEmptyWord() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.emptyWord();
        assertTrue(language.contains(reader.read("ε")));

        assertFalse(language.contains(reader.read("a")));
        assertFalse(language.contains(reader.read("ab")));
        assertFalse(language.contains(reader.read("bbccbb")));
        assertFalse(language.contains(reader.read("abcabcabc")));
        assertFalse(language.contains(reader.read("aaaaaaaaaa")));
    }

    @Test
    public void testEvenLength() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.evenLength();
        assertTrue(language.contains(reader.read("ε")));
        assertTrue(language.contains(reader.read("ab")));
        assertTrue(language.contains(reader.read("aa")));
        assertTrue(language.contains(reader.read("abcabc")));
        assertTrue(language.contains(reader.read("aaabbbccca")));
        assertTrue(language.contains(reader.read("aabbaabbccbb")));

        assertFalse(language.contains(reader.read("a")));
        assertFalse(language.contains(reader.read("abc")));
        assertFalse(language.contains(reader.read("aabbc")));
        assertFalse(language.contains(reader.read("bbbbbbb")));
        assertFalse(language.contains(reader.read("abcabcabc")));
    }

    @Test
    public void testOddLength() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.oddLength();
        assertTrue(language.contains(reader.read("a")));
        assertTrue(language.contains(reader.read("b")));
        assertTrue(language.contains(reader.read("abc")));
        assertTrue(language.contains(reader.read("aaa")));
        assertTrue(language.contains(reader.read("bcabc")));
        assertTrue(language.contains(reader.read("abcabcabc")));

        assertFalse(language.contains(reader.read("ε")));
        assertFalse(language.contains(reader.read("aa")));
        assertFalse(language.contains(reader.read("abab")));
        assertFalse(language.contains(reader.read("abcabc")));
        assertFalse(language.contains(reader.read("cccccccccccccccc")));
    }

    @Test
    public void testMax() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.max(3);
        assertTrue(language.contains(reader.read("ε")));
        assertTrue(language.contains(reader.read("a")));
        assertTrue(language.contains(reader.read("cc")));
        assertTrue(language.contains(reader.read("ba")));
        assertTrue(language.contains(reader.read("abc")));
        assertTrue(language.contains(reader.read("ccc")));

        assertFalse(language.contains(reader.read("aaaa")));
        assertFalse(language.contains(reader.read("abca")));
        assertFalse(language.contains(reader.read("abcabc")));
        assertFalse(language.contains(reader.read("bcabacab")));
        assertFalse(language.contains(reader.read("ccccccccccccccccc")));
    }

    @Test
    public void testMin() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.min(2);
        assertTrue(language.contains(reader.read("aa")));
        assertTrue(language.contains(reader.read("ab")));
        assertTrue(language.contains(reader.read("aaa")));
        assertTrue(language.contains(reader.read("abc")));
        assertTrue(language.contains(reader.read("aabbccaaa")));
        assertTrue(language.contains(reader.read("ccccccccccc")));

        assertFalse(language.contains(reader.read("ε")));
        assertFalse(language.contains(reader.read("a")));
        assertFalse(language.contains(reader.read("b")));
        assertFalse(language.contains(reader.read("c")));
    }

    @Test
    public void testBetween() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.between(2, 4);
        assertTrue(language.contains(reader.read("aa")));
        assertTrue(language.contains(reader.read("cb")));
        assertTrue(language.contains(reader.read("abc")));
        assertTrue(language.contains(reader.read("bbb")));
        assertTrue(language.contains(reader.read("acba")));
        assertTrue(language.contains(reader.read("cccc")));

        assertFalse(language.contains(reader.read("ε")));
        assertFalse(language.contains(reader.read("a")));
        assertFalse(language.contains(reader.read("b")));
        assertFalse(language.contains(reader.read("c")));
    }

    @Test
    public void testExact() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.exact(3);
        assertTrue(language.contains(reader.read("aaa")));
        assertTrue(language.contains(reader.read("bac")));
        assertTrue(language.contains(reader.read("abc")));
        assertTrue(language.contains(reader.read("ccb")));
        assertTrue(language.contains(reader.read("baa")));
        assertTrue(language.contains(reader.read("aca")));

        assertFalse(language.contains(reader.read("ε")));
        assertFalse(language.contains(reader.read("a")));
        assertFalse(language.contains(reader.read("bc")));
        assertFalse(language.contains(reader.read("abcabc")));
        assertFalse(language.contains(reader.read("ccccccc")));
    }

    @Test
    public void testWords() {
        RegularLanguage<IndexedSymbol> language =
                RegularLanguages.words("aba", "aca", "bab", "bcb", "cac", "cbc", "");
        assertTrue(language.contains(reader.read("ε")));
        assertTrue(language.contains(reader.read("aba")));
        assertTrue(language.contains(reader.read("aca")));
        assertTrue(language.contains(reader.read("bab")));
        assertTrue(language.contains(reader.read("bcb")));
        assertTrue(language.contains(reader.read("cac")));

        assertFalse(language.contains(reader.read("a")));
        assertFalse(language.contains(reader.read("bb")));
        assertFalse(language.contains(reader.read("ccc")));
        assertFalse(language.contains(reader.read("ccb")));
        assertFalse(language.contains(reader.read("abccba")));
    }

    @Test
    public void testInfix() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.infix("aba");
        assertTrue(language.contains(reader.read("aba")));
        assertTrue(language.contains(reader.read("caba")));
        assertTrue(language.contains(reader.read("abac")));
        assertTrue(language.contains(reader.read("cabac")));
        assertTrue(language.contains(reader.read("accabacba")));
        assertTrue(language.contains(reader.read("abbcaabacccccc")));

        assertFalse(language.contains(reader.read("ε")));
        assertFalse(language.contains(reader.read("ab")));
        assertFalse(language.contains(reader.read("abca")));
        assertFalse(language.contains(reader.read("cbabc")));
        assertFalse(language.contains(reader.read("cccccccc")));
    }

    @Test
    public void testPrefix() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.prefix("abc");
        assertTrue(language.contains(reader.read("abc")));
        assertTrue(language.contains(reader.read("abca")));
        assertTrue(language.contains(reader.read("abcbc")));
        assertTrue(language.contains(reader.read("abcccccc")));
        assertTrue(language.contains(reader.read("abcabcabc")));

        assertFalse(language.contains(reader.read("ε")));
        assertFalse(language.contains(reader.read("ab")));
        assertFalse(language.contains(reader.read("aabc")));
        assertFalse(language.contains(reader.read("abbcc")));
        assertFalse(language.contains(reader.read("bbbbbb")));
    }

    @Test
    public void testSuffix() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.suffix("cbc");
        assertTrue(language.contains(reader.read("cbc")));
        assertTrue(language.contains(reader.read("acbc")));
        assertTrue(language.contains(reader.read("abcbc")));
        assertTrue(language.contains(reader.read("ccccbc")));
        assertTrue(language.contains(reader.read("bbbbcbc")));
        assertTrue(language.contains(reader.read("aaaaaaaaaacbc")));

        assertFalse(language.contains(reader.read("ε")));
        assertFalse(language.contains(reader.read("bc")));
        assertFalse(language.contains(reader.read("abc")));
        assertFalse(language.contains(reader.read("aaacb")));
        assertFalse(language.contains(reader.read("acbca")));
    }

    @Test
    public void testAtMostTwoAs() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.atMostTwoAs();
        assertTrue(language.contains(reader.read("ε")));
        assertTrue(language.contains(reader.read("a")));
        assertTrue(language.contains(reader.read("b")));
        assertTrue(language.contains(reader.read("abc")));
        assertTrue(language.contains(reader.read("abbbbbca")));
        assertTrue(language.contains(reader.read("aabcbcbcbcbcb")));

        assertFalse(language.contains(reader.read("aaa")));
        assertFalse(language.contains(reader.read("abcaba")));
        assertFalse(language.contains(reader.read("aabbccaabbcc")));
        assertFalse(language.contains(reader.read("cbabcabcaa")));
    }

    @Test
    public void testNoAs() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.noAs();
        assertTrue(language.contains(reader.read("ε")));
        assertTrue(language.contains(reader.read("b")));
        assertTrue(language.contains(reader.read("c")));
        assertTrue(language.contains(reader.read("cbc")));
        assertTrue(language.contains(reader.read("ccccc")));
        assertTrue(language.contains(reader.read("bccbccbbcbccb")));

        assertFalse(language.contains(reader.read("a")));
        assertFalse(language.contains(reader.read("abc")));
        assertFalse(language.contains(reader.read("bac")));
        assertFalse(language.contains(reader.read("cba")));
        assertFalse(language.contains(reader.read("cbcbcacba")));
    }

    @Test
    public void testEvenNumberOfAs() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.evenNumberOfAs();
        assertTrue(language.contains(reader.read("ε")));
        assertTrue(language.contains(reader.read("bb")));
        assertTrue(language.contains(reader.read("aa")));
        assertTrue(language.contains(reader.read("aba")));
        assertTrue(language.contains(reader.read("abca")));
        assertTrue(language.contains(reader.read("aaaac")));

        assertFalse(language.contains(reader.read("a")));
        assertFalse(language.contains(reader.read("abc")));
        assertFalse(language.contains(reader.read("abaca")));
        assertFalse(language.contains(reader.read("aaaaa")));
        assertFalse(language.contains(reader.read("acbcabcac")));
    }

    @Test
    public void testFirstEqualsLast() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.firstEqualsLast();
        assertTrue(language.contains(reader.read("aa")));
        assertTrue(language.contains(reader.read("aba")));
        assertTrue(language.contains(reader.read("ccc")));
        assertTrue(language.contains(reader.read("cbabc")));
        assertTrue(language.contains(reader.read("acbacaba")));
        assertTrue(language.contains(reader.read("cbcbacabbc")));

        assertFalse(language.contains(reader.read("ε")));
        assertFalse(language.contains(reader.read("ab")));
        assertFalse(language.contains(reader.read("abc")));
        assertFalse(language.contains(reader.read("cabca")));
        assertFalse(language.contains(reader.read("abbacc")));
    }

    @Test
    public void testAllWords() {
        RegularLanguage<IndexedSymbol> language = RegularLanguages.allWords();
        assertTrue(language.contains(reader.read("ε")));
        assertTrue(language.contains(reader.read("aa")));
        assertTrue(language.contains(reader.read("ab")));
        assertTrue(language.contains(reader.read("aba")));
        assertTrue(language.contains(reader.read("abc")));
        assertTrue(language.contains(reader.read("ccc")));
        assertTrue(language.contains(reader.read("cabca")));
        assertTrue(language.contains(reader.read("cbabc")));
        assertTrue(language.contains(reader.read("abbacc")));
        assertTrue(language.contains(reader.read("acbacaba")));
        assertTrue(language.contains(reader.read("cbcbacabbc")));
    }
}

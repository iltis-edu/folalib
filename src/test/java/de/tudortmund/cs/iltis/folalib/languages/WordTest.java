package de.tudortmund.cs.iltis.folalib.languages;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import de.tudortmund.cs.iltis.utils.test.AdvancedTest;
import java.io.Serializable;
import org.junit.Test;

public class WordTest extends AdvancedTest {
    private final Word<Character> epsilon;
    private final Word<Character> a;
    private final Word<Character> b;
    private final Word<Character> c;
    private final Word<Character> aa;
    private final Word<Character> ab;
    private final Word<Character> ac;
    private final Word<Character> aaa;
    private final Word<Character> aab;
    private final Word<Character> aba;

    public WordTest() {
        this.epsilon = Words.characterWord("");
        this.a = Words.characterWord("a");
        this.b = Words.characterWord("b");
        this.c = Words.characterWord("c");
        this.aa = Words.characterWord("aa");
        this.ab = Words.characterWord("ab");
        this.ac = Words.characterWord("ac");
        this.aaa = Words.characterWord("aaa");
        this.aab = Words.characterWord("aab");
        this.aba = Words.characterWord("aba");
    }

    @Test
    public void isPrefixTest() {
        assertPrefix(epsilon, epsilon, a, b, c, aa, ab, ac, aaa, aab, aba);
        assertPrefix(a, a, aa, ab, aaa, aab, aba);
        assertNoPrefix(a, epsilon, b, c);
        assertPrefix(b, b);
        assertNoPrefix(b, epsilon, a, c, aa, ab);
        assertPrefix(aa, aa, aaa, aab);
        assertNoPrefix(aa, epsilon, a);
    }

    @SafeVarargs
    protected final <S extends Serializable> void assertPrefix(Word<S> word, Word<S>... others) {
        for (Word<S> other : others) assertTrue(word.isPrefixOf(other));
    }

    @SafeVarargs
    protected final <S extends Serializable> void assertNoPrefix(Word<S> word, Word<S>... others) {
        for (Word<S> other : others) assertFalse(word.isPrefixOf(other));
    }
}

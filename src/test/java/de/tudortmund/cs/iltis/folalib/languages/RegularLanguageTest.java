package de.tudortmund.cs.iltis.folalib.languages;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFAExecutor;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFATestHelper;
import de.tudortmund.cs.iltis.folalib.expressions.regular.Range;
import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.expressions.regular.Symbol;
import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.RightRegularProduction;
import de.tudortmund.cs.iltis.folalib.io.reader.regex.RegularExpressionReaderProperties;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class RegularLanguageTest extends NFATestHelper {
    private final Word<IndexedSymbol> epsilon;
    private final Word<IndexedSymbol> a;
    private final Word<IndexedSymbol> b;
    private final Word<IndexedSymbol> aa;
    private final Word<IndexedSymbol> ab;
    private final Word<IndexedSymbol> ac;
    private final Word<IndexedSymbol> ba;
    private final Word<IndexedSymbol> aaa;
    private final Word<IndexedSymbol> aab;
    private final Word<IndexedSymbol> aba;

    private final Alphabet<IndexedSymbol> alphabet = Alphabets.indexedSymbolAlphabet("abc");

    public RegularLanguageTest() {
        epsilon = new Word<>();
        a = Words.indexedSymbolWord("a");
        b = Words.indexedSymbolWord("b");
        aa = Words.indexedSymbolWord("aa");
        ab = Words.indexedSymbolWord("ab");
        ac = Words.indexedSymbolWord("ac");
        ba = Words.indexedSymbolWord("ba");
        aaa = Words.indexedSymbolWord("aaa");
        aab = Words.indexedSymbolWord("aab");
        aba = Words.indexedSymbolWord("aba");
    }

    @Test
    public void testEquivalence() {
        NFA<Integer, Integer> mod2 =
                new NFABuilder<Integer, Integer>(new Alphabet<>(0, 1))
                        .withInitial(0)
                        .withStates(1)
                        .withAccepting(0)
                        .withTransition(0, 0, 0)
                        .withTransition(1, 0, 1)
                        .withTransition(0, 1, 1)
                        .withTransition(1, 1, 0)
                        .build()
                        .unwrap();

        NFA<Integer, Integer> alsoMod2 =
                new NFABuilder<Integer, Integer>(new Alphabet<>(0, 1))
                        .withInitial(0)
                        .withStates(1, 3)
                        .withAccepting(0, 2)
                        .withTransition(0, 0, 0)
                        .withTransition(1, 0, 1)
                        .withTransition(2, 0, 2)
                        .withTransition(3, 0, 3)
                        .withTransition(0, 1, 1)
                        .withTransition(1, 1, 2)
                        .withTransition(2, 1, 3)
                        .withTransition(3, 1, 0)
                        .build()
                        .unwrap();

        assertTrue(new RegularLanguage<>(mod2).isEqualTo(new RegularLanguage<>(alsoMod2)));
    }

    @Test
    public void testGetAlphabet() {
        RegularExpression<IndexedSymbol> regex =
                RegularExpression.fromString(
                        "a*(b+c)*", RegularExpressionReaderProperties.createDefault(alphabet));

        assertEquals(
                new RegularLanguage<>(regex).getAlphabet(), Alphabets.indexedSymbolAlphabet("abc"));
    }

    @Test
    public void testEquivalenceWithRegex() {
        RegularExpression<IndexedSymbol> regex =
                RegularExpression.fromString(
                        "a*(b+c)*", RegularExpressionReaderProperties.createDefault(alphabet));

        NFA<Integer, IndexedSymbol> nfa =
                new NFABuilder<Integer, IndexedSymbol>(
                                new Alphabet<>(
                                        new IndexedSymbol("a"),
                                        new IndexedSymbol("b"),
                                        new IndexedSymbol("c")))
                        .withInitial(0)
                        .withAccepting(1)
                        .withTransition(0, new IndexedSymbol("a"), 0)
                        .withEpsilonTransition(0, 1)
                        .withTransition(1, new IndexedSymbol("b"), 1)
                        .withTransition(1, new IndexedSymbol("c"), 1)
                        .build()
                        .unwrap();

        assertTrue(new RegularLanguage<>(regex).isEqualTo(new RegularLanguage<>(nfa)));
    }

    @Test
    public void testNonEquivalenceDifferentAlphabets() {
        assertFalse(
                new RegularLanguage<>(
                                RegularExpression.fromString(
                                        "a",
                                        RegularExpressionReaderProperties.createDefault(alphabet)))
                        .isEqualTo(
                                new RegularLanguage<>(
                                        RegularExpression.fromString(
                                                "b",
                                                RegularExpressionReaderProperties.createDefault(
                                                        alphabet)))));
    }

    @Test
    public void simpleFiniteRE() {
        RegularLanguage<IndexedSymbol> empty =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "ε", RegularExpressionReaderProperties.createDefault(alphabet)));
        RegularLanguage<IndexedSymbol> singleA =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "a", RegularExpressionReaderProperties.createDefault(alphabet)));
        RegularLanguage<IndexedSymbol> doubleA =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "aa", RegularExpressionReaderProperties.createDefault(alphabet)));

        assertTrue(empty.contains(epsilon));
        assertFalse(empty.contains(a));
        assertFalse(empty.contains(aa));

        assertFalse(singleA.contains(epsilon));
        assertTrue(singleA.contains(a));
        assertFalse(singleA.contains(aa));

        assertFalse(doubleA.contains(epsilon));
        assertFalse(doubleA.contains(a));
        assertTrue(doubleA.contains(aa));
    }

    @Test
    public void simpleEvenAWithoutBTest() {
        RegularExpression<IndexedSymbol> reEvenAs =
                RegularExpression.fromString(
                        "(aa)*", RegularExpressionReaderProperties.createDefault(alphabet));

        RegularLanguage<IndexedSymbol> langEvenAs = new RegularLanguage<>(reEvenAs);

        assertTrue(langEvenAs.contains(epsilon));
        assertTrue(langEvenAs.contains(aa));
        assertTrue(langEvenAs.contains(Words.concat(aa, aa)));

        assertFalse(langEvenAs.contains(a));
        assertFalse(langEvenAs.contains(ab));
        assertFalse(langEvenAs.contains(aaa));
    }

    @Test
    public void alternatingAB() {
        RegularExpression<IndexedSymbol> re =
                RegularExpression.fromString(
                        "(ab)*", RegularExpressionReaderProperties.createDefault(alphabet));

        RegularLanguage<IndexedSymbol> lang = new RegularLanguage<>(re);

        assertTrue(lang.contains(epsilon));
        assertTrue(lang.contains(ab));
        assertTrue(lang.contains(Words.concat(ab, ab)));
        assertTrue(lang.contains(Words.replicate(ab, 3)));

        assertFalse(lang.contains(a));
        assertFalse(lang.contains(b));
        assertFalse(lang.contains(ba));
    }

    @Test
    public void simpleEvenAWithBTest() {
        RegularExpression<IndexedSymbol> reEvenAs =
                RegularExpression.fromString(
                        "b*(ab*ab*)*", RegularExpressionReaderProperties.createDefault(alphabet));

        RegularLanguage<IndexedSymbol> langEvenAs = new RegularLanguage<>(reEvenAs);

        assertTrue(langEvenAs.contains(epsilon));
        assertTrue(langEvenAs.contains(b));
        assertTrue(langEvenAs.contains(Words.replicate(b, 2)));
        assertTrue(langEvenAs.contains(Words.replicate(b, 3)));
        assertTrue(langEvenAs.contains(Words.concat(b, aa)));
        assertTrue(langEvenAs.contains(Words.concat(Words.replicate(b, 3), aa)));
        assertTrue(langEvenAs.contains(aab));
        assertTrue(langEvenAs.contains(aba));

        assertFalse(langEvenAs.contains(a));
        assertFalse(langEvenAs.contains(ab));
        assertFalse(langEvenAs.contains(ac));
        assertFalse(langEvenAs.contains(aaa));
    }

    @Test
    public void testComputeRegularGrammarFromNFA() {
        // The nfa, grammar and test case is taken from TI lecture (Buchin), slide 59

        String q0 = "q0";
        String q1 = "q1";
        String q2 = "q2";
        String q3 = "q3";

        NFA<String, Character> nfa =
                new NFABuilder<String, Character>(Alphabets.characterAlphabet("ab"))
                        .withInitial(q0)
                        .withAccepting(q3)
                        .withStates(q1, q2)
                        .withTransition(q0, 'a', q1)
                        .withTransition(q1, 'a', q2)
                        .withTransition(q2, 'a', q3)
                        .withTransition(q3, 'a', q0)
                        .withTransition(q0, 'b', q3)
                        .withTransition(q3, 'b', q2)
                        .withTransition(q2, 'b', q1)
                        .withTransition(q1, 'b', q0)
                        .build()
                        .unwrap();

        Grammar<
                        Character,
                        LinkedHashSet<String>,
                        RightRegularProduction<Character, LinkedHashSet<String>>>
                rg = RegularLanguage.computeRegularGrammarFromNFA(nfa);

        RightRegularProduction<Character, LinkedHashSet<String>> prod1 =
                new RightRegularProduction<>(
                        new LinkedHashSet<>(Collections.singleton(q0)),
                        'a',
                        new LinkedHashSet<>(Collections.singleton(q1)));
        RightRegularProduction<Character, LinkedHashSet<String>> prod2 =
                new RightRegularProduction<>(
                        new LinkedHashSet<>(Collections.singleton(q0)),
                        'b',
                        new LinkedHashSet<>(Collections.singleton(q3)));
        RightRegularProduction<Character, LinkedHashSet<String>> prod3 =
                new RightRegularProduction<>(new LinkedHashSet<>(Collections.singleton(q0)), 'b');
        RightRegularProduction<Character, LinkedHashSet<String>> prod4 =
                new RightRegularProduction<>(
                        new LinkedHashSet<>(Collections.singleton(q1)),
                        'a',
                        new LinkedHashSet<>(Collections.singleton(q2)));
        RightRegularProduction<Character, LinkedHashSet<String>> prod5 =
                new RightRegularProduction<>(
                        new LinkedHashSet<>(Collections.singleton(q1)),
                        'b',
                        new LinkedHashSet<>(Collections.singleton(q0)));
        RightRegularProduction<Character, LinkedHashSet<String>> prod6 =
                new RightRegularProduction<>(
                        new LinkedHashSet<>(Collections.singleton(q2)),
                        'a',
                        new LinkedHashSet<>(Collections.singleton(q3)));
        RightRegularProduction<Character, LinkedHashSet<String>> prod7 =
                new RightRegularProduction<>(new LinkedHashSet<>(Collections.singleton(q2)), 'a');
        RightRegularProduction<Character, LinkedHashSet<String>> prod8 =
                new RightRegularProduction<>(
                        new LinkedHashSet<>(Collections.singleton(q2)),
                        'b',
                        new LinkedHashSet<>(Collections.singleton(q1)));
        RightRegularProduction<Character, LinkedHashSet<String>> prod9 =
                new RightRegularProduction<>(
                        new LinkedHashSet<>(Collections.singleton(q3)),
                        'a',
                        new LinkedHashSet<>(Collections.singleton(q0)));
        RightRegularProduction<Character, LinkedHashSet<String>> prod10 =
                new RightRegularProduction<>(
                        new LinkedHashSet<>(Collections.singleton(q3)),
                        'b',
                        new LinkedHashSet<>(Collections.singleton(q2)));

        Set<RightRegularProduction<Character, LinkedHashSet<String>>> productions =
                rg.getProductions();

        assertTrue(productions.contains(prod1));
        assertTrue(productions.contains(prod2));
        assertTrue(productions.contains(prod3));
        assertTrue(productions.contains(prod4));
        assertTrue(productions.contains(prod5));
        assertTrue(productions.contains(prod6));
        assertTrue(productions.contains(prod7));
        assertTrue(productions.contains(prod8));
        assertTrue(productions.contains(prod9));
        assertTrue(productions.contains(prod10));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRegexWithRangeAndNaturalSerializableComparator() {
        Alphabet<Character> localAlphabet = Alphabets.characterAlphabet("abc");

        Symbol<Character> a = new Symbol<>(localAlphabet, 'a');
        Symbol<Character> c = new Symbol<>(localAlphabet, 'c');

        RegularLanguage<Character> language =
                new RegularLanguage<>(Range.from(localAlphabet, a.getSymbol(), c.getSymbol()));
        NFA<Serializable, Character> nfa = (NFA<Serializable, Character>) language.getNFA();

        NFAExecutor<? extends Serializable, Character> exec =
                new NFAExecutor<>(nfa, new Word<>('a'));
        assertTrue(exec.run());
        exec = new NFAExecutor<>(nfa, new Word<>('b'));
        assertTrue(exec.run());
        exec = new NFAExecutor<>(nfa, new Word<>('c'));
        assertTrue(exec.run());

        exec = new NFAExecutor<>(nfa, Words.characterWord("ab"));
        assertFalse(exec.run());
        exec = new NFAExecutor<>(nfa, new Word<>('d'));
        assertFalse(exec.run());
    }

    @Test
    public void testIsEqualToWithLargeAlphabet() {
        Alphabet<IndexedSymbol> largeAlphabet =
                Alphabets.indexedSymbolAlphabet(
                        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        Alphabet<IndexedSymbol> smallAlphabet = Alphabets.indexedSymbolAlphabet("ab");

        RegularLanguage<IndexedSymbol> target =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "a*",
                                RegularExpressionReaderProperties.createDefault(largeAlphabet)));
        RegularLanguage<IndexedSymbol> test1 =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "a*",
                                RegularExpressionReaderProperties.createDefault(smallAlphabet)));
        RegularLanguage<IndexedSymbol> test2 =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "aab*",
                                RegularExpressionReaderProperties.createDefault(smallAlphabet)));

        Assert.assertFalse(test1.isEqualTo(test2));
        Assert.assertTrue(test1.isEqualTo(new RegularLanguage<>(target.getNFA())));
        Assert.assertTrue(test1.isEqualTo(target));
        Assert.assertTrue(target.isEqualTo(test1));
    }
}

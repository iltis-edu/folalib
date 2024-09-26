package de.tudortmund.cs.iltis.folalib.automata.pushdown.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.*;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.transformation.PDAToCFGTransformation;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.languages.*;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;

public class PDAAcceptingStatesToEmptyStackConversionTest extends Utils {

    private final int MAX_STEPS = 1000;

    private final String q0 = "q0";
    private final String q1 = "q1";

    /* Automaton taken from GTI lecture slides (SS18), page 343 */
    PDA<String, Character, Integer> longerPrefixThanSuffixPDA =
            new PDABuilder<String, Character, Integer>(Alphabets.characterAlphabet("ab"))
                    .withStackSymbols(new Alphabet<>(0, 1, 2))
                    .withStates(q0, q1)
                    .withInitial(q0)
                    .withAccepting(q1)
                    .withInitialStackSymbol(0)
                    .withAcceptanceStrategy(PDAAcceptanceStrategy.ACCEPTING_STATES)
                    .withTransition(q0, 'a', 0, q0, new PDAStackWord<>(1, 0))
                    .withTransition(q0, 'a', 1, q0, new PDAStackWord<>(1, 1))
                    .withTransition(q0, 'a', 2, q0, new PDAStackWord<>(1, 2))
                    .withEpsilonTransition(q0, 0, q1, new PDAStackWord<>(0))
                    .withEpsilonTransition(q0, 1, q1, new PDAStackWord<>(1))
                    .withEpsilonTransition(q0, 2, q1, new PDAStackWord<>(2))
                    .withTransition(q1, 'b', 1, q1, new PDAStackWord<>())
                    .build()
                    .unwrap();

    @Test
    public void testConversion() {
        PDAConversion<
                        String,
                        MaybeGenerated<String, String>,
                        Character,
                        Integer,
                        MaybeGenerated<Integer, String>>
                conversion = new PDAAcceptingStatesToEmptyStackConversion<>();
        PDA<MaybeGenerated<String, String>, Character, MaybeGenerated<Integer, String>>
                convertedPDA = conversion.apply(longerPrefixThanSuffixPDA);

        Set<Word<Character>> wordList = new LinkedHashSet<>();
        wordList.add(Words.characterWord(""));
        wordList.add(Words.characterWord("aa"));
        wordList.add(Words.characterWord("aaaabbb"));
        wordList.add(Words.characterWord("aaabbb"));
        wordList.add(Words.characterWord("aaabbbb"));
        wordList.add(Words.characterWord("aababbbbb"));
        wordList.add(Words.characterWord("bbbbbbb"));
        assertEquals(
                PDAAcceptanceStrategy.ACCEPTING_STATES,
                longerPrefixThanSuffixPDA.getAcceptanceStrategy());
        assertEquals(PDAAcceptanceStrategy.EMPTY_STACK, convertedPDA.getAcceptanceStrategy());
        // sanity check: each automaton is equivalent to itself
        assertEquivalent(longerPrefixThanSuffixPDA, longerPrefixThanSuffixPDA, wordList, MAX_STEPS);
        assertEquivalent(convertedPDA, convertedPDA, wordList, MAX_STEPS);
        // actual test case
        assertEquivalent(longerPrefixThanSuffixPDA, convertedPDA, wordList, MAX_STEPS);
    }

    @Test
    public void testMultipleInitialStates() {
        PDA<String, Character, Integer> pda =
                new PDABuilder<>(longerPrefixThanSuffixPDA)
                        .withInitial("q2")
                        .withEpsilonTransition(
                                "q2",
                                longerPrefixThanSuffixPDA.getInitialStackSymbol(),
                                q0,
                                new PDAStackWord<>(
                                        longerPrefixThanSuffixPDA.getInitialStackSymbol()))
                        .build()
                        .unwrap();

        PDAConversion<
                        String,
                        MaybeGenerated<String, String>,
                        Character,
                        Integer,
                        MaybeGenerated<Integer, String>>
                conversion = new PDAAcceptingStatesToEmptyStackConversion<>();
        PDA<MaybeGenerated<String, String>, Character, MaybeGenerated<Integer, String>>
                convertedPDA = conversion.apply(pda);

        Set<Word<Character>> wordList = new LinkedHashSet<>();
        wordList.add(Words.characterWord(""));
        wordList.add(Words.characterWord("aa"));
        wordList.add(Words.characterWord("aaaabbb"));
        wordList.add(Words.characterWord("aaabbb"));
        wordList.add(Words.characterWord("aaabbbb"));
        wordList.add(Words.characterWord("aababbbbb"));
        wordList.add(Words.characterWord("bbbbbbb"));
        assertEquals(
                PDAAcceptanceStrategy.ACCEPTING_STATES,
                longerPrefixThanSuffixPDA.getAcceptanceStrategy());
        assertEquals(PDAAcceptanceStrategy.EMPTY_STACK, convertedPDA.getAcceptanceStrategy());
        // sanity check: each automaton is equivalent to itself
        assertEquivalent(longerPrefixThanSuffixPDA, longerPrefixThanSuffixPDA, wordList, MAX_STEPS);
        assertEquivalent(convertedPDA, convertedPDA, wordList, MAX_STEPS);
        // actual test case
        assertEquivalent(longerPrefixThanSuffixPDA, convertedPDA, wordList, MAX_STEPS);
    }

    @Test
    public void testFixedBug() {
        Alphabet<Character> inputAlphabet = Alphabets.characterAlphabet("abc");
        Alphabet<Character> stackAlphabet = Alphabets.characterAlphabet("ABC");
        String q0 = "q0";
        String q1 = "q1";
        String q2 = "q2";
        String q3 = "q3";
        Character initialStackSymbol = '#';
        PDABuilder<String, Character, Character> builder = new PDABuilder<>(inputAlphabet);
        Character wildcard = null;

        PDA<String, Character, Character> emptyLanugagePDA =
                builder.withStackSymbols(stackAlphabet)
                        .withStates(q0, q1, q2, q3)
                        .withInitial(q0)
                        .withInitialStackSymbol(initialStackSymbol)
                        .withAccepting(q3)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.ACCEPTING_STATES)
                        .withTransition(
                                q0,
                                'a',
                                PDAStackSymbol.wildcard(),
                                q0,
                                new PDAStackWord<>('A', wildcard))
                        .withTransition(
                                q0,
                                'b',
                                PDAStackSymbol.wildcard(),
                                q0,
                                new PDAStackWord<>('B', wildcard))
                        .withTransition(
                                q0,
                                'c',
                                PDAStackSymbol.wildcard(),
                                q1,
                                new PDAStackWord<>(wildcard))
                        .withTransition(q1, 'a', 'A', q1, new PDAStackWord<>())
                        .withTransition(q1, 'b', 'B', q1, new PDAStackWord<>())
                        .withEpsilonTransition(q1, 'C', q1, new PDAStackWord<>())
                        // .withTransition(q1, 'c', 'C', q1, new PDAStackWord<>())

                        .withEpsilonTransition(q1, initialStackSymbol, q2, new PDAStackWord<>())
                        .withEpsilonTransition(
                                q2,
                                PDAStackSymbol.wildcard(),
                                q3,
                                new PDAStackWord<>(initialStackSymbol))
                        .build()
                        .unwrap();

        assertRejects(emptyLanugagePDA, Words.characterWord(""), MAX_STEPS);
        assertRejects(emptyLanugagePDA, Words.characterWord("abcba"), MAX_STEPS);
        assertRejects(emptyLanugagePDA, Words.characterWord("aabcbaa"), MAX_STEPS);
        assertRejects(emptyLanugagePDA, Words.characterWord("a"), MAX_STEPS);
        assertRejects(emptyLanugagePDA, Words.characterWord("b"), MAX_STEPS);
        assertRejects(emptyLanugagePDA, Words.characterWord("ab"), MAX_STEPS);

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> cfg =
                new PDAToCFGTransformation<String, Character, Character>()
                        .transform(emptyLanugagePDA);
        ContextFreeLanguage<Character> cfl = new ContextFreeLanguage<>(cfg);

        assertFalse(cfl.contains(Words.characterWord("")));
        assertFalse(cfl.contains(Words.characterWord("abcba")));
        assertFalse(cfl.contains(Words.characterWord("aabcbaa")));
        assertFalse(cfl.contains(Words.characterWord("a")));
        assertFalse(cfl.contains(Words.characterWord("ab")));
    }
}

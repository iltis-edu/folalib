package de.tudortmund.cs.iltis.folalib.languages;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.expressions.regular.EmptyWord;
import de.tudortmund.cs.iltis.folalib.expressions.regular.Symbol;
import java.util.Comparator;
import java.util.List;
import org.junit.Test;

public class RegularLanguageSmallestWordTest {

    @Test
    public void testShorterWord() {
        RegularLanguage<Character> lang =
                new RegularLanguage<>(
                        new Symbol<>('a')
                                .concat(new Symbol<>('b'), new Symbol<>('c'))
                                .or(new Symbol<>('z').concat(new Symbol<>('z'))));

        assertEquals(new Word<>('z', 'z'), lang.getRandomShortestWord());
        assertEquals(new Word<>('z', 'z'), lang.getSmallestWord(Comparator.naturalOrder()));
        assertEquals(new Word<>('z', 'z'), lang.getSmallestWord(Comparator.reverseOrder()));
    }

    @Test
    public void testEqualSize() {
        RegularLanguage<Character> lang =
                new RegularLanguage<>(
                        new Symbol<>('a')
                                .concat(new Symbol<>('b'))
                                .or(new Symbol<>('b').concat(new Symbol<>('a'))));

        assertTrue(
                List.of(new Word<>('a', 'b'), new Word<>('b', 'a'))
                        .contains(lang.getRandomShortestWord()));
        assertEquals(new Word<>('a', 'b'), lang.getSmallestWord(Comparator.naturalOrder()));
        assertEquals(new Word<>('b', 'a'), lang.getSmallestWord(Comparator.reverseOrder()));
    }

    @Test
    public void testEqualSize2() {
        RegularLanguage<Character> lang =
                new RegularLanguage<>(
                        new Symbol<>('b')
                                .concat(new Symbol<>('a'))
                                .or(
                                        new Symbol<>('b').concat(new Symbol<>('b')),
                                        new Symbol<>('c').concat(new Symbol<>('a')),
                                        new Symbol<>('a').concat(new Symbol<>('c')),
                                        new Symbol<>('a').concat(new Symbol<>('d'))));

        assertTrue(
                List.of(
                                new Word<>('b', 'a'),
                                new Word<>('b', 'b'),
                                new Word<>('c', 'a'),
                                new Word<>('a', 'c'),
                                new Word<>('a', 'd'))
                        .contains(lang.getRandomShortestWord()));
        assertEquals(new Word<>('a', 'c'), lang.getSmallestWord(Comparator.naturalOrder()));
        assertEquals(new Word<>('c', 'a'), lang.getSmallestWord(Comparator.reverseOrder()));
    }

    @Test
    public void testEpsilon() {
        RegularLanguage<Character> lang =
                new RegularLanguage<>(new EmptyWord<Character>().or(new Symbol<>('a')));

        assertEquals(new Word<>(), lang.getRandomShortestWord());
        assertEquals(new Word<>(), lang.getSmallestWord(Comparator.naturalOrder()));
        assertEquals(new Word<>(), lang.getSmallestWord(Comparator.reverseOrder()));
    }

    @Test
    public void testMultipleInitialStates() {
        RegularLanguage<Character> lang =
                new RegularLanguage<>(
                        new NFABuilder<Integer, Character>(Alphabets.characterAlphabet("ab"))
                                .withStates(1, 2, 3, 4)
                                .withAccepting(3)
                                .withInitial(1, 4)
                                .withTransition(1, 'b', 2)
                                .withTransition(4, 'a', 2)
                                .withTransition(2, 'b', 3)
                                .build()
                                .unwrap());

        assertTrue(
                List.of(new Word<>('a', 'b'), new Word<>('b', 'b'))
                        .contains(lang.getRandomShortestWord()));
        assertEquals(new Word<>('a', 'b'), lang.getSmallestWord(Comparator.naturalOrder()));
        assertEquals(new Word<>('b', 'b'), lang.getSmallestWord(Comparator.reverseOrder()));
    }

    @Test
    public void testUnreachableAcceptingState() {
        RegularLanguage<Character> lang =
                new RegularLanguage<>(
                        new NFABuilder<Integer, Character>(Alphabets.characterAlphabet("ab"))
                                .withStates(1, 2, 3, 9)
                                .withAccepting(3, 9)
                                .withInitial(1)
                                .withTransition(1, 'a', 2)
                                .withTransition(2, 'b', 3)
                                .withTransition(9, 'a', 1)
                                .build()
                                .unwrap());

        assertEquals(new Word<>('a', 'b'), lang.getRandomShortestWord());
        assertEquals(new Word<>('a', 'b'), lang.getSmallestWord(Comparator.naturalOrder()));
        assertEquals(new Word<>('a', 'b'), lang.getSmallestWord(Comparator.reverseOrder()));
    }

    @Test
    public void testEmptyLanguage() {
        RegularLanguage<Character> lang =
                new RegularLanguage<>(
                        new NFABuilder<Integer, Character>(Alphabets.characterAlphabet("ab"))
                                .withStates(1, 2, 3, 9)
                                // No accepting states
                                .withInitial(1)
                                .withTransition(1, 'a', 2)
                                .withTransition(2, 'b', 3)
                                .withTransition(9, 'a', 1)
                                .build()
                                .unwrap());

        assertNull(lang.getRandomShortestWord());
        assertNull(lang.getSmallestWord(Comparator.naturalOrder()));
        assertNull(lang.getSmallestWord(Comparator.reverseOrder()));
    }
}

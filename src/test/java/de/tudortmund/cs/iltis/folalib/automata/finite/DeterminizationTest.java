package de.tudortmund.cs.iltis.folalib.automata.finite;

import static org.junit.Assert.assertEquals;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import java.util.Arrays;
import java.util.LinkedHashSet;
import org.junit.Test;

public class DeterminizationTest {
    @Test
    public void testDeterminazation() {
        NFA<Integer, Character> toDeterminize =
                new NFABuilder<Integer, Character>(new Alphabet<>('a'))
                        .withInitial(0)
                        .withStates(1, 2, 3)
                        .withAccepting(4)
                        .withTransition(0, 'a', 1)
                        .withEpsilonTransition(0, 2)
                        .withEpsilonTransition(1, 0)
                        .withTransition(1, 'a', 2)
                        .withTransition(1, 'a', 3)
                        .withTransition(2, 'a', 2)
                        .withTransition(3, 'a', 2)
                        .withTransition(3, 'a', 3)
                        .withEpsilonTransition(3, 0)
                        .withTransition(3, 'a', 4)
                        .build()
                        .unwrap();

        NFA<LinkedHashSet<Integer>, Character> expected =
                new DFABuilder<LinkedHashSet<Integer>, Character>(new Alphabet<>('a'))
                        .withInitial(makeLinkedHashSet(0, 2))
                        .withStates(makeLinkedHashSet(0, 1, 2), makeLinkedHashSet(0, 1, 2, 3))
                        .withAccepting(makeLinkedHashSet(0, 1, 2, 3, 4))
                        .withTransition(makeLinkedHashSet(0, 2), 'a', makeLinkedHashSet(0, 1, 2))
                        .withTransition(
                                makeLinkedHashSet(0, 1, 2), 'a', makeLinkedHashSet(0, 1, 2, 3))
                        .withTransition(
                                makeLinkedHashSet(0, 1, 2, 3),
                                'a',
                                makeLinkedHashSet(0, 1, 2, 3, 4))
                        .withTransition(
                                makeLinkedHashSet(0, 1, 2, 3, 4),
                                'a',
                                makeLinkedHashSet(0, 1, 2, 3, 4))
                        .build()
                        .unwrap();

        NFA<LinkedHashSet<Integer>, Character> determinized = toDeterminize.determinize();

        assertEquals(expected, determinized);
    }

    @Test
    public void testDeterminazationWithMultipleInitialStates() {
        NFA<Integer, Character> toDeterminize =
                new NFABuilder<Integer, Character>(new Alphabet<>('a'))
                        .withInitial(0, 4)
                        .withStates(1, 2, 3)
                        .withAccepting(4)
                        .withTransition(0, 'a', 1)
                        .withEpsilonTransition(0, 2)
                        .withEpsilonTransition(1, 0)
                        .withTransition(1, 'a', 2)
                        .withTransition(1, 'a', 3)
                        .withTransition(2, 'a', 2)
                        .withTransition(3, 'a', 2)
                        .withTransition(3, 'a', 3)
                        .withEpsilonTransition(3, 0)
                        .withTransition(3, 'a', 4)
                        .build()
                        .unwrap();

        NFA<LinkedHashSet<Integer>, Character> expected =
                new DFABuilder<LinkedHashSet<Integer>, Character>(new Alphabet<>('a'))
                        .withInitial(makeLinkedHashSet(0, 2, 4))
                        .withStates(makeLinkedHashSet(0, 1, 2), makeLinkedHashSet(0, 1, 2, 3))
                        .withAccepting(makeLinkedHashSet(0, 1, 2, 3, 4), makeLinkedHashSet(0, 2, 4))
                        .withTransition(makeLinkedHashSet(0, 2, 4), 'a', makeLinkedHashSet(0, 1, 2))
                        .withTransition(
                                makeLinkedHashSet(0, 1, 2), 'a', makeLinkedHashSet(0, 1, 2, 3))
                        .withTransition(
                                makeLinkedHashSet(0, 1, 2, 3),
                                'a',
                                makeLinkedHashSet(0, 1, 2, 3, 4))
                        .withTransition(
                                makeLinkedHashSet(0, 1, 2, 3, 4),
                                'a',
                                makeLinkedHashSet(0, 1, 2, 3, 4))
                        .build()
                        .unwrap();

        NFA<LinkedHashSet<Integer>, Character> determinized = toDeterminize.determinize();

        assertEquals(expected, determinized);
    }

    private static <T> LinkedHashSet<T> makeLinkedHashSet(T... ts) {
        return new LinkedHashSet<>(Arrays.asList(ts));
    }
}

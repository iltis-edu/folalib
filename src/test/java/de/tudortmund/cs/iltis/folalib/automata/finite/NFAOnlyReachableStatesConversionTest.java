package de.tudortmund.cs.iltis.folalib.automata.finite;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.finite.conversion.NFAOnlyReachableStatesConversion;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import org.junit.Test;

public class NFAOnlyReachableStatesConversionTest {

    @Test
    public void test() {
        NFA<Character, Character> nfaToConvert =
                new NFABuilder<Character, Character>(Alphabets.characterAlphabet("abcd"))
                        .withStates('A', 'B', 'C', 'X', 'Y', 'Z')
                        .withInitial('A')
                        .withAccepting('C', 'Y')
                        .withEpsilonTransition('X', 'Y')
                        .withEpsilonTransition('Y', 'A')
                        .withTransition('Y', 'a', 'X')
                        .withTransition('A', 'b', 'B')
                        .withTransition('B', 'c', 'C')
                        .withTransition('X', 'b', 'B')
                        .withTransition('B', 'a', 'A')
                        .withStates('Z')
                        .build()
                        .unwrap();

        NFA<Character, Character> targetNfa =
                new NFABuilder<Character, Character>(Alphabets.characterAlphabet("abcd"))
                        .withStates('A', 'B', 'C')
                        .withInitial('A')
                        .withAccepting('C')
                        .withTransition('A', 'b', 'B')
                        .withTransition('B', 'c', 'C')
                        .withTransition('B', 'a', 'A')
                        .build()
                        .unwrap();

        assertEquals(
                targetNfa,
                new NFAOnlyReachableStatesConversion<Character, Character>().apply(nfaToConvert));
    }
}

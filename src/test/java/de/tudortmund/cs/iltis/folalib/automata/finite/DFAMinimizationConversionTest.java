package de.tudortmund.cs.iltis.folalib.automata.finite;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.finite.conversion.DFAMinimizationConversion;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import java.util.Arrays;
import java.util.LinkedHashSet;
import org.junit.Test;

public class DFAMinimizationConversionTest {

    private final DFAMinimizationConversion<Integer, Character> converter =
            new DFAMinimizationConversion<>();

    /**
     * Example taken from Buchin TI lecture slide 65 from topic "Reguläre Sprachen" (page 111 in
     * PDF)
     */
    @Test
    public void testMinimization() {
        NFA<Integer, Character> toMinimize =
                new DFABuilder<Integer, Character>(Alphabets.characterAlphabet("ab"))
                        .withStates(0, 1, 2, 3, 4)
                        .withInitial(0)
                        .withAccepting(4)
                        .withTransition(0, 'a', 1)
                        .withTransition(0, 'b', 2)
                        .withTransition(1, 'a', 4)
                        .withTransition(1, 'b', 2)
                        .withTransition(2, 'a', 3)
                        .withTransition(2, 'b', 2)
                        .withTransition(3, 'a', 4)
                        .withTransition(3, 'b', 0)
                        .withTransition(4, 'a', 4)
                        .withTransition(4, 'b', 4)

                        // Add unreachable state and superfluous transition (not in Buchin example)
                        // Should get removed automatically
                        .withStates(9)
                        .withTransition(9, 'a', 0)
                        .withTransition(9, 'b', 1) // needs to be total
                        .build()
                        .unwrap();

        NFA<LinkedHashSet<Integer>, Character> targetDfa =
                new DFABuilder<LinkedHashSet<Integer>, Character>(Alphabets.characterAlphabet("ab"))
                        .withStates(
                                makeLinkedHashSet(0, 2),
                                makeLinkedHashSet(1, 3),
                                makeLinkedHashSet(4))
                        .withInitial(makeLinkedHashSet(0, 2))
                        .withAccepting(makeLinkedHashSet(4))
                        .withTransition(makeLinkedHashSet(0, 2), 'a', makeLinkedHashSet(1, 3))
                        .withTransition(makeLinkedHashSet(0, 2), 'b', makeLinkedHashSet(0, 2))
                        .withTransition(makeLinkedHashSet(1, 3), 'a', makeLinkedHashSet(4))
                        .withTransition(makeLinkedHashSet(1, 3), 'b', makeLinkedHashSet(0, 2))
                        .withTransition(makeLinkedHashSet(4), 'a', makeLinkedHashSet(4))
                        .withTransition(makeLinkedHashSet(4), 'b', makeLinkedHashSet(4))
                        .build()
                        .unwrap();

        NFA<LinkedHashSet<Integer>, Character> minimizedDfa = converter.apply(toMinimize);
        assertEquals(targetDfa, minimizedDfa);
        assertTrue(new RegularLanguage<>(targetDfa).isEqualTo(new RegularLanguage<>(minimizedDfa)));
    }

    private static LinkedHashSet<Integer> makeLinkedHashSet(Integer... data) {
        return new LinkedHashSet<>(Arrays.asList(data));
    }
}

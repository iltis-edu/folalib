package de.tudortmund.cs.iltis.folalib.automata.finite;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import java.util.Collection;
import org.junit.Test;

public class EpsilonNFARemoveEpsilonTransitionsConversionTest {

    /**
     * This test case is taken from <a
     * href="https://www.geeksforgeeks.org/conversion-of-epsilon-nfa-to-nfa/">here</a>.
     */
    @Test
    public void testExampleNFA() {
        NFA<String, Integer> toConvert =
                new NFABuilder<String, Integer>(new Alphabet<>(0, 1))
                        .withStates("q0", "q1", "q2", "q3", "q4")
                        .withInitial("q0")
                        .withAccepting("q2")
                        .withTransition("q0", 1, "q1")
                        .withEpsilonTransition("q0", "q2")
                        .withTransition("q1", 1, "q0")
                        .withTransition("q2", 0, "q3")
                        .withTransition("q2", 1, "q4")
                        .withTransition("q3", 0, "q2")
                        .withTransition("q4", 0, "q2")
                        .build()
                        .unwrap();

        NFA<String, Integer> target =
                new NFABuilder<String, Integer>(new Alphabet<>(0, 1))
                        .withStates("q0", "q1", "q2", "q3", "q4")
                        .withInitial("q0", "q2")
                        .withAccepting("q0", "q2")
                        .withTransition("q0", 1, "q1")
                        .withTransition("q0", 0, "q3")
                        .withTransition("q0", 1, "q4")
                        .withTransition("q1", 1, "q0")
                        .withTransition("q2", 0, "q3")
                        .withTransition("q2", 1, "q4")
                        .withTransition("q3", 0, "q2")
                        .withTransition("q4", 0, "q2")
                        .build()
                        .unwrap();

        NFA<String, Integer> converted = toConvert.removeEpsilonTransitions();

        assertEquals(target, converted);
    }

    @Test
    public void testNFAWithCirclingEpsilonTransitions() {
        NFA<String, Integer> toConvert =
                new NFABuilder<String, Integer>(new Alphabet<>(0, 1))
                        .withStates("q0", "q1", "q2", "q3", "q4")
                        .withInitial("q0", "q4")
                        .withAccepting("q2", "q3")
                        .withEpsilonTransition("q2", "q2")
                        .withTransition("q0", 1, "q1")
                        .withEpsilonTransition("q0", "q2")
                        .withEpsilonTransition("q1", "q2")
                        .withEpsilonTransition("q2", "q3")
                        .withEpsilonTransition("q3", "q1")
                        .withEpsilonTransition("q3", "q4")
                        .withEpsilonTransition("q4", "q3")
                        .withTransition("q2", 1, "q0")
                        .withTransition("q1", 1, "q4")
                        .withTransition("q4", 0, "q4")
                        .build()
                        .unwrap();

        NFA<String, Integer> converted = toConvert.removeEpsilonTransitions();

        assertTrue(new RegularLanguage<>(toConvert).isEqualTo(new RegularLanguage<>(converted)));
        assertTrue(
                converted.getTransitions().getTransitions().values().stream()
                        .flatMap(Collection::stream)
                        .noneMatch(NFATransition::isEpsilon));
    }

    @Test
    public void testNFAWithCirclingEpsilonTransitionsInTheMiddle() {
        NFA<String, Integer> toConvert =
                new NFABuilder<String, Integer>(new Alphabet<>(0, 1))
                        .withStates("q0", "q1", "q2", "q3", "q4", "q5")
                        .withInitial("q0", "q2")
                        .withAccepting("q3", "q5")
                        .withEpsilonTransition("q0", "q3")
                        .withEpsilonTransition("q3", "q0")
                        .withTransition("q0", 0, "q1")
                        .withTransition("q1", 1, "q2")
                        .withTransition("q2", 0, "q0")
                        .withTransition("q3", 0, "q4")
                        .withTransition("q4", 1, "q5")
                        .withTransition("q5", 0, "q3")
                        .build()
                        .unwrap();

        NFA<String, Integer> converted = toConvert.removeEpsilonTransitions();

        assertTrue(new RegularLanguage<>(toConvert).isEqualTo(new RegularLanguage<>(converted)));
        assertTrue(
                converted.getTransitions().getTransitions().values().stream()
                        .flatMap(Collection::stream)
                        .noneMatch(NFATransition::isEpsilon));
    }
}

package de.tudortmund.cs.iltis.folalib.automata.finite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import org.junit.Test;

public class EpsilonNFAOnlyOneInitialStateTest {

    @Test
    public void test() {
        NFA<Integer, Character> nfa =
                new NFABuilder<Integer, Character>(Alphabets.characterAlphabet("abc"))
                        .withStates(1, 2, 3, 4, 5, 6)
                        .withInitial(1, 2, 3)
                        .withAccepting(4, 5, 6)
                        .withTransition(1, 'a', 4)
                        .withTransition(2, 'b', 5)
                        .withTransition(3, 'c', 6)
                        .build()
                        .unwrap();

        NFA<MaybeGenerated<Integer, String>, Character> targetNfa =
                new NFABuilder<MaybeGenerated<Integer, String>, Character>(
                                Alphabets.characterAlphabet("abc"))
                        .withStates(
                                new MaybeGenerated.Input<>(1),
                                new MaybeGenerated.Input<>(2),
                                new MaybeGenerated.Input<>(3),
                                new MaybeGenerated.Input<>(4),
                                new MaybeGenerated.Input<>(5),
                                new MaybeGenerated.Input<>(6),
                                new MaybeGenerated.Generated<>("newInitialState"))
                        .withInitial(new MaybeGenerated.Generated<>("newInitialState"))
                        .withAccepting(
                                new MaybeGenerated.Input<>(4),
                                new MaybeGenerated.Input<>(5),
                                new MaybeGenerated.Input<>(6))
                        .withTransition(
                                new MaybeGenerated.Input<>(1), 'a', new MaybeGenerated.Input<>(4))
                        .withTransition(
                                new MaybeGenerated.Input<>(2), 'b', new MaybeGenerated.Input<>(5))
                        .withTransition(
                                new MaybeGenerated.Input<>(3), 'c', new MaybeGenerated.Input<>(6))
                        .withEpsilonTransition(
                                new MaybeGenerated.Generated<>("newInitialState"),
                                new MaybeGenerated.Input<>(1))
                        .withEpsilonTransition(
                                new MaybeGenerated.Generated<>("newInitialState"),
                                new MaybeGenerated.Input<>(2))
                        .withEpsilonTransition(
                                new MaybeGenerated.Generated<>("newInitialState"),
                                new MaybeGenerated.Input<>(3))
                        .build()
                        .unwrap();

        NFA<MaybeGenerated<Integer, String>, Character> convertedNfa = nfa.onlyOneInitialState();

        assertEquals(targetNfa, convertedNfa);

        assertTrue(new RegularLanguage<>(nfa).isEqualTo(new RegularLanguage<>(convertedNfa)));
    }
}

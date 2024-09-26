package de.tudortmund.cs.iltis.folalib.automata.pushdown.conversion;

import static org.junit.Assert.assertEquals;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.*;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.folalib.languages.Words;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;

public class PDAEmptyStackToAcceptingStatesConversionTest extends Utils {

    private final int MAX_STEPS = 1000;

    private final String q0 = "q0";
    private final String q1 = "q1";

    /* Automaton taken from GTI lecture slides (SS18), page 337 */
    private final PDA<String, Character, Integer> correctParenthesesPDA =
            new PDABuilder<String, Character, Integer>(Alphabets.characterAlphabet("(){}"))
                    .withStackSymbols(new Alphabet<>(0, 1, 2))
                    .withStates(q0, q1)
                    .withInitial(q0)
                    .withInitialStackSymbol(0)
                    .withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK)
                    .withTransition(q0, '(', 0, q0, new PDAStackWord<>(1, 0))
                    .withTransition(q0, '(', 1, q0, new PDAStackWord<>(1, 1))
                    .withTransition(q0, '(', 2, q0, new PDAStackWord<>(1, 2))
                    .withTransition(q0, '{', 0, q0, new PDAStackWord<>(2, 0))
                    .withTransition(q0, '{', 1, q0, new PDAStackWord<>(2, 1))
                    .withTransition(q0, '{', 2, q0, new PDAStackWord<>(2, 2))
                    .withTransition(q0, ')', 1, q0, new PDAStackWord<>())
                    .withTransition(q0, '}', 2, q0, new PDAStackWord<>())
                    .withEpsilonTransition(q0, 0, q1, new PDAStackWord<>())
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
                conversion = new PDAEmptyStackToAcceptingStatesConversion<>();
        PDA<MaybeGenerated<String, String>, Character, MaybeGenerated<Integer, String>>
                convertedPDA = conversion.apply(correctParenthesesPDA);

        Set<Word<Character>> wordList = new LinkedHashSet<>();
        wordList.add(Words.characterWord(""));
        wordList.add(Words.characterWord("(())"));
        wordList.add(Words.characterWord("(){}()"));
        wordList.add(Words.characterWord("({()()})()"));
        wordList.add(Words.characterWord("{{()}}"));
        wordList.add(Words.characterWord("(()"));
        wordList.add(Words.characterWord("()({)})"));
        wordList.add(Words.characterWord("()({)}"));
        assertEquals(
                PDAAcceptanceStrategy.EMPTY_STACK, correctParenthesesPDA.getAcceptanceStrategy());
        assertEquals(PDAAcceptanceStrategy.ACCEPTING_STATES, convertedPDA.getAcceptanceStrategy());
        // sanity check: each automaton is equivalent to itself
        assertEquivalent(correctParenthesesPDA, correctParenthesesPDA, wordList, MAX_STEPS);
        assertEquivalent(convertedPDA, convertedPDA, wordList, MAX_STEPS);
        // actual test case
        assertEquivalent(correctParenthesesPDA, convertedPDA, wordList, MAX_STEPS);
    }

    @Test
    public void testErrorMultipleInitialStates() {
        PDA<String, Character, Integer> pda =
                new PDABuilder<>(correctParenthesesPDA)
                        .withInitial("q2")
                        .withEpsilonTransition(
                                "q2",
                                correctParenthesesPDA.getInitialStackSymbol(),
                                q0,
                                new PDAStackWord<>(correctParenthesesPDA.getInitialStackSymbol()))
                        .build()
                        .unwrap();

        PDAConversion<
                        String,
                        MaybeGenerated<String, String>,
                        Character,
                        Integer,
                        MaybeGenerated<Integer, String>>
                conversion = new PDAEmptyStackToAcceptingStatesConversion<>();
        PDA<MaybeGenerated<String, String>, Character, MaybeGenerated<Integer, String>>
                convertedPDA = conversion.apply(pda);

        Set<Word<Character>> wordList = new LinkedHashSet<>();
        wordList.add(Words.characterWord(""));
        wordList.add(Words.characterWord("(())"));
        wordList.add(Words.characterWord("(){}()"));
        wordList.add(Words.characterWord("({()()})()"));
        wordList.add(Words.characterWord("{{()}}"));
        wordList.add(Words.characterWord("(()"));
        wordList.add(Words.characterWord("()({)})"));
        wordList.add(Words.characterWord("()({)}"));
        assertEquals(
                PDAAcceptanceStrategy.EMPTY_STACK, correctParenthesesPDA.getAcceptanceStrategy());
        assertEquals(PDAAcceptanceStrategy.ACCEPTING_STATES, convertedPDA.getAcceptanceStrategy());
        // sanity check: each automaton is equivalent to itself
        assertEquivalent(correctParenthesesPDA, correctParenthesesPDA, wordList, MAX_STEPS);
        assertEquivalent(convertedPDA, convertedPDA, wordList, MAX_STEPS);
        // actual test case
        assertEquivalent(correctParenthesesPDA, convertedPDA, wordList, MAX_STEPS);
    }
}

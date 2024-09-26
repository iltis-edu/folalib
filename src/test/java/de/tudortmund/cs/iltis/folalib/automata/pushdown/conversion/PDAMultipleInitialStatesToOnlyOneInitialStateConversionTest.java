package de.tudortmund.cs.iltis.folalib.automata.pushdown.conversion;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.*;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.folalib.languages.Words;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import org.junit.Test;

public class PDAMultipleInitialStatesToOnlyOneInitialStateConversionTest extends Utils {

    private final int MAX_STEPS = 1000;

    /**
     * This PDA accepts the language L = {a^nb^(n+1) or b^na^(n+1) or c^1 with n > 0}. For example
     * words see below. This automaton consists of three independent PDA's, each of them only accept
     * one third of the language, i.e. one accepts the words beginning with a, another one accepts
     * the words beginning with b and the third one accepts the word 'c'. With multiple initial
     * states support they can be merged together and accept all three kinds of words.
     */
    @Test
    public void testAutomatonWithMultipleInitialStates() {
        Alphabet<Character> inputAlphabet = Alphabets.characterAlphabet("abc");
        Alphabet<Integer> stackAlphabet = new Alphabet<>(0, 1);
        String q0 = "q0";
        String q1 = "q1";
        String q2 = "q2";
        String q3 = "q3";
        String q4 = "q4";
        PDABuilder<String, Character, Integer> builder = new PDABuilder<>(inputAlphabet);

        PDA<String, Character, Integer> pda =
                builder.withStackSymbols(stackAlphabet)
                        .withStates(q0, q1, q2, q3)
                        .withInitial(q0, q2, q4)
                        .withInitialStackSymbol(0)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK)
                        .withTransition(q0, 'a', 0, q0, new PDAStackWord<>(1, 0))
                        .withTransition(q0, 'a', 1, q0, new PDAStackWord<>(1, 1))
                        .withEpsilonTransition(q0, 1, q1, new PDAStackWord<>(1))
                        .withTransition(q1, 'b', 1, q1, new PDAStackWord<>())
                        .withTransition(q1, 'b', 0, q1, new PDAStackWord<>())
                        .withTransition(q2, 'b', 0, q2, new PDAStackWord<>(1, 0))
                        .withTransition(q2, 'b', 1, q2, new PDAStackWord<>(1, 1))
                        .withEpsilonTransition(q2, 1, q3, new PDAStackWord<>(1))
                        .withTransition(q3, 'a', 1, q3, new PDAStackWord<>())
                        .withTransition(q3, 'a', 0, q3, new PDAStackWord<>())
                        .withTransition(q4, 'c', 0, q4, new PDAStackWord<>())
                        .build()
                        .unwrap();

        PDA<MaybeGenerated<String, String>, Character, Integer> pdaConverted =
                new PDAMultipleInitialStatesToOnlyOneInitialStateConversion<
                                String, Character, Integer>()
                        .apply(pda);

        assertRejects(pdaConverted, Words.characterWord(""), MAX_STEPS);
        assertAccepts(pdaConverted, Words.characterWord("c"), MAX_STEPS);
        assertAccepts(pdaConverted, Words.characterWord("abb"), MAX_STEPS);
        assertAccepts(pdaConverted, Words.characterWord("baa"), MAX_STEPS);
        assertAccepts(pdaConverted, Words.characterWord("aabbb"), MAX_STEPS);
        assertAccepts(pdaConverted, Words.characterWord("bbaaa"), MAX_STEPS);

        assertRejects(pdaConverted, Words.characterWord("aabb"), MAX_STEPS);
        assertRejects(pdaConverted, Words.characterWord("bbaa"), MAX_STEPS);
    }

    /**
     * Simpy taken from {@link PDAStepperTest} to test conversion with PDA which does not need a
     * conversion.
     */
    @Test
    public void testAcceptanceLongerPrefixThanSuffix() {
        Alphabet<Character> inputAlphabet = Alphabets.characterAlphabet("ab");
        Alphabet<Integer> stackAlphabet = new Alphabet<>(0, 1, 2);
        String q0 = "q0";
        String q1 = "q1";
        PDABuilder<String, Character, Integer> builder = new PDABuilder<>(inputAlphabet);
        /* Automaton taken from GTI lecture slides (SS18), page 343 */
        Integer wildcard = null;
        PDA<String, Character, Integer> longerPrefixThanSuffixPDA =
                builder.withStackSymbols(stackAlphabet)
                        .withStates(q0, q1)
                        .withInitial(q0)
                        .withAccepting(q1)
                        .withInitialStackSymbol(0)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.ACCEPTING_STATES)
                        .withTransition(q0, 'a', 0, q0, new PDAStackWord<>(1, 0))
                        .withTransition(q0, 'a', 1, q0, new PDAStackWord<>(1, 1))
                        .withTransition(q0, 'a', 2, q0, new PDAStackWord<>(1, 2))
                        .withEpsilonTransition(
                                q0, PDAStackSymbol.wildcard(), q1, new PDAStackWord<>(wildcard))
                        .withTransition(q1, 'b', 1, q1, new PDAStackWord<>())
                        .build()
                        .unwrap();

        PDA<MaybeGenerated<String, String>, Character, Integer> pdaConverted =
                new PDAMultipleInitialStatesToOnlyOneInitialStateConversion<
                                String, Character, Integer>()
                        .apply(longerPrefixThanSuffixPDA);

        assertAccepts(pdaConverted, Words.characterWord(""), MAX_STEPS);
        assertAccepts(pdaConverted, Words.characterWord("aa"), MAX_STEPS);
        assertAccepts(pdaConverted, Words.characterWord("aaaabbb"), MAX_STEPS);
        assertAccepts(pdaConverted, Words.characterWord("aaabbb"), MAX_STEPS);
        assertRejects(pdaConverted, Words.characterWord("aaabbbb"), MAX_STEPS); // more 'b' than 'a'
        assertRejects(
                pdaConverted,
                Words.characterWord("aababbbbb"),
                MAX_STEPS); // not of form (a^i)(b^j)
        assertRejects(pdaConverted, Words.characterWord("bbbbbbb"), MAX_STEPS); // no 'a' at all
    }
}

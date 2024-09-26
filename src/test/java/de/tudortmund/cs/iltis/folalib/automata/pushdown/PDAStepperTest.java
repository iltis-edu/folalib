package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.folalib.languages.Words;
import org.junit.Test;

public class PDAStepperTest extends Utils {

    private final int MAX_STEPS = 1000;

    @Test
    public void testAcceptanceCorrectParentheses() {
        Alphabet<Character> inputAlphabet = Alphabets.characterAlphabet("({)}");
        Alphabet<Integer> stackAlphabet = new Alphabet<>(0, 1, 2);
        String q0 = "q0";
        String q1 = "q1";
        PDABuilder<String, Character, Integer> builder = new PDABuilder<>(inputAlphabet);
        /* Automaton taken from GTI lecture slides (SS18), page 337 */
        Integer wildcard = null;
        PDA<String, Character, Integer> correctParenthesesPDA =
                builder.withStackSymbols(stackAlphabet)
                        .withStates(q0, q1)
                        .withInitial(q0)
                        .withInitialStackSymbol(0)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK)
                        .withTransition(
                                q0,
                                '(',
                                PDAStackSymbol.wildcard(),
                                q0,
                                new PDAStackWord<>(1, wildcard))
                        .withTransition(
                                q0,
                                '{',
                                PDAStackSymbol.wildcard(),
                                q0,
                                new PDAStackWord<>(2, wildcard))
                        .withTransition(q0, ')', 1, q0, new PDAStackWord<>())
                        .withTransition(q0, '}', 2, q0, new PDAStackWord<>())
                        .withEpsilonTransition(q0, 0, q1, new PDAStackWord<>())
                        .build()
                        .unwrap();

        assertAccepts(correctParenthesesPDA, Words.characterWord(""), MAX_STEPS);
        assertAccepts(correctParenthesesPDA, Words.characterWord("(())"), MAX_STEPS);
        assertAccepts(correctParenthesesPDA, Words.characterWord("(){}()"), MAX_STEPS);
        assertAccepts(correctParenthesesPDA, Words.characterWord("({()()})()"), MAX_STEPS);
        assertAccepts(correctParenthesesPDA, Words.characterWord("{{()}}"), MAX_STEPS);
        assertRejects(
                correctParenthesesPDA,
                Words.characterWord("(()"),
                MAX_STEPS); // missing closing parenthesis
        assertRejects(
                correctParenthesesPDA,
                Words.characterWord("()({)})"),
                MAX_STEPS); // missing opening parenthesis
        assertRejects(
                correctParenthesesPDA,
                Words.characterWord("()({)}"),
                MAX_STEPS); // opening and closing parenthesis do not match
    }

    @Test
    public void testAcceptanceReverseWord() {
        Alphabet<Character> inputAlphabet = Alphabets.characterAlphabet("ab");
        Alphabet<Integer> stackAlphabet = new Alphabet<>(0, 1, 2);
        String q0 = "q0";
        String q1 = "q1";
        String q2 = "q2";
        PDABuilder<String, Character, Integer> builder = new PDABuilder<>(inputAlphabet);
        /* Automaton taken from GTI lecture slides (SS18), page 338 */
        PDA<String, Character, Integer> reversedWordPDA =
                builder.withStackSymbols(stackAlphabet)
                        .withStates(q0, q1, q2)
                        .withInitial(q0)
                        .withInitialStackSymbol(0)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK)
                        .withTransition(q0, 'a', 0, q0, new PDAStackWord<>(1, 0))
                        .withTransition(q0, 'a', 1, q0, new PDAStackWord<>(1, 1))
                        .withTransition(q0, 'a', 2, q0, new PDAStackWord<>(1, 2))
                        .withTransition(q0, 'b', 0, q0, new PDAStackWord<>(2, 0))
                        .withTransition(q0, 'b', 1, q0, new PDAStackWord<>(2, 1))
                        .withTransition(q0, 'b', 2, q0, new PDAStackWord<>(2, 2))
                        .withEpsilonTransition(q0, 0, q1, new PDAStackWord<>(0))
                        .withEpsilonTransition(q0, 1, q1, new PDAStackWord<>(1))
                        .withEpsilonTransition(q0, 2, q1, new PDAStackWord<>(2))
                        .withTransition(q1, 'a', 1, q1, new PDAStackWord<>())
                        .withTransition(q1, 'b', 2, q1, new PDAStackWord<>())
                        .withEpsilonTransition(q1, 0, q2, new PDAStackWord<>())
                        .build()
                        .unwrap();
        assertAccepts(reversedWordPDA, Words.characterWord(""), MAX_STEPS);
        assertAccepts(reversedWordPDA, Words.characterWord("aa"), MAX_STEPS);
        assertAccepts(reversedWordPDA, Words.characterWord("ababbaba"), MAX_STEPS);
        assertAccepts(reversedWordPDA, Words.characterWord("aaaaaaaa"), MAX_STEPS);
        assertRejects(
                reversedWordPDA, Words.characterWord("ababbab"), MAX_STEPS); // final 'a' is missing
        assertRejects(reversedWordPDA, Words.characterWord("aabaa"), MAX_STEPS); // uneven length
        assertRejects(
                reversedWordPDA,
                Words.characterWord("baabbaaba"),
                MAX_STEPS); // excessive 'a at the end
    }

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
        assertAccepts(longerPrefixThanSuffixPDA, Words.characterWord(""), MAX_STEPS);
        assertAccepts(longerPrefixThanSuffixPDA, Words.characterWord("aa"), MAX_STEPS);
        assertAccepts(longerPrefixThanSuffixPDA, Words.characterWord("aaaabbb"), MAX_STEPS);
        assertAccepts(longerPrefixThanSuffixPDA, Words.characterWord("aaabbb"), MAX_STEPS);
        assertRejects(
                longerPrefixThanSuffixPDA,
                Words.characterWord("aaabbbb"),
                MAX_STEPS); // more 'b' than 'a'
        assertRejects(
                longerPrefixThanSuffixPDA,
                Words.characterWord("aababbbbb"),
                MAX_STEPS); // not of form (a^i)(b^j)
        assertRejects(
                longerPrefixThanSuffixPDA,
                Words.characterWord("bbbbbbb"),
                MAX_STEPS); // no 'a' at all
    }

    /**
     * This PDA accepts the language L = {a^nb^(n+1) or b^na^(n+1) with n > 0}. For example words
     * see below. This automaton consists of two independent PDA's, each of them only accept one
     * half of the language, i.e. one accepts the words beginning with a, the other one accepts the
     * words beginning with b. With multiple initial states support they can be merged together and
     * accept both kinds of words.
     */
    @Test
    public void testAutomatonWithMultipleInitialStates() {
        Alphabet<Character> inputAlphabet = Alphabets.characterAlphabet("ab");
        Alphabet<Integer> stackAlphabet = new Alphabet<>(0, 1);
        String q0 = "q0";
        String q1 = "q1";
        String q2 = "q2";
        String q3 = "q3";
        PDABuilder<String, Character, Integer> builder = new PDABuilder<>(inputAlphabet);

        PDA<String, Character, Integer> pda =
                builder.withStackSymbols(stackAlphabet)
                        .withStates(q0, q1, q2, q3)
                        .withInitial(q0, q2)
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
                        .build()
                        .unwrap();

        assertRejects(pda, Words.characterWord(""), MAX_STEPS);
        assertAccepts(pda, Words.characterWord("abb"), MAX_STEPS);
        assertAccepts(pda, Words.characterWord("baa"), MAX_STEPS);
        assertAccepts(pda, Words.characterWord("aabbb"), MAX_STEPS);
        assertAccepts(pda, Words.characterWord("bbaaa"), MAX_STEPS);

        assertRejects(pda, Words.characterWord("aabb"), MAX_STEPS);
        assertRejects(pda, Words.characterWord("bbaa"), MAX_STEPS);
    }
}

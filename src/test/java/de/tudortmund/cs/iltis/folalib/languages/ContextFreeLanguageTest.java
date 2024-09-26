package de.tudortmund.cs.iltis.folalib.languages;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.*;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import org.junit.Test;

public class ContextFreeLanguageTest extends Utils {

    @Test
    public void testContextFreeLanguage() {
        Alphabet<String> nonTerminals = new Alphabet<>("S");
        Alphabet<Character> terminals = new Alphabet<>('a', 'b');
        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);
        builder.withStartSymbol("S")
                .withProduction("S")
                .t('a')
                .nt("S")
                .t('b')
                .nt("S")
                .finish()
                .withProduction("S")
                .t('b')
                .nt("S")
                .t('a')
                .nt("S")
                .finish()
                .withEpsProduction("S");

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> cfg =
                builder.build().unwrap();
        ContextFreeLanguage<Character> language = new ContextFreeLanguage<>(cfg);
        assertFalse(language.contains(Words.characterWord("a")));
        assertFalse(language.contains(Words.characterWord("b")));
        assertTrue(language.contains(Words.characterWord("aabb")));
        assertFalse(language.contains(Words.characterWord("abb")));
        assertFalse(language.contains(Words.characterWord("bbbbbbbb")));
    }

    @Test
    public void testContextFreeLanguageFromPDA() {
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

        ContextFreeLanguage<Character> language = new ContextFreeLanguage<>(correctParenthesesPDA);
        assertTrue(language.contains(Words.characterWord("")));
        assertTrue(language.contains(Words.characterWord("(())")));
        assertTrue(language.contains(Words.characterWord("(){}()")));
        assertTrue(language.contains(Words.characterWord("({()()})()")));
        assertTrue(language.contains(Words.characterWord("{{()}}")));
        assertFalse(language.contains(Words.characterWord("(()"))); // missing closing parenthesis
        assertFalse(
                language.contains(Words.characterWord("()({)})"))); // missing opening parenthesis
        assertFalse(
                language.contains(
                        Words.characterWord(
                                "()({)}"))); // opening and closing parenthesis do not match
    }

    @Test
    public void testContextFreeLanguageFromCFG() {
        Alphabet<Character> terminals = Alphabets.characterAlphabet("ab01()+*");
        Alphabet<String> nonTerminals = new Alphabet<>("A", "B");

        // The grammar and test case is taken from GTI lecture, slide 254
        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>>
                arithmeticExpressionsGrammar =
                        builder.withStartSymbol("A")
                                .withProduction("A")
                                .nt("B")
                                .finish()
                                .withProduction("A")
                                .nt("A")
                                .t('+')
                                .nt("A")
                                .finish()
                                .withProduction("A")
                                .nt("A")
                                .t('*')
                                .nt("A")
                                .finish()
                                .withProduction("A")
                                .t('(')
                                .nt("A")
                                .t(')')
                                .finish()
                                .withProduction("B")
                                .t('a')
                                .finish()
                                .withProduction("B")
                                .t('b')
                                .finish()
                                .withProduction("B")
                                .nt("B")
                                .t('a')
                                .finish()
                                .withProduction("B")
                                .nt("B")
                                .t('b')
                                .finish()
                                .withProduction("B")
                                .nt("B")
                                .t('0')
                                .finish()
                                .withProduction("B")
                                .nt("B")
                                .t('1')
                                .finish()
                                .build()
                                .unwrap();

        ContextFreeLanguage<Character> language =
                new ContextFreeLanguage<>(arithmeticExpressionsGrammar);
        assertTrue(language.contains(Words.characterWord("a+a")));
        assertTrue(language.contains(Words.characterWord("aaaaaa")));
        assertTrue(language.contains(Words.characterWord("(a*b0)")));
        assertTrue(language.contains(Words.characterWord("(a1)*(b0+b0)")));
        assertTrue(language.contains(Words.characterWord("bbbb0+(aa)")));
        assertFalse(language.contains(Words.characterWord("")));
        assertFalse(language.contains(Words.characterWord("(a+b"))); // missing closing parenthesis
        assertFalse(
                language.contains(Words.characterWord("(a+b)-(b+a)"))); // minus (-) is not allowed
        assertFalse(
                language.contains(
                        Words.characterWord(
                                "a0+0a"))); // identifier must not start with 0 or 1 (here: 0a)
        assertFalse(
                language.contains(Words.characterWord("(ab)*(a))"))); // missing opening parenthesis
    }
}

package de.tudortmund.cs.iltis.folalib.automata.pushdown.transformation;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.*;
import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import java.util.Set;
import org.junit.Test;

public class PDAToCFGTransformationTest extends Utils {

    private final Alphabet<Character> inputAlphabet = Alphabets.characterAlphabet("ab");
    private final Alphabet<Integer> stackAlphabet = new Alphabet<>(0, 1);
    private final String p = "p";
    private final String q = "q";
    private final PDABuilder<String, Character, Integer> builder = new PDABuilder<>(inputAlphabet);

    // The PDA and test case is taken from GTI lecture, slide 358
    private final PDA<String, Character, Integer> pda =
            builder.withStackSymbols(stackAlphabet)
                    .withStates(p, q)
                    .withInitial(p)
                    .withInitialStackSymbol(0)
                    .withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK)
                    .withTransition(p, 'a', 1, p, new PDAStackWord<>(1, 1))
                    .withTransition(p, 'a', 0, p, new PDAStackWord<>(1, 0))
                    .withTransition(p, 'b', 1, p, new PDAStackWord<>())
                    .withEpsilonTransition(p, 0, p, new PDAStackWord<>())
                    .withTransition(q, 'b', 1, q, new PDAStackWord<>(1, 1))
                    .withTransition(q, 'b', 0, q, new PDAStackWord<>(1, 0))
                    .withTransition(q, 'a', 1, q, new PDAStackWord<>())
                    .withEpsilonTransition(q, 0, q, new PDAStackWord<>())
                    .withEpsilonTransition(p, 0, q, new PDAStackWord<>(0))
                    .withEpsilonTransition(q, 0, p, new PDAStackWord<>(0))
                    .build()
                    .unwrap();

    @Test
    public void testTransform() {
        PDAToCFGTransformation<String, Character, Integer> transformation =
                new PDAToCFGTransformation<>();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> grammar =
                transformation.transform(pda);
        Set<ContextFreeProduction<Character, String>> productions = grammar.getProductions();

        ContextFreeProduction<Character, String> cfp0 =
                new ContextFreeProduction<>("S", new SententialForm<>(nonTerm(p, 0, p)));
        ContextFreeProduction<Character, String> cfp1 =
                new ContextFreeProduction<>("S", new SententialForm<>(nonTerm(p, 0, q)));

        ContextFreeProduction<Character, String> cfp2 =
                new ContextFreeProduction<>(
                        prod(p, 0, p),
                        new SententialForm<>(term('a'), nonTerm(p, 1, p), nonTerm(p, 0, p)));
        ContextFreeProduction<Character, String> cfp3 =
                new ContextFreeProduction<>(
                        prod(p, 0, p),
                        new SententialForm<>(term('a'), nonTerm(p, 1, q), nonTerm(q, 0, p)));
        ContextFreeProduction<Character, String> cfp4 =
                new ContextFreeProduction<>(prod(p, 0, p), new SententialForm<>());
        ContextFreeProduction<Character, String> cfp5 =
                new ContextFreeProduction<>(prod(p, 0, p), new SententialForm<>(nonTerm(q, 0, p)));

        ContextFreeProduction<Character, String> cfp6 =
                new ContextFreeProduction<>(
                        prod(p, 0, q),
                        new SententialForm<>(term('a'), nonTerm(p, 1, p), nonTerm(p, 0, q)));
        ContextFreeProduction<Character, String> cfp7 =
                new ContextFreeProduction<>(
                        prod(p, 0, q),
                        new SententialForm<>(term('a'), nonTerm(p, 1, q), nonTerm(q, 0, q)));
        ContextFreeProduction<Character, String> cfp8 =
                new ContextFreeProduction<>(prod(p, 0, q), new SententialForm<>(nonTerm(q, 0, q)));

        ContextFreeProduction<Character, String> cfp9 =
                new ContextFreeProduction<>(
                        prod(p, 1, p),
                        new SententialForm<>(term('a'), nonTerm(p, 1, p), nonTerm(p, 1, p)));
        ContextFreeProduction<Character, String> cfp10 =
                new ContextFreeProduction<>(
                        prod(p, 1, p),
                        new SententialForm<>(term('a'), nonTerm(p, 1, q), nonTerm(q, 1, p)));
        ContextFreeProduction<Character, String> cfp11 =
                new ContextFreeProduction<>(prod(p, 1, p), new SententialForm<>(term('b')));

        ContextFreeProduction<Character, String> cfp12 =
                new ContextFreeProduction<>(
                        prod(p, 1, q),
                        new SententialForm<>(term('a'), nonTerm(p, 1, p), nonTerm(p, 1, q)));
        ContextFreeProduction<Character, String> cfp13 =
                new ContextFreeProduction<>(
                        prod(p, 1, q),
                        new SententialForm<>(term('a'), nonTerm(p, 1, q), nonTerm(q, 1, q)));

        ContextFreeProduction<Character, String> cfp14 =
                new ContextFreeProduction<>(
                        prod(q, 0, q),
                        new SententialForm<>(term('b'), nonTerm(q, 1, q), nonTerm(q, 0, q)));
        ContextFreeProduction<Character, String> cfp15 =
                new ContextFreeProduction<>(
                        prod(q, 0, q),
                        new SententialForm<>(term('b'), nonTerm(q, 1, p), nonTerm(p, 0, q)));
        ContextFreeProduction<Character, String> cfp16 =
                new ContextFreeProduction<>(prod(q, 0, q), new SententialForm<>());
        ContextFreeProduction<Character, String> cfp17 =
                new ContextFreeProduction<>(prod(q, 0, q), new SententialForm<>(nonTerm(p, 0, q)));

        ContextFreeProduction<Character, String> cfp18 =
                new ContextFreeProduction<>(
                        prod(q, 0, p),
                        new SententialForm<>(term('b'), nonTerm(q, 1, q), nonTerm(q, 0, p)));
        ContextFreeProduction<Character, String> cfp19 =
                new ContextFreeProduction<>(
                        prod(q, 0, p),
                        new SententialForm<>(term('b'), nonTerm(q, 1, p), nonTerm(p, 0, p)));
        ContextFreeProduction<Character, String> cfp20 =
                new ContextFreeProduction<>(prod(q, 0, p), new SententialForm<>(nonTerm(p, 0, p)));

        ContextFreeProduction<Character, String> cfp21 =
                new ContextFreeProduction<>(
                        prod(q, 1, q),
                        new SententialForm<>(term('b'), nonTerm(q, 1, q), nonTerm(q, 1, q)));
        ContextFreeProduction<Character, String> cfp22 =
                new ContextFreeProduction<>(
                        prod(q, 1, q),
                        new SententialForm<>(term('b'), nonTerm(q, 1, p), nonTerm(p, 1, q)));
        ContextFreeProduction<Character, String> cfp23 =
                new ContextFreeProduction<>(prod(q, 1, q), new SententialForm<>(term('a')));

        ContextFreeProduction<Character, String> cfp24 =
                new ContextFreeProduction<>(
                        prod(q, 1, p),
                        new SententialForm<>(term('b'), nonTerm(q, 1, q), nonTerm(q, 1, p)));
        ContextFreeProduction<Character, String> cfp25 =
                new ContextFreeProduction<>(
                        prod(q, 1, p),
                        new SententialForm<>(term('b'), nonTerm(q, 1, p), nonTerm(p, 1, p)));

        assertEquals(pda.getAlphabet(), grammar.getTerminals()); // input alphabet must be identical
        assertEquals(9, grammar.getNonTerminals().size());
        assertEquals(26, grammar.getProductions().size());

        assertContains(productions, cfp0);
        assertContains(productions, cfp1);
        assertContains(productions, cfp2);
        assertContains(productions, cfp3);
        assertContains(productions, cfp4);
        assertContains(productions, cfp5);
        assertContains(productions, cfp6);
        assertContains(productions, cfp7);
        assertContains(productions, cfp8);
        assertContains(productions, cfp9);
        assertContains(productions, cfp10);
        assertContains(productions, cfp11);
        assertContains(productions, cfp12);
        assertContains(productions, cfp13);
        assertContains(productions, cfp14);
        assertContains(productions, cfp15);
        assertContains(productions, cfp16);
        assertContains(productions, cfp17);
        assertContains(productions, cfp18);
        assertContains(productions, cfp19);
        assertContains(productions, cfp20);
        assertContains(productions, cfp21);
        assertContains(productions, cfp22);
        assertContains(productions, cfp23);
        assertContains(productions, cfp24);
        assertContains(productions, cfp25);
    }

    @Test
    public void testConversionMultipleInitialStates() {
        PDA<String, Character, Integer> originPDA = builder.withInitial(p, q).build().unwrap();

        PDAToCFGTransformation<String, Character, Integer> transformation =
                new PDAToCFGTransformation<>();
        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> grammar =
                transformation.transform(originPDA);

        // The conversion to only one initial state and the transformation are both working
        // independently, so checking the
        // whole generated grammar is unnecessary.
    }

    // Private helper methods to construct terminals, non-terminals and production names more easily
    private GrammarSymbol<Character, String> term(Character c) {
        return new GrammarSymbol.Terminal<>(c);
    }

    private GrammarSymbol<Character, String> nonTerm(String p, Integer i, String q) {
        return new GrammarSymbol.NonTerminal<>(prod(p, i, q));
    }

    private String prod(String p, Integer i, String q) {
        return "X_In{" + p + "},In{" + i + "},In{" + q + "}";
    }
}

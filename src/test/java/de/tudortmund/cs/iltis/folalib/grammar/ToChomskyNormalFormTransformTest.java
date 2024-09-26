package de.tudortmund.cs.iltis.folalib.grammar;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ToChomskyNormalFormTransform;
import de.tudortmund.cs.iltis.folalib.grammar.production.ChomskyNormalformProduction;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.transform.ConstrainedSupplier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class ToChomskyNormalFormTransformTest {

    @Test
    public void testEliminationOfUselessNonTerminals() {
        // The test case is taken from the GTI lecture slides (SS 18), page 294
        Alphabet<String> nonTerminals = new Alphabet<>("S", "A", "B", "C", "D", "E", "F");
        Alphabet<Character> terminals = new Alphabet<>('a', 'b', 'c');
        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);
        builder.withStartSymbol("S")
                .withProduction("S")
                .t('b')
                .nt("D")
                .nt("D")
                .finish()
                .withProduction("S")
                .nt("C")
                .t('a')
                .finish()
                .withProduction("S")
                .t('b')
                .t('c')
                .finish()
                .withProduction("A")
                .nt("B")
                .finish()
                .withProduction("A")
                .t('a')
                .nt("C")
                .nt("C")
                .finish()
                .withProduction("A")
                .t('b')
                .t('a')
                .nt("D")
                .finish()
                .withProduction("B")
                .t('c')
                .nt("B")
                .nt("D")
                .finish()
                .withEpsProduction("B")
                .withProduction("B")
                .nt("A")
                .nt("C")
                .finish()
                .withProduction("C")
                .t('b')
                .nt("D")
                .finish()
                .withProduction("C")
                .t('a')
                .nt("B")
                .nt("A")
                .finish()
                .withProduction("D")
                .nt("C")
                .nt("D")
                .finish()
                .withProduction("D")
                .t('a')
                .finish()
                .withProduction("D")
                .nt("E")
                .nt("F")
                .finish()
                .withProduction("E")
                .nt("E")
                .t('b')
                .finish()
                .withProduction("F")
                .t('a')
                .finish();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> cfg =
                builder.build().unwrap();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> result =
                ToChomskyNormalFormTransform.eliminateUselessNonTerminals(cfg);

        assertEquals("S", result.getStartSymbol());
        assertEquals(terminals, result.getTerminals());
        assertEquals(new Alphabet<>("S", "A", "B", "C", "D"), result.getNonTerminals());
        assertEquals(13, result.getProductions().size());

        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("S", rhs(t('b'), nt("D"), nt("D"))));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("C"), t('a'))));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S", rhs(t('b'), t('c'))));

        assertContains(result.getProductions(), new ContextFreeProduction<>("A", rhs(nt("B"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("A", rhs(t('a'), nt("C"), nt("C"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("A", rhs(t('b'), t('a'), nt("D"))));

        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("B", rhs(t('c'), nt("B"), nt("D"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("B", rhs()));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("B", rhs(nt("A"), nt("C"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("C", rhs(t('b'), nt("D"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("C", rhs(t('a'), nt("B"), nt("A"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("D", rhs(nt("C"), nt("D"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("D", rhs(t('a'))));
    }

    @Test
    public void testEliminationOfUselessNonTerminalsWithNonGeneratingStartSymbol() {
        Alphabet<String> nonTerminals = new Alphabet<>("S");
        Alphabet<Character> terminals = new Alphabet<>('a', 'b');
        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);
        builder.withStartSymbol("S").withProduction("S").t('a').nt("S").t('b').finish();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> cfg =
                builder.build().unwrap();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> result =
                ToChomskyNormalFormTransform.eliminateUselessNonTerminals(cfg);

        assertEquals("S", result.getStartSymbol());
        assertEquals(terminals, result.getTerminals());
        assertEquals(new Alphabet<>("S"), result.getNonTerminals());
        assertEquals(0, result.getProductions().size());
    }

    @Test
    public void testSeparationOfTerminalsAndNonTerminals() {
        // The test case is taken from the GTI lecture slides (SS 18), page 298
        Alphabet<String> nonTerminals = new Alphabet<>("S", "A", "B", "C", "D");
        Alphabet<Character> terminals = new Alphabet<>('a', 'b', 'c');
        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);
        builder.withStartSymbol("S")
                .withProduction("S")
                .t('b')
                .nt("D")
                .nt("D")
                .finish()
                .withProduction("S")
                .nt("C")
                .t('a')
                .finish()
                .withProduction("S")
                .t('b')
                .t('c')
                .finish()
                .withProduction("A")
                .nt("B")
                .finish()
                .withProduction("A")
                .t('a')
                .nt("C")
                .nt("C")
                .finish()
                .withProduction("A")
                .t('b')
                .t('a')
                .nt("D")
                .finish()
                .withProduction("B")
                .t('c')
                .nt("B")
                .nt("D")
                .finish()
                .withEpsProduction("B")
                .withProduction("B")
                .nt("A")
                .nt("C")
                .finish()
                .withProduction("C")
                .t('b')
                .nt("D")
                .finish()
                .withProduction("C")
                .t('a')
                .nt("B")
                .nt("A")
                .finish()
                .withProduction("D")
                .nt("C")
                .nt("D")
                .finish()
                .withProduction("D")
                .t('a')
                .finish();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> cfg =
                builder.build().unwrap();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> result =
                ToChomskyNormalFormTransform.separateTerminalsFromNonTerminals(
                        cfg,
                        new ConstrainedSupplier<>() {
                            @Override
                            public void constrain(String s) {
                                // not relevant: we only return valid non-terminals
                            }

                            private final String[] nonTerminals = {"W_a", "W_b", "W_c"};
                            private int index = 0;

                            @Override
                            public String get() {
                                return nonTerminals[index++];
                            }
                        });

        assertEquals("S", result.getStartSymbol());
        assertEquals(terminals, result.getTerminals());
        assertEquals(
                new Alphabet<>("S", "A", "B", "C", "D", "W_a", "W_b", "W_c"),
                result.getNonTerminals());
        assertEquals(16, result.getProductions().size());

        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("S", rhs(nt("W_b"), nt("D"), nt("D"))));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("C"), nt("W_a"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("S", rhs(nt("W_b"), nt("W_c"))));

        assertContains(result.getProductions(), new ContextFreeProduction<>("A", rhs(nt("B"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("A", rhs(nt("W_a"), nt("C"), nt("C"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("A", rhs(nt("W_b"), nt("W_a"), nt("D"))));

        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("B", rhs(nt("W_c"), nt("B"), nt("D"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("B", rhs()));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("B", rhs(nt("A"), nt("C"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("C", rhs(nt("W_b"), nt("D"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("C", rhs(nt("W_a"), nt("B"), nt("A"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("D", rhs(nt("C"), nt("D"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("D", rhs(nt("W_a"))));

        assertContains(result.getProductions(), new ContextFreeProduction<>("W_a", rhs(t('a'))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("W_b", rhs(t('b'))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("W_c", rhs(t('c'))));
    }

    @Test
    public void testSeparationOfTerminalsAndNonTerminalsIfNotAllTerminalsAreUsed() {
        Alphabet<String> nonTerminals = new Alphabet<>("S", "P", "R");
        Alphabet<Character> terminals = new Alphabet<>('0', '1');
        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);
        builder.withStartSymbol("S")
                .withProduction("S")
                .t('0')
                .finish()
                .withProduction("S")
                .t('0')
                .nt("P")
                .t('0')
                .finish()
                .withEpsProduction("P")
                .withProduction("P")
                .nt("R")
                .finish()
                .withProduction("R")
                .nt("S")
                .finish()
                .withProduction("R")
                .nt("R")
                .finish()
                .withProduction("R")
                .nt("P")
                .finish();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> cfg =
                builder.build().unwrap();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> result =
                ToChomskyNormalFormTransform.separateTerminalsFromNonTerminals(
                        cfg,
                        new ConstrainedSupplier<>() {
                            @Override
                            public void constrain(String s) {
                                // not relevant: we only return valid non-terminals
                            }

                            private final String[] nonTerminals = {"Q"};
                            private int index = 0;

                            @Override
                            public String get() {
                                return nonTerminals[index++];
                            }
                        });

        assertEquals("S", result.getStartSymbol());
        assertEquals(terminals, result.getTerminals());
        assertEquals(new Alphabet<>("S", "P", "R", "Q"), result.getNonTerminals());
        assertEquals(8, result.getProductions().size());

        assertContains(result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("Q"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("S", rhs(nt("Q"), nt("P"), nt("Q"))));

        assertContains(result.getProductions(), new ContextFreeProduction<>("Q", rhs(t('0'))));

        assertContains(result.getProductions(), new ContextFreeProduction<>("P", rhs()));
        assertContains(result.getProductions(), new ContextFreeProduction<>("P", rhs(nt("R"))));

        assertContains(result.getProductions(), new ContextFreeProduction<>("R", rhs(nt("P"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("R", rhs(nt("S"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("R", rhs(nt("R"))));
    }

    @Test
    public void testShortenRightHandSideOfProductions1() {
        // The test case is taken from the GTI lecture slides (SS 18), page 300
        Alphabet<String> nonTerminals =
                new Alphabet<>("S", "A", "B", "C", "D", "W_a", "W_b", "W_c");
        Alphabet<Character> terminals = new Alphabet<>('a', 'b', 'c');
        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);
        builder.withStartSymbol("S")
                .withProduction("S")
                .nt("W_b")
                .nt("D")
                .nt("D")
                .finish()
                .withProduction("S")
                .nt("C")
                .nt("W_a")
                .finish()
                .withProduction("S")
                .nt("W_b")
                .nt("W_c")
                .finish()
                .withProduction("A")
                .nt("B")
                .finish()
                .withProduction("A")
                .nt("W_a")
                .nt("C")
                .nt("C")
                .finish()
                .withProduction("A")
                .nt("W_b")
                .nt("W_a")
                .nt("D")
                .finish()
                .withProduction("B")
                .nt("W_c")
                .nt("B")
                .nt("D")
                .finish()
                .withEpsProduction("B")
                .withProduction("B")
                .nt("A")
                .nt("C")
                .finish()
                .withProduction("C")
                .nt("W_b")
                .nt("D")
                .finish()
                .withProduction("C")
                .nt("W_a")
                .nt("B")
                .nt("A")
                .finish()
                .withProduction("D")
                .nt("C")
                .nt("D")
                .finish()
                .withProduction("D")
                .nt("W_a")
                .finish()
                .withProduction("W_a")
                .t('a')
                .finish()
                .withProduction("W_b")
                .t('b')
                .finish()
                .withProduction("W_c")
                .t('c')
                .finish();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> cfg =
                builder.build().unwrap();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> result =
                ToChomskyNormalFormTransform.shortenRightHandSideOfProductions(
                        cfg,
                        new ConstrainedSupplier<>() {
                            @Override
                            public void constrain(String s) {
                                // not relevant: we only return valid non-terminals
                            }

                            private final String[] nonTerminals = {
                                "S_1", "A_1", "A_2", "B_1", "C_1"
                            };
                            private int index = 0;

                            @Override
                            public String get() {
                                return nonTerminals[index++];
                            }
                        });

        assertEquals("S", result.getStartSymbol());
        assertEquals(terminals, result.getTerminals());
        assertEquals(
                new Alphabet<>(
                        "S", "A", "B", "C", "D", "W_a", "W_b", "W_c", "S_1", "A_1", "A_2", "B_1",
                        "C_1"),
                result.getNonTerminals());
        assertEquals(21, result.getProductions().size());

        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("S", rhs(nt("W_b"), nt("S_1"))));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("C"), nt("W_a"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("S", rhs(nt("W_b"), nt("W_c"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S_1", rhs(nt("D"), nt("D"))));

        assertContains(result.getProductions(), new ContextFreeProduction<>("A", rhs(nt("B"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("A", rhs(nt("W_a"), nt("A_1"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("A", rhs(nt("W_b"), nt("A_2"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("A_1", rhs(nt("C"), nt("C"))));

        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("A_2", rhs(nt("W_a"), nt("D"))));

        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("B", rhs(nt("W_c"), nt("B_1"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("B", rhs()));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("B", rhs(nt("A"), nt("C"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("B_1", rhs(nt("B"), nt("D"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("C", rhs(nt("W_b"), nt("D"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("C", rhs(nt("W_a"), nt("C_1"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("C_1", rhs(nt("B"), nt("A"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("D", rhs(nt("C"), nt("D"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("D", rhs(nt("W_a"))));

        assertContains(result.getProductions(), new ContextFreeProduction<>("W_a", rhs(t('a'))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("W_b", rhs(t('b'))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("W_c", rhs(t('c'))));
    }

    @Test
    public void testShortenRightHandSideOfProductions2() {
        Alphabet<String> nonTerminals = new Alphabet<>("S", "A", "B");
        Alphabet<Character> terminals = new Alphabet<>('a', 'b');
        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);
        builder.withStartSymbol("S")
                .withProduction("S")
                .nt("A")
                .nt("S")
                .nt("B")
                .nt("S")
                .finish()
                .withProduction("S")
                .nt("B")
                .nt("S")
                .nt("A")
                .nt("S")
                .finish()
                .withEpsProduction("S")
                .withProduction("A")
                .t('a')
                .finish()
                .withProduction("B")
                .t('b')
                .finish();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> cfg =
                builder.build().unwrap();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> result =
                ToChomskyNormalFormTransform.shortenRightHandSideOfProductions(
                        cfg,
                        new ConstrainedSupplier<>() {
                            @Override
                            public void constrain(String s) {
                                // not relevant: we only return valid non-terminals
                            }

                            private final String[] nonTerminals = {"Z_1", "Z_2", "Z_3", "Z_4"};
                            private int index = 0;

                            @Override
                            public String get() {
                                return nonTerminals[index++];
                            }
                        });

        assertEquals("S", result.getStartSymbol());
        assertEquals(terminals, result.getTerminals());
        assertEquals(
                new Alphabet<>("S", "A", "B", "Z_1", "Z_2", "Z_3", "Z_4"),
                result.getNonTerminals());
        assertEquals(9, result.getProductions().size());

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("A"), nt("Z_1"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("Z_1", rhs(nt("S"), nt("Z_2"))));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("Z_2", rhs(nt("B"), nt("S"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("B"), nt("Z_3"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("Z_3", rhs(nt("S"), nt("Z_4"))));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("Z_4", rhs(nt("A"), nt("S"))));

        assertContains(result.getProductions(), new ContextFreeProduction<>("S", rhs()));
        assertContains(result.getProductions(), new ContextFreeProduction<>("A", rhs(t('a'))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("B", rhs(t('b'))));
    }

    @Test
    public void testRemoveEpsilonProductions() {
        // The test case is taken from the GTI lecture slides (SS 18), page 303
        Alphabet<String> nonTerminals =
                new Alphabet<>(
                        "S", "A", "B", "C", "D", "W_a", "W_b", "W_c", "S_1", "A_1", "A_2", "B_1",
                        "C_1");
        Alphabet<Character> terminals = new Alphabet<>('a', 'b', 'c');
        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);
        builder.withStartSymbol("S")
                .withProduction("S")
                .nt("W_b")
                .nt("S_1")
                .finish()
                .withProduction("S")
                .nt("C")
                .nt("W_a")
                .finish()
                .withProduction("S")
                .nt("W_b")
                .nt("W_c")
                .finish()
                .withProduction("S_1")
                .nt("D")
                .nt("D")
                .finish()
                .withProduction("A")
                .nt("B")
                .finish()
                .withProduction("A")
                .nt("W_a")
                .nt("A_1")
                .finish()
                .withProduction("A")
                .nt("W_b")
                .nt("A_2")
                .finish()
                .withProduction("A_1")
                .nt("C")
                .nt("C")
                .finish()
                .withProduction("A_2")
                .nt("W_a")
                .nt("D")
                .finish()
                .withProduction("B")
                .nt("W_c")
                .nt("B_1")
                .finish()
                .withEpsProduction("B")
                .withProduction("B")
                .nt("A")
                .nt("C")
                .finish()
                .withProduction("B_1")
                .nt("B")
                .nt("D")
                .finish()
                .withProduction("C")
                .nt("W_b")
                .nt("D")
                .finish()
                .withProduction("C")
                .nt("W_a")
                .nt("C_1")
                .finish()
                .withProduction("C_1")
                .nt("B")
                .nt("A")
                .finish()
                .withProduction("D")
                .nt("C")
                .nt("D")
                .finish()
                .withProduction("D")
                .nt("W_a")
                .finish()
                .withProduction("W_a")
                .t('a')
                .finish()
                .withProduction("W_b")
                .t('b')
                .finish()
                .withProduction("W_c")
                .t('c')
                .finish();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> cfg =
                builder.build().unwrap();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> result =
                ToChomskyNormalFormTransform.removeEpsilonProductions(cfg);

        assertEquals("S", result.getStartSymbol());
        assertEquals(terminals, result.getTerminals());
        assertEquals(
                new Alphabet<>(
                        "S", "A", "B", "C", "D", "W_a", "W_b", "W_c", "S_1", "A_1", "A_2", "B_1",
                        "C_1"),
                result.getNonTerminals());
        assertEquals(25, result.getProductions().size());

        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("S", rhs(nt("W_b"), nt("S_1"))));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("C"), nt("W_a"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("S", rhs(nt("W_b"), nt("W_c"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S_1", rhs(nt("D"), nt("D"))));

        assertContains(result.getProductions(), new ContextFreeProduction<>("A", rhs(nt("B"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("A", rhs(nt("W_a"), nt("A_1"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("A", rhs(nt("W_b"), nt("A_2"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("A_1", rhs(nt("C"), nt("C"))));

        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("A_2", rhs(nt("W_a"), nt("D"))));

        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("B", rhs(nt("W_c"), nt("B_1"))));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("B", rhs(nt("A"), nt("C"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("B", rhs(nt("C"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("B_1", rhs(nt("B"), nt("D"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("B_1", rhs(nt("D"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("C", rhs(nt("W_b"), nt("D"))));
        assertContains(
                result.getProductions(),
                new ContextFreeProduction<>("C", rhs(nt("W_a"), nt("C_1"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("C", rhs(nt("W_a"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("C_1", rhs(nt("B"), nt("A"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("C_1", rhs(nt("A"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("C_1", rhs(nt("B"))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("D", rhs(nt("C"), nt("D"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("D", rhs(nt("W_a"))));

        assertContains(result.getProductions(), new ContextFreeProduction<>("W_a", rhs(t('a'))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("W_b", rhs(t('b'))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("W_c", rhs(t('c'))));
    }

    @Test
    public void testRemoveEpsilonProductionsDoesNotBuildSelfLoops() {
        Alphabet<String> nonTerminals = new Alphabet<>("S", "A", "B");
        Alphabet<Character> terminals = new Alphabet<>('a', 'b');
        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);
        builder.withStartSymbol("S")
                .withProduction("S")
                .nt("S")
                .nt("S")
                .finish()
                .withProduction("S")
                .nt("A")
                .nt("S")
                .finish()
                .withProduction("S")
                .nt("B")
                .nt("S")
                .finish()
                .withProduction("S")
                .nt("A")
                .nt("B")
                .finish()
                .withProduction("S")
                .nt("B")
                .nt("A")
                .finish()
                .withProduction("A")
                .t('a')
                .finish()
                .withProduction("B")
                .t('b')
                .finish()
                .withEpsProduction("S");

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> cfg =
                builder.build().unwrap();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> result =
                ToChomskyNormalFormTransform.removeEpsilonProductions(cfg);

        assertEquals("S", result.getStartSymbol());
        assertEquals(terminals, result.getTerminals());
        assertEquals(new Alphabet<>("S", "A", "B"), result.getNonTerminals());
        assertEquals(9, result.getProductions().size());

        /* the production `S -> S` would be a self loop in the non-terminals graph which is fatal later on,
        because then no topological ordering exists */
        assertFalse(
                result.getProductions().contains(new ContextFreeProduction<>("S", rhs(nt("S")))));

        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("S"), nt("S"))));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("A"), nt("S"))));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("B"), nt("S"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("A"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("B"))));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("A"), nt("B"))));
        assertContains(
                result.getProductions(), new ContextFreeProduction<>("S", rhs(nt("B"), nt("A"))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("A", rhs(t('a'))));
        assertContains(result.getProductions(), new ContextFreeProduction<>("B", rhs(t('b'))));
    }

    @Test
    public void testRemoveSingleVariableProductions() {
        // The test case is taken from the GTI lecture slides (SS 18), page 307
        Alphabet<String> nonTerminals =
                new Alphabet<>(
                        "S", "A", "B", "C", "D", "W_a", "W_b", "W_c", "S_1", "A_1", "A_2", "B_1",
                        "C_1");
        Alphabet<Character> terminals = new Alphabet<>('a', 'b', 'c');
        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);
        builder.withStartSymbol("S")
                .withProduction("S")
                .nt("W_b")
                .nt("S_1")
                .finish()
                .withProduction("S")
                .nt("C")
                .nt("W_a")
                .finish()
                .withProduction("S")
                .nt("W_b")
                .nt("W_c")
                .finish()
                .withProduction("S_1")
                .nt("D")
                .nt("D")
                .finish()
                .withProduction("A")
                .nt("B")
                .finish()
                .withProduction("A")
                .nt("W_a")
                .nt("A_1")
                .finish()
                .withProduction("A")
                .nt("W_b")
                .nt("A_2")
                .finish()
                .withProduction("A_1")
                .nt("C")
                .nt("C")
                .finish()
                .withProduction("A_2")
                .nt("W_a")
                .nt("D")
                .finish()
                .withProduction("B")
                .nt("W_c")
                .nt("B_1")
                .finish()
                .withProduction("B")
                .nt("A")
                .nt("C")
                .finish()
                .withProduction("B")
                .nt("C")
                .finish()
                .withProduction("B_1")
                .nt("B")
                .nt("D")
                .finish()
                .withProduction("B_1")
                .nt("D")
                .finish()
                .withProduction("C")
                .nt("W_b")
                .nt("D")
                .finish()
                .withProduction("C")
                .nt("W_a")
                .nt("C_1")
                .finish()
                .withProduction("C")
                .nt("W_a")
                .finish()
                .withProduction("C_1")
                .nt("B")
                .nt("A")
                .finish()
                .withProduction("C_1")
                .nt("A")
                .finish()
                .withProduction("C_1")
                .nt("B")
                .finish()
                .withProduction("D")
                .nt("C")
                .nt("D")
                .finish()
                .withProduction("D")
                .nt("W_a")
                .finish()
                .withProduction("W_a")
                .t('a')
                .finish()
                .withProduction("W_b")
                .t('b')
                .finish()
                .withProduction("W_c")
                .t('c')
                .finish();

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> cfg =
                builder.build().unwrap();

        ContextFreeGrammar<Character, String, ChomskyNormalformProduction<Character, String>>
                result = ToChomskyNormalFormTransform.removeSingleVariableProductions(cfg);

        assertEquals("S", result.getStartSymbol());
        assertEquals(terminals, result.getTerminals());
        assertEquals(
                new Alphabet<>(
                        "S", "A", "B", "C", "D", "W_a", "W_b", "W_c", "S_1", "A_1", "A_2", "B_1",
                        "C_1"),
                result.getNonTerminals());
        assertEquals(37, result.getProductions().size());

        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("S", "W_b", "S_1"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("S", "C", "W_a"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("S", "W_b", "W_c"));

        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("S_1", "D", "D"));

        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("A", "W_a", "A_1"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("A", "W_b", "A_2"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("A", "W_c", "B_1"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("A", "A", "C"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("A", "W_b", "D"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("A", "W_a", "C_1"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>("A", 'a'));

        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("A_1", "C", "C"));

        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("A_2", "W_a", "D"));

        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("B", "W_c", "B_1"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("B", "A", "C"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("B", "W_b", "D"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("B", "W_a", "C_1"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>("B", 'a'));

        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("B_1", "B", "D"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("B_1", "C", "D"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>("B_1", 'a'));

        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("C", "W_b", "D"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("C", "W_a", "C_1"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>("C", 'a'));

        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("C_1", "B", "A"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("C_1", "W_a", "A_1"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("C_1", "W_b", "A_2"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("C_1", "W_c", "B_1"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("C_1", "A", "C"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("C_1", "W_b", "D"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("C_1", "W_a", "C_1"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>("C_1", 'a'));

        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("D", "C", "D"));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>("D", 'a'));

        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>("W_a", 'a'));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>("W_b", 'b'));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>("W_c", 'c'));
    }

    @Test
    public void testRemoveSingleVariableProductionsWithExistingSelfLoop() {
        Alphabet<String> nonTerminals = new Alphabet<>("0", "1");
        Alphabet<Character> terminals = new Alphabet<>('a', 'b');
        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);
        builder.withStartSymbol("0")
                .withProduction("0")
                .nt("0")
                .finish() // this is a self loop which constitutes a special case for cycle-removal
                .withProduction("0")
                .nt("1")
                .finish()
                .withProduction("0")
                .t('a');

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> cfg =
                builder.build().unwrap();

        ContextFreeGrammar<Character, String, ChomskyNormalformProduction<Character, String>>
                result = ToChomskyNormalFormTransform.removeSingleVariableProductions(cfg);

        assertEquals("0", result.getStartSymbol());
        assertEquals(terminals, result.getTerminals());
        assertEquals(nonTerminals, result.getNonTerminals());
        assertEquals(0, result.getProductions().size());
    }

    @Test
    public void testRemoveSingleVariableProductionsWithUnreachableCycles() {
        Alphabet<Character> terminals = new Alphabet<>('a', 'b');
        Alphabet<String> nonTerminals = new Alphabet<>("S", "P", "R", "Q", "T");

        ContextFreeGrammarBuilder<Character, String> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);

        ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>> grammar =
                builder.withStartSymbol("S")
                        .withProduction("S")
                        .nt("Q")
                        .finish()
                        .withProduction("S")
                        .nt("Q")
                        .nt("T")
                        .finish()
                        .withProduction("T")
                        .nt("P")
                        .nt("Q")
                        .finish()
                        .withProduction("T")
                        .nt("Q")
                        .finish()
                        .withProduction("Q")
                        .t('a')
                        .finish()

                        // these productions contain a cycle (namely P -> R -> P) but are not
                        // reachable from S by exclusively using
                        // unit productions which makes them a special case for cycle removal.
                        .withProduction("P")
                        .nt("R")
                        .finish()
                        .withProduction("R")
                        .nt("P")
                        .finish()
                        .withProduction("R")
                        .nt("S")
                        .finish()
                        .withProduction("R")
                        .nt("R")
                        .finish()
                        .build()
                        .unwrap();

        ContextFreeGrammar<Character, String, ChomskyNormalformProduction<Character, String>>
                result = ToChomskyNormalFormTransform.removeSingleVariableProductions(grammar);

        assertEquals("S", result.getStartSymbol());
        assertEquals(terminals, result.getTerminals());
        assertEquals(nonTerminals, result.getNonTerminals());
        assertEquals(7, result.getProductions().size());

        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>("S", 'a'));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>("T", 'a'));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>("Q", 'a'));
        assertContains(
                result.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>("S", "Q", "T"));

        // P and R form a SCC but we do not know which one is picked as the representative, so we
        // have to test via `anyMatch` here.
        assertTrue(
                new Alphabet<>("P", "R")
                        .stream()
                                .anyMatch(
                                        str ->
                                                result.getProductions()
                                                                .contains(
                                                                        new ChomskyNormalformProduction
                                                                                .TerminalProduction<>(
                                                                                str, 'a'))
                                                        && result.getProductions()
                                                                .contains(
                                                                        new ChomskyNormalformProduction
                                                                                        .TwoNonTerminalsProduction<
                                                                                Character, String>(
                                                                                "T", str, "Q"))
                                                        && result.getProductions()
                                                                .contains(
                                                                        new ChomskyNormalformProduction
                                                                                        .TwoNonTerminalsProduction<
                                                                                Character, String>(
                                                                                str, "Q", "T"))));
    }

    @Test
    public void testCNFTransformGrammar() {
        Alphabet<Character> nonTerminals = new Alphabet<>('S', 'A', 'B');
        Alphabet<Character> terminals = new Alphabet<>('x', 'y');

        ContextFreeGrammar<Character, Character, ContextFreeProduction<Character, Character>>
                grammar =
                        new ContextFreeGrammarBuilder<>(terminals, nonTerminals)
                                .withStartSymbol('S')
                                .withProduction('S')
                                .nt('A', 'S', 'A')
                                .finish()
                                .withProduction('S')
                                .t('x')
                                .nt('B')
                                .finish()
                                .withProduction('A')
                                .nt('B')
                                .finish()
                                .withProduction('A')
                                .nt('S')
                                .finish()
                                .withProduction('B')
                                .t('y')
                                .finish()
                                .withProduction('B')
                                .finish()
                                .build()
                                .unwrap();

        ContextFreeGrammar<Character, Character, ChomskyNormalformProduction<Character, Character>>
                cnf =
                        ToChomskyNormalFormTransform.convertToCnf(
                                grammar,
                                new ConstrainedSupplier<>() {
                                    private char current = 'X';
                                    private final Set<Character> blocked = new HashSet<>();

                                    @Override
                                    public Character get() {
                                        char next;
                                        do {
                                            next = current++;
                                        } while (blocked.contains(next));
                                        return next;
                                    }

                                    @Override
                                    public void constrain(Character character) {
                                        blocked.add(character);
                                    }
                                });

        /*
        Algorithm by hand:
            Step 1: Separation
                S -> ASA | XB
                A -> B | S
                B -> Y | eps
                X -> x
                Y -> y
            Step 2: Shortening
                S -> AZ | XB
                Z -> SA
                A -> B | S
                B -> Y | eps
                X -> x
                Y -> y
            Step 3: Epsilon elimination
                Only A and B are derivable to epsilon

                S -> AZ | Z | XB | X
                Z -> SA | S
                A -> B | S
                B -> Y
                X -> x
                Y -> y
            Step 4: Eliminate cycles of chain rules
                We get the following graph of chain rules:

                Z === S <-- A --> B
                      |           |
                      v           v
                      X           Y

                so we only have one cycle.

                S -> AS | XB | X | SA
                A -> B | S
                B -> Y
                X -> x
                Y -> y
            Step 5: Topological Ordering to eliminate remaining chain rules
                A topological ordering of the above graph (without the cycle) is ABYSX

                S -> AS | XB | SA |  x
                B -> y
                A -> AS | XB | x | y
                X -> x
                Y -> y
         */
        assertContains(
                cnf.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>('S', 'A', 'S'));
        assertContains(
                cnf.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>('S', 'S', 'A'));
        assertContains(
                cnf.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>('S', 'X', 'B'));
        assertContains(
                cnf.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>('S', 'x'));

        assertContains(
                cnf.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>('B', 'y'));

        assertContains(
                cnf.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>('A', 'A', 'S'));
        assertContains(
                cnf.getProductions(),
                new ChomskyNormalformProduction.TwoNonTerminalsProduction<>('A', 'X', 'B'));
        assertContains(
                cnf.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>('A', 'x'));
        assertContains(
                cnf.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>('A', 'y'));

        assertContains(
                cnf.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>('X', 'x'));
        assertContains(
                cnf.getProductions(),
                new ChomskyNormalformProduction.TerminalProduction<>('Y', 'y'));
    }

    protected <T> void assertContains(Collection<T> collection, T element) {
        assertTrue(collection.contains(element));
    }

    // Helper methods to sanely construct context free production objects
    private static GrammarSymbol<Character, String> t(Character c) {
        return new GrammarSymbol.Terminal<>(c);
    }

    private static GrammarSymbol<Character, String> nt(String s) {
        return new GrammarSymbol.NonTerminal<>(s);
    }

    @SafeVarargs
    private static SententialForm<Character, String> rhs(
            GrammarSymbol<Character, String>... symbols) {
        return new SententialForm<>(symbols);
    }
}

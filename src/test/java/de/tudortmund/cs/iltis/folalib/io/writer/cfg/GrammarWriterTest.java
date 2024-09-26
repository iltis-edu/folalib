package de.tudortmund.cs.iltis.folalib.io.writer.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ChomskyNormalFormGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ChomskyNormalformProduction;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;

public class GrammarWriterTest {

    @Test
    public void testWriteContextFreeGrammar() {
        Alphabet<Character> terminals = Alphabets.characterAlphabet("ab");
        Alphabet<Integer> nonTerminals = new Alphabet<>(0, 1, 2, 3);
        ContextFreeGrammarBuilder<Character, Integer> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);

        builder.withStartSymbol(0)
                .withProduction(0)
                .nt(1)
                .nt(2)
                .finish()
                .withProduction(1)
                .t('a')
                .nt(2)
                .t('b')
                .finish()
                .withEpsProduction(0)
                .withEpsProduction(2)
                .withProduction(2)
                .nt(3)
                .nt(3)
                .finish()
                .withProduction(3)
                .t('a')
                .finish()
                .withProduction(3)
                .t('b')
                .finish()
                .withProduction(1)
                .t('a')
                .finish();

        ContextFreeGrammar<Character, Integer, ContextFreeProduction<Character, Integer>> cfg =
                builder.build().unwrap();

        String written = new GrammarWriter().write(cfg);
        Set<String> lines = new LinkedHashSet<>(Arrays.asList(written.split("\n")));
        assertEquals(8, lines.size());
        assertTrue(lines.contains("0 -> 1 2"));
        assertTrue(lines.contains("1 -> a 2 b"));
        assertTrue(lines.contains("0 -> ε"));
        assertTrue(lines.contains("2 -> ε"));
        assertTrue(lines.contains("2 -> 3 3"));
        assertTrue(lines.contains("3 -> a"));
        assertTrue(lines.contains("3 -> b"));
        assertTrue(lines.contains("1 -> a"));
    }

    @Test
    public void testWriteChomskyNormalFormGrammar() {
        Alphabet<Character> terminals = Alphabets.characterAlphabet("ab");
        Alphabet<Integer> nonTerminals = new Alphabet<>(0, 1, 2, 3);
        ChomskyNormalFormGrammarBuilder<Character, Integer> builder =
                new ChomskyNormalFormGrammarBuilder<>(terminals, nonTerminals);

        builder.withStartSymbol(0)
                .withProduction(0, 1, 2)
                .withProduction(1, 0, 2)
                .withProduction(0, 'a')
                .withProduction(1, 'b')
                .withProduction(2, 'b');

        ContextFreeGrammar<Character, Integer, ChomskyNormalformProduction<Character, Integer>>
                cfg = builder.build().unwrap();

        String written = new GrammarWriter().write(cfg);
        Set<String> lines = new LinkedHashSet<>(Arrays.asList(written.split("\n")));
        assertEquals(5, lines.size());
        assertTrue(lines.contains("0 -> 1 2"));
        assertTrue(lines.contains("1 -> 0 2"));
        assertTrue(lines.contains("0 -> a"));
        assertTrue(lines.contains("1 -> b"));
        assertTrue(lines.contains("2 -> b"));
    }

    @Test
    public void testWriteContextFreeGrammarWithCustomSymbolToString() {
        Alphabet<Character> terminals = Alphabets.characterAlphabet("ab");
        Alphabet<Integer> nonTerminals = new Alphabet<>(0, 1, 2, 3);
        ContextFreeGrammarBuilder<Character, Integer> builder =
                new ContextFreeGrammarBuilder<>(terminals, nonTerminals);

        builder.withStartSymbol(0)
                .withProduction(0)
                .nt(1)
                .nt(2)
                .finish()
                .withProduction(1)
                .t('a')
                .nt(2)
                .t('b')
                .finish()
                .withEpsProduction(0)
                .withEpsProduction(2)
                .withProduction(2)
                .nt(3)
                .nt(3)
                .finish()
                .withProduction(3)
                .t('a')
                .finish()
                .withProduction(3)
                .t('b')
                .finish()
                .withProduction(1)
                .t('a')
                .finish();

        ContextFreeGrammar<Character, Integer, ContextFreeProduction<Character, Integer>> cfg =
                builder.build().unwrap();

        String written =
                new GrammarWriter(
                                GrammarWriterProperties.defaultTextProperties(),
                                gs ->
                                        gs.match(
                                                t -> "T_" + t.toString(),
                                                n -> "N_" + (((Integer) n) + 13)),
                                null)
                        .write(cfg);
        Set<String> lines = new LinkedHashSet<>(Arrays.asList(written.split("\n")));
        assertEquals(8, lines.size());
        assertTrue(lines.contains("N_13 -> N_14 N_15"));
        assertTrue(lines.contains("N_14 -> T_a N_15 T_b"));
        assertTrue(lines.contains("N_13 -> ε"));
        assertTrue(lines.contains("N_15 -> ε"));
        assertTrue(lines.contains("N_15 -> N_16 N_16"));
        assertTrue(lines.contains("N_16 -> T_a"));
        assertTrue(lines.contains("N_16 -> T_b"));
        assertTrue(lines.contains("N_14 -> T_a"));
    }
}

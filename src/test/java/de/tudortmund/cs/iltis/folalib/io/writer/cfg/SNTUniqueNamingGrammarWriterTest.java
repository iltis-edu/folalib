package de.tudortmund.cs.iltis.folalib.io.writer.cfg;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.io.writer.cfg.preprocessing.SNTUniqueNaming;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;

public class SNTUniqueNamingGrammarWriterTest {

    @Test
    public void testSNTUniqueNamingGrammarWriter() {
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

        // TODO: make this test case more resilient against changes (such as a different mapping
        // nonTerminal <-> excelColumnName)
        String written = new GrammarWriter(new SNTUniqueNaming()).write(cfg);
        Set<String> lines = new LinkedHashSet<>(Arrays.asList(written.split("\n")));
        assertEquals(8, lines.size());
        assertTrue(lines.contains("S{0} -> N{1} N{2}"));
        assertTrue(lines.contains("N{1} -> T{a} N{2} T{b}"));
        assertTrue(lines.contains("S{0} -> ε"));
        assertTrue(lines.contains("N{2} -> ε"));
        assertTrue(lines.contains("N{2} -> N{3} N{3}"));
        assertTrue(lines.contains("N{3} -> T{a}"));
        assertTrue(lines.contains("N{3} -> T{b}"));
        assertTrue(lines.contains("N{1} -> T{a}"));
    }
}

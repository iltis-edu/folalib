package de.tudortmund.cs.iltis.folalib.io.writer.cfg;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.io.writer.cfg.preprocessing.NonTerminalsToExcelColumnNames;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;

public class ExcelColumnNameStyleGrammarWriterTest {

    @Test
    public void testExcelColumnNameStyleGrammarWriter() {
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

        String written = new GrammarWriter(new NonTerminalsToExcelColumnNames()).write(cfg);
        Set<String> lines = new LinkedHashSet<>(Arrays.asList(written.split("\n")));
        assertEquals(8, lines.size());
        assertTrue(lines.contains("S -> A B"));
        assertTrue(lines.contains("A -> a B b"));
        assertTrue(lines.contains("S -> ε"));
        assertTrue(lines.contains("B -> ε"));
        assertTrue(lines.contains("B -> C C"));
        assertTrue(lines.contains("C -> a"));
        assertTrue(lines.contains("C -> b"));
        assertTrue(lines.contains("A -> a"));
    }
}

package de.tudortmund.cs.iltis.folalib.io.writer.cfg;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import org.junit.Test;

public class JSONStyleGrammarWriterTest {

    @Test
    public void testJSONStyleGrammarWriter() {
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

        // TODO: make this test case more resilient against changes, e.g. if the productions are
        // iterated over in a different order
        String written = new JSONStyleGrammarWriter().write(cfg);
        assertEquals(
                "{ \"startSymbol\": \"0\", \"productions\": [ \"0 -> 1 2\", \"1 -> a 2 b\", \"0 -> ε\", \"2 -> ε\", \"2 -> 3 3\", \"3 -> a\", \"3 -> b\", \"1 -> a\"] }",
                written);
    }
}

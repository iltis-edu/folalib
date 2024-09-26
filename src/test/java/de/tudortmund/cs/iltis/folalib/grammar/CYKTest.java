package de.tudortmund.cs.iltis.folalib.grammar;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ChomskyNormalFormGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk.CorrectCYKTableau;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk.InternalCYKTableauEntry;
import de.tudortmund.cs.iltis.folalib.grammar.production.ChomskyNormalformProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.folalib.languages.Words;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Test;

public class CYKTest {
    @Test
    public void testCYK() {
        // This tests case is taken from Prof. Schwentick's GTI SS18 slides, chapter B11, slide 17
        // (with the epsilon production removed)
        ContextFreeGrammar<Character, Character, ChomskyNormalformProduction<Character, Character>>
                grammar =
                        new ChomskyNormalFormGrammarBuilder<>(
                                        new Alphabet<>('0', '1'),
                                        new Alphabet<>('S', 'T', 'N', 'E', 'A', 'B', 'C', 'D'))
                                .withStartSymbol('S')
                                .withProduction('S', 'N', 'B')
                                .withProduction('S', 'E', 'A')
                                .withProduction('T', 'N', 'B')
                                .withProduction('T', 'E', 'A')
                                .withProduction('N', '0')
                                .withProduction('E', '1')
                                .withProduction('A', '0')
                                .withProduction('B', '1')
                                .withProduction('A', 'N', 'T')
                                .withProduction('A', 'E', 'C')
                                .withProduction('B', 'E', 'T')
                                .withProduction('B', 'N', 'D')
                                .withProduction('C', 'A', 'A')
                                .withProduction('D', 'B', 'B')
                                .build()
                                .unwrap();

        CorrectCYKTableau<Character, Character> tableau =
                CorrectCYKTableau.compute(grammar, Words.characterWord("01110100"));

        assertTrue(tableau.accepts());

        // Sample the actual tableau

        assertTrue(tableau.get(1, 3).isEmpty());
        assertTrue(tableau.get(1, 5).isEmpty());

        for (int i = 0; i < 8; ++i) {
            for (int j = i; j < 8; ++j) {
                if (!(i == 1 && (j == 3 || j == 5))) assertFalse(tableau.get(i, j).isEmpty());
            }
        }

        assertEntry(tableau, 0, 7, makeLinkedHashSet(new Pair<>('S', 0), new Pair<>('T', 0)));
        assertEntry(tableau, 3, 6, makeLinkedHashSet(new Pair<>('S', 3), new Pair<>('T', 3)));
        assertEntry(tableau, 4, 6, makeLinkedHashSet(new Pair<>('A', 4)));
        assertEntry(tableau, 2, 4, makeLinkedHashSet(new Pair<>('B', 2)));
    }

    private static <T> Set<T> makeLinkedHashSet(T... elements) {
        Set<T> result = new LinkedHashSet<>(Arrays.asList(elements));
        return result;
    }

    @Test
    public void testCYKOnEpsilon() {
        ContextFreeGrammar<Character, Character, ChomskyNormalformProduction<Character, Character>>
                grammar =
                        new ChomskyNormalFormGrammarBuilder<>(
                                        new Alphabet<>('a'), new Alphabet<>('S'))
                                .withStartSymbol('S')
                                .build()
                                .unwrap();

        assertFalse(CorrectCYKTableau.compute(grammar, new Word<>()).accepts());
    }

    private static void assertEntry(
            CorrectCYKTableau<Character, Character> tableau,
            int i,
            int j,
            Set<Pair<Character, Integer>> expected) {
        Set<InternalCYKTableauEntry<Character, Character>> entries = tableau.get(i, j);

        assertEquals(entries.size(), expected.size());

        outer:
        for (Pair<Character, Integer> pair : expected) {
            for (InternalCYKTableauEntry<Character, Character> entry : entries) {
                if (entry.getK() == pair.second() && entry.getNonTerminal() == pair.first())
                    continue outer;
            }
            fail("CYKTableau does not contain entry " + pair + " at position " + i + ", " + j);
        }
    }
}

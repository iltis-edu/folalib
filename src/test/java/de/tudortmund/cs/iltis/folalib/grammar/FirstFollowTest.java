package de.tudortmund.cs.iltis.folalib.grammar;

import static junit.framework.TestCase.*;

import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.SetWithEpsilon;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import java.util.Map;
import org.junit.Test;

public class FirstFollowTest {
    private final ContextFreeGrammar<
                    Character, Character, ContextFreeProduction<Character, Character>>
            testGrammar;

    public FirstFollowTest() {
        // Example taken from lecture "Übersetzerbau", Chapter "Parsing Top-Down", slides 56 and 62
        testGrammar =
                new ContextFreeGrammarBuilder<>(
                                new Alphabet<>('(', ')', '+', 'i', '*'),
                                new Alphabet<>('E', 'T', 'X', 'Y'))
                        .withStartSymbol('E')
                        .withProduction('E')
                        .nt('T', 'X')
                        .finish()
                        .withProduction('T')
                        .t('(')
                        .nt('E')
                        .t(')')
                        .finish()
                        .withProduction('T')
                        .t('i')
                        .nt('Y')
                        .finish()
                        .withProduction('X')
                        .t('+')
                        .nt('E')
                        .finish()
                        .withEpsProduction('X')
                        .withProduction('Y')
                        .t('*')
                        .nt('T')
                        .finish()
                        .withEpsProduction('Y')
                        .build()
                        .unwrap();
    }

    @Test
    public void testFirstSets() {
        Map<Character, SetWithEpsilon<Character>> firstSets = testGrammar.firstSets();

        assertEquals(firstSets.get('T').size(), 2);
        assertTrue(firstSets.get('T').contains('i'));
        assertTrue(firstSets.get('T').contains('('));
        assertFalse(firstSets.get('T').containsEpsilon());

        assertEquals(firstSets.get('E').size(), 2);
        assertTrue(firstSets.get('E').contains('i'));
        assertTrue(firstSets.get('E').contains('('));
        assertFalse(firstSets.get('E').containsEpsilon());

        assertEquals(firstSets.get('X').size(), 2);
        assertTrue(firstSets.get('X').contains('+'));
        assertTrue(firstSets.get('X').containsEpsilon());

        assertEquals(firstSets.get('Y').size(), 2);
        assertTrue(firstSets.get('Y').contains('*'));
        assertTrue(firstSets.get('Y').containsEpsilon());
    }

    @Test
    public void testFollowSets() {
        Map<Character, SetWithEpsilon<Character>> followSets = testGrammar.followSets();

        assertEquals(followSets.get('E').size(), 2);
        assertTrue(followSets.get('E').containsEpsilon());
        assertTrue(followSets.get('E').contains(')'));

        assertEquals(followSets.get('X').size(), 2);
        assertTrue(followSets.get('X').containsEpsilon());
        assertTrue(followSets.get('X').contains(')'));

        assertEquals(followSets.get('T').size(), 3);
        assertTrue(followSets.get('T').containsEpsilon());
        assertTrue(followSets.get('T').contains(')'));
        assertTrue(followSets.get('T').contains('+'));

        assertEquals(followSets.get('Y').size(), 3);
        assertTrue(followSets.get('Y').containsEpsilon());
        assertTrue(followSets.get('Y').contains(')'));
        assertTrue(followSets.get('Y').contains('+'));
    }
}

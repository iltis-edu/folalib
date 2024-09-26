package de.tudortmund.cs.iltis.folalib.grammar.specialization;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.construction.specialization.GrammarToC0GrammarSpecialization;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import java.util.List;
import org.junit.Test;

public class GrammarToC0GrammarSpecializationTest {

    @Test
    public void testFaults() {
        Grammar<Character, Character, Production<Character, Character>> grammar =
                new Grammar<>(
                        Alphabets.characterAlphabet("abB"),
                        Alphabets.characterAlphabet("ABb"),
                        'S',
                        List.of(
                                new Production<>(
                                        new SententialForm<>(),
                                        new SententialForm<>(
                                                new GrammarSymbol.Terminal<>('c'),
                                                new GrammarSymbol.NonTerminal<>('C')))));

        GrammarConstructionFaultCollection resultErr =
                new GrammarToC0GrammarSpecialization<Character, Character>()
                        .specialize(grammar)
                        .unwrapErr();

        assertEquals(6, resultErr.getFaults().size());

        assertEquals(
                1,
                resultErr.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == GrammarConstructionFaultReason.EMPTY_LHS)
                        .count());
        assertEquals(
                2,
                resultErr.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == GrammarConstructionFaultReason
                                                        .ALPHABETS_NOT_DISJOINT)
                        .count());
        assertEquals(
                1,
                resultErr.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == GrammarConstructionFaultReason.UNKNOWN_TERMINAL)
                        .count());
        assertEquals(
                2,
                resultErr.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == GrammarConstructionFaultReason
                                                        .UNKNOWN_NONTERMINAL)
                        .count());
    }
}

package de.tudortmund.cs.iltis.folalib.grammar.specialization;

import static org.junit.Assert.assertEquals;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.RightRegularGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.construction.specialization.GrammarToRightRegularGrammarSpecialization;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.grammar.production.RightRegularProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import java.util.List;
import org.junit.Test;

public class GrammarToRightRegularGrammarSpecializationTest {

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
                                                new GrammarSymbol.NonTerminal<>(
                                                        'C'))), // Not only one non-terminal on LHS
                                new Production<>(
                                        new SententialForm<>(
                                                new GrammarSymbol.NonTerminal<>('A'),
                                                new GrammarSymbol.NonTerminal<>('B')),
                                        new SententialForm<>()), // Epsilon rules are not allowed.
                                // Does not produce a rhs error
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.Terminal<>('a')),
                                        new SententialForm<>(
                                                new GrammarSymbol.NonTerminal<>(
                                                        'A'))), // Invalid rhs
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('A')),
                                        new SententialForm<>(
                                                new GrammarSymbol.Terminal<>('a'),
                                                new GrammarSymbol.Terminal<>('a'))), // Invalid rhs
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('B')),
                                        new SententialForm<>(
                                                new GrammarSymbol.NonTerminal<>('A'),
                                                new GrammarSymbol.Terminal<>(
                                                        'a'))), // Left regular RHS -> mix
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('B')),
                                        new SententialForm<>(
                                                new GrammarSymbol.Terminal<>('a'),
                                                new GrammarSymbol.NonTerminal<>('A')))));

        GrammarConstructionFaultCollection resultErr =
                new GrammarToRightRegularGrammarSpecialization<Character, Character>()
                        .specialize(grammar)
                        .unwrapErr();

        assertEquals(13, resultErr.getFaults().size());

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
        assertEquals(
                3,
                resultErr.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == GrammarConstructionFaultReason
                                                        .LHS_NOT_ONLY_ONE_NONTERMINAL)
                        .count());
        assertEquals(
                1,
                resultErr.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == GrammarConstructionFaultReason.THE_EPSILON_RULE)
                        .count());
        assertEquals(
                2,
                resultErr.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == GrammarConstructionFaultReason
                                                        .INVALID_REGULAR_RHS)
                        .count());
        assertEquals(
                1,
                resultErr.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == GrammarConstructionFaultReason
                                                        .RIGHT_AND_LEFT_REGULAR_RHS_MIX)
                        .count());
    }

    @Test
    public void testCorrectGrammar() {
        Grammar<Character, Character, Production<Character, Character>> grammar =
                new Grammar<>(
                        Alphabets.characterAlphabet("abc"),
                        Alphabets.characterAlphabet("SAB"),
                        'S',
                        List.of(
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('S')),
                                        new SententialForm<>()),
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('S')),
                                        new SententialForm<>(new GrammarSymbol.Terminal<>('a'))),
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('A')),
                                        new SententialForm<>(
                                                new GrammarSymbol.Terminal<>('a'),
                                                new GrammarSymbol.NonTerminal<>('B'))),
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('A')),
                                        new SententialForm<>(
                                                new GrammarSymbol.Terminal<>('c'),
                                                new GrammarSymbol.NonTerminal<>('A')))));

        Grammar<Character, Character, RightRegularProduction<Character, Character>> result =
                new GrammarToRightRegularGrammarSpecialization<Character, Character>()
                        .specialize(grammar)
                        .unwrap();

        assertEquals(
                new RightRegularGrammarBuilder<>(
                                Alphabets.characterAlphabet("abc"),
                                Alphabets.characterAlphabet("SAB"))
                        .withStartSymbol('S')
                        .withProduction(new RightRegularProduction<>('S'))
                        .withProduction('S', 'a')
                        .withProduction('A', 'a', 'B')
                        .withProduction('A', 'c', 'A')
                        .build()
                        .unwrap(),
                result);
    }
}

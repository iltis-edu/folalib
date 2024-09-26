package de.tudortmund.cs.iltis.folalib.grammar.specialization;

import static org.junit.Assert.assertEquals;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.construction.specialization.GrammarToContextSensitiveGrammarSpecialization;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import java.util.List;
import org.junit.Test;

public class GrammarToContextSensitiveGrammarSpecializationTest {

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
                                                new GrammarSymbol.NonTerminal<>('C'),
                                                new GrammarSymbol.NonTerminal<>(
                                                        'S'))), // S can occur on the rhs without
                                // causing a fault
                                new Production<>(
                                        new SententialForm<>(
                                                new GrammarSymbol.Terminal<>('a'),
                                                new GrammarSymbol.Terminal<>('b')),
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('A'))),
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('A')),
                                        new SententialForm<>()) // Only epsilon fault because A is
                                // not start symbol
                                ));

        GrammarConstructionFaultCollection resultErr =
                new GrammarToContextSensitiveGrammarSpecialization<Character, Character>()
                        .specialize(grammar)
                        .unwrapErr();

        assertEquals(9, resultErr.getFaults().size());

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
                3,
                resultErr.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == GrammarConstructionFaultReason
                                                        .UNKNOWN_NONTERMINAL)
                        .count());
        assertEquals(
                1,
                resultErr.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == GrammarConstructionFaultReason.NON_MONOTONIC)
                        .count());
        assertEquals(
                1,
                resultErr.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == GrammarConstructionFaultReason.THE_EPSILON_RULE)
                        .count());
    }

    @Test
    public void testTheEpsilonRuleWithStartSymbolInRhs() {
        Grammar<Character, Character, Production<Character, Character>> grammar =
                new Grammar<>(
                        Alphabets.characterAlphabet("a"),
                        Alphabets.characterAlphabet("SA"),
                        'S',
                        List.of(
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('S')),
                                        new SententialForm<>()),
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('A')),
                                        new SententialForm<>(
                                                new GrammarSymbol.NonTerminal<>('S')))));

        GrammarConstructionFaultCollection resultErr =
                new GrammarToContextSensitiveGrammarSpecialization<Character, Character>()
                        .specialize(grammar)
                        .unwrapErr();

        assertEquals(1, resultErr.getFaults().size());

        assertEquals(
                1,
                resultErr.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == GrammarConstructionFaultReason.THE_EPSILON_RULE)
                        .count());
    }

    @Test
    public void testTheEpsilonRule() {
        Grammar<Character, Character, Production<Character, Character>> grammar =
                new Grammar<>(
                        Alphabets.characterAlphabet("a"),
                        Alphabets.characterAlphabet("SA"),
                        'S',
                        List.of(
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('S')),
                                        new SententialForm<>()),
                                new Production<>(
                                        new SententialForm<>(new GrammarSymbol.NonTerminal<>('A')),
                                        new SententialForm<>(new GrammarSymbol.Terminal<>('a')))));

        new GrammarToContextSensitiveGrammarSpecialization<Character, Character>()
                .specialize(grammar)
                .unwrap();
    }
}

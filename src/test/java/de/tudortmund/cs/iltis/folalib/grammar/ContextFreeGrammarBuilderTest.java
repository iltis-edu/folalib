package de.tudortmund.cs.iltis.folalib.grammar;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.SyntaxFault;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.util.List;
import org.junit.Test;

public class ContextFreeGrammarBuilderTest {

    private final Alphabet<Character> terminals = Alphabets.characterAlphabet("ab");
    private final Alphabet<String> nonTerminals = new Alphabet<>("S", "A");
    private final ContextFreeGrammarBuilder<Character, String> builder =
            new ContextFreeGrammarBuilder<>(terminals, nonTerminals);

    @Test
    public void testUnknownNonTerminalSymbol() {
        builder.buildAndReset();
        builder.withStartSymbol("S")
                .withProduction("S")
                .nt("A")
                .nt("B")
                .finish() /* B is not in `nonTerminals` alphabet */
                .withProduction("A")
                .t('a')
                .finish();

        GrammarConstructionFaultCollection result = builder.build().unwrapErr();
        List<Fault<GrammarConstructionFaultReason>> faults = result.getFaults();

        assertEquals(1, faults.size());
        assertTrue(faults.contains(SyntaxFault.unknownNonTerminalSymbol("B")));
    }

    @Test
    public void testUnknownTerminalSymbol() {
        builder.buildAndReset();
        builder.withStartSymbol("S")
                .withProduction("S")
                .t('c')
                .nt("A")
                .finish() /* c is not in `terminals` alphabet */
                .withProduction("A")
                .t('a')
                .finish();

        GrammarConstructionFaultCollection result = builder.build().unwrapErr();
        List<Fault<GrammarConstructionFaultReason>> faults = result.getFaults();

        assertEquals(1, faults.size());
        assertTrue(faults.contains(SyntaxFault.unknownTerminalSymbol('c')));
    }
}

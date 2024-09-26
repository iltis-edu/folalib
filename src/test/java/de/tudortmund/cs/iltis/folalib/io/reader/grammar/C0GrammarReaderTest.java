package de.tudortmund.cs.iltis.folalib.io.reader.grammar;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.ProductionFault;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.SyntaxFault;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.fault.GrammarParsingFaultReason;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol.standard.DeriveFromFirstProductionStartSymbolDerivationStrategy;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol.standard.GivenSymbolStartSymbolDerivationStrategy;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.io.parser.error.IncorrectParseInputException;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFault;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultCollection;
import java.util.List;
import org.junit.Test;

public class C0GrammarReaderTest {

    @SuppressWarnings("all")
    // We cannot use the test rig here because the fault mapping is different
    // (GrammarConstructionFaultCollection)
    @Test
    public void testGrammarBuilderFault() {
        try {
            new C0GrammarReader(
                            GrammarReaderProperties.createDefault(
                                    "a",
                                    "A",
                                    new GivenSymbolStartSymbolDerivationStrategy(
                                            new IndexedSymbol("S"))))
                    .read("A -> a");
            fail();
        } catch (IncorrectParseInputException e) {
            List<GrammarConstructionFaultReason> faults =
                    (List<GrammarConstructionFaultReason>)
                            e.getFaultMapping()
                                    .get(GrammarConstructionFaultCollection.class)
                                    .getFaults();

            assertEquals(1, faults.size());
            assertTrue(
                    faults.contains(SyntaxFault.unknownNonTerminalSymbol(new IndexedSymbol("S"))));

            assertNull(e.getFaultMapping().getOutput());
        }
    }

    @SuppressWarnings("all")
    @Test
    public void testGrammarSpecializationFaultAndParsingFault() {
        try {
            new C0GrammarReader(
                            GrammarReaderProperties.createDefault(
                                    "a",
                                    "A",
                                    new GivenSymbolStartSymbolDerivationStrategy(
                                            new IndexedSymbol("S"))))
                    .read("A -> a| ,\n eps -> eps");
            fail();
        } catch (IncorrectParseInputException e) {
            List<GrammarConstructionFaultReason> grammarFaults =
                    (List<GrammarConstructionFaultReason>)
                            e.getFaultMapping()
                                    .get(GrammarConstructionFaultCollection.class)
                                    .getFaults();

            assertEquals(2, grammarFaults.size());
            assertTrue(
                    grammarFaults.contains(
                            SyntaxFault.unknownNonTerminalSymbol(new IndexedSymbol("S"))));
            assertTrue(
                    grammarFaults.contains(
                            new ProductionFault<>(
                                    GrammarConstructionFaultReason.EMPTY_LHS,
                                    new Production(
                                            new SententialForm<>(), new SententialForm<>()))));

            List<ParsingFault> parsingFaults =
                    (List<ParsingFault>)
                            e.getFaultMapping().get(ParsingFaultCollection.class).getFaults();

            assertEquals(1, parsingFaults.size());
            assertEquals(
                    1,
                    parsingFaults.stream()
                            .filter(
                                    fault ->
                                            fault.getReason()
                                                    == GrammarParsingFaultReason
                                                            .LINE_ENDS_WITH_RIGHT_SIDE_SEPARATOR)
                            .count());

            assertNull(e.getFaultMapping().getOutput());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStartSymbolDerive() {
        Grammar<IndexedSymbol, IndexedSymbol, Production<IndexedSymbol, IndexedSymbol>> grammar =
                new C0GrammarReader(
                                GrammarReaderProperties.createDefault(
                                        "[a-z]",
                                        "[A-Z]",
                                        new DeriveFromFirstProductionStartSymbolDerivationStrategy()))
                        .read("A -> aB \n B -> c");

        assertEquals(new IndexedSymbol("A"), grammar.getStartSymbol());

        try {
            new C0GrammarReader(
                            GrammarReaderProperties.createDefault(
                                    "[a-z]",
                                    "[A-Z]",
                                    new DeriveFromFirstProductionStartSymbolDerivationStrategy()))
                    .read("Ab -> aB \n B -> c");
            fail();
        } catch (IncorrectParseInputException e) {
            List<ParsingFault> parsingFaults =
                    (List<ParsingFault>)
                            e.getFaultMapping().get(ParsingFaultCollection.class).getFaults();

            assertEquals(1, parsingFaults.size());
            assertEquals(
                    1,
                    parsingFaults.stream()
                            .filter(
                                    fault ->
                                            fault.getReason()
                                                    == GrammarParsingFaultReason
                                                            .START_SYMBOL_NOT_DERIVABLE)
                            .count());

            assertNull(e.getFaultMapping().getOutput());
        }
    }
}

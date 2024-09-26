package de.tudortmund.cs.iltis.folalib.io.reader.word;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.io.ParserTestRig;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultCollection;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultReason;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.GivenAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.fault.GrammarParsingFaultReason;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import de.tudortmund.cs.iltis.utils.io.parser.error.IncorrectParseInputException;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultCollection;
import de.tudortmund.cs.iltis.utils.io.parser.general.GeneralParsingFaultReason;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.RegularSymbolSplittingPolicy;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import org.junit.BeforeClass;
import org.junit.Test;

public class WordReaderTest extends ParserTestRig<Word<IndexedSymbol>, Word<IndexedSymbol>> {

    private static final IndexedSymbol a0 = new IndexedSymbol("a", "0", "l");
    private static final IndexedSymbol a1 = new IndexedSymbol("a", "1", "l");
    private static final IndexedSymbol a2 = new IndexedSymbol("a", "2", "r");
    private static final IndexedSymbol a3 = new IndexedSymbol("a", "3", "r");
    private static final IndexedSymbol a4 = new IndexedSymbol("a", "4", "c");
    private static final IndexedSymbol a5 = new IndexedSymbol("a", "5", "c");

    private static final WordReader reader =
            new WordReader(
                    WordReaderProperties.createDefault(new Alphabet<>(a0, a1, a2, a3, a4, a5)));
    private static final WordReader restrictedAlphabetReader =
            new WordReader(
                    WordReaderProperties.createDefault(
                            RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                            new GivenAlphabetPolicy(new Alphabet<>(a0)),
                            true));

    @BeforeClass
    public static void initTestRig() {
        Function<Word<IndexedSymbol>, Word<IndexedSymbol>> returnUnchanged = input -> input;

        positives = new LinkedList<>();
        negatives = new LinkedList<>();

        /*-----------------------------------------*\
         + POSITIVES                               +
        \*-----------------------------------------*/

        // test read words
        positives.add(new Object[] {reader, "a_1^la^c_4", returnUnchanged, new Word<>(a1, a4)});
        positives.add(
                new Object[] {
                    reader,
                    "a_0^l a^l_0 a_5^c a^r_3 a_2^r",
                    returnUnchanged,
                    new Word<>(a0, a0, a5, a3, a2)
                });

        // test read words with weird blanks between symbols
        positives.add(
                new Object[] {
                    reader, " \u200B a_0^l \n a^c_4", returnUnchanged, new Word<>(a0, a4)
                });

        // test epsilon
        positives.add(new Object[] {reader, " eps \n ", returnUnchanged, new Word<>()});
        positives.add(new Object[] {reader, " \nε  ", returnUnchanged, new Word<>()});

        /*-----------------------------------------*\
         + NEGATIVES                               +
        \*-----------------------------------------*/

        // test read empty word
        negatives.add(
                new Object[] {
                    reader,
                    "",
                    returnUnchanged,
                    new Word<>(),
                    List.of(new Pair<>(GrammarParsingFaultReason.BLANK_INPUT, 1))
                });

        // test read words with weird blanks inside symbols
        negatives.add(
                new Object[] {
                    reader,
                    "a _ 2 ^ \t r    a_0^l",
                    returnUnchanged,
                    null,
                    List.of(
                            new Pair<>(
                                    GeneralParsingFaultReason.INVALID_SYMBOL,
                                    2) // Because of finite symbol splitting policy
                            )
                });

        // test read words with invalid prefix
        negatives.add(
                new Object[] {
                    reader,
                    "aa_2^r",
                    returnUnchanged,
                    new Word<>(new IndexedSymbol("a"), a2),
                    List.of(new Pair<>(AlphabetInferenceFaultReason.SYMBOL_NOT_ALLOWED, 1))
                });

        // test read words with invalid infix
        negatives.add(
                new Object[] {
                    reader,
                    "a_2*^r",
                    returnUnchanged,
                    null,
                    List.of(new Pair<>(GeneralParsingFaultReason.INVALID_SYMBOL, 1))
                });

        // test read words with invalid suffix
        negatives.add(
                new Object[] {
                    reader,
                    "a_2^rt",
                    returnUnchanged,
                    new Word<>(a2, new IndexedSymbol("t")),
                    List.of(new Pair<>(AlphabetInferenceFaultReason.SYMBOL_NOT_ALLOWED, 1))
                });

        // test read words with incomplete indexed symbol
        negatives.add(
                new Object[] {
                    reader,
                    "a^r_",
                    returnUnchanged,
                    null,
                    List.of(new Pair<>(GeneralParsingFaultReason.INVALID_SYMBOL, 1))
                });

        // test multiple epsilons
        negatives.add(
                new Object[] {
                    reader,
                    "eps eps ε",
                    returnUnchanged,
                    new Word<>(),
                    List.of(new Pair<>(GrammarParsingFaultReason.ABUNDANT_EPSILONS, 1))
                });

        // test epsilon symbol mix
        negatives.add(
                new Object[] {
                    reader,
                    "eps a_0^l eps a_1^l ε",
                    returnUnchanged,
                    new Word<>(a0, a1),
                    List.of(new Pair<>(GrammarParsingFaultReason.SYMBOL_EPSILON_MIX, 1))
                });
    }

    @Test
    public void testAlphabetInferenceFault() {
        try {
            // z gets split correctly but is then rejected by the alphabet inference
            restrictedAlphabetReader.read("z a");
            fail();
        } catch (IncorrectParseInputException e) {
            AlphabetInferenceFaultCollection alphabetFaults =
                    (AlphabetInferenceFaultCollection)
                            e.getFaultMapping().get(AlphabetInferenceFaultCollection.class);

            assertEquals(
                    2,
                    alphabetFaults.getFaults().stream()
                            .filter(
                                    fault ->
                                            fault.getReason()
                                                    == AlphabetInferenceFaultReason
                                                            .SYMBOL_NOT_ALLOWED)
                            .count());
            assertEquals(
                    new Word<>(new IndexedSymbol("z"), new IndexedSymbol("a")),
                    e.getFaultMapping().getOutput());
        }
    }

    @Test
    public void testAlphabetInferenceFaultAndParsingFault() {
        try {
            // z gets split correctly but is then rejected by the alphabet inference
            restrictedAlphabetReader.read("z eps a");
            fail();
        } catch (IncorrectParseInputException e) {
            AlphabetInferenceFaultCollection alphabetFaults =
                    (AlphabetInferenceFaultCollection)
                            e.getFaultMapping().get(AlphabetInferenceFaultCollection.class);
            assertEquals(
                    2,
                    alphabetFaults.getFaults().stream()
                            .filter(
                                    fault ->
                                            fault.getReason()
                                                    == AlphabetInferenceFaultReason
                                                            .SYMBOL_NOT_ALLOWED)
                            .count());

            ParsingFaultCollection parsingFaults =
                    (ParsingFaultCollection) e.getFaultMapping().get(ParsingFaultCollection.class);
            assertEquals(
                    1,
                    parsingFaults.getFaults().stream()
                            .filter(
                                    fault ->
                                            fault.getReason()
                                                    == GrammarParsingFaultReason.SYMBOL_EPSILON_MIX)
                            .count());

            assertEquals(
                    new Word<>(new IndexedSymbol("z"), new IndexedSymbol("a")),
                    e.getFaultMapping().getOutput());
        }
    }
}

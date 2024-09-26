package de.tudortmund.cs.iltis.folalib.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.expressions.regular.Symbol;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFault;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultCollection;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultReason;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferAlphabetAndExpandPolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferFromRegexPolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferMinimalAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferMinimalNonEmptyAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.reader.regex.RegularExpressionReader;
import de.tudortmund.cs.iltis.folalib.io.reader.regex.RegularExpressionReaderProperties;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.io.parser.error.IncorrectParseInputException;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultTypeMapping;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.RegularSymbolSplittingPolicy;
import java.util.List;
import org.junit.Test;

public class AlphabetInferenceTest {

    IndexedSymbol hashtag = new IndexedSymbol("#");
    final Alphabet<IndexedSymbol> baseAlphabet = new Alphabet<>(hashtag);

    @Test
    public void testInferMinimalAlphabetStrategy() {
        RegularExpression<IndexedSymbol> regex =
                new RegularExpressionReader(
                                RegularExpressionReaderProperties.createDefault(
                                        RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                                        new InferMinimalAlphabetPolicy(),
                                        null,
                                        true))
                        .read("eps");

        assertEquals(new Alphabet<>(), regex.getAlphabet());
    }

    @Test
    public void testInferAlphabetAndExpandStrategy() {
        RegularExpression<IndexedSymbol> regex =
                new RegularExpressionReader(
                                RegularExpressionReaderProperties.createDefault(
                                        RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                                        new InferAlphabetAndExpandPolicy(baseAlphabet),
                                        null,
                                        true))
                        .read("abcdefgh");

        assertEquals(Alphabets.indexedSymbolAlphabet("#abcdefgh"), regex.getAlphabet());
    }

    @Test
    public void testInferAlphabetStrategy() {

        RegularExpressionReader reader =
                new RegularExpressionReader(
                        RegularExpressionReaderProperties.createDefault(
                                RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                                new InferMinimalNonEmptyAlphabetPolicy(baseAlphabet),
                                null,
                                true));

        RegularExpression<IndexedSymbol> regex = reader.read("abcdefgh");
        // No hashtag in this test
        assertEquals(Alphabets.indexedSymbolAlphabet("abcdefgh"), regex.getAlphabet());

        regex = reader.read("eps empty");
        assertEquals(Alphabets.indexedSymbolAlphabet("#"), regex.getAlphabet());

        regex = reader.read("empty eps a");
        assertEquals(Alphabets.indexedSymbolAlphabet("a"), regex.getAlphabet());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInferFromRegex() {
        RegularExpressionReader reader =
                new RegularExpressionReader(
                        RegularExpressionReaderProperties.createDefault(
                                RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                                new InferFromRegexPolicy(
                                        "[a-c]", Alphabets.indexedSymbolAlphabet("abc")),
                                null,
                                true));

        try {
            reader.read("a+b+c+d+z");
            fail();
        } catch (IncorrectParseInputException e) {
            ParsingFaultTypeMapping<?> mapping = e.getFaultMapping();

            assertEquals(
                    new Symbol<>(new IndexedSymbol("a"))
                            .or(
                                    new Symbol<>(new IndexedSymbol("b")),
                                    new Symbol<>(new IndexedSymbol("c")),
                                    new Symbol<>(new IndexedSymbol("d")),
                                    new Symbol<>(new IndexedSymbol("z"))),
                    mapping.getOutput());

            List<AlphabetInferenceFault> faults =
                    (List<AlphabetInferenceFault>)
                            e.getFaultMapping()
                                    .get(AlphabetInferenceFaultCollection.class)
                                    .getFaults();

            assertEquals(2, faults.size());
            assertEquals(
                    2,
                    faults.stream()
                            .filter(
                                    fault ->
                                            fault.getReason()
                                                    == AlphabetInferenceFaultReason
                                                            .SYMBOL_NOT_ALLOWED)
                            .count());
        }
    }
}

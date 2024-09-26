package de.tudortmund.cs.iltis.folalib.io.reader.sentential;

import static org.junit.Assert.assertEquals;

import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.io.ParserTestRig;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultCollection;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultReason;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.GivenAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.fault.GrammarParsingFaultReason;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.standard.InferFromRegexInputConverter;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import de.tudortmund.cs.iltis.utils.io.parser.error.IncorrectParseInputException;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultCollection;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.RegularSymbolSplittingPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.junit.BeforeClass;
import org.junit.Test;

public class SententialFormReaderTest
        extends ParserTestRig<
                SententialForm<IndexedSymbol, IndexedSymbol>,
                SententialForm<IndexedSymbol, IndexedSymbol>> {

    private static IndexedSymbol a = new IndexedSymbol("a");
    private static IndexedSymbol b = new IndexedSymbol("b");
    private static IndexedSymbol c = new IndexedSymbol("c");
    private static IndexedSymbol d = new IndexedSymbol("d");

    private static IndexedSymbol A = new IndexedSymbol("A");
    private static IndexedSymbol B = new IndexedSymbol("B");
    private static IndexedSymbol C = new IndexedSymbol("C");
    private static IndexedSymbol D = new IndexedSymbol("D");

    private static IndexedSymbol x = new IndexedSymbol("x");
    private static IndexedSymbol X = new IndexedSymbol("X");

    private static Alphabet<IndexedSymbol> terminals = new Alphabet<>(a, b, c, d);
    private static Alphabet<IndexedSymbol> nonTerminals = new Alphabet<>(A, B, C, D);

    private static GrammarSymbol.Terminal<IndexedSymbol, IndexedSymbol> aT =
            new GrammarSymbol.Terminal<>(a);
    private static GrammarSymbol.Terminal<IndexedSymbol, IndexedSymbol> bT =
            new GrammarSymbol.Terminal<>(b);
    private static GrammarSymbol.Terminal<IndexedSymbol, IndexedSymbol> cT =
            new GrammarSymbol.Terminal<>(c);
    private static GrammarSymbol.Terminal<IndexedSymbol, IndexedSymbol> dT =
            new GrammarSymbol.Terminal<>(d);
    private static GrammarSymbol.Terminal<IndexedSymbol, IndexedSymbol> xT =
            new GrammarSymbol.Terminal<>(x);

    private static GrammarSymbol.NonTerminal<IndexedSymbol, IndexedSymbol> A_NT =
            new GrammarSymbol.NonTerminal<>(A);
    private static GrammarSymbol.NonTerminal<IndexedSymbol, IndexedSymbol> B_NT =
            new GrammarSymbol.NonTerminal<>(B);
    private static GrammarSymbol.NonTerminal<IndexedSymbol, IndexedSymbol> C_NT =
            new GrammarSymbol.NonTerminal<>(C);
    private static GrammarSymbol.NonTerminal<IndexedSymbol, IndexedSymbol> D_NT =
            new GrammarSymbol.NonTerminal<>(D);
    private static GrammarSymbol.NonTerminal<IndexedSymbol, IndexedSymbol> X_NT =
            new GrammarSymbol.NonTerminal<>(X);

    private static SententialFormReader reader =
            new SententialFormReader(
                    SententialFormReaderProperties.createDefault(
                            RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                            new GivenAlphabetPolicy(terminals),
                            new GivenAlphabetPolicy(nonTerminals),
                            new InferFromRegexInputConverter("[a-z]", "[A-Z]"),
                            true));

    @BeforeClass
    public static void initTestRig() {
        Function<
                        SententialForm<IndexedSymbol, IndexedSymbol>,
                        SententialForm<IndexedSymbol, IndexedSymbol>>
                returnUnchanged = input -> input;

        positives = new ArrayList<>();
        negatives = new ArrayList<>();

        /*-----------------------------------------*\
         + POSITIVES                               +
        \*-----------------------------------------*/

        // test general
        positives.add(
                new Object[] {
                    reader,
                    "abCD A a   \n Bdc",
                    returnUnchanged,
                    new SententialForm<>(aT, bT, C_NT, D_NT, A_NT, aT, B_NT, dT, cT)
                });

        // test epsilon
        positives.add(new Object[] {reader, "eps", returnUnchanged, new SententialForm<>()});
        positives.add(new Object[] {reader, "ε", returnUnchanged, new SententialForm<>()});

        /*-----------------------------------------*\
         + NEGATIVES                               +
        \*-----------------------------------------*/

        // test empty input
        negatives.add(
                new Object[] {
                    reader,
                    "  \n  ",
                    returnUnchanged,
                    new SententialForm<>(),
                    List.of(new Pair<>(GrammarParsingFaultReason.BLANK_INPUT, 1))
                });

        // test ambiguous symbol
        negatives.add(
                new Object[] {
                    reader,
                    "A a 1 b 2 B",
                    returnUnchanged,
                    new SententialForm<>(A_NT, aT, bT, B_NT),
                    List.of(new Pair<>(GrammarParsingFaultReason.AMBIGUOUS_SYMBOL, 2))
                });

        // test multiple epsilons
        negatives.add(
                new Object[] {
                    reader,
                    "εε eps epsilon",
                    returnUnchanged,
                    new SententialForm<>(),
                    List.of(new Pair<>(GrammarParsingFaultReason.ABUNDANT_EPSILONS, 1))
                });

        // test epsilon symbol mix
        negatives.add(
                new Object[] {
                    reader,
                    "εε B c eps a epsilon d",
                    returnUnchanged,
                    new SententialForm<>(B_NT, cT, aT, dT),
                    List.of(new Pair<>(GrammarParsingFaultReason.SYMBOL_EPSILON_MIX, 1))
                });

        // test unknown sentential form -> bail out
        negatives.add(
                new Object[] {
                    reader,
                    "1 2 3",
                    returnUnchanged,
                    null,
                    List.of(
                            new Pair<>(GrammarParsingFaultReason.UNKNOWN_SENTENTIAL_FORM, 1),
                            new Pair<>(GrammarParsingFaultReason.AMBIGUOUS_SYMBOL, 3))
                });
        negatives.add(
                new Object[] {
                    reader,
                    "1 ε 2 ε 3 ε eps",
                    returnUnchanged,
                    null,
                    List.of(
                            new Pair<>(GrammarParsingFaultReason.UNKNOWN_SENTENTIAL_FORM, 1),
                            new Pair<>(GrammarParsingFaultReason.AMBIGUOUS_SYMBOL, 3),
                            new Pair<>(GrammarParsingFaultReason.SYMBOL_EPSILON_MIX, 1))
                });
    }

    @Test
    public void testAlphabetInference() {
        try {
            reader.read("x A X b");
        } catch (IncorrectParseInputException e) {
            assertEquals(new SententialForm<>(xT, A_NT, X_NT, bT), e.getFaultMapping().getOutput());

            AlphabetInferenceFaultCollection alphabetFaultCollection =
                    (AlphabetInferenceFaultCollection)
                            e.getFaultMapping().get(AlphabetInferenceFaultCollection.class);
            assertEquals(
                    2,
                    alphabetFaultCollection.getFaults().stream()
                            .filter(
                                    fault ->
                                            fault.getReason()
                                                    == AlphabetInferenceFaultReason
                                                            .SYMBOL_NOT_ALLOWED)
                            .count());
        }

        try {
            reader.read("x A eps X b eps 1");
        } catch (IncorrectParseInputException e) {
            assertEquals(new SententialForm<>(xT, A_NT, X_NT, bT), e.getFaultMapping().getOutput());

            AlphabetInferenceFaultCollection alphabetFaultCollection =
                    (AlphabetInferenceFaultCollection)
                            e.getFaultMapping().get(AlphabetInferenceFaultCollection.class);
            assertEquals(
                    2,
                    alphabetFaultCollection.getFaults().stream()
                            .filter(
                                    fault ->
                                            fault.getReason()
                                                    == AlphabetInferenceFaultReason
                                                            .SYMBOL_NOT_ALLOWED)
                            .count());

            ParsingFaultCollection parsingFaultCollection =
                    (ParsingFaultCollection) e.getFaultMapping().get(ParsingFaultCollection.class);
            assertEquals(
                    1,
                    parsingFaultCollection.getFaults().stream()
                            .filter(
                                    fault ->
                                            fault.getReason()
                                                    == GrammarParsingFaultReason.AMBIGUOUS_SYMBOL)
                            .count());
            assertEquals(
                    1,
                    parsingFaultCollection.getFaults().stream()
                            .filter(
                                    fault ->
                                            fault.getReason()
                                                    == GrammarParsingFaultReason.SYMBOL_EPSILON_MIX)
                            .count());
        }
    }
}

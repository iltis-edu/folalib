package de.tudortmund.cs.iltis.folalib.io.reader.regex;

import de.tudortmund.cs.iltis.folalib.expressions.regular.*;
import de.tudortmund.cs.iltis.folalib.io.ParserTestRig;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultReason;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.*;
import de.tudortmund.cs.iltis.folalib.io.parser.regex.fault.RegexParsingFaultReason;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import de.tudortmund.cs.iltis.utils.io.parser.general.GeneralParsingFaultReason;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.RegularSymbolSplittingPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.junit.BeforeClass;

public class RegularExpressionReaderTest
        extends ParserTestRig<RegularExpression<IndexedSymbol>, RegularExpression<IndexedSymbol>> {

    @BeforeClass
    public static void initTestRig() {
        Function<RegularExpression<IndexedSymbol>, RegularExpression<IndexedSymbol>>
                returnUnchanged = input -> input;

        IndexedSymbol a = new IndexedSymbol("a");
        IndexedSymbol b = new IndexedSymbol("b");
        IndexedSymbol c = new IndexedSymbol("c");
        IndexedSymbol d = new IndexedSymbol("d");
        IndexedSymbol e = new IndexedSymbol("e");
        IndexedSymbol p = new IndexedSymbol("p");
        IndexedSymbol s = new IndexedSymbol("s");

        final Alphabet<IndexedSymbol> alphabet = new Alphabet<>(a, b, c, d, e, p, s);
        final Alphabet<IndexedSymbol> domainForRange =
                new Alphabet<>(
                        a,
                        b,
                        c,
                        d,
                        e,
                        new IndexedSymbol("f"),
                        new IndexedSymbol("u"),
                        p,
                        s,
                        new IndexedSymbol("z"));

        RegularExpressionReader readerStandard =
                new RegularExpressionReader(
                        RegularExpressionReaderProperties.createDefault(
                                RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                                new GivenAlphabetPolicy(alphabet),
                                domainForRange,
                                true));
        RegularExpressionReader readerExtended =
                new RegularExpressionReader(
                        RegularExpressionReaderProperties.createDefaultExtendedRegEx(
                                RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                                new GivenAlphabetPolicy(alphabet),
                                domainForRange,
                                true));

        positives = new ArrayList<>();
        negatives = new ArrayList<>();

        /*-----------------------------------------*\
         + POSITIVES                               +
        \*-----------------------------------------*/

        // test general
        positives.add(
                new Object[] {
                    readerStandard,
                    "ab* + a* + epse + eps + ((a+b)*c)* +e ps",
                    returnUnchanged,
                    new Symbol<>(alphabet, a)
                            .concat(new Symbol<>(alphabet, b).star())
                            .or(
                                    new Symbol<>(alphabet, a).star(),
                                    new Concatenation<>(
                                            new Symbol<>(alphabet, e),
                                            new Symbol<>(alphabet, p),
                                            new Symbol<>(alphabet, s),
                                            new Symbol<>(alphabet, e)),
                                    new EmptyWord<>(alphabet),
                                    new Symbol<>(alphabet, a)
                                            .or(new Symbol<>(alphabet, b))
                                            .star()
                                            .concat(new Symbol<>(alphabet, c))
                                            .star(),
                                    new Concatenation<>(
                                            new Symbol<>(alphabet, e),
                                            new Symbol<>(alphabet, p),
                                            new Symbol<>(alphabet, s)))
                });

        // test extended
        positives.add(
                new Object[] {
                    readerExtended,
                    "((ab⁺ + (ab)*)? c{4}d {2,50}+[a- e])+[e-a]⁺ + a⁺*?",
                    returnUnchanged,
                    new Symbol<>(alphabet, a)
                            .concat(new Symbol<>(alphabet, b).plus())
                            .or(
                                    new Concatenation<>(
                                                    new Symbol<>(alphabet, a),
                                                    new Symbol<>(alphabet, b))
                                            .star())
                            .optional()
                            .concat(
                                    new Symbol<>(alphabet, c).repetition(4),
                                    new Symbol<>(alphabet, d).repetition(2, 50))
                            .or(Range.from(alphabet, a, e))
                            .or(
                                    Range.from(alphabet, e, a).plus(),
                                    new Symbol<>(a).plus().star().optional())
                });

        // test fixed repetition
        positives.add(
                new Object[] {
                    readerExtended,
                    "a^3",
                    returnUnchanged,
                    new Repetition<>(new Symbol<>(alphabet, a), 3)
                });

        positives.add(
                new Object[] {
                    readerExtended,
                    "a ^ 3",
                    returnUnchanged,
                    new Repetition<>(new Symbol<>(alphabet, a), 3)
                });

        positives.add(
                new Object[] {
                    readerExtended,
                    "a ^ 3 b",
                    returnUnchanged,
                    new Repetition<>(new Symbol<>(alphabet, a), 3).concat(new Symbol<>(alphabet, b))
                });

        positives.add(
                new Object[] {
                    readerExtended,
                    "a ^ 345 b",
                    returnUnchanged,
                    new Repetition<>(new Symbol<>(alphabet, a), 345)
                            .concat(new Symbol<>(alphabet, b))
                });

        /*-----------------------------------------*\
         + NEGATIVES                               +
        \*-----------------------------------------*/

        // test cardinality operator after alternation fault
        negatives.add(
                new Object[] {
                    readerExtended,
                    "a+?*+b",
                    returnUnchanged,
                    readerStandard.read("a+emptyset+b"),
                    List.of(
                            new Pair<>(RegexParsingFaultReason.NO_ALTERNATIVE, 1),
                            new Pair<>(
                                    RegexParsingFaultReason
                                            .CARDINALITY_OPERATOR_AFTER_ALTERNATION_SYMBOL,
                                    1))
                });

        // test no alternative fault
        negatives.add(
                new Object[] {
                    readerStandard,
                    "++b",
                    returnUnchanged,
                    readerStandard.read("b"),
                    List.of(new Pair<>(RegexParsingFaultReason.NO_ALTERNATIVE, 1))
                });

        negatives.add(
                new Object[] {
                    readerStandard,
                    "++",
                    returnUnchanged,
                    readerStandard.read("emptyset"),
                    List.of(new Pair<>(RegexParsingFaultReason.NO_ALTERNATIVE, 1))
                });

        // test start with cardinality operator fault

        // This should work because we are using standard regex, not extended regex
        Alphabet<IndexedSymbol> alphabetLocal =
                Alphabets.unionOf(alphabet, Alphabets.indexedSymbolAlphabet("⁺"));
        positives.add(
                new Object[] {
                    new RegularExpressionReader(
                            RegularExpressionReaderProperties.createDefault(alphabetLocal)),
                    "⁺*a",
                    returnUnchanged,
                    new Symbol<>(alphabetLocal, new IndexedSymbol("⁺"))
                            .star()
                            .concat(new Symbol<>(alphabetLocal, a))
                });

        negatives.add(
                new Object[] {
                    readerExtended,
                    "⁺*ab",
                    returnUnchanged,
                    readerStandard.read("ab"),
                    List.of(new Pair<>(RegexParsingFaultReason.START_WITH_CARDINALITY_OPERATOR, 1))
                });

        negatives.add(
                new Object[] {
                    readerExtended,
                    "{8}?",
                    returnUnchanged,
                    readerStandard.read("emptyset"),
                    List.of(new Pair<>(RegexParsingFaultReason.START_WITH_CARDINALITY_OPERATOR, 1))
                });

        // test empty regex
        negatives.add(
                new Object[] {
                    readerStandard,
                    "",
                    returnUnchanged,
                    readerStandard.read("emptyset"),
                    List.of(new Pair<>(RegexParsingFaultReason.EMPTY_REGEX, 1))
                });
        // 2x Tab + 3x Space
        negatives.add(
                new Object[] {
                    readerStandard,
                    "         ",
                    returnUnchanged,
                    readerStandard.read("emptyset"),
                    List.of(new Pair<>(RegexParsingFaultReason.EMPTY_REGEX, 1))
                });

        // test non-numeric repetition operands fault
        negatives.add(
                new Object[] {
                    readerExtended,
                    "a{2,3l5}",
                    returnUnchanged,
                    null, // not repairable because bailing out in lexer is usually enabled. The
                    // non-numeric token leads directly to bailing out instead of getting
                    // discarded. For a test case with disabled bailing out see below
                    List.of(new Pair<>(RegexParsingFaultReason.REPETITION_OPERAND_NOT_NUMERIC, 1))
                });

        negatives.add(
                new Object[] {
                    readerExtended,
                    "a{2i,35}",
                    returnUnchanged,
                    null, // not repairable because bailing out in lexer is usually enabled. The
                    // non-numeric token leads directly to bailing out instead of getting
                    // discarded. For a test case with disabled bailing out see below
                    List.of(new Pair<>(RegexParsingFaultReason.REPETITION_OPERAND_NOT_NUMERIC, 1))
                });

        negatives.add(
                new Object[] {
                    readerExtended,
                    "a{2i,u35}",
                    returnUnchanged,
                    null, // not repairable because bailing out in lexer is usually enabled. The
                    // non-numeric token leads directly to bailing out instead of getting
                    // discarded. For a test case with disabled bailing out see below
                    List.of(new Pair<>(RegexParsingFaultReason.REPETITION_OPERAND_NOT_NUMERIC, 2))
                });

        negatives.add(
                new Object[] {
                    readerExtended,
                    "a{i,u}",
                    returnUnchanged,
                    null, // not repairable
                    List.of(new Pair<>(RegexParsingFaultReason.REPETITION_OPERAND_NOT_NUMERIC, 2))
                });

        negatives.add(
                new Object[] {
                    readerExtended,
                    "a{u7}",
                    returnUnchanged,
                    null, // not repairable because bailing out in lexer is usually enabled. The
                    // non-numeric token leads directly to bailing out instead of getting
                    // discarded. For a test case with disabled bailing out see below
                    List.of(new Pair<>(RegexParsingFaultReason.REPETITION_OPERAND_NOT_NUMERIC, 1))
                });

        negatives.add(
                new Object[] {
                    readerExtended,
                    "a{,u}",
                    returnUnchanged,
                    null, // not repairable
                    List.of(new Pair<>(RegexParsingFaultReason.REPETITION_OPERAND_NOT_NUMERIC, 1))
                });

        // test extended regex with whitespaces in repetition operands
        negatives.add(
                new Object[] {
                    readerExtended,
                    "a{7 8   5, 8 4 3}",
                    returnUnchanged,
                    null, // not repairable
                    List.of(new Pair<>(GeneralParsingFaultReason.VARIOUS, 1))
                });

        // test ambiguous repetition definition
        negatives.add(
                new Object[] {
                    readerExtended,
                    "a{5 23}",
                    returnUnchanged,
                    new Repetition<>(new Symbol<>(alphabet, a), 5, 23),
                    List.of(new Pair<>(RegexParsingFaultReason.AMBIGUOUS_REPETITION_DEFINITION, 1))
                });

        // test symbol not in alphabet
        negatives.add(
                new Object[] {
                    readerExtended,
                    "f",
                    returnUnchanged,
                    new Symbol<>(
                            Alphabets.unionOf(alphabet, Alphabets.indexedSymbolAlphabet("f")),
                            new IndexedSymbol("f")),
                    List.of(new Pair<>(AlphabetInferenceFaultReason.SYMBOL_NOT_ALLOWED, 1))
                });

        // test range operand not in alphabet
        negatives.add(
                new Object[] {
                    readerExtended,
                    "[a-  f]",
                    returnUnchanged,
                    Range.from(
                            Alphabets.unionOf(alphabet, Alphabets.indexedSymbolAlphabet("f")),
                            a,
                            new IndexedSymbol("f")),
                    List.of(new Pair<>(AlphabetInferenceFaultReason.SYMBOL_NOT_ALLOWED, 1))
                });

        negatives.add(
                new Object[] {
                    readerExtended,
                    "[u-  f]",
                    returnUnchanged,
                    Range.from(
                            Alphabets.unionOf(alphabet, Alphabets.indexedSymbolAlphabet("fu")),
                            new IndexedSymbol("u"),
                            new IndexedSymbol("f")),
                    List.of(new Pair<>(AlphabetInferenceFaultReason.SYMBOL_NOT_ALLOWED, 2))
                });

        negatives.add(
                new Object[] {
                    readerExtended,
                    "[u-  f][f-z]",
                    returnUnchanged,
                    Range.from(
                                    Alphabets.unionOf(
                                            alphabet, Alphabets.indexedSymbolAlphabet("fuz")),
                                    new IndexedSymbol("u"),
                                    new IndexedSymbol("f"))
                            .concat(
                                    Range.from(
                                            Alphabets.unionOf(
                                                    alphabet,
                                                    Alphabets.indexedSymbolAlphabet("fuz")),
                                            new IndexedSymbol("f"),
                                            new IndexedSymbol("z"))),
                    List.of(new Pair<>(AlphabetInferenceFaultReason.SYMBOL_NOT_ALLOWED, 3))
                });

        // test fixed repetition not numeric
        negatives.add(
                new Object[] {
                    readerExtended,
                    "a^b",
                    returnUnchanged,
                    null, // not repairable
                    List.of(new Pair<>(RegexParsingFaultReason.REPETITION_OPERAND_NOT_NUMERIC, 1))
                });

        negatives.add(
                new Object[] {
                    readerExtended,
                    "a ^ b",
                    returnUnchanged,
                    null, // not repairable
                    List.of(new Pair<>(RegexParsingFaultReason.REPETITION_OPERAND_NOT_NUMERIC, 1))
                });

        negatives.add(
                new Object[] {
                    readerExtended,
                    "a ^ b a",
                    returnUnchanged,
                    null, // not repairable
                    List.of(new Pair<>(RegexParsingFaultReason.REPETITION_OPERAND_NOT_NUMERIC, 1))
                });

        // test all faults at once with one not allowed symbol
        Alphabet<IndexedSymbol> helperAlphabet =
                Alphabets.unionOf(alphabet, Alphabets.indexedSymbolAlphabet("f"));
        negatives.add(
                new Object[] {
                    readerExtended,
                    "⁺a+++?⁺b{5}[e-f]",
                    returnUnchanged,
                    // This is equal because the alphabet inference fails here (because of the fault
                    // being thrown because of
                    // the f). Instead, the best guess alphabet is used which contains all specified
                    // symbols (a-e,p,s) plus
                    // the f because it is used in the range expression.
                    new Alternative<>(
                            new Symbol<>(helperAlphabet, a),
                            new EmptyWord<>(helperAlphabet),
                            new EmptyWord<>(helperAlphabet),
                            new Symbol<>(helperAlphabet, b)
                                    .repetition(5)
                                    .concat(Range.from(helperAlphabet, e, new IndexedSymbol("f")))),
                    List.of(
                            new Pair<>(RegexParsingFaultReason.START_WITH_CARDINALITY_OPERATOR, 1),
                            new Pair<>(RegexParsingFaultReason.NO_ALTERNATIVE, 2),
                            new Pair<>(
                                    RegexParsingFaultReason
                                            .CARDINALITY_OPERATOR_AFTER_ALTERNATION_SYMBOL,
                                    1),
                            new Pair<>(AlphabetInferenceFaultReason.SYMBOL_NOT_ALLOWED, 1))
                });

        // test all faults at once with only allowed symbols
        negatives.add(
                new Object[] {
                    readerExtended,
                    "⁺a+++?⁺b{5}[a-c]",
                    returnUnchanged,
                    new Alternative<>(
                            new Symbol<>(alphabet, a),
                            new EmptyWord<>(alphabet),
                            new EmptyWord<>(alphabet),
                            new Symbol<>(alphabet, b)
                                    .repetition(5)
                                    .concat(Range.from(alphabet, a, c))),
                    List.of(
                            new Pair<>(RegexParsingFaultReason.NO_ALTERNATIVE, 2),
                            new Pair<>(RegexParsingFaultReason.START_WITH_CARDINALITY_OPERATOR, 1),
                            new Pair<>(
                                    RegexParsingFaultReason
                                            .CARDINALITY_OPERATOR_AFTER_ALTERNATION_SYMBOL,
                                    1))
                });

        // Check discard of non-numeric tokens in repetition
        RegularExpressionReader nonLexerBailOutReader =
                new RegularExpressionReader(
                        RegularExpressionReaderProperties.createDefaultExtendedRegEx());
        nonLexerBailOutReader.setBailOutInLexer(false);
        negatives.add(
                new Object[] {
                    nonLexerBailOutReader,
                    "a{a 4 fg, 6 g}",
                    returnUnchanged,
                    new Repetition<>(new Symbol<>(a), 4, 6),
                    List.of(new Pair<>(RegexParsingFaultReason.REPETITION_OPERAND_NOT_NUMERIC, 3))
                });
    }
}

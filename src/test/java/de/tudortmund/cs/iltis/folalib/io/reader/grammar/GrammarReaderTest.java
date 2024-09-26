package de.tudortmund.cs.iltis.folalib.io.reader.grammar;

import static org.junit.Assert.fail;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.C0GrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.io.ParserTestRig;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultReason;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.GivenAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferFromRegexPolicy;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.fault.GrammarParsingFaultReason;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.standard.InferFromRegexInputConverter;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol.standard.GivenSymbolStartSymbolDerivationStrategy;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import de.tudortmund.cs.iltis.utils.io.parser.error.IncorrectParseInputException;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultCollection;
import de.tudortmund.cs.iltis.utils.io.parser.general.GeneralParsingFaultReason;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.RegularSymbolSplittingPolicy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import org.junit.BeforeClass;
import org.junit.Test;

public class GrammarReaderTest
        extends ParserTestRig<
                Grammar<IndexedSymbol, IndexedSymbol, Production<IndexedSymbol, IndexedSymbol>>,
                Grammar<IndexedSymbol, IndexedSymbol, Production<IndexedSymbol, IndexedSymbol>>> {

    @BeforeClass
    public static void initTestRig() {
        Function<
                        Grammar<
                                IndexedSymbol,
                                IndexedSymbol,
                                Production<IndexedSymbol, IndexedSymbol>>,
                        Grammar<
                                IndexedSymbol,
                                IndexedSymbol,
                                Production<IndexedSymbol, IndexedSymbol>>>
                returnUnchanged = input -> input;

        IndexedSymbol a = new IndexedSymbol("a");
        IndexedSymbol b = new IndexedSymbol("b");
        IndexedSymbol c = new IndexedSymbol("c");
        IndexedSymbol d = new IndexedSymbol("d");

        IndexedSymbol startNonTerminal = new IndexedSymbol("S");
        IndexedSymbol A = new IndexedSymbol("A");
        IndexedSymbol B = new IndexedSymbol("B");
        IndexedSymbol C = new IndexedSymbol("C");

        Alphabet<IndexedSymbol> terminals = new Alphabet<>(a, b, c, d);
        Alphabet<IndexedSymbol> nonTerminals = new Alphabet<>(startNonTerminal, A, B, C);

        GrammarReader reader =
                new GrammarReader(
                        GrammarReaderProperties.createDefault(
                                RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                                new GivenAlphabetPolicy(terminals),
                                new GivenAlphabetPolicy(nonTerminals),
                                new InferFromRegexInputConverter("[a-z]", "[A-Z]"),
                                new GivenSymbolStartSymbolDerivationStrategy(startNonTerminal),
                                true));

        positives = new ArrayList<>();
        negatives = new ArrayList<>();

        /*-----------------------------------------*\
         + POSITIVES                               +
        \*-----------------------------------------*/

        // test general
        positives.add(
                new Object[] {
                    reader,
                    "S -> AB,\n A->CB, B -> Bb | b, CB -> cC, C->d,\n, Bd -> db, Ba  -> C",
                    returnUnchanged,
                    new C0GrammarBuilder<>(terminals, nonTerminals)
                            .withStartSymbol(startNonTerminal)
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .nt(A, B)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(A)
                            .finish()
                            .rhs()
                            .nt(C, B)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(B)
                            .finish()
                            .rhs()
                            .nt(B)
                            .t(b)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(B)
                            .finish()
                            .rhs()
                            .t(b)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(C, B)
                            .finish()
                            .rhs()
                            .t(c)
                            .nt(C)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(C)
                            .finish()
                            .rhs()
                            .t(d)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(B)
                            .t(d)
                            .finish()
                            .rhs()
                            .t(d, b)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(B)
                            .t(a)
                            .finish()
                            .rhs()
                            .nt(C)
                            .finish()
                            .finish()
                            .build()
                            .unwrap()
                });

        // test infer from regex strategy
        positives.add(
                new Object[] {
                    new GrammarReader(GrammarReaderProperties.createDefault()),
                    "S->a",
                    returnUnchanged,
                    new C0GrammarBuilder<>(new Alphabet<>(a), new Alphabet<>(startNonTerminal))
                            .withStartSymbol(startNonTerminal)
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .t(a)
                            .finish()
                            .finish()
                            .build()
                            .unwrap()
                });

        // test epsilon on both sides (should be possible because there are no restrictions in the
        // general grammar reader
        positives.add(
                new Object[] {
                    new GrammarReader(GrammarReaderProperties.createDefault()),
                    "ε-> eps",
                    returnUnchanged,
                    new Grammar<>(
                            new Alphabet<>(
                                    new IndexedSymbol("+")), // Because of default inference policy
                            new Alphabet<>(
                                    new IndexedSymbol("#")), // Because of default inference policy
                            startNonTerminal,
                            List.of(
                                    new Production<>(
                                            new SententialForm<>(), new SententialForm<>())))
                });

        /*-----------------------------------------*\
         + NEGATIVES                               +
        \*-----------------------------------------*/

        // test empty input
        negatives.add(
                new Object[] {
                    reader,
                    "",
                    returnUnchanged,
                    new C0GrammarBuilder<>(terminals, nonTerminals)
                            .withStartSymbol(startNonTerminal)
                            .build()
                            .unwrap(),
                    List.of(new Pair<>(GrammarParsingFaultReason.EMPTY_GRAMMAR, 1))
                });

        // test empty production
        negatives.add(
                new Object[] {
                    reader,
                    "S -> a, ->",
                    returnUnchanged,
                    new Grammar<>(
                            terminals,
                            nonTerminals,
                            startNonTerminal,
                            List.of(
                                    new Production<>(
                                            new SententialForm<>(
                                                    new GrammarSymbol.NonTerminal<>(
                                                            startNonTerminal)),
                                            new SententialForm<>(new GrammarSymbol.Terminal<>(a))),
                                    new Production<>(
                                            new SententialForm<>(), new SententialForm<>()))),
                    List.of(new Pair<>(GrammarParsingFaultReason.INCOMPLETE_PRODUCTION, 1))
                });

        // test missing left side
        negatives.add(
                new Object[] {
                    reader,
                    " -> a",
                    returnUnchanged,
                    new Grammar<>(
                            terminals,
                            nonTerminals,
                            startNonTerminal,
                            List.of(
                                    new Production<>(
                                            new SententialForm<>(),
                                            new SententialForm<>(
                                                    new GrammarSymbol.Terminal<>(a))))),
                    List.of(new Pair<>(GrammarParsingFaultReason.INCOMPLETE_PRODUCTION, 1))
                });
        negatives.add(
                new Object[] {
                    reader,
                    " -> || a ||",
                    returnUnchanged,
                    new Grammar<>(
                            terminals,
                            nonTerminals,
                            startNonTerminal,
                            List.of(
                                    new Production<>(
                                            new SententialForm<>(),
                                            new SententialForm<>(
                                                    new GrammarSymbol.Terminal<>(a))))),
                    List.of(
                            new Pair<>(GrammarParsingFaultReason.INCOMPLETE_PRODUCTION, 1),
                            new Pair<>(GrammarParsingFaultReason.ABUNDANT_RIGHT_SIDE_SEPARATORS, 2))
                });

        // test missing right side
        negatives.add(
                new Object[] {
                    reader,
                    "S -> ",
                    returnUnchanged,
                    new C0GrammarBuilder<>(terminals, nonTerminals)
                            .withStartSymbol(startNonTerminal)
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .finish()
                            .build()
                            .unwrap(),
                    List.of(new Pair<>(GrammarParsingFaultReason.INCOMPLETE_PRODUCTION, 1))
                });

        // test invalid (alphabet inference), but recognized (at categorized as terminals and
        // non-terminals) symbols
        negatives.add(
                new Object[] {
                    reader,
                    "S -> aB R, Z -> b",
                    returnUnchanged,
                    new C0GrammarBuilder<>(
                                    terminals,
                                    Alphabets.unionOf(
                                            nonTerminals, Alphabets.indexedSymbolAlphabet("RZ")))
                            .withStartSymbol(startNonTerminal)
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .t(a)
                            .nt(B, new IndexedSymbol("R"))
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(new IndexedSymbol("Z"))
                            .finish()
                            .rhs()
                            .t(b)
                            .finish()
                            .finish()
                            .build()
                            .unwrap(),
                    List.of(new Pair<>(AlphabetInferenceFaultReason.SYMBOL_NOT_ALLOWED, 2))
                });

        // test abundant right side operators and ends with separator
        negatives.add(
                new Object[] {
                    reader,
                    "S -> a || | B ||",
                    returnUnchanged,
                    new C0GrammarBuilder<>(terminals, nonTerminals)
                            .withStartSymbol(startNonTerminal)
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .t(a)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .nt(B)
                            .finish()
                            .finish()
                            .build()
                            .unwrap(),
                    List.of(
                            new Pair<>(GrammarParsingFaultReason.ABUNDANT_RIGHT_SIDE_SEPARATORS, 2),
                            new Pair<>(
                                    GrammarParsingFaultReason.LINE_ENDS_WITH_RIGHT_SIDE_SEPARATOR,
                                    1))
                });

        // test starts with separator
        negatives.add(
                new Object[] {
                    reader,
                    "S -> | a | B",
                    returnUnchanged,
                    new C0GrammarBuilder<>(terminals, nonTerminals)
                            .withStartSymbol(startNonTerminal)
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .t(a)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .nt(B)
                            .finish()
                            .finish()
                            .build()
                            .unwrap(),
                    List.of(
                            new Pair<>(
                                    GrammarParsingFaultReason.LINE_STARTS_WITH_RIGHT_SIDE_SEPARATOR,
                                    1))
                });

        // test starts and ends with separator
        negatives.add(
                new Object[] {
                    reader,
                    "S -> | a | B |",
                    returnUnchanged,
                    new C0GrammarBuilder<>(terminals, nonTerminals)
                            .withStartSymbol(startNonTerminal)
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .t(a)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .nt(B)
                            .finish()
                            .finish()
                            .build()
                            .unwrap(),
                    List.of(
                            new Pair<>(
                                    GrammarParsingFaultReason.LINE_STARTS_WITH_RIGHT_SIDE_SEPARATOR,
                                    1),
                            new Pair<>(
                                    GrammarParsingFaultReason.LINE_ENDS_WITH_RIGHT_SIDE_SEPARATOR,
                                    1))
                });

        // test missing production arrow
        negatives.add(
                new Object[] {
                    reader,
                    "S -> a, | a S||, S -> B, |a",
                    returnUnchanged,
                    new C0GrammarBuilder<>(terminals, nonTerminals)
                            .withStartSymbol(startNonTerminal)
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .t(a)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .nt(B)
                            .finish()
                            .finish()
                            .build()
                            .unwrap(),
                    List.of(new Pair<>(GrammarParsingFaultReason.MISSING_PRODUCTION_ARROW, 2))
                });

        // test abundant epsilons
        negatives.add(
                new Object[] {
                    reader,
                    "S -> a, S -> εε, ε eps -> a",
                    returnUnchanged,
                    new Grammar<>(
                            terminals,
                            nonTerminals,
                            startNonTerminal,
                            List.of(
                                    new Production<>(
                                            new SententialForm<>(
                                                    new GrammarSymbol.NonTerminal<>(
                                                            startNonTerminal)),
                                            new SententialForm<>(new GrammarSymbol.Terminal<>(a))),
                                    new Production<>(
                                            new SententialForm<>(
                                                    new GrammarSymbol.NonTerminal<>(
                                                            startNonTerminal)),
                                            new SententialForm<>()),
                                    new Production<>(
                                            new SententialForm<>(),
                                            new SententialForm<>(
                                                    new GrammarSymbol.Terminal<>(a))))),
                    List.of(new Pair<>(GrammarParsingFaultReason.ABUNDANT_EPSILONS, 2))
                });

        // test symbol - epsilon mix in sentential form (all epsilons in mixed sentential forms are
        // simply removed)
        negatives.add(
                new Object[] {
                    reader,
                    "S eps -> eps a, A -> εaε | a εε eps b, A -> ε",
                    returnUnchanged,
                    new C0GrammarBuilder<>(terminals, nonTerminals)
                            .withStartSymbol(startNonTerminal)
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .t(a)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(A)
                            .finish()
                            .rhs()
                            .t(a)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(A)
                            .finish()
                            .rhs()
                            .t(a, b)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(A)
                            .finish()
                            .finish()
                            .build()
                            .unwrap(),
                    List.of(new Pair<>(GrammarParsingFaultReason.SYMBOL_EPSILON_MIX, 4))
                });

        // test right side separator on lhs -> bail out
        negatives.add(
                new Object[] {
                    reader,
                    "S | A || B | -> a",
                    returnUnchanged,
                    null,
                    List.of(new Pair<>(GrammarParsingFaultReason.RIGHT_SIDE_SEPARATOR_ON_LHS, 1))
                });
        negatives.add(
                new Object[] {
                    reader,
                    "| S -> a",
                    returnUnchanged,
                    null,
                    List.of(new Pair<>(GrammarParsingFaultReason.RIGHT_SIDE_SEPARATOR_ON_LHS, 1))
                });
        negatives.add(
                new Object[] {
                    reader,
                    "S | -> a",
                    returnUnchanged,
                    null,
                    List.of(new Pair<>(GrammarParsingFaultReason.RIGHT_SIDE_SEPARATOR_ON_LHS, 1))
                });

        // test multiple production arrows in one line
        negatives.add(
                new Object[] {
                    reader,
                    "S -> -> a, -> S | -> |b, | S -> S -> b ->",
                    returnUnchanged,
                    new Grammar<>(
                            terminals,
                            nonTerminals,
                            startNonTerminal,
                            new LinkedList<>()), // empty grammar
                    List.of(new Pair<>(GrammarParsingFaultReason.MULTIPLE_PRODUCTION_ARROWS, 3))
                });

        // test everything at once
        negatives.add(
                new Object[] {
                    reader,
                    "S -> a ,, B->aCZd || b,,, C -> A | B | C |, Sa|b\n, B -> εεε, A -> ε b, S -> a -> B",
                    returnUnchanged,
                    new C0GrammarBuilder<>(
                                    terminals,
                                    Alphabets.unionOf(
                                            nonTerminals, Alphabets.indexedSymbolAlphabet("Z")))
                            .withStartSymbol(startNonTerminal)
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .t(a)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(B)
                            .finish()
                            .rhs()
                            .t(a)
                            .nt(C, new IndexedSymbol("Z"))
                            .t(d)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(B)
                            .finish()
                            .rhs()
                            .t(b)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(C)
                            .finish()
                            .rhs()
                            .nt(A)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(C)
                            .finish()
                            .rhs()
                            .nt(B)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(C)
                            .finish()
                            .rhs()
                            .nt(C)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(B)
                            .finish()
                            .finish()
                            .withProduction()
                            .lhs()
                            .nt(A)
                            .finish()
                            .rhs()
                            .t(b)
                            .finish()
                            .finish()
                            .build()
                            .unwrap(),
                    List.of(
                            new Pair<>(AlphabetInferenceFaultReason.SYMBOL_NOT_ALLOWED, 1),
                            new Pair<>(GrammarParsingFaultReason.ABUNDANT_RIGHT_SIDE_SEPARATORS, 1),
                            new Pair<>(
                                    GrammarParsingFaultReason.LINE_ENDS_WITH_RIGHT_SIDE_SEPARATOR,
                                    1),
                            new Pair<>(GrammarParsingFaultReason.MISSING_PRODUCTION_ARROW, 1),
                            new Pair<>(GrammarParsingFaultReason.ABUNDANT_EPSILONS, 1),
                            new Pair<>(GrammarParsingFaultReason.SYMBOL_EPSILON_MIX, 1),
                            new Pair<>(GrammarParsingFaultReason.MULTIPLE_PRODUCTION_ARROWS, 1))
                });

        // test infer from regex strategy
        negatives.add(
                new Object[] {
                    new GrammarReader(
                            GrammarReaderProperties.createDefault(
                                    RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                                    new InferFromRegexPolicy(
                                            "a|1", Alphabets.indexedSymbolAlphabet("#")),
                                    new InferFromRegexPolicy(
                                            "S|1", Alphabets.indexedSymbolAlphabet("#")),
                                    new InferFromRegexInputConverter("[a-z]", "[A-Z]"),
                                    new GivenSymbolStartSymbolDerivationStrategy(startNonTerminal),
                                    true)),
                    "Sb -> a1",
                    returnUnchanged,
                    new C0GrammarBuilder<>(new Alphabet<>(a, b), new Alphabet<>(startNonTerminal))
                            .withStartSymbol(startNonTerminal)
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .t(b)
                            .finish()
                            .rhs()
                            .t(a)
                            .finish()
                            .finish()
                            .build()
                            .unwrap(),
                    List.of(
                            new Pair<>(AlphabetInferenceFaultReason.SYMBOL_NOT_ALLOWED, 1),
                            new Pair<>(GrammarParsingFaultReason.AMBIGUOUS_SYMBOL, 1))
                });

        // test unknown sentential form (all symbols are ambiguous)
        negatives.add(
                new Object[] {
                    new GrammarReader(
                            GrammarReaderProperties.createDefault(
                                    RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                                    new InferFromRegexPolicy(
                                            "a|1", Alphabets.indexedSymbolAlphabet("#")),
                                    new InferFromRegexPolicy(
                                            "S|1", Alphabets.indexedSymbolAlphabet("#")),
                                    new InferFromRegexInputConverter("[a-z]", "[A-Z]"),
                                    new GivenSymbolStartSymbolDerivationStrategy(startNonTerminal),
                                    true)),
                    "S -> 12",
                    returnUnchanged,
                    null,
                    List.of(
                            new Pair<>(GrammarParsingFaultReason.UNKNOWN_SENTENTIAL_FORM, 1),
                            new Pair<>(GrammarParsingFaultReason.AMBIGUOUS_SYMBOL, 2))
                });

        // test ambiguous symbol
        negatives.add(
                new Object[] {
                    new GrammarReader(
                            GrammarReaderProperties.createDefault(
                                    RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                                    new InferFromRegexPolicy(
                                            "a|1", Alphabets.indexedSymbolAlphabet("#")),
                                    new InferFromRegexPolicy(
                                            "S|1", Alphabets.indexedSymbolAlphabet("#")),
                                    new InferFromRegexInputConverter("[a-z]", "[A-Z]"),
                                    new GivenSymbolStartSymbolDerivationStrategy(startNonTerminal),
                                    true)),
                    "S -> 1a",
                    returnUnchanged,
                    new C0GrammarBuilder<>(new Alphabet<>(a), new Alphabet<>(startNonTerminal))
                            .withStartSymbol(startNonTerminal)
                            .withProduction()
                            .lhs()
                            .nt(startNonTerminal)
                            .finish()
                            .rhs()
                            .t(a)
                            .finish()
                            .finish()
                            .build()
                            .unwrap(),
                    List.of(new Pair<>(GrammarParsingFaultReason.AMBIGUOUS_SYMBOL, 1))
                });
    }

    @Test
    public void testRandomInputString() {
        // This monstrosity of a string is a randomly generated 5000 characters string and consists
        // of the symbols:
        // "S", "a", "ɛ", "|", "," and "->".
        // This test just tests if every combination of characters is covered by a fault rule in the
        // grammar. If no
        // GeneralParsingFaultReason#VARIOUS-fault occurs then we're good.
        // If some error rule in the visitor leads to bailing out it does not matter because a
        // VARIOUS fault would
        // occur earlier during parsing.

        try {
            new GrammarReader(GrammarReaderProperties.createDefault())
                    .read(
                            "SSɛɛ->,,aSɛ,SaaS,aɛa->|SSS->|a,ɛ,->a|aɛ|a,aɛ,ɛ|,->,,aS|ɛ|S|->||,aɛ|Sɛ,->ɛaɛɛɛ"
                                    + ",,,a|aS||ɛ,ɛSɛ->ɛS,aɛ,|,ɛ->||,SɛS|S,aɛ,ɛSa,||ɛ,ɛSS->,a,a->|a->SS->,->|aS,ɛaɛa"
                                    + "|,->->S|->->->|a,S|->aa,SɛɛSaɛ|ɛS,S,S,,a->ɛa,Sɛ,,,aaɛɛS->S->S,a->|,a||Sɛɛ->->,,Sa->"
                                    + "Sɛɛ|Sɛ->->,->->aɛ||,aS|S->,ɛ->->,ɛ->,SS,a->Sa|->a,ɛa,ɛ,,a|,Saɛ||Sɛ|a|->|,|Sɛa->,aS"
                                    + ",SɛɛaaSɛ->|,|,a->|ɛS->|,->|,->,aɛa|S,->->S|ɛ|ɛ->,ɛ|,aɛɛɛ->->S->->Sa,aS||ɛ,a->->S,->|ɛ"
                                    + "S,ɛ,|,,|,aaS,->->a->|aS,->->,||->Saɛɛa->a|->|->S|Sɛ|->ɛɛS|aS->a,S|,a|->aaS->,->->|,|,"
                                    + "ɛ->S,->aaS,|->ɛ->->->,a->|aS,|SS,|->ɛaS,S->ɛS->ɛɛɛaS,,S,,,|ɛ,a->||a|S|->S->|ɛS,|S|->"
                                    + ",|ɛ,S,||SɛS,a|ɛ||,S->aS,ɛS,Sɛ,ɛ->->|a->|SS||ɛaɛa->->ɛ,ɛaa->ɛ||Sɛ,ɛɛS|S,S,||a|"
                                    + "ɛɛ|->|->Sa->SɛS|->->,||a,,a,|->a,|->SɛɛaS,aɛ,,S,a->ɛS,->|,aɛaɛ|SɛaS->->,SS,->|,->,a"
                                    + "ɛa|a,SaSaɛɛ|->a|||ɛa|Sa|,|,->,|a,|ɛɛa->->ɛɛSɛSa,|->|->,,Sɛa|a,ɛS->ɛaaS,a|a,->a"
                                    + "ɛ->->S,SS|S,|,ɛ,aaSa->||Sɛa||a->->|->aa,,ɛ,,|->SS->->S|->aaa,->|S,,a->a|||->ɛ,->SS||"
                                    + "|ɛ,||||->ɛ->ɛ||S,ɛ,|a,ɛ->|ɛ,,,->,->Sa|aSɛSaaɛaaS|a->,aSSa|->,|a,S|S,aaS||Sɛa|"
                                    + "aɛ|a->|SɛS||->aS|ɛSS->aɛ,|ɛ|,aaSɛɛ|ɛ,||S->S,a->,aɛ->,||,ɛaɛ|ɛS,a,aSa,a|ɛSaɛɛ"
                                    + "ɛ,|->,ɛS->,,,|ɛ|->,Sɛ|->,,Sɛ|,,->|->S|aɛɛaa->->||,|->|S,aɛ|->,S,ɛ->|ɛ,aS,S|Sa,,ɛɛ"
                                    + "ɛaɛ->->|,->,->->a->->ɛɛɛSSɛaS->ɛ,->S->|ɛ,a,->->|a|ɛ,SaSSa,ɛ|S->,ɛS|ɛɛ,a->a|a|->ɛSaSɛɛ"
                                    + ",->ɛ|ɛa->,S,||ɛ||->ɛSaS->->||,aSS|->S||ɛɛS|S||ɛ->->ɛ|ɛɛ|ɛ|||->,|SS,,|aaɛ,ɛ|||ɛ->"
                                    + "->ɛaɛ,,S|->|Saɛ->Sɛ|->,->S|->|ɛ||,ɛaSɛɛ,aS|ɛɛɛ,S,,a|S|,S,|->ɛɛ->,a->aa||aɛ||,aS"
                                    + "|,ɛS|||aɛɛa||->->aaS||->ɛɛ->,,->|->->a,,->S||->,,a->->->Sɛ|ɛ->aɛ|,|ɛSa,->ɛ->Saɛ||S->,S"
                                    + "ɛɛS,->S|a,ɛ->Sɛ->SɛSɛ->a,ɛɛ|a,ɛa->aɛ->|a|ɛ->S->ɛS->S->ɛ->,->a->SS|SSSS|,a|->Sɛa|,S|S"
                                    + ",Sa->a->->,a,->->aa->->->Sɛ->S,->a->->->aɛɛ||ɛ->aSɛ->|Sɛ|->->a|->a|,Sɛɛ|->,|aSaa,a->,,,,aɛ"
                                    + "aa|S,,aa|,aS||a,->||ɛ||ɛ->ɛ->||,->,aSaa->||ɛaSaa|Sɛaa,,,|S|a|->a|SS|S|a->a,S,"
                                    + "a,|SS,S->->S->S->|->->ɛ,,ɛ|a,||aɛ|->,Sa,,aSS,aSSSɛ->|ɛS,ɛSSɛ,S|,|ɛ->ɛɛ|aSɛɛ|->S,"
                                    + "Sa->->|,|,Sa|->ɛaSa->->ɛ|SSɛSɛ->||S->S->,->||ɛɛ->SS|Saɛ|S,|SɛS->ɛSaaSS|aSɛS,|S|S|"
                                    + ",a,,ɛ,||Sɛ||->|ɛa|ɛɛSɛɛa|S|ɛ->S,S->|ɛɛaɛ,->->ɛaaɛa->->S->ɛ->SɛS->a||SS,ɛaa->S->a->a"
                                    + ",aa,S,->S->S->ɛɛa|||aS->|->ɛ->,a->Sɛa|aS,ɛ->|->a,SSɛS->->aɛ->,a->->aSS,S->,,||||S->a,,"
                                    + "->SɛS|a->SSaɛ,,|ɛS->ɛa|a,->->->aS->S|->->aaSɛ,->|S,,,,a->,aaaSɛ->|a|||S,|->|->->SS,aS"
                                    + ",,SaSɛ->ɛa->->aS|->ɛɛ|S,,a->aaSaSɛ|->a,S,,|aS|,||aaɛ|ɛɛ,->a||S->->->ɛɛɛS|->,aa->ɛS"
                                    + "||->|,ɛaɛ->S,Sɛ|a,,S->S->aS|->SSS->a->->|ɛ->ɛ->ɛS->,->|Sɛ,S,Sɛɛ,S,,|aS|S|Sa|->,aa|->"
                                    + "aɛ|a,aSS,->,a->aɛ->,aa,->aaɛS|,ɛ,,,ɛa->S,Sɛ|aS,,,SSa->aS,aaɛaɛ|S->|aa->->S||S->|"
                                    + "ɛ,|a,->,,,,a,||,a|Sɛɛɛ|->SSS->->ɛa|->|,|||||,aSaaɛ|ɛaɛɛ|,Sɛɛ->aaa|,a,Sa,|,ɛ->"
                                    + "SSɛaɛ,a|S->S|aa,SS->ɛ,,SɛSaɛ|ɛ,->aaSaa,S,ɛ->|ɛɛaS->,|ɛ->->ɛ,->S->->Sɛ->Sɛɛɛ|->SSSɛ"
                                    + "ɛɛSɛ->,,S,a,S|->,->||a->S->->->,->,SS->,,|->aɛɛɛ->|,S|ɛS->||ɛ,,||->,ɛ|a|||->->aɛɛ->->ɛa"
                                    + "Saa->S|->ɛ->S->->ɛ|a|ɛ->,,,,aɛ,|ɛ|aa|a|,->,aSSSɛ,,a|ɛɛ||ɛ->|ɛɛSɛ->,ɛS|,|S|SSɛ,->"
                                    + "Sɛ->->ɛ->|ɛS,ɛSa,a,->S|SɛSa,,|S|ɛa|->,|aaɛS||->->,,,a,->ɛɛa|->->ɛ|,,->,||S|,S|a||"
                                    + "ɛaaaɛ|||->,SS,|a,Sɛa->S->a->,aa->aɛS->,|,->ɛaS|a->a->a|,->SSa|aSɛ,,|S||,->|ɛaɛS||"
                                    + "ɛS|ɛSaaɛaaS,->aɛɛ||SS,S->->|a->,aaɛa|a|a,aaa->->aaaɛɛ->a||aa|->->->->Sɛaɛ->a,a->a,a"
                                    + "a->|||Sɛɛa|,SaSS,->a->a||->,a->a,S->Sa|a->->->ɛa|a,SS|ɛ,|->ɛ,,ɛɛS->|,,ɛ,ɛa||S,S,a"
                                    + ",a->S,->Sɛ|,|,,,,ɛ|->,->->->->->a,ɛ->ɛ->ɛ->S,,,->|a->aSSaS,a,Saa->Sɛɛɛɛ|S,SɛɛɛS,,Sa|"
                                    + "Sa||ɛ->aɛSaSS->,|S,,|ɛ,,aSɛ|Saɛ,S->,a|Sɛ->a->||->ɛa,->ɛ,aSS->->->S,->SɛS|a,,ɛ,||S"
                                    + "Sɛ->->a|->|S,S->S|->|S,ɛ|a,a->a,ɛ|aSa||ɛ,->,->,aaaɛ->->|ɛ->S,->ɛS,,a->,|aS||ɛɛ->|S,|"
                                    + ",aSa->->SS,Sa,a|ɛɛɛa,|S,S,->ɛ|S,,->||ɛa->ɛSaSɛɛɛS,S|aaSɛ|->ɛ->SɛSSS,,a->S->ɛ->,S"
                                    + "a|,ɛa,||ɛ|a,,ɛa->->a,S,|ɛaS,ɛ|,S,ɛ,a,|->|,->->->aS->S,aɛ->|Sɛɛ,aS,,ɛ|,->,|->aaɛ,"
                                    + "ɛ,Sɛ|ɛ->->aɛ->||Sa->S|ɛ,aaɛɛɛ||ɛ||a,|aa|ɛ|->|->|S->SɛɛaaaaSSɛ,S->,,ɛa|S,a,,Sɛa"
                                    + "ɛSaɛSSa->|,->ɛ|,|||ɛɛ,S->ɛSɛɛ,aSaSɛ,|->->|S,,aɛ|a->a,S,aɛaɛS|->ɛ,->->ɛaS|,,,|,->"
                                    + "->ɛ|,S->ɛaa|a|SS->|ɛ,a|ɛS||aa,->ɛa,aS,Sɛ->,SS->->,ɛSaaS,S->ɛ,|SɛS|S,S->|->ɛa|->aS"
                                    + "aaɛ->|a,,Sa->,,|->S|,,S->S|SɛaS,ɛ,->|->,Sa->S|->ɛɛ|->||,,,|S,Sɛ,|ɛ|,a|->|a|S,aS->"
                                    + "->,,S|ɛS->ɛ|->||aɛ->Sa|a,,a->ɛ|->ɛ,,,->|->->ɛa|->S,SɛSa|,ɛ->|,a|,,aɛ||SSa|ɛ->S,aɛ->"
                                    + "aSaS,aɛ|,aSSSa|,->ɛa->aa,a->,,,ɛ|SS->aaaSS,,Sa->Sɛa|S|S,|a,S,,ɛ->S,S||ɛS,->->|"
                                    + ",ɛɛ,a,,SSɛS->a,aaa,->S,ɛa|a,,aS|->->ɛ|aaSSS|->|ɛaaSaaɛS,->,->,SɛS->a|S|SS|S|,->"
                                    + "SaSSɛɛ,->->ɛɛ->->,,Sɛ,Sɛ->aɛ|aɛS,,aa,ɛS->aaSa->a|S|ɛ|,ɛ,,,a,ɛ->a->ɛaa,ɛɛa,ɛɛ->S->"
                                    + ",ɛ||ɛ||SSɛaS->Sɛɛ->a,->ɛ,,->,ɛ|a|a,->aSɛSɛ->Sa,ɛ,a|->ɛ->|ɛSSɛɛS|->ɛSSaaɛSɛ->aSaa"
                                    + "aS||SS,|,a,ɛ->|a->ɛ->a|->||S,,ɛ->,->ɛa->,|S|->aSɛ|a->ɛ,,->,,aɛ||,SɛSa,aɛɛa|a->ɛ,S"
                                    + "|aɛS||ɛɛ||S,->->->ɛ|ɛ|,,S|ɛ|ɛ,aɛ->ɛS|aS,,->->,->->aa->->ɛ,Saɛ->,||ɛa|S,ɛɛ|->ɛ->|Sa|"
                                    + "Sa||ɛ,aaa->,->,Sɛ|->,S->ɛa|S,->ɛ->->Saaa,->|Sɛ|->aaa|ɛ->->|S,SɛS->|->S,aaaɛ|Sa|||a,"
                                    + ",SSSɛɛ,|->,,,|,ɛa->ɛ,S||S||->aSS->,ɛ->||->,ɛ->,,,->,,->SSSSSaSaS,->SaS->SaɛSa,,SS"
                                    + "ɛS->ɛ,|,SSS|,a->,ɛ,SaaaSa|S|SS->ɛ|aɛɛSɛSɛ|,,aɛaS,|->|Sa->S|ɛ->a,ɛ|ɛ,a|a|,|Sɛ"
                                    + "->ɛS->||Sɛ->S,Sa,||aS,aS,|->,->ɛSSaɛ,SS|->->ɛɛaɛ->->S->,|->a||S,->ɛ|S,|->|ɛ,ɛS,SaS,"
                                    + "ɛɛ->|aɛ->||ɛɛ->aSSa->a,->,|Sa->,->aɛa->aaɛS,a->->,S->SSa->S,ɛS,ɛ,ɛaSS|a->ɛ,ɛS->|SSSɛ"
                                    + ",||S|ɛ,ɛ|S|ɛ->ɛa|aaa|,a,|ɛɛS,->,Sa,Sa,a,->aaɛ->ɛ|ɛ|a->,ɛa->->SSɛa->SSa,Sɛ,aSaɛ"
                                    + "SS||a|,,||a,,->->->,SSa->a->a,->->Sa|a->Sɛɛ->ɛ->ɛɛ->->|->,a|ɛɛ,SSS->,ɛɛa->,a|a->ɛɛaɛ,S"
                                    + "ɛ,,->,ɛ,->ɛ||,->->,->ɛa,,Sɛɛ,aSSSɛ|->->ɛ,->->->->->ɛ->a,|,|ɛ->|a->Saa|->Sa|ɛa->|,->ɛS->|->"
                                    + ",|ɛ->ɛ,->|||aɛ,ɛ,||||ɛaɛ|||->Saɛ,S,S,|ɛaaa,|S->aɛaɛaɛ->,||S,,->Sɛa||a|S|a|->|"
                                    + ",,,aaaɛ|Saɛ,ɛ|->->|->,->S,S->,,ɛS,a|ɛa,a->ɛ->ɛSa,Saa,->->->->aɛ,->|aaaɛ|,SSaɛɛSɛ||"
                                    + "|ɛɛ||ɛ|aɛ,Sɛ,ɛ|ɛaaS->ɛa->,ɛSS|aS|->Sɛ->,a,|||,Sɛ->Sɛ|Sɛ->Sa->SSS->,->->ɛS->ɛS|||,"
                                    + "aɛɛSɛa->->||a|SS|->,SS||S,a|,aɛSS,Saɛ,a->a,a|ɛ,aa->,ɛ,ɛSa->aSɛ->a|a,ɛS,S|ɛ,aa"
                                    + "->a|a,Sɛ->a->->S->a->S->ɛSaɛ||->,|aaaɛa->->|->|aɛaɛa,,a,a,|ɛ,aa->->|a->,S|a,,S,ɛSɛ|ɛ"
                                    + "||,->,SSaɛ|,->ɛ->a->,a->a,->S,a->S||SS->->a|S->SS||aSS|S|ɛ,Sa->ɛaSɛ->,,a|->->,SS->aSɛ"
                                    + "|aSɛ|,ɛ,S|ɛ|ɛ->S||aa|,ɛaɛSɛ,->,S,,S->->,->ɛSɛaɛ->S||Sɛɛ,,,S,ɛa|Sa,ɛ|,->,a|ɛ,->"
                                    + ",|||,SS,||ɛ->S->,a|Sɛa,,ɛ,ɛa|,S->,|a->->->aS->,->aa|S|aɛ,aa->S,aaɛɛ|ɛ,,,a->a|->S,"
                                    + "->->S->ɛ->ɛ->S,S|,|ɛ,,|ɛ|->ɛ->|ɛɛ||,Sa->|Sɛa,,|,S->->|ɛɛS->ɛ,->->ɛ||->S|S,||,,->->ɛ->|ɛ"
                                    + "a->->ɛɛ,ɛ->aɛ,->Saɛ|a,->S->,,ɛ->S->,|ɛa->->,|->aɛSS->,Saa,,||,->,SSɛ->ɛ->ɛ,|SS,|SSaS->"
                                    + "->ɛ||aSS,|ɛ->ɛɛ||S,|,,,->|,ɛa|->S");
        } catch (IncorrectParseInputException e) {
            ParsingFaultCollection faults =
                    (ParsingFaultCollection) e.getFaultMapping().get(ParsingFaultCollection.class);

            if (faults.getFaults().stream()
                    .anyMatch(fault -> fault.getReason() == GeneralParsingFaultReason.VARIOUS))
                fail();
        }
    }
}

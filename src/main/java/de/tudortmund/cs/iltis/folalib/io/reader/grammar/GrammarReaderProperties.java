package de.tudortmund.cs.iltis.folalib.io.reader.grammar;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferencePolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.GivenAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferFromRegexPolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferMinimalNonEmptyAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.GrammarParserOperators;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.InputToGrammarSymbolConverter;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.standard.GivenAlphabetsInputConverter;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.standard.InferFromRegexInputConverter;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol.StartSymbolDerivationStrategy;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol.standard.GivenSymbolStartSymbolDerivationStrategy;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.StringUtils;
import de.tudortmund.cs.iltis.utils.io.parser.customizable.CustomizableLexingProperties;
import de.tudortmund.cs.iltis.utils.io.parser.customizable.CustomizableLexingPropertiesProvidable;
import de.tudortmund.cs.iltis.utils.io.parser.general.ParsableSymbol;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.FiniteSetSymbolSplittingPolicy;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.RegularSymbolSplittingPolicy;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.SymbolSplittingPolicy;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A property object used by {@link GrammarReader} and subclasses.
 *
 * <p>There are two {@link AlphabetInferencePolicy}s stored in this property-object. One is used for
 * the terminal alphabet, the other is used for the non-terminal alphabet. These two policies
 * specify what the final alphabets shall look like and which symbols are allowed in each of them.
 * The functionality differs from the {@link SymbolSplittingPolicy} which determines which strings
 * are recognized as symbols by the parser. The {@link AlphabetInferencePolicy} determines if these
 * recognized symbols are actually allowed in the {@link Grammar} as terminals or non-terminals.
 *
 * <p>The {@link InputToGrammarSymbolConverter} is used to determine if a read input symbol is a
 * terminal or a non-terminal. This is completely separate from the {@link
 * AlphabetInferencePolicy}s, i.e. not allowed symbols can still be parsed and generate an error
 * later. E.g. The {@link InputToGrammarSymbolConverter} could be the regular expression "[a-z]" for
 * terminals and "[A-Z]" for non-terminals. But the {@link AlphabetInferencePolicy}s could only
 * specify "[a-f]" for terminals and "[A-F]" for non-terminals. This would lead to a correct parser
 * output if "not allowed" symbols (like e.g. "x" or "y") were used because the parser can assign
 * these inputs to either the terminals or the non-terminals. Later, after parsing there will be
 * errors because of the {@link AlphabetInferencePolicy}, but there is still a parser output.
 *
 * <p>The {@link StartSymbolDerivationStrategy} is used to derive a start non-terminal from the
 * input. The start symbol can also be set beforehand manually by choosing an appropriate strategy.
 */
public class GrammarReaderProperties extends CustomizableLexingProperties {

    private AlphabetInferencePolicy terminalAlphabetInferencePolicy;
    private AlphabetInferencePolicy nonTerminalAlphabetInferencePolicy;
    private InputToGrammarSymbolConverter inputToGrammarSymbolConverter;
    private StartSymbolDerivationStrategy startSymbolDerivationStrategy;

    /** Only constructable via the convenience methods */
    private GrammarReaderProperties() {}

    @Override
    public ParsableSymbol getSymbolForNonSeparatedText() {
        return GrammarParserOperators.SYMBOL_CONCATENATION;
    }

    @Override
    public ParsableSymbol getSymbolForISymbols() {
        return GrammarParserOperators.SYMBOL;
    }

    @Override
    public ParsableSymbol getSymbolForSeparation() {
        return GrammarParserOperators.WHITESPACE;
    }

    @Override
    public CustomizableLexingPropertiesProvidable clone() {
        GrammarReaderProperties props = new GrammarReaderProperties();
        props.addSeparationSymbols(separationSymbols);
        props.addSeparatingOperators(separatingOperators);
        props.addNonSeparatingOperators(nonSeparatingOperators);
        props.setSymbolSplittingPolicy(symbolSplittingPolicy);
        props.addParenthesesSymbols(parenthesesMap, allowedParenthesesTypes);
        props.setTerminalAlphabetInferencePolicy(terminalAlphabetInferencePolicy);
        props.setNonTerminalAlphabetInferencePolicy(nonTerminalAlphabetInferencePolicy);
        props.setInputToGrammarSymbolConverter(inputToGrammarSymbolConverter);
        props.setStartSymbolDerivationStrategy(startSymbolDerivationStrategy);
        return props;
    }

    public void setTerminalAlphabetInferencePolicy(
            AlphabetInferencePolicy terminalAlphabetInferencePolicy) {
        Objects.requireNonNull(terminalAlphabetInferencePolicy);
        this.terminalAlphabetInferencePolicy = terminalAlphabetInferencePolicy;
    }

    public AlphabetInferencePolicy getTerminalAlphabetInferencePolicy() {
        return this.terminalAlphabetInferencePolicy;
    }

    public void setNonTerminalAlphabetInferencePolicy(
            AlphabetInferencePolicy nonTerminalAlphabetInferencePolicy) {
        Objects.requireNonNull(nonTerminalAlphabetInferencePolicy);
        this.nonTerminalAlphabetInferencePolicy = nonTerminalAlphabetInferencePolicy;
    }

    public AlphabetInferencePolicy getNonTerminalAlphabetInferencePolicy() {
        return this.nonTerminalAlphabetInferencePolicy;
    }

    public void setInputToGrammarSymbolConverter(
            InputToGrammarSymbolConverter inputToGrammarSymbolConverter) {
        Objects.requireNonNull(inputToGrammarSymbolConverter);
        this.inputToGrammarSymbolConverter = inputToGrammarSymbolConverter;
    }

    public InputToGrammarSymbolConverter getInputToGrammarSymbolConverter() {
        return inputToGrammarSymbolConverter;
    }

    public void setStartSymbolDerivationStrategy(
            StartSymbolDerivationStrategy startSymbolDerivationStrategy) {
        Objects.requireNonNull(startSymbolDerivationStrategy);
        this.startSymbolDerivationStrategy = startSymbolDerivationStrategy;
    }

    public StartSymbolDerivationStrategy getStartSymbolDerivationStrategy() {
        return startSymbolDerivationStrategy;
    }

    /*-----------------------------------------*\
     | Convenience methods                     |
    \*-----------------------------------------*/

    /**
     * The inferred alphabets will consist of all used terminals and non-terminals. Terminals must
     * be a lowercase letter from the english alphabet. Iff no terminals exist in the grammar the
     * alphabet Σ={+} will be used instead. Non-terminals must be an uppercase letter from the
     * english alphabet. Iff no non-terminals exist in the grammar the alphabet Σ={#} will be used
     * instead. The start symbol (included in the non-terminals) is "S". The splitting policy {@link
     * RegularSymbolSplittingPolicy#ALL_UNARY_SYMBOLS_POLICY} will be used to recognize symbols.
     */
    public static GrammarReaderProperties createDefault() {
        return createDefault(
                RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                new InferMinimalNonEmptyAlphabetPolicy(Alphabets.indexedSymbolAlphabet("+")),
                new InferMinimalNonEmptyAlphabetPolicy(Alphabets.indexedSymbolAlphabet("#")),
                new InferFromRegexInputConverter("[a-z]", "[A-Z]"),
                new GivenSymbolStartSymbolDerivationStrategy(new IndexedSymbol("S")),
                true);
    }

    /**
     * Uses two given alphabets for terminals and nonTerminals respectively. All symbols from those
     * two alphabets and all unary symbols are recognised as symbols, but only those that are
     * included in one of the alphabets can be further processed.
     *
     * @param terminals The alphabet of the terminals
     * @param nonTerminals The alphabet of the non-terminals
     * @param startSymbolDerivationStrategy The strategy to derive the start non-terminal
     */
    public static GrammarReaderProperties createDefault(
            Alphabet<IndexedSymbol> terminals,
            Alphabet<IndexedSymbol> nonTerminals,
            StartSymbolDerivationStrategy startSymbolDerivationStrategy) {
        Alphabet<IndexedSymbol> allSymbols = Alphabets.unionOf(terminals, nonTerminals);

        RegularSymbolSplittingPolicy indexedSymbolsSplittingPolicy =
                new FiniteSetSymbolSplittingPolicy(allSymbols.toUnmodifiableSet())
                        .toRegularSymbolSplittingPolicy();

        return createDefault(
                indexedSymbolsSplittingPolicy.or(
                        RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY),
                new GivenAlphabetPolicy(terminals),
                new GivenAlphabetPolicy(nonTerminals),
                new GivenAlphabetsInputConverter(terminals, nonTerminals),
                startSymbolDerivationStrategy,
                true);
    }

    /**
     * Uses two given regular expressions for terminals and nonTerminals respectively. The two
     * resulting alphabets will consist of all used symbols respectively. Iff no terminals exist in
     * the grammar the alphabet Σ={+} will be used instead. Iff no non-terminals exist in the
     * grammar the alphabet Σ={#} will be used instead. All symbols from those two alphabets and all
     * unary symbols are recognised as symbols, but only those that match one of the regular
     * expressions can be further processed.
     *
     * @param terminalsRegExp The regular expression for the terminals
     * @param nonTerminalsRegExp The regular expression for the non-terminals
     * @param startSymbolDerivationStrategy The strategy to derive the start non-terminal
     */
    public static GrammarReaderProperties createDefault(
            String terminalsRegExp,
            String nonTerminalsRegExp,
            StartSymbolDerivationStrategy startSymbolDerivationStrategy) {
        return createDefault(
                new RegularSymbolSplittingPolicy(terminalsRegExp)
                        .or(new RegularSymbolSplittingPolicy(nonTerminalsRegExp))
                        .or(RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY),
                new InferFromRegexPolicy(terminalsRegExp, new Alphabet<>(new IndexedSymbol("+"))),
                new InferFromRegexPolicy(
                        nonTerminalsRegExp, new Alphabet<>(new IndexedSymbol("#"))),
                new InferFromRegexInputConverter(terminalsRegExp, nonTerminalsRegExp),
                startSymbolDerivationStrategy,
                true);
    }

    /**
     * A fully configurable convenience method.
     *
     * @param addDefaultOperators Indicates whether the default operators including the separation
     *     symbols and the allowed parentheses symbols shall be added.
     */
    public static GrammarReaderProperties createDefault(
            SymbolSplittingPolicy splittingPolicy,
            AlphabetInferencePolicy terminalAlphabetInferenceStrategy,
            AlphabetInferencePolicy nonTerminalAlphabetInferencePolicy,
            InputToGrammarSymbolConverter inputToGrammarSymbolConverter,
            StartSymbolDerivationStrategy startSymbolDerivationStrategy,
            boolean addDefaultOperators) {
        GrammarReaderProperties props = new GrammarReaderProperties();

        props.setSymbolSplittingPolicy(splittingPolicy);
        props.setTerminalAlphabetInferencePolicy(terminalAlphabetInferenceStrategy);
        props.setNonTerminalAlphabetInferencePolicy(nonTerminalAlphabetInferencePolicy);
        props.setInputToGrammarSymbolConverter(inputToGrammarSymbolConverter);
        props.setStartSymbolDerivationStrategy(startSymbolDerivationStrategy);

        if (addDefaultOperators) {
            props.addSeparatingOperator("->", GrammarParserOperators.PRODUCTION_ARROW);
            props.addSeparatingOperator(">", GrammarParserOperators.PRODUCTION_ARROW);
            props.addSeparatingOperator("→", GrammarParserOperators.PRODUCTION_ARROW);
            props.addSeparatingOperator("ε", GrammarParserOperators.EPSILON);
            props.addSeparatingOperator("|", GrammarParserOperators.RIGHT_SIDE_SEPARATOR);
            props.addSeparatingOperator(",", GrammarParserOperators.LINE_SEPARATOR);
            props.addSeparatingOperator(";", GrammarParserOperators.LINE_SEPARATOR);
            // Add line breaking whitespaces (e.g. \\n) as line separators
            StringUtils.getUnicodeLineBreakingWhitespaces()
                    .forEach(
                            lineBreakingCharacter ->
                                    props.addSeparatingOperator(
                                            lineBreakingCharacter.toString(),
                                            GrammarParserOperators.LINE_SEPARATOR));

            props.addNonSeparatingOperator("eps", GrammarParserOperators.EPSILON);
            props.addNonSeparatingOperator("epsilon", GrammarParserOperators.EPSILON);

            // Add non line breaking whitespaces as separation symbols
            props.addSeparationSymbols(
                    StringUtils.getUnicodeNonLineBreakingWhitespaces().stream()
                            .map(Object::toString)
                            .collect(Collectors.toSet()));
        }

        return props;
    }
}

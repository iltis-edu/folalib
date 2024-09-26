package de.tudortmund.cs.iltis.folalib.io.reader.sentential;

import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferencePolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.GivenAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferFromRegexPolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferMinimalAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.GrammarParserOperators;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.InputToGrammarSymbolConverter;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.standard.GivenAlphabetsInputConverter;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.standard.InferFromRegexInputConverter;
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
 * A property object used by {@link SententialFormReader}.
 *
 * <p>There are two {@link AlphabetInferencePolicy}s stored in this property-object. One is used for
 * the terminal alphabet, the other is used for the non-terminal alphabet. The functionality differs
 * from the {@link SymbolSplittingPolicy} which determines which strings are recognized as symbols
 * by the parser. The {@link AlphabetInferencePolicy} determines if these recognized symbols are
 * actually allowed in the {@link SententialForm}. This is used only for symbols that were
 * unambiguously assigned to the terminals or the non-terminals by the {@link
 * InputToGrammarSymbolConverter} mentioned below. If the assignment fails, the symbol will be
 * discarded and not be analysed by the {@link AlphabetInferencePolicy}s.
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
 */
public class SententialFormReaderProperties extends CustomizableLexingProperties {

    private AlphabetInferencePolicy terminalAlphabetInferencePolicy;
    private AlphabetInferencePolicy nonTerminalAlphabetInferencePolicy;
    private InputToGrammarSymbolConverter inputToGrammarSymbolConverter;

    /** Only constructable via the convenience methods */
    private SententialFormReaderProperties() {}

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
        SententialFormReaderProperties props = new SententialFormReaderProperties();
        props.addSeparationSymbols(separationSymbols);
        props.addSeparatingOperators(separatingOperators);
        props.addNonSeparatingOperators(nonSeparatingOperators);
        props.setSymbolSplittingPolicy(symbolSplittingPolicy);
        props.addParenthesesSymbols(parenthesesMap, allowedParenthesesTypes);
        props.setTerminalAlphabetInferencePolicy(terminalAlphabetInferencePolicy);
        props.setNonTerminalAlphabetInferencePolicy(nonTerminalAlphabetInferencePolicy);
        props.setInputToGrammarSymbolConverter(inputToGrammarSymbolConverter);
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

    /*-----------------------------------------*\
     | Convenience methods                     |
    \*-----------------------------------------*/

    /**
     * There is no restriction on the alphabets. Every symbol that can either be assigned to the
     * terminals or the non-terminals is allowed. Terminals must be a lowercase letter from the
     * english alphabet to get recognized. Non-terminals must be an uppercase letter from the
     * english alphabet to get recognized. The splitting policy {@link
     * RegularSymbolSplittingPolicy#ALL_UNARY_SYMBOLS_POLICY} will be used to recognize symbols.
     */
    public static SententialFormReaderProperties createDefault() {
        return createDefault(
                RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                new InferMinimalAlphabetPolicy(), // Suitable because the inferred alphabet will be
                // ignored
                new InferMinimalAlphabetPolicy(), // Suitable because the inferred alphabet will be
                // ignored
                new InferFromRegexInputConverter("[a-z]", "[A-Z]"),
                true);
    }

    /**
     * Uses two given alphabets for terminals and non-terminals respectively. All symbols from those
     * two alphabets and all unary symbols are recognised as symbols, but only those that are
     * included in one of the alphabets can be further processed.
     *
     * @param terminals The alphabet of the terminals
     * @param nonTerminals The alphabet of the non-terminals
     */
    public static SententialFormReaderProperties createDefault(
            Alphabet<IndexedSymbol> terminals, Alphabet<IndexedSymbol> nonTerminals) {
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
                true);
    }

    /**
     * Uses the given regular expressions for terminals and non-terminals respectively. All symbols
     * matching one of the regular expression and all unary symbols are recognized as symbols. Only
     * the symbols matching the regular expressions are allowed by the alphabet inference.
     *
     * @param terminalsRegExp The regular expression for the terminals
     * @param nonTerminalsRegExp The regular expression for the non-terminals
     */
    public static SententialFormReaderProperties createDefault(
            String terminalsRegExp, String nonTerminalsRegExp) {
        return createDefault(
                new RegularSymbolSplittingPolicy(terminalsRegExp)
                        .or(new RegularSymbolSplittingPolicy(nonTerminalsRegExp))
                        .or(RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY),
                new InferFromRegexPolicy(terminalsRegExp, new Alphabet<>(new IndexedSymbol("+"))),
                new InferFromRegexPolicy(
                        nonTerminalsRegExp, new Alphabet<>(new IndexedSymbol("#"))),
                new InferFromRegexInputConverter(terminalsRegExp, nonTerminalsRegExp),
                true);
    }

    /**
     * Uses the given splitting policy and the given input converter to assign the split symbols to
     * the terminals or non-terminals. There is no restriction on the alphabets.
     */
    public static SententialFormReaderProperties createDefault(
            SymbolSplittingPolicy splittingPolicy,
            InputToGrammarSymbolConverter inputToGrammarSymbolConverter) {
        return createDefault(
                splittingPolicy,
                new InferMinimalAlphabetPolicy(), // Suitable because the inferred alphabet will be
                // ignored
                new InferMinimalAlphabetPolicy(), // Suitable because the inferred alphabet will be
                // ignored
                inputToGrammarSymbolConverter,
                true);
    }

    /**
     * A fully configurable convenience method.
     *
     * @param addDefaultOperators Indicates whether the default operators including the separation
     *     symbols shall be added.
     */
    public static SententialFormReaderProperties createDefault(
            SymbolSplittingPolicy splittingPolicy,
            AlphabetInferencePolicy terminalAlphabetInferenceStrategy,
            AlphabetInferencePolicy nonTerminalAlphabetInferencePolicy,
            InputToGrammarSymbolConverter inputToGrammarSymbolConverter,
            boolean addDefaultOperators) {
        SententialFormReaderProperties props = new SententialFormReaderProperties();

        props.setSymbolSplittingPolicy(splittingPolicy);
        props.setTerminalAlphabetInferencePolicy(terminalAlphabetInferenceStrategy);
        props.setNonTerminalAlphabetInferencePolicy(nonTerminalAlphabetInferencePolicy);
        props.setInputToGrammarSymbolConverter(inputToGrammarSymbolConverter);

        if (addDefaultOperators) {
            props.addSeparatingOperator("ε", GrammarParserOperators.EPSILON);

            props.addNonSeparatingOperator("eps", GrammarParserOperators.EPSILON);
            props.addNonSeparatingOperator("epsilon", GrammarParserOperators.EPSILON);

            // Add non line breaking whitespaces as separation symbols
            props.addSeparationSymbols(
                    StringUtils.getUnicodeWhitespaces().stream()
                            .map(Object::toString)
                            .collect(Collectors.toSet()));
        }

        return props;
    }
}

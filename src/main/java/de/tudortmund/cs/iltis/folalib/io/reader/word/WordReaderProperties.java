package de.tudortmund.cs.iltis.folalib.io.reader.word;

import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferencePolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.GivenAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferMinimalAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.GrammarParserOperators;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
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
 * A property object used by {@link WordReader}.
 *
 * <p>The {@link AlphabetInferencePolicy} is used to restrict the symbols used in the input.
 */
public class WordReaderProperties extends CustomizableLexingProperties {

    private AlphabetInferencePolicy alphabetInferencePolicy;

    /** Only constructable via the convenience methods */
    protected WordReaderProperties() {}

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
        WordReaderProperties props = new WordReaderProperties();
        props.addSeparationSymbols(separationSymbols);
        props.addSeparatingOperators(separatingOperators);
        props.addNonSeparatingOperators(nonSeparatingOperators);
        props.setSymbolSplittingPolicy(symbolSplittingPolicy);
        props.addParenthesesSymbols(parenthesesMap, allowedParenthesesTypes);
        props.setAlphabetInferencePolicy(alphabetInferencePolicy);
        return props;
    }

    public void setAlphabetInferencePolicy(AlphabetInferencePolicy alphabetInferencePolicy) {
        Objects.requireNonNull(alphabetInferencePolicy);
        this.alphabetInferencePolicy = alphabetInferencePolicy;
    }

    public AlphabetInferencePolicy getAlphabetInferencePolicy() {
        return this.alphabetInferencePolicy;
    }

    /*-----------------------------------------*\
     | Convenience methods                     |
    \*-----------------------------------------*/

    /**
     * There is no restriction on the alphabets. The splitting policy {@link
     * RegularSymbolSplittingPolicy#ALL_UNARY_SYMBOLS_POLICY} will be used to recognize symbols.
     */
    public static WordReaderProperties createDefault() {
        return createDefault(
                RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                new InferMinimalAlphabetPolicy(), // Suitable because the inferred alphabet will be
                // ignored
                true);
    }

    /**
     * Uses the given alphabet to restrict the symbols by using {@link GivenAlphabetPolicy}. All
     * symbols from the alphabet and all unary symbols are recognised as symbols.
     */
    public static WordReaderProperties createDefault(Alphabet<IndexedSymbol> alphabet) {
        RegularSymbolSplittingPolicy indexedSymbolsSplittingPolicy =
                new FiniteSetSymbolSplittingPolicy(alphabet.toUnmodifiableSet())
                        .toRegularSymbolSplittingPolicy();

        return createDefault(
                indexedSymbolsSplittingPolicy.or(
                        RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY),
                new GivenAlphabetPolicy(alphabet),
                true);
    }

    /**
     * A fully configurable convenience method.
     *
     * @param addDefaultOperators Indicates whether the default operators including the separation
     *     symbols shall be added.
     */
    public static WordReaderProperties createDefault(
            SymbolSplittingPolicy splittingPolicy,
            AlphabetInferencePolicy alphabetInferencePolicy,
            boolean addDefaultOperators) {
        WordReaderProperties props = new WordReaderProperties();

        props.setSymbolSplittingPolicy(splittingPolicy);
        props.setAlphabetInferencePolicy(alphabetInferencePolicy);

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

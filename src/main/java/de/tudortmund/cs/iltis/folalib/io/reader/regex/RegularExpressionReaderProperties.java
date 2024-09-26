package de.tudortmund.cs.iltis.folalib.io.reader.regex;

import de.tudortmund.cs.iltis.folalib.expressions.regular.Range;
import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferencePolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.GivenAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferFromRegexPolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferMinimalNonEmptyAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.parser.regex.RegularExpressionOperators;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.folalib.util.NullCheck;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.StringUtils;
import de.tudortmund.cs.iltis.utils.function.SerializableComparator;
import de.tudortmund.cs.iltis.utils.io.parser.customizable.CustomizableLexingProperties;
import de.tudortmund.cs.iltis.utils.io.parser.customizable.CustomizableLexingPropertiesProvidable;
import de.tudortmund.cs.iltis.utils.io.parser.general.ParsableSymbol;
import de.tudortmund.cs.iltis.utils.io.parser.parentheses.ParenthesesType;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.FiniteSetSymbolSplittingPolicy;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.RegularSymbolSplittingPolicy;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.SymbolSplittingPolicy;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * The {@link AlphabetInferencePolicy} specifies what the final alphabet shall look like and which
 * symbols are allowed. The functionality differs from the {@link SymbolSplittingPolicy} which
 * determines which strings are recognized as symbols by the parser. The {@link
 * AlphabetInferencePolicy} determines if these recognized symbols are actually allowed in the
 * {@link RegularExpression}.
 *
 * <p>The {@code domainForRange} is used only for the Range expression. All symbols that shall occur
 * in a Range must be specified in this domain. The domain and the final alphabet can differ. The
 * domain can be null (i.e. empty) if the Range expression is not used. A fault will be reported if
 * the Range expression gets used while the domain is empty.
 *
 * <p>The {@link Comparator} is used to induce an order on the domain. It is used to determine which
 * symbols are between the lower and the upper bound of the Range expressions. If the comparator is
 * null (i.e. not set) the natural order of the IndexedSymbols will be used.
 */
public class RegularExpressionReaderProperties extends CustomizableLexingProperties {

    /** Specifies what the final alphabet shall look like and which symbols are allowed */
    private AlphabetInferencePolicy alphabetInferencePolicy;

    /**
     * This is used only for the Range expression. All symbols that shall occur in a Range must be
     * specified in this domain. The domain and the final alphabet can differ. If no domainForRange
     * gets set (or gets set with {@code null}) an empty {@link Alphabet} will be used instead. This
     * will result in a fault in the parser if the {@link Range} expression gets used.
     */
    private Alphabet<IndexedSymbol> domainForRange;

    /**
     * Used to induce an order into the domain. It is used to determine which symbols are between
     * the lower and the upper bound of the Range expressions. If no {@link Comparator} gets set (or
     * gets set with {@code null}) the {@link Comparator#naturalOrder()} will be used instead.
     */
    private SerializableComparator<IndexedSymbol> comparator;

    /** Only constructable via the convenience methods */
    private RegularExpressionReaderProperties() {
        // This is always the default comparator
        comparator = SerializableComparator.naturalOrder();
    }

    @Override
    public ParsableSymbol getSymbolForNonSeparatedText() {
        return RegularExpressionOperators.WORD;
    }

    @Override
    public ParsableSymbol getSymbolForISymbols() {
        return RegularExpressionOperators.SYMBOL;
    }

    @Override
    public ParsableSymbol getSymbolForSeparation() {
        return RegularExpressionOperators.WHITESPACE;
    }

    @Override
    public CustomizableLexingPropertiesProvidable clone() {
        RegularExpressionReaderProperties props = new RegularExpressionReaderProperties();
        props.addSeparationSymbols(separationSymbols);
        props.addSeparatingOperators(separatingOperators);
        props.addNonSeparatingOperators(nonSeparatingOperators);
        props.setSymbolSplittingPolicy(symbolSplittingPolicy);
        props.addParenthesesSymbols(parenthesesMap, allowedParenthesesTypes);
        props.setAlphabetInferencePolicy(alphabetInferencePolicy);
        props.setComparator(comparator);
        props.setDomainForRange(domainForRange);
        return props;
    }

    public void setAlphabetInferencePolicy(AlphabetInferencePolicy alphabetInferencePolicy) {
        Objects.requireNonNull(alphabetInferencePolicy);
        this.alphabetInferencePolicy = alphabetInferencePolicy;
    }

    public AlphabetInferencePolicy getAlphabetInferencePolicy() {
        return this.alphabetInferencePolicy;
    }

    /**
     * If the {@code domainForRange} is {@code null} an empty {@link Alphabet} will be used instead
     */
    public void setDomainForRange(@Nullable Alphabet<IndexedSymbol> domainForRange) {
        if (domainForRange == null) {
            this.domainForRange = new Alphabet<>();
        } else {
            NullCheck.requireAllNonNull(domainForRange.toUnmodifiableSet());
            this.domainForRange = domainForRange;
        }
    }

    public Alphabet<IndexedSymbol> getDomainForRange() {
        return this.domainForRange;
    }

    /**
     * If the {@code comparator} is {@code null} the {@link Comparator#naturalOrder()} will be used
     * instead.
     */
    public void setComparator(@Nullable SerializableComparator<IndexedSymbol> comparator) {
        if (comparator == null) this.comparator = SerializableComparator.naturalOrder();
        else this.comparator = comparator;
    }

    public Comparator<IndexedSymbol> getComparator() {
        return this.comparator;
    }

    /*-----------------------------------------*\
     | Convenience methods                     |
    \*-----------------------------------------*/

    /**
     * Infers the alphabet automatically and uses {@link
     * RegularSymbolSplittingPolicy#ALL_UNARY_SYMBOLS_POLICY}. The base alphabet Σ={#} will be used
     * if not a single symbol is used in the regular expression. The used domain for the Range
     * expression will consist of all characters of the english alphabet (upper case and lower case)
     * and all digits (0-9).
     */
    public static RegularExpressionReaderProperties createDefault() {
        return createDefault(
                RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                new InferMinimalNonEmptyAlphabetPolicy(Alphabets.indexedSymbolAlphabet("#")),
                defaultDomainForRange,
                true);
    }

    /**
     * Uses the given {@code alphabet} for the constructed regular expression and additionally
     * infers a splitting policy out of it. This derived splitting policy gets merged with {@link
     * RegularSymbolSplittingPolicy#ALL_UNARY_SYMBOLS_POLICY}. The domain for the Range expression
     * will be the given {@code alphabet} merged with all characters of the english alphabet (upper
     * case and lower case) and all digits (0-9).
     */
    public static RegularExpressionReaderProperties createDefault(
            Alphabet<IndexedSymbol> alphabet) {
        RegularSymbolSplittingPolicy indexedSymbolsSplittingPolicy =
                new FiniteSetSymbolSplittingPolicy(alphabet.toUnmodifiableSet())
                        .toRegularSymbolSplittingPolicy();

        return createDefault(
                indexedSymbolsSplittingPolicy.or(
                        RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY),
                new GivenAlphabetPolicy(alphabet),
                Alphabets.unionOf(alphabet, defaultDomainForRange),
                true);
    }

    /**
     * Infers the alphabet automatically but every symbol read (or that is implicitly used via the
     * Range expression) must match the specified regular expression. The base alphabet Σ={#} will
     * be used if no alphabet can get inferred. The given {@code regExp} will also be used to infer
     * a splitting policy. This derived splitting policy gets merged with {@link
     * RegularSymbolSplittingPolicy#ALL_UNARY_SYMBOLS_POLICY}. The domain for the Range expression
     * will consist of all characters of the english alphabet (upper case and lower case) and all
     * digits (0-9).
     */
    public static RegularExpressionReaderProperties createDefault(String regExp) {
        return createDefault(
                new RegularSymbolSplittingPolicy("regExp")
                        .or(RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY),
                new InferFromRegexPolicy(regExp, Alphabets.indexedSymbolAlphabet("#")),
                defaultDomainForRange,
                true);
    }

    /**
     * Uses the given {@code splittingPolicy}, the given {@code alphabetInferencePolicy} and the
     * given domain for the {@link Range} expression.
     *
     * @param addDefaultOperators Indicates whether the default operators including the separation
     *     symbols and the allowed parentheses symbols shall be added.
     */
    public static RegularExpressionReaderProperties createDefault(
            SymbolSplittingPolicy splittingPolicy,
            AlphabetInferencePolicy alphabetInferencePolicy,
            @Nullable Alphabet<IndexedSymbol> domainForRange,
            boolean addDefaultOperators) {

        RegularExpressionReaderProperties props = new RegularExpressionReaderProperties();
        props.setSymbolSplittingPolicy(splittingPolicy);
        props.setAlphabetInferencePolicy(alphabetInferencePolicy);
        props.setDomainForRange(domainForRange);

        if (addDefaultOperators) {
            props.addSeparatingOperator("+", RegularExpressionOperators.ALTERNATION);
            props.addSeparatingOperator("*", RegularExpressionOperators.KLEENE_STAR);
            props.addSeparatingOperator("^*", RegularExpressionOperators.KLEENE_STAR);
            props.addSeparatingOperator("ε", RegularExpressionOperators.EPSILON);
            props.addSeparatingOperator("(", RegularExpressionOperators.OPENING_PARENTHESIS);
            props.addSeparatingOperator(")", RegularExpressionOperators.CLOSING_PARENTHESIS);
            props.addSeparatingOperator("∅", RegularExpressionOperators.EMPTY_SET);

            props.addNonSeparatingOperator("eps", RegularExpressionOperators.EPSILON);
            props.addNonSeparatingOperator("epsilon", RegularExpressionOperators.EPSILON);
            props.addNonSeparatingOperator("empty", RegularExpressionOperators.EMPTY_SET);
            props.addNonSeparatingOperator("emptyset", RegularExpressionOperators.EMPTY_SET);

            props.addSeparationSymbols(
                    StringUtils.getUnicodeWhitespaces().stream()
                            .map(Object::toString)
                            .collect(Collectors.toSet()));

            props.addAllowedParenthesesSymbol(
                    ParenthesesType.PARENTHESES,
                    RegularExpressionOperators.OPENING_PARENTHESIS,
                    RegularExpressionOperators.CLOSING_PARENTHESIS);
        }

        return props;
    }

    /**
     * Infers the alphabet automatically and uses {@link
     * RegularSymbolSplittingPolicy#ALL_UNARY_SYMBOLS_POLICY}. The base alphabet Σ={#} will be used
     * if not a single symbol is used in the regular expression. The used domain for the Range
     * expression will consist of all characters of the english alphabet (upper case and lower case)
     * and all digits (0-9).
     */
    public static RegularExpressionReaderProperties createDefaultExtendedRegEx() {
        return createDefaultExtendedRegEx(
                RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                new InferMinimalNonEmptyAlphabetPolicy(Alphabets.indexedSymbolAlphabet("#")),
                defaultDomainForRange,
                true);
    }

    /**
     * Uses the given {@code alphabet} for the constructed regular expression and additionally
     * infers a splitting policy out of it. This derived splitting policy gets merged with {@link
     * RegularSymbolSplittingPolicy#ALL_UNARY_SYMBOLS_POLICY}. The domain for the Range expression
     * will be the given {@code alphabet} merged with all characters of the english alphabet (upper
     * case and lower case) and all digits (0-9).
     */
    public static RegularExpressionReaderProperties createDefaultExtendedRegEx(
            Alphabet<IndexedSymbol> alphabet) {
        RegularSymbolSplittingPolicy indexedSymbolsSplittingPolicy =
                new FiniteSetSymbolSplittingPolicy(alphabet.toUnmodifiableSet())
                        .toRegularSymbolSplittingPolicy();

        return createDefaultExtendedRegEx(
                indexedSymbolsSplittingPolicy.or(
                        RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY),
                new GivenAlphabetPolicy(alphabet),
                Alphabets.unionOf(alphabet, defaultDomainForRange),
                true);
    }

    /**
     * Infers the alphabet automatically but every symbol read (or that is implicitly used via the
     * Range expression) must match the specified regular expression. The base alphabet Σ={#} will
     * be used if no alphabet can get inferred. The given {@code regExp} will also be used to infer
     * a splitting policy. This derived splitting policy gets merged with {@link
     * RegularSymbolSplittingPolicy#ALL_UNARY_SYMBOLS_POLICY}. The domain for the Range expression
     * will consist of all characters of the english alphabet (upper case and lower case) and all
     * digits (0-9).
     */
    public static RegularExpressionReaderProperties createDefaultExtendedRegEx(String regExp) {
        return createDefaultExtendedRegEx(
                new RegularSymbolSplittingPolicy("regExp")
                        .or(RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY),
                new InferFromRegexPolicy(regExp, Alphabets.indexedSymbolAlphabet("#")),
                defaultDomainForRange,
                true);
    }

    /**
     * Uses the given {@code splittingPolicy}, the given {@code alphabetInferencePolicy} and the
     * given domain for the {@link Range} expression.
     *
     * @param addDefaultOperators Indicates whether the default operators including the separation
     *     symbols and the allowed parentheses symbols shall be added.
     */
    public static RegularExpressionReaderProperties createDefaultExtendedRegEx(
            SymbolSplittingPolicy splittingPolicy,
            AlphabetInferencePolicy alphabetInferencePolicy,
            @Nullable Alphabet<IndexedSymbol> domainForRange,
            boolean addDefaultOperators) {

        RegularExpressionReaderProperties props =
                createDefault(
                        splittingPolicy,
                        alphabetInferencePolicy,
                        domainForRange,
                        addDefaultOperators);

        if (addDefaultOperators) {
            props.addSeparatingOperator("^", RegularExpressionOperators.FIXED_REPETITION_OPENER);
            props.addSeparatingOperator("{", RegularExpressionOperators.OPENING_REPETITION);
            props.addSeparatingOperator("^{", RegularExpressionOperators.OPENING_REPETITION);
            props.addSeparatingOperator("}", RegularExpressionOperators.CLOSING_REPETITION);
            props.addSeparatingOperator(",", RegularExpressionOperators.REPETITION_SEPARATOR);
            props.addSeparatingOperator("⁺", RegularExpressionOperators.KLEENE_PLUS);
            props.addSeparatingOperator("^+", RegularExpressionOperators.KLEENE_PLUS);
            props.addSeparatingOperator("?", RegularExpressionOperators.OPTIONAL);
            props.addSeparatingOperator("^?", RegularExpressionOperators.OPTIONAL);
            props.addSeparatingOperator("[", RegularExpressionOperators.OPENING_RANGE);
            props.addSeparatingOperator("]", RegularExpressionOperators.CLOSING_RANGE);
            props.addSeparatingOperator("-", RegularExpressionOperators.RANGE_SEPARATOR);

            props.addAllowedParenthesesSymbol(
                    ParenthesesType.BRACES,
                    RegularExpressionOperators.OPENING_REPETITION,
                    RegularExpressionOperators.CLOSING_REPETITION);
            props.addAllowedParenthesesSymbol(
                    ParenthesesType.BRACKETS,
                    RegularExpressionOperators.OPENING_RANGE,
                    RegularExpressionOperators.CLOSING_RANGE);
        }

        return props;
    }

    /*-----------------------------------------*\
     | For RUB-TI                              |
    \*-----------------------------------------*/

    /**
     * Infers the alphabet automatically and uses {@link
     * RegularSymbolSplittingPolicy#ALL_UNARY_SYMBOLS_POLICY}. The base alphabet Σ={#} will be used
     * if not a single symbol is used in the regular expression. The used domain for the Range
     * expression will consist of all characters of the english alphabet (upper case and lower case)
     * and all digits (0-9).
     */
    public static RegularExpressionReaderProperties createDefaultForRUBTI() {
        return createDefaultForRUBTI(
                RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY,
                new InferMinimalNonEmptyAlphabetPolicy(Alphabets.indexedSymbolAlphabet("#")),
                defaultDomainForRange,
                true);
    }

    /**
     * Uses the given {@code alphabet} for the constructed regular expression and additionally
     * infers a splitting policy out of it. This derived splitting policy gets merged with {@link
     * RegularSymbolSplittingPolicy#ALL_UNARY_SYMBOLS_POLICY}. The domain for the Range expression
     * will be the given {@code alphabet} merged with all characters of the english alphabet (upper
     * case and lower case) and all digits (0-9).
     */
    public static RegularExpressionReaderProperties createDefaultForRUBTI(
            Alphabet<IndexedSymbol> alphabet) {
        RegularSymbolSplittingPolicy indexedSymbolsSplittingPolicy =
                new FiniteSetSymbolSplittingPolicy(alphabet.toUnmodifiableSet())
                        .toRegularSymbolSplittingPolicy();

        return createDefaultForRUBTI(
                indexedSymbolsSplittingPolicy.or(
                        RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY),
                new GivenAlphabetPolicy(alphabet),
                Alphabets.unionOf(alphabet, defaultDomainForRange),
                true);
    }

    /**
     * Infers the alphabet automatically but every symbol read (or that is implicitly used via the
     * Range expression) must match the specified regular expression. The base alphabet Σ={#} will
     * be used if no alphabet can get inferred. The given {@code regExp} will also be used to infer
     * a splitting policy. This derived splitting policy gets merged with {@link
     * RegularSymbolSplittingPolicy#ALL_UNARY_SYMBOLS_POLICY}. The domain for the Range expression
     * will consist of all characters of the english alphabet (upper case and lower case) and all
     * digits (0-9).
     */
    public static RegularExpressionReaderProperties createDefaultForRUBTI(String regExp) {
        return createDefaultForRUBTI(
                new RegularSymbolSplittingPolicy("regExp")
                        .or(RegularSymbolSplittingPolicy.ALL_UNARY_SYMBOLS_POLICY),
                new InferFromRegexPolicy(regExp, Alphabets.indexedSymbolAlphabet("#")),
                defaultDomainForRange,
                true);
    }

    /**
     * Uses the given {@code splittingPolicy}, the given {@code alphabetInferencePolicy} and the
     * given domain for the {@link Range} expression.
     *
     * @param addDefaultOperators Indicates whether the default operators including the separation
     *     symbols and the allowed parentheses symbols shall be added.
     */
    public static RegularExpressionReaderProperties createDefaultForRUBTI(
            SymbolSplittingPolicy splittingPolicy,
            AlphabetInferencePolicy alphabetInferencePolicy,
            @Nullable Alphabet<IndexedSymbol> domainForRange,
            boolean addDefaultOperators) {

        RegularExpressionReaderProperties props = new RegularExpressionReaderProperties();
        props.setSymbolSplittingPolicy(splittingPolicy);
        props.setAlphabetInferencePolicy(alphabetInferencePolicy);
        props.setDomainForRange(domainForRange);

        if (addDefaultOperators) {
            props.addSeparatingOperator("|", RegularExpressionOperators.ALTERNATION);
            props.addSeparatingOperator("*", RegularExpressionOperators.KLEENE_STAR);
            props.addSeparatingOperator("^*", RegularExpressionOperators.KLEENE_STAR);
            props.addSeparatingOperator("+", RegularExpressionOperators.KLEENE_PLUS);
            props.addSeparatingOperator("^+", RegularExpressionOperators.KLEENE_PLUS);
            props.addSeparatingOperator("⁺", RegularExpressionOperators.KLEENE_PLUS);
            props.addSeparatingOperator("ε", RegularExpressionOperators.EPSILON);
            props.addSeparatingOperator("(", RegularExpressionOperators.OPENING_PARENTHESIS);
            props.addSeparatingOperator(")", RegularExpressionOperators.CLOSING_PARENTHESIS);
            props.addSeparatingOperator("∅", RegularExpressionOperators.EMPTY_SET);

            props.addNonSeparatingOperator("eps", RegularExpressionOperators.EPSILON);
            props.addNonSeparatingOperator("epsilon", RegularExpressionOperators.EPSILON);
            props.addNonSeparatingOperator("empty", RegularExpressionOperators.EMPTY_SET);
            props.addNonSeparatingOperator("emptyset", RegularExpressionOperators.EMPTY_SET);

            props.addSeparationSymbols(
                    StringUtils.getUnicodeWhitespaces().stream()
                            .map(Object::toString)
                            .collect(Collectors.toSet()));

            props.addAllowedParenthesesSymbol(
                    ParenthesesType.PARENTHESES,
                    RegularExpressionOperators.OPENING_PARENTHESIS,
                    RegularExpressionOperators.CLOSING_PARENTHESIS);
        }

        return props;
    }

    // Yes, I know you could do that by using a loop, but then it wouldn't be available at compile
    // time, and it needs some runtime
    private static final Alphabet<IndexedSymbol> defaultDomainForRange =
            Alphabets.indexedSymbolAlphabet(
                    "abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789");
}

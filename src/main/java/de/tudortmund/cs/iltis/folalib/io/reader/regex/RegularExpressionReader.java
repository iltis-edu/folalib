package de.tudortmund.cs.iltis.folalib.io.reader.regex;

import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultCollection;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultReason;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferencePolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.InferenceException;
import de.tudortmund.cs.iltis.folalib.io.parser.regex.RegularExpressionConstructionVisitor;
import de.tudortmund.cs.iltis.folalib.io.parser.regex.RegularExpressionOperators;
import de.tudortmund.cs.iltis.folalib.io.parser.regex.RegularExpressionParser;
import de.tudortmund.cs.iltis.folalib.io.parser.regex.fault.RegexParsingFaultReason;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import de.tudortmund.cs.iltis.utils.io.parser.customizable.CustomizableLexingReader;
import de.tudortmund.cs.iltis.utils.io.parser.error.visitor.VisitorErrorHandler;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFault;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultTypeMapping;
import de.tudortmund.cs.iltis.utils.io.parser.general.*;
import de.tudortmund.cs.iltis.utils.io.parser.parentheses.ParenthesesParsingFaultReason;
import de.tudortmund.cs.iltis.utils.io.parser.parentheses.RepairingParenthesesChecker;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.RegularSymbolSplittingPolicy;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.SymbolSplittingPolicy;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.SymbolsNotSplittableException;
import java.util.LinkedList;
import java.util.List;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Triple;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * Uses {@link RegularExpressionParser} to construct a {@link RegularExpression}.
 *
 * <p>The following faults may occur in this reader: {@link GeneralParsingFaultReason#VARIOUS},
 * {@link GeneralParsingFaultReason#INVALID_SYMBOL}, {@link ParenthesesParsingFaultReason} (all),
 * {@link RegexParsingFaultReason} (all), {@link AlphabetInferenceFaultReason} (all)
 *
 * <p>The {@link ParsingFaultTypeMapping} can contain a {@link AlphabetInferenceFaultCollection}
 * with its belonging {@link AlphabetInferenceFaultReason}s if the input contains symbols that are
 * not allowed by the chosen policy.
 */
public class RegularExpressionReader
        extends CustomizableLexingReader<
                RegularExpression<IndexedSymbol>,
                RegularExpression<IndexedSymbol>,
                RegularExpressionParser> {

    private PostLexingConversionMode currentPostLexMode;

    /**
     * Creates a new regex reader with the given properties. The {@link AlphabetInferencePolicy}s
     * for and the {@link SymbolSplittingPolicy} must be not {@code null}.
     *
     * @throws NullPointerException if the property object is {@code null}.
     */
    public RegularExpressionReader(RegularExpressionReaderProperties properties) {
        super(properties, new RepairingParenthesesChecker(properties));

        currentPostLexMode = PostLexingConversionMode.STANDARD;

        // It is important to not use the designated method to add this converter because it has to
        // be first in this
        // list.
        postLexConverters.add(0, this::changePostLexingMode);
    }

    @Override
    protected RegularExpressionParser prepareParser(TokenStream tokenStream) {
        return new RegularExpressionParser(tokenStream);
    }

    @Override
    protected RegularExpression<IndexedSymbol> executeParser(
            RegularExpressionParser parser,
            AbstractParseTreeVisitor<RegularExpression<IndexedSymbol>> visitor) {
        return visitor.visit(parser.onlyRegex());
    }

    @Override
    protected AbstractParseTreeVisitor<RegularExpression<IndexedSymbol>> prepareParseTreeVisitor(
            ParsingCreator creator, VisitorErrorHandler errorHandler) {
        RegularExpressionReaderProperties regexProperties =
                (RegularExpressionReaderProperties) properties;
        return new RegularExpressionConstructionVisitor(
                regexProperties.getDomainForRange(), regexProperties.getComparator(), errorHandler);
    }

    @Override
    protected ParsingFaultTypeMapping<RegularExpression<IndexedSymbol>>
            convertParserOutputToReaderOutput(
                    ParsingFaultTypeMapping<RegularExpression<IndexedSymbol>> mapping) {
        RegularExpressionReaderProperties regexProperties =
                (RegularExpressionReaderProperties) properties;

        RegularExpression<IndexedSymbol> mappingOutput = mapping.getOutput();
        if (mappingOutput == null) return mapping;

        AlphabetInferencePolicy alphabetInferencePolicy =
                regexProperties.getAlphabetInferencePolicy();
        try {
            Alphabet<IndexedSymbol> newAlphabet =
                    alphabetInferencePolicy.getInferredAlphabet(
                            mappingOutput.withMinimalAlphabet().getAlphabet());
            RegularExpression<IndexedSymbol> newRegex = tryWithAlphabet(mappingOutput, newAlphabet);
            return new ParsingFaultTypeMapping<>(mapping.getInput(), newRegex, mapping.getAll());
        } catch (InferenceException e) {
            // Add the alphabet inference faults to the mapping
            mapping = mapping.with(e.getFaultCollection());
            RegularExpression<IndexedSymbol> newRegex =
                    tryWithAlphabet(mappingOutput, e.getBestFitAlphabet());

            return new ParsingFaultTypeMapping<>(mapping.getInput(), newRegex, mapping.getAll());
        }
    }

    /**
     * A helper method to deal with possible error occurring while calling {@link
     * RegularExpression#withAlphabet(Alphabet)}.
     */
    private RegularExpression<IndexedSymbol> tryWithAlphabet(
            RegularExpression<IndexedSymbol> regex, Alphabet<IndexedSymbol> alphabet) {
        try {
            return regex.withAlphabet(alphabet);
        } catch (Exception withAlphabetError) {
            throw new RuntimeException(
                    "Internal error: The alphabet returned by the inference policy is not "
                            + "applicable for the constructed regular expression. [Original fault message: "
                            + withAlphabetError.getMessage()
                            + "]");
        }
    }

    /**
     * This is a postlex converter which is used to change the reader mode based on the current
     * reader mode and the current input token. It is used in the constructor of this class
     *
     * @return Will always return {@code null} so the standard postlex converter will still be
     *     called.
     */
    private Triple<List<Token>, List<ParsingFault>, Boolean> changePostLexingMode(Token token) {
        if (currentPostLexMode == PostLexingConversionMode.STANDARD
                && token.getType() == RegularExpressionOperators.OPENING_REPETITION.getTokenType())
            currentPostLexMode = PostLexingConversionMode.REPETITION;
        else if (currentPostLexMode == PostLexingConversionMode.REPETITION
                && token.getType() == RegularExpressionOperators.CLOSING_REPETITION.getTokenType())
            currentPostLexMode = PostLexingConversionMode.STANDARD;

        // Used for fixed repetition ("a^n"):
        else if (currentPostLexMode == PostLexingConversionMode.STANDARD
                && token.getType()
                        == RegularExpressionOperators.FIXED_REPETITION_OPENER.getTokenType())
            currentPostLexMode = PostLexingConversionMode.NEXT_TOKEN_IS_FIXED_REPETITION;
        else if (currentPostLexMode == PostLexingConversionMode.NEXT_TOKEN_IS_FIXED_REPETITION
                && token.getType() != RegularExpressionOperators.WHITESPACE.getTokenType())
            currentPostLexMode = PostLexingConversionMode.THIS_TOKEN_IS_FIXED_REPETITION;
        else if (currentPostLexMode == PostLexingConversionMode.THIS_TOKEN_IS_FIXED_REPETITION
                && token.getType() != RegularExpressionOperators.WHITESPACE.getTokenType())
            currentPostLexMode = PostLexingConversionMode.STANDARD;

        return null;
    }

    /**
     * Used in {@link #registerDefaultPostLexConverters()}. Uses a {@link SymbolSplittingPolicy} to
     * split a string into (multiple) indexed symbols.
     *
     * <p>If the current reader mode of {@code this} is {@link PostLexingConversionMode
     * ::REPETITION} the {@link RegularSymbolSplittingPolicy::NUMBER} splitting policy gets used and
     * the tokens will be of type {@link RegularExpressionOperators::REPETITION_OPERAND}.
     */
    @Override
    protected Triple<List<Token>, List<ParsingFault>, Boolean> splitIntoISYMBOLs(
            Token oldToken, int newType) {
        if (currentPostLexMode == PostLexingConversionMode.STANDARD) {
            return super.splitIntoISYMBOLs(oldToken, newType);
        } else if (currentPostLexMode == PostLexingConversionMode.REPETITION
                || currentPostLexMode == PostLexingConversionMode.THIS_TOKEN_IS_FIXED_REPETITION) {

            return handleRepetitionOperandToken(oldToken);
        }

        // Any other mode should be unreachable here
        throw new RuntimeException("Internal error in RegularExpressionReader");
    }

    private Triple<List<Token>, List<ParsingFault>, Boolean> handleRepetitionOperandToken(
            Token oldToken) {
        List<ParsingFault> faultList = new LinkedList<>();
        List<Pair<Integer, IndexedSymbol>> symbols;

        try {
            symbols = RegularSymbolSplittingPolicy.NUMBERS_POLICY.splitSymbols(oldToken.getText());
        } catch (SymbolsNotSplittableException e) {
            faultList.add(
                    new ParsingFault(
                            RegexParsingFaultReason.REPETITION_OPERAND_NOT_NUMERIC,
                            oldToken.getLine() - 1,
                            oldToken.getCharPositionInLine(),
                            oldToken.getText()));

            if (currentPostLexMode == PostLexingConversionMode.THIS_TOKEN_IS_FIXED_REPETITION) {
                // For fixed repetition (e.g. a^3 instead of a{3}) we bail out if the repetition
                // operand is not
                // numeric
                return new Triple<>(new LinkedList<>(), faultList, true);
            } else {
                // In any other case we can simply discard this token and search for another numeric
                // operand
                return new Triple<>(new LinkedList<>(), faultList, isBailOutInLexer);
            }
        }

        List<Token> newTokenList =
                getNewTokenList(
                        oldToken,
                        symbols,
                        RegularExpressionOperators.REPETITION_OPERAND.getTokenType());
        return new Triple<>(newTokenList, faultList, false);
    }

    /** Based on the current mode of the reader the post lexing process can differ. */
    private enum PostLexingConversionMode {
        STANDARD,
        REPETITION,
        NEXT_TOKEN_IS_FIXED_REPETITION, // Used for the "a^n" syntax
        THIS_TOKEN_IS_FIXED_REPETITION // Used for the "a^n" syntax
    }
}

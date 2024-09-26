package de.tudortmund.cs.iltis.folalib.io.reader.sentential;

import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultCollection;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultReason;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferencePolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.InferenceException;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.GrammarConstructionVisitor;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.GrammarParser;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.fault.GrammarParsingFaultReason;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.io.parser.customizable.CustomizableLexingReader;
import de.tudortmund.cs.iltis.utils.io.parser.error.visitor.VisitorErrorHandler;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultTypeMapping;
import de.tudortmund.cs.iltis.utils.io.parser.general.GeneralParsingFaultReason;
import de.tudortmund.cs.iltis.utils.io.parser.general.ParsingCreator;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * Reads a {@link SententialForm} of {@link IndexedSymbol}s bases on the properties given in {@link
 * SententialFormReaderProperties}. This reader uses the {@link GrammarParser}.
 *
 * <p>The following faults may occur in this reader: {@link GeneralParsingFaultReason#VARIOUS},
 * {@link GeneralParsingFaultReason#INVALID_SYMBOL}, {@link
 * GrammarParsingFaultReason#ABUNDANT_EPSILONS}, {@link
 * GrammarParsingFaultReason#SYMBOL_EPSILON_MIX}, {@link
 * GrammarParsingFaultReason#AMBIGUOUS_SYMBOL}, {@link
 * GrammarParsingFaultReason#UNKNOWN_SENTENTIAL_FORM}, {@link
 * GrammarParsingFaultReason#BLANK_INPUT}, {@link AlphabetInferenceFaultReason} (all)
 *
 * <p>The {@link ParsingFaultTypeMapping} can contain a {@link AlphabetInferenceFaultCollection}
 * with its belonging {@link AlphabetInferenceFaultReason}s if the input contains symbols that are
 * not allowed by the chosen policy.
 */
public class SententialFormReader
        extends CustomizableLexingReader<
                SententialForm<IndexedSymbol, IndexedSymbol>,
                SententialForm<IndexedSymbol, IndexedSymbol>,
                GrammarParser> {

    public SententialFormReader(SententialFormReaderProperties props) {
        super(props);
    }

    @Override
    protected GrammarParser prepareParser(TokenStream tokenStream) {
        return new GrammarParser(tokenStream);
    }

    @Override
    protected AbstractParseTreeVisitor<SententialForm<IndexedSymbol, IndexedSymbol>>
            prepareParseTreeVisitor(ParsingCreator creator, VisitorErrorHandler errorHandler) {
        return new GrammarConstructionVisitor.SententialFormVisitor(
                ((SententialFormReaderProperties) properties).getInputToGrammarSymbolConverter(),
                errorHandler);
    }

    @Override
    protected SententialForm<IndexedSymbol, IndexedSymbol> executeParser(
            GrammarParser parser,
            AbstractParseTreeVisitor<SententialForm<IndexedSymbol, IndexedSymbol>> visitor) {
        ParserRuleContext ctx = parser.sententialFormOrEmtpy();
        if (isVerbose()) System.out.println("tree: " + ctx.toStringTree());
        return visitor.visit(ctx);
    }

    @Override
    protected ParsingFaultTypeMapping<SententialForm<IndexedSymbol, IndexedSymbol>>
            convertParserOutputToReaderOutput(
                    ParsingFaultTypeMapping<SententialForm<IndexedSymbol, IndexedSymbol>> mapping) {
        SententialForm<IndexedSymbol, IndexedSymbol> parserOutput = mapping.getOutput();

        // The parser's output is null if the parser or visitor bailed out
        if (parserOutput == null) return mapping;

        // Check alphabets for forbidden symbols
        AlphabetInferenceFaultCollection inferenceFaultCollection = checkAlphabets(parserOutput);
        if (inferenceFaultCollection.containsAnyFault())
            mapping = mapping.with(inferenceFaultCollection);

        return mapping;
    }

    /**
     * Checks both terminals and non-terminals based on the inference policies defined in the {@link
     * SententialFormReaderProperties}.
     *
     * @return An {@link AlphabetInferenceFaultCollection}, possibly empty.
     */
    private AlphabetInferenceFaultCollection checkAlphabets(
            SententialForm<IndexedSymbol, IndexedSymbol> sententialForm) {
        SententialFormReaderProperties grammarProps = (SententialFormReaderProperties) properties;

        AlphabetInferencePolicy terminalAlphabetInferencePolicy =
                grammarProps.getTerminalAlphabetInferencePolicy();
        AlphabetInferencePolicy nonTerminalAlphabetInferencePolicy =
                grammarProps.getNonTerminalAlphabetInferencePolicy();
        AlphabetInferenceFaultCollection inferenceFaults = new AlphabetInferenceFaultCollection();

        try {
            // The inferred alphabet is useless here. We only use the inference to validate the used
            // symbols
            terminalAlphabetInferencePolicy.getInferredAlphabet(
                    new Alphabet<>(sententialForm.getTerminals()));
        } catch (InferenceException e) {
            inferenceFaults = inferenceFaults.withFaults(e.getFaultCollection());
        }
        try {
            // The inferred alphabet is useless here. We only use the inference to validate the used
            // symbols
            nonTerminalAlphabetInferencePolicy.getInferredAlphabet(
                    new Alphabet<>(sententialForm.getNonTerminals()));
        } catch (InferenceException e) {
            inferenceFaults = inferenceFaults.withFaults(e.getFaultCollection());
        }

        return inferenceFaults;
    }
}

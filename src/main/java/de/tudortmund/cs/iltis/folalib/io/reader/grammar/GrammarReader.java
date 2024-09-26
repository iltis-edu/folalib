package de.tudortmund.cs.iltis.folalib.io.reader.grammar;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.construction.specialization.IGrammarSpecialization;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultCollection;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultReason;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferencePolicy;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.InferenceException;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.GrammarConstructionVisitor;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.GrammarParser;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.fault.GrammarParsingFaultReason;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.InputToGrammarSymbolConverter;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol.StartSymbolDerivationException;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.util.Result;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.collections.FaultCollection;
import de.tudortmund.cs.iltis.utils.collections.ListSet;
import de.tudortmund.cs.iltis.utils.io.parser.customizable.CustomizableLexingReader;
import de.tudortmund.cs.iltis.utils.io.parser.error.IncorrectParseInputException;
import de.tudortmund.cs.iltis.utils.io.parser.error.visitor.VisitorErrorHandler;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFault;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultCollection;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultTypeMapping;
import de.tudortmund.cs.iltis.utils.io.parser.general.GeneralParsingFaultReason;
import de.tudortmund.cs.iltis.utils.io.parser.general.ParsingCreator;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.SymbolSplittingPolicy;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Triple;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * Reads a string input and parses it to a {@link Grammar} object if possible. Uses {@link
 * GrammarParser}. No grammar-related (semantic) faults get checked here, only the syntax of the
 * grammar (see {@link GrammarParsingFaultReason}). I.e. the faults from {@link
 * GrammarConstructionFaultReason} will <b>not</b> be added to the output fault collection.
 *
 * <p>If the parsed grammar is supposed to be specialized to a specific type of the chomsky
 * hierarchy, consider using {@link C0GrammarReader}, {@link ContextSensitiveGrammarReader}, {@link
 * ContextFreeGrammarReader}, {@link RightRegularGrammarReader} or {@link LeftRegularGrammarReader}.
 *
 * <p>The following faults may occur in this reader: {@link GeneralParsingFaultReason#VARIOUS},
 * {@link GeneralParsingFaultReason#INVALID_SYMBOL}, {@link GrammarParsingFaultReason} (all), {@link
 * AlphabetInferenceFaultReason} (all)
 *
 * <p>The {@link ParsingFaultTypeMapping} can contain a {@link AlphabetInferenceFaultCollection}
 * with its belonging {@link AlphabetInferenceFaultReason}s if the input contains symbols that are
 * not allowed by the chosen policy.
 */
public class GrammarReader
        extends CustomizableLexingReader<
                List<Production<IndexedSymbol, IndexedSymbol>>,
                Grammar<IndexedSymbol, IndexedSymbol, Production<IndexedSymbol, IndexedSymbol>>,
                GrammarParser> {

    /**
     * Creates a new grammar reader with the given properties. Both {@link AlphabetInferencePolicy}s
     * for terminals and non-terminals, the {@link InputToGrammarSymbolConverter}, the {@link
     * SymbolSplittingPolicy} and the start non-terminal must be not {@code null}.
     *
     * <p><b>This reader does not check for semantic correctness!</b>
     *
     * @throws NullPointerException if the property object is {@code null}.
     */
    public GrammarReader(GrammarReaderProperties properties) {
        super(properties);
    }

    @Override
    protected GrammarParser prepareParser(TokenStream tokenStream) {
        return new GrammarParser(tokenStream);
    }

    @Override
    protected AbstractParseTreeVisitor<List<Production<IndexedSymbol, IndexedSymbol>>>
            prepareParseTreeVisitor(ParsingCreator creator, VisitorErrorHandler errorHandler) {
        return new GrammarConstructionVisitor(
                ((GrammarReaderProperties) properties).getInputToGrammarSymbolConverter(),
                errorHandler);
    }

    @Override
    protected List<Production<IndexedSymbol, IndexedSymbol>> executeParser(
            GrammarParser parser,
            AbstractParseTreeVisitor<List<Production<IndexedSymbol, IndexedSymbol>>> visitor) {
        ParserRuleContext ctx = parser.entry();
        if (isVerbose()) System.out.println("tree: " + ctx.toStringTree());
        return visitor.visit(ctx);
    }

    @Override
    protected ParsingFaultTypeMapping<
                    Grammar<IndexedSymbol, IndexedSymbol, Production<IndexedSymbol, IndexedSymbol>>>
            convertParserOutputToReaderOutput(
                    ParsingFaultTypeMapping<List<Production<IndexedSymbol, IndexedSymbol>>>
                            mapping) {

        // If any of the following steps require to return an empty ParsingFaultTypeMapping, this
        // one can be returned
        // (possibly extended by additional faults). The output is always null then.
        ParsingFaultTypeMapping<
                        Grammar<
                                IndexedSymbol,
                                IndexedSymbol,
                                Production<IndexedSymbol, IndexedSymbol>>>
                errorFaultMapping =
                        new ParsingFaultTypeMapping<>(mapping.getInput(), null, mapping.getAll());

        List<Production<IndexedSymbol, IndexedSymbol>> parserOutput = mapping.getOutput();

        // The parser's output is null if the parser or visitor bailed out
        if (parserOutput == null) return errorFaultMapping;

        // If the parser actually constructed a list of productions we build a grammar from this.

        // First, infer the alphabets that shall be used
        Triple<Alphabet<IndexedSymbol>, Alphabet<IndexedSymbol>, AlphabetInferenceFaultCollection>
                alphabetInferenceOutput = executeAlphabetInference(parserOutput);
        if (alphabetInferenceOutput.c.containsAnyFault())
            errorFaultMapping =
                    errorFaultMapping.with(
                            alphabetInferenceOutput
                                    .c); // Does not override the ParsingFaultCollection if present

        // Then, derive the start non-terminal that shall be used. If that fails, return null as
        // input because a grammar cannot be built.
        Result<IndexedSymbol, ParsingFault> startSymbolDerivationOutput =
                deriveStartSymbol(parserOutput);
        if (startSymbolDerivationOutput.match(ok -> false, err -> true)) {
            return extendFaultMappingWithParsingFault(
                    errorFaultMapping, startSymbolDerivationOutput.unwrapErr());
        }

        // Then, build the actual grammar
        return buildGrammarAndWrapIntoTypeMapping(
                startSymbolDerivationOutput.unwrap(),
                alphabetInferenceOutput.a,
                alphabetInferenceOutput.b,
                parserOutput,
                errorFaultMapping.getInput(),
                errorFaultMapping.getAll());
    }

    /**
     * Checks both terminals and non-terminals based on the inference policies defined in the {@link
     * GrammarReaderProperties}.
     *
     * @return A triple containing the two alphabets to use (always present) and an {@link
     *     AlphabetInferenceFaultCollection}, possibly empty.
     */
    private Triple<
                    Alphabet<IndexedSymbol>,
                    Alphabet<IndexedSymbol>,
                    AlphabetInferenceFaultCollection>
            executeAlphabetInference(List<Production<IndexedSymbol, IndexedSymbol>> parserOutput) {
        GrammarReaderProperties grammarProps = (GrammarReaderProperties) properties;

        AlphabetInferencePolicy terminalAlphabetInferencePolicy =
                grammarProps.getTerminalAlphabetInferencePolicy();
        AlphabetInferencePolicy nonTerminalAlphabetInferencePolicy =
                grammarProps.getNonTerminalAlphabetInferencePolicy();
        Alphabet<IndexedSymbol> newTerminalAlphabet;
        Alphabet<IndexedSymbol> newNonTerminalAlphabet;
        AlphabetInferenceFaultCollection inferenceFaults = new AlphabetInferenceFaultCollection();

        try {
            newTerminalAlphabet =
                    terminalAlphabetInferencePolicy.getInferredAlphabet(
                            getMinimalTerminalAlphabet(parserOutput));
        } catch (InferenceException e) {
            inferenceFaults = inferenceFaults.withFaults(e.getFaultCollection());
            newTerminalAlphabet = e.getBestFitAlphabet();
        }
        try {
            newNonTerminalAlphabet =
                    nonTerminalAlphabetInferencePolicy.getInferredAlphabet(
                            getMinimalNonTerminalAlphabet(parserOutput));
        } catch (InferenceException e) {
            inferenceFaults = inferenceFaults.withFaults(e.getFaultCollection());
            newNonTerminalAlphabet = e.getBestFitAlphabet();
        }

        return new Triple<>(newTerminalAlphabet, newNonTerminalAlphabet, inferenceFaults);
    }

    /**
     * @return An {@link Alphabet} of all terminals actually used in all {@param productions}.
     */
    private Alphabet<IndexedSymbol> getMinimalTerminalAlphabet(
            List<Production<IndexedSymbol, IndexedSymbol>> productions) {
        ListSet<IndexedSymbol> usedTerminals = new ListSet<>();
        productions.forEach(production -> usedTerminals.addAll(production.getTerminals()));
        return new Alphabet<>(usedTerminals);
    }

    /**
     * @return An {@link Alphabet} of all non-terminals actually used in all {@param productions}.
     */
    private Alphabet<IndexedSymbol> getMinimalNonTerminalAlphabet(
            List<Production<IndexedSymbol, IndexedSymbol>> productions) {
        ListSet<IndexedSymbol> usedNonTerminals = new ListSet<>();
        productions.forEach(production -> usedNonTerminals.addAll(production.getNonTerminals()));
        return new Alphabet<>(usedNonTerminals);
    }

    /**
     * @return If successful, the derived start symbol is returned. If not successful, the generated
     *     {@link ParsingFault} is returned.
     */
    private Result<IndexedSymbol, ParsingFault> deriveStartSymbol(
            List<Production<IndexedSymbol, IndexedSymbol>> productions) {
        try {
            GrammarReaderProperties grammarProps = (GrammarReaderProperties) properties;
            IndexedSymbol startSymbol =
                    grammarProps.getStartSymbolDerivationStrategy().deriveStartSymbol(productions);
            return new Result.Ok<>(startSymbol);
        } catch (StartSymbolDerivationException e) {
            return new Result.Err<>(
                    new ParsingFault(
                            GrammarParsingFaultReason.START_SYMBOL_NOT_DERIVABLE, 0, 0, ""));
        }
    }

    /**
     * Extends the given {@param mapping} with a new {@link ParsingFault}. If the mapping already
     * contains a {@link ParsingFaultCollection}, this collection gets extended by {@param newFault}
     * (the previous faults in this collection are preserved). If the mapping does not already
     * contain a {@link ParsingFaultCollection}, a new one containing the {@param newFault} will be
     * created.
     *
     * <p>Other {@link FaultCollection}s contained in the {@param mapping} are also preserved.
     *
     * @return The previous {@param mapping} but extended by {@param newFault}
     */
    private ParsingFaultTypeMapping<
                    Grammar<IndexedSymbol, IndexedSymbol, Production<IndexedSymbol, IndexedSymbol>>>
            extendFaultMappingWithParsingFault(
                    ParsingFaultTypeMapping<
                                    Grammar<
                                            IndexedSymbol,
                                            IndexedSymbol,
                                            Production<IndexedSymbol, IndexedSymbol>>>
                            mapping,
                    ParsingFault newFault) {

        ParsingFaultCollection faultCollection =
                (ParsingFaultCollection) mapping.get(ParsingFaultCollection.class);
        // In case there is no ParsingFaultCollection present (no faults found previously)
        if (faultCollection == null) faultCollection = new ParsingFaultCollection();

        faultCollection = faultCollection.withFault(newFault);
        // This ParsingFaultCollection will override the previous one while keeping all other fault
        // collections (e.g. alphabet inference)
        return mapping.with(faultCollection);
    }

    /**
     * Builds a grammar based on the given parameters and wraps it into a {@link
     * ParsingFaultTypeMapping}.
     */
    private ParsingFaultTypeMapping<
                    Grammar<IndexedSymbol, IndexedSymbol, Production<IndexedSymbol, IndexedSymbol>>>
            buildGrammarAndWrapIntoTypeMapping(
                    IndexedSymbol startSymbol,
                    Alphabet<IndexedSymbol> terminals,
                    Alphabet<IndexedSymbol> nonTerminals,
                    List<Production<IndexedSymbol, IndexedSymbol>> productions,
                    String parserInput,
                    Map<String, FaultCollection<?, ?>> faultCollections) {

        Grammar<IndexedSymbol, IndexedSymbol, Production<IndexedSymbol, IndexedSymbol>> grammar =
                new Grammar<>(terminals, nonTerminals, startSymbol, productions);
        return new ParsingFaultTypeMapping<>(parserInput, grammar, faultCollections);
    }

    @SuppressWarnings("unchecked")
    /*package-private*/ static <
                    Prod extends Production<IndexedSymbol, IndexedSymbol>,
                    Gram extends Grammar<IndexedSymbol, IndexedSymbol, Prod>>
            Gram parseAndSpecializeInput(
                    String inputString,
                    GrammarReaderProperties properties,
                    IGrammarSpecialization<IndexedSymbol, IndexedSymbol, Prod, Gram> specializer)
                    throws IncorrectParseInputException {

        Grammar<IndexedSymbol, IndexedSymbol, Production<IndexedSymbol, IndexedSymbol>>
                parsedGrammar;
        ParsingFaultTypeMapping<?> generalReaderMapping;
        try {
            parsedGrammar = new GrammarReader(properties).read(inputString);
            generalReaderMapping = new ParsingFaultTypeMapping<>(inputString, null);
        } catch (IncorrectParseInputException e) {
            parsedGrammar =
                    (Grammar<
                                    IndexedSymbol,
                                    IndexedSymbol,
                                    Production<IndexedSymbol, IndexedSymbol>>)
                            e.getFaultMapping().getOutput(); // Can be either a grammar or null
            generalReaderMapping =
                    new ParsingFaultTypeMapping<>(inputString, null, e.getFaultMapping().getAll());
        }

        if (parsedGrammar == null) throw new IncorrectParseInputException(generalReaderMapping);

        // If a grammar was built (with possible faults) we can try to cast it down to a higher-type
        // grammar and add
        // more faults if necessary
        Result<Gram, GrammarConstructionFaultCollection> resultGrammar =
                specializer.specialize(parsedGrammar);

        ParsingFaultTypeMapping<?> finalGeneralReaderMapping =
                generalReaderMapping; // Because of the lambda expression, avoids bugs
        return resultGrammar.match(
                constructedGrammar -> {
                    if (finalGeneralReaderMapping.containsAny()) {
                        ParsingFaultTypeMapping<?> newMapping =
                                new ParsingFaultTypeMapping<>(
                                        finalGeneralReaderMapping.getInput(),
                                        constructedGrammar,
                                        finalGeneralReaderMapping.getAll());
                        throw new IncorrectParseInputException(newMapping);
                    }

                    return constructedGrammar;
                },
                constructionFaultCollection -> {
                    ParsingFaultTypeMapping<?> newMapping =
                            finalGeneralReaderMapping.with(constructionFaultCollection);
                    throw new IncorrectParseInputException(newMapping);
                });
    }
}

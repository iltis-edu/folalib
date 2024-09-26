package de.tudortmund.cs.iltis.folalib.io.reader.word;

import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultCollection;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultReason;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferMinimalAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.fault.GrammarParsingFaultReason;
import de.tudortmund.cs.iltis.folalib.io.reader.sentential.SententialFormReader;
import de.tudortmund.cs.iltis.folalib.io.reader.sentential.SententialFormReaderProperties;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.io.parser.error.IncorrectParseInputException;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultTypeMapping;
import de.tudortmund.cs.iltis.utils.io.parser.general.GeneralParsingFaultReason;
import de.tudortmund.cs.iltis.utils.io.reader.general.Reader;
import java.util.stream.Collectors;

/**
 * Reads a {@link Word} of {@link IndexedSymbol}s bases on the properties given in {@link
 * WordReaderProperties}. This reader uses the {@link SententialFormReader} internally.
 *
 * <p>The following faults may occur in this reader: {@link GeneralParsingFaultReason#VARIOUS},
 * {@link GeneralParsingFaultReason#INVALID_SYMBOL}, {@link
 * GrammarParsingFaultReason#ABUNDANT_EPSILONS}, {@link
 * GrammarParsingFaultReason#SYMBOL_EPSILON_MIX}, {@link GrammarParsingFaultReason#BLANK_INPUT},
 * {@link AlphabetInferenceFaultReason} (all)
 *
 * <p>The {@link ParsingFaultTypeMapping} can contain a {@link AlphabetInferenceFaultCollection}
 * with its belonging {@link AlphabetInferenceFaultReason}s if the input contains symbols that are
 * not allowed by the chosen policy.
 */
public class WordReader implements Reader<Word<IndexedSymbol>> {

    private SententialFormReader internalSententialFormReader;

    public WordReader(WordReaderProperties properties) {
        setupInternalReader(properties);
    }

    private void setupInternalReader(WordReaderProperties properties) {
        SententialFormReaderProperties sententialFormReaderProperties =
                SententialFormReaderProperties.createDefault(
                        properties.getSymbolSplittingPolicy(),
                        properties.getAlphabetInferencePolicy(),
                        new InferMinimalAlphabetPolicy(), // Suitable because every symbol will be a
                        // terminal
                        GrammarSymbol.Terminal::new, // Map every symbol to a terminal
                        false);
        sententialFormReaderProperties.addSeparatingOperators(properties.getSeparatingOperators());
        sententialFormReaderProperties.addNonSeparatingOperators(
                properties.getNonSeparatingOperators());
        sententialFormReaderProperties.addSeparationSymbols(properties.getSeparationSymbols());

        internalSententialFormReader = new SententialFormReader(sententialFormReaderProperties);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Word<IndexedSymbol> read(Object o) throws IncorrectParseInputException {
        try {
            return new Word<>(
                    internalSententialFormReader.read(o).stream()
                            .map(
                                    GrammarSymbol
                                            ::unwrapTerminal) // Calling unwrapTerminal here is fine
                            // because there cannot be
                            // non-terminals in the output
                            .collect(Collectors.toList()));
        } catch (IncorrectParseInputException e) {
            // In case an error occurred we also need to build a Word out of the thrown sentential
            // form if present

            ParsingFaultTypeMapping<SententialForm<IndexedSymbol, IndexedSymbol>> oldMapping =
                    (ParsingFaultTypeMapping<SententialForm<IndexedSymbol, IndexedSymbol>>)
                            e.getFaultMapping(); // We know that this is correct

            ParsingFaultTypeMapping<Word<IndexedSymbol>> newMapping;

            if (oldMapping.getOutput() == null)
                newMapping =
                        new ParsingFaultTypeMapping<>(
                                oldMapping.getInput(), null, oldMapping.getAll());
            else
                newMapping =
                        new ParsingFaultTypeMapping<>(
                                oldMapping.getInput(),
                                new Word<>(
                                        oldMapping.getOutput().stream()
                                                .map(GrammarSymbol::unwrapTerminal) // Calling
                                                // unwrapTerminal
                                                // here is fine
                                                // because there
                                                // cannot be
                                                // non-terminals
                                                // in the output
                                                .collect(Collectors.toList())),
                                oldMapping.getAll());

            throw new IncorrectParseInputException(newMapping);
        }
    }
}

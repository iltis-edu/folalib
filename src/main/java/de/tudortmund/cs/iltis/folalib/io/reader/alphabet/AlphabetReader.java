package de.tudortmund.cs.iltis.folalib.io.reader.alphabet;

import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard.InferMinimalAlphabetPolicy;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.fault.GrammarParsingFaultReason;
import de.tudortmund.cs.iltis.folalib.io.reader.word.WordReader;
import de.tudortmund.cs.iltis.folalib.io.reader.word.WordReaderProperties;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.StringUtils;
import de.tudortmund.cs.iltis.utils.io.parser.error.IncorrectParseInputException;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFault;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultCollection;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultTypeMapping;
import de.tudortmund.cs.iltis.utils.io.parser.general.GeneralParsingFaultReason;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.SymbolSplittingPolicy;
import de.tudortmund.cs.iltis.utils.io.reader.general.Reader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reads an Alphabet of {@link IndexedSymbol}s based on a given {@link SymbolSplittingPolicy}. Uses
 * {@link WordReader} internally.
 *
 * <p>All whitespaces of {@link StringUtils#getUnicodeWhitespaces()} are silently ignored. An empty
 * input leads to an empty alphabet. No faults will be thrown in that case.
 *
 * <p>The following faults may occur in this reader: {@link GeneralParsingFaultReason#VARIOUS},
 * {@link GeneralParsingFaultReason#INVALID_SYMBOL}
 */
public class AlphabetReader implements Reader<Alphabet<IndexedSymbol>> {

    private WordReader internalWordReader;

    /** Uses the given {@param splittingPolicy} to split the symbols and read an alphabet. */
    public AlphabetReader(SymbolSplittingPolicy splittingPolicy) {
        setupInternalReader(splittingPolicy);
    }

    private void setupInternalReader(SymbolSplittingPolicy splittingPolicy) {
        WordReaderProperties wordReaderProperties =
                WordReaderProperties.createDefault(
                        splittingPolicy,
                        new InferMinimalAlphabetPolicy(), // The alphabet inference/restriction is
                        // always irrelevant because this reader
                        // is the one used to determine the
                        // alphabet
                        false);
        // Add non line breaking whitespaces as separation symbols. This is needed because the
        // convenience method does not do that because it would also add the epsilon symbols
        wordReaderProperties.addSeparationSymbols(
                StringUtils.getUnicodeWhitespaces().stream()
                        .map(Object::toString)
                        .collect(Collectors.toSet()));

        internalWordReader = new WordReader(wordReaderProperties);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Alphabet<IndexedSymbol> read(Object o) throws IncorrectParseInputException {
        try {
            return new Alphabet<>(internalWordReader.read(o).toUnmodifiableList());
        } catch (IncorrectParseInputException e) {
            // If the input is empty we catch the fault and return an empty alphabet instead
            List<ParsingFault> faults =
                    (List<ParsingFault>)
                            e.getFaultMapping()
                                    .get(ParsingFaultCollection.class)
                                    .getFaults(); // This is the only type of fault collection that
            // can be thrown here
            if (faults.size() == 1
                    && faults.stream()
                            .anyMatch(
                                    fault ->
                                            fault.getReason()
                                                    == GrammarParsingFaultReason.BLANK_INPUT))
                return new Alphabet<>();

            // In case an error occurred we also need to build an Alphabet out of the thrown Word if
            // present
            ParsingFaultTypeMapping<Word<IndexedSymbol>> oldMapping =
                    (ParsingFaultTypeMapping<Word<IndexedSymbol>>)
                            e.getFaultMapping(); // We know that this is correct

            ParsingFaultTypeMapping<Alphabet<IndexedSymbol>> newMapping;

            if (oldMapping.getOutput() == null)
                newMapping =
                        new ParsingFaultTypeMapping<>(
                                oldMapping.getInput(), null, oldMapping.getAll());
            else
                newMapping =
                        new ParsingFaultTypeMapping<>(
                                oldMapping.getInput(),
                                new Alphabet<>(oldMapping.getOutput().toUnmodifiableList()),
                                oldMapping.getAll());

            throw new IncorrectParseInputException(newMapping);
        }
    }
}

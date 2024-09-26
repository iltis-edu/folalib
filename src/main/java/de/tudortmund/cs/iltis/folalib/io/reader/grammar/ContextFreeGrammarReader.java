package de.tudortmund.cs.iltis.folalib.io.reader.grammar;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.construction.specialization.GrammarToContextFreeGrammarSpecialization;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultCollection;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferenceFaultReason;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.fault.GrammarParsingFaultReason;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.io.parser.error.IncorrectParseInputException;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultCollection;
import de.tudortmund.cs.iltis.utils.io.parser.fault.ParsingFaultTypeMapping;
import de.tudortmund.cs.iltis.utils.io.parser.general.GeneralParsingFaultReason;
import de.tudortmund.cs.iltis.utils.io.reader.general.Reader;
import java.util.Objects;

/**
 * Uses the {@link GrammarReader} to parse the input into a {@link Grammar}. After that, the parsed
 * grammar will be specialized into a type 2 grammar in the chomsky hierarchy.
 *
 * <p>If faults in either of those two processes occur a {@link ParsingFaultTypeMapping} will be
 * thrown. There are four different cases: <br>
 * <br>
 * <b> 1) No specialization fault occurred & No parsing fault occurred</b> <br>
 * The specialized grammar gets returned. <br>
 * <br>
 * <b> 2) No specialization fault occurred & Parsing fault occurred</b> <br>
 * The parsed and repaired grammar gets specialized and wrapped in a thrown fault type mapping. The
 * fault type mapping contains a {@link ParsingFaultCollection}. <br>
 * <br>
 * <b> 3) Specialization fault occurred & No parsing fault occurred</b> <br>
 * A fault type mapping with no output ({@code null}) gets thrown. The fault type mapping contains a
 * {@link GrammarConstructionFaultCollection}. <br>
 * <br>
 * <b> 4) Specialization fault occurred & Parsing fault occurred</b> <br>
 * A fault type mapping with no output ({@code null}) gets thrown. The fault type mapping contains a
 * {@link ParsingFaultCollection} and a {@link GrammarConstructionFaultCollection}. <br>
 * The following faults may occur in this reader: {@link GeneralParsingFaultReason#VARIOUS}, {@link
 * GeneralParsingFaultReason#INVALID_SYMBOL}, {@link GrammarParsingFaultReason} (all), {@link
 * AlphabetInferenceFaultReason} (all), {@link GrammarConstructionFaultReason} (see docs to identify
 * applicable faults)
 *
 * <p>The {@link ParsingFaultTypeMapping} can contain a {@link AlphabetInferenceFaultCollection}
 * with its belonging {@link AlphabetInferenceFaultReason}s if the input contains symbols that are
 * not allowed by the chosen policy.
 *
 * <p>The {@link ParsingFaultTypeMapping} can contain a {@link GrammarConstructionFaultCollection}
 * with its belonging {@link GrammarConstructionFaultReason}s if the specialization of the parsed
 * grammar failed.
 */
public class ContextFreeGrammarReader
        implements Reader<
                ContextFreeGrammar<
                        IndexedSymbol,
                        IndexedSymbol,
                        ContextFreeProduction<IndexedSymbol, IndexedSymbol>>> {

    private GrammarReaderProperties properties;

    public ContextFreeGrammarReader(GrammarReaderProperties properties) {
        Objects.requireNonNull(properties);
        this.properties = properties;
    }

    @Override
    public ContextFreeGrammar<
                    IndexedSymbol,
                    IndexedSymbol,
                    ContextFreeProduction<IndexedSymbol, IndexedSymbol>>
            read(Object o) throws IncorrectParseInputException {
        return GrammarReader.parseAndSpecializeInput(
                o.toString(), properties, new GrammarToContextFreeGrammarSpecialization<>());
    }
}

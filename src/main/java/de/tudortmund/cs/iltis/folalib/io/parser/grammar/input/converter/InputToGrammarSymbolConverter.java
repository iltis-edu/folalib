package de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter;

import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.io.Serializable;

/**
 * An input to grammar symbol converter is capable of converting an input as {@link IndexedSymbol}
 * into either a {@link GrammarSymbol.Terminal} or into a {@link GrammarSymbol.NonTerminal}. This
 * process is highly interleaved with the parsing process and acts as a black box to the parser. It
 * is important to ensure that the set for terminals and the set for non-terminals do not intersect.
 */
public interface InputToGrammarSymbolConverter extends Serializable {

    GrammarSymbol<IndexedSymbol, IndexedSymbol> convertInput(IndexedSymbol input)
            throws InputConversionException;
}

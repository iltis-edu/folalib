package de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.GrammarParser;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.io.Serializable;
import java.util.List;

/**
 * A start symbol derivation strategy is capable of deriving a start symbol from the interpreted
 * productions by the {@link GrammarParser}. This symbol can then be used to build the actual {@link
 * Grammar} object. A start symbol derivation strategy can throw a {@link
 * StartSymbolDerivationException} if it is not possible to derive a start symbol from the given
 * input.
 */
public interface StartSymbolDerivationStrategy extends Serializable {

    IndexedSymbol deriveStartSymbol(List<Production<IndexedSymbol, IndexedSymbol>> productions)
            throws StartSymbolDerivationException;
}

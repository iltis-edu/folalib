package de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol.standard;

import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol.StartSymbolDerivationException;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol.StartSymbolDerivationStrategy;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.util.List;
import java.util.Set;

/**
 * Uses the first production to derive the start symbol. The first production's left-hand side must
 * contain exactly one {@link GrammarSymbol.NonTerminal}. Otherwise, a {@link
 * StartSymbolDerivationException} will be thrown.
 */
public class DeriveFromFirstProductionStartSymbolDerivationStrategy
        implements StartSymbolDerivationStrategy {

    @Override
    public IndexedSymbol deriveStartSymbol(
            List<Production<IndexedSymbol, IndexedSymbol>> productions)
            throws StartSymbolDerivationException {
        if (productions.isEmpty())
            throw new StartSymbolDerivationException("No productions to derive start symbol");

        Production<IndexedSymbol, IndexedSymbol> firstProduction = productions.get(0);
        if (!firstProduction.getLhs().getTerminals().isEmpty())
            throw new StartSymbolDerivationException(
                    "Left side of first production contains terminals");

        Set<IndexedSymbol> lhsNonTerminals = firstProduction.getLhs().getNonTerminals();
        if (lhsNonTerminals.size() != 1)
            throw new StartSymbolDerivationException(
                    "Left side of first production does not contain exactly one non-terminal");

        // A set provides no get()-method for some reason
        return lhsNonTerminals.iterator().next();
    }
}

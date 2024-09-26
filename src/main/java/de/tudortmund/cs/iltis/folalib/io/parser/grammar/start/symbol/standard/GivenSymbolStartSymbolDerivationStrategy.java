package de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol.standard;

import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.start.symbol.StartSymbolDerivationStrategy;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.util.List;
import java.util.Objects;

/** Uses the given {@link IndexedSymbol} as start symbol. */
public class GivenSymbolStartSymbolDerivationStrategy implements StartSymbolDerivationStrategy {

    private IndexedSymbol startSymbol;

    public GivenSymbolStartSymbolDerivationStrategy(IndexedSymbol startNonTerminal) {
        Objects.requireNonNull(startNonTerminal);
        this.startSymbol = startNonTerminal;
    }

    @Override
    public IndexedSymbol deriveStartSymbol(
            List<Production<IndexedSymbol, IndexedSymbol>> productions) {
        return startSymbol;
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private GivenSymbolStartSymbolDerivationStrategy() {}
}

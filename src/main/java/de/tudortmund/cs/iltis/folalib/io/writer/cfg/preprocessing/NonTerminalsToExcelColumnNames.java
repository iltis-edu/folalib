package de.tudortmund.cs.iltis.folalib.io.writer.cfg.preprocessing;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.transform.ConstrainedSupplier;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A preprocessing function to be used in conjunction with GrammarWriters
 *
 * <p>This preprocessing maps all non-terminals to Excel column names A, B, C, ..., AA, AB, AC, ...
 * . The start symbol is guaranteed to be mapped to S.
 */
public class NonTerminalsToExcelColumnNames
        implements SerializableFunction<
                Grammar<?, ?, ? extends Production<?, ?>>,
                Grammar<?, ?, ? extends Production<?, ?>>> {

    @Override
    public Grammar<?, ?, ? extends Production<?, ?>> apply(
            Grammar<?, ?, ? extends Production<?, ?>> grammar) {
        Serializable startSymbol = grammar.getStartSymbol();
        Set<Serializable> remainingNonTerminals =
                grammar.getNonTerminals().stream()
                        .filter(n -> !startSymbol.equals(n))
                        .collect(Collectors.toSet());

        HashMap<Serializable, String> mapping = new HashMap<>();
        mapping.put(startSymbol, "S");
        ConstrainedSupplier<String> supplier =
                ConstrainedSupplier.constrainedExcelColumnNameSupplier();
        supplier.constrain("S");
        for (Serializable nonTerminal : remainingNonTerminals) {
            mapping.put(nonTerminal, supplier.get());
        }

        return grammar.mapNonTerminals(mapping::get);
    }
}

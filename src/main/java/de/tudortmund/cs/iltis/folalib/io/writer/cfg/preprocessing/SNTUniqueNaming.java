package de.tudortmund.cs.iltis.folalib.io.writer.cfg.preprocessing;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A preprocessing function to be used in conjunction with GrammarWriters
 *
 * <p>This preprocessing maps the start symbol to S{x}, all other non-terminals to N{x} and all
 * terminals to T{x} where x is the result of {@code toString()} for the respective terminal or
 * non-terminal.
 *
 * <p>This preprocessing offers a simple way to "encode" the start symbol into a string
 * representation of a grammar which does not explicitly state the start symbol (see {@link
 * de.tudortmund.cs.iltis.folalib.io.writer.cfg.GrammarWriter}). This preprocessing allows a grammar
 * to be uniquely reconstructed from its string representation.
 */
public class SNTUniqueNaming
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
        HashMap<Serializable, String> nonTerminalsMapping = new HashMap<>();
        nonTerminalsMapping.put(startSymbol, "S{" + startSymbol + "}");
        for (Serializable nonTerminal : remainingNonTerminals) {
            nonTerminalsMapping.put(nonTerminal, "N{" + nonTerminal + "}");
        }

        HashMap<Serializable, String> terminalsMapping = new HashMap<>();
        for (Serializable terminal : grammar.getTerminals()) {
            terminalsMapping.put(terminal, "T{" + terminal + "}");
        }

        return grammar.map(terminalsMapping::get, nonTerminalsMapping::get);
    }
}

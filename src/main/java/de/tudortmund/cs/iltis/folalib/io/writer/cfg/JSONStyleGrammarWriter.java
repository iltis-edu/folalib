package de.tudortmund.cs.iltis.folalib.io.writer.cfg;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.io.writer.cfg.preprocessing.NonTerminalsToExcelColumnNames;
import de.tudortmund.cs.iltis.folalib.io.writer.cfg.preprocessing.SNTUniqueNaming;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.util.ArrayList;
import java.util.List;

/**
 * A GrammarWriter which outputs a grammar as a string in JSON format
 *
 * <p>The format is simple JSON as illustrated below
 *
 * <pre>
 *     { "startSymbol": "S", "productions": ["S -> a A b b", "S -> ε", "A -> b S", "B -> b", "B -> S A S"] }
 * </pre>
 *
 * Note that multiple productions with the same left hand side are <b>not</b> unified to something
 * like
 *
 * <pre>
 *     B -> b | S A S
 * </pre>
 *
 * If you want to customise the terminals/non-terminals beforehand you can pass a custom
 * preprocessing function to the constructor.
 *
 * <p>This writer is usually appropriate if you want to uniquely reconstruct the original grammar.
 */
public class JSONStyleGrammarWriter extends GrammarWriter {

    /** Constructs a new JSONStyleGrammarWriter which outputs the grammar as is */
    public JSONStyleGrammarWriter() {
        this(null);
    }

    /**
     * Constructs a new GrammarWriter which preprocess the grammar before outputting it
     *
     * @param preprocessing the function to preprocess the grammar beforehand, e.g. {@link
     *     NonTerminalsToExcelColumnNames} or {@link SNTUniqueNaming}
     */
    public JSONStyleGrammarWriter(
            SerializableFunction<
                            Grammar<?, ?, ? extends Production<?, ?>>,
                            Grammar<?, ?, ? extends Production<?, ?>>>
                    preprocessing) {
        super(GrammarWriterProperties.defaultJSONProperties(), preprocessing);
    }

    /**
     * Constructs a new GrammarWriter which preprocess the grammar before outputting it
     *
     * @param preprocessing the function to preprocess the grammar beforehand, e.g. {@link
     *     NonTerminalsToExcelColumnNames} or {@link SNTUniqueNaming}
     * @param symbolToString the function to use to convert a {@link GrammarSymbol} to a String,
     *     useful for escaping special chars
     */
    public JSONStyleGrammarWriter(
            SerializableFunction<
                            Grammar<?, ?, ? extends Production<?, ?>>,
                            Grammar<?, ?, ? extends Production<?, ?>>>
                    preprocessing,
            SerializableFunction<GrammarSymbol<?, ?>, String> symbolToString) {
        super(GrammarWriterProperties.defaultJSONProperties(), symbolToString, preprocessing);
    }

    @Override
    public String write(Grammar<?, ?, ? extends Production<?, ?>> grammar) {
        Grammar<?, ?, ? extends Production<?, ?>> mappedGrammar =
                preprocessing != null ? preprocessing.apply(grammar) : grammar;
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"startSymbol\": ")
                .append("\"")
                .append(mappedGrammar.getStartSymbol().toString())
                .append("\"")
                .append(", \"productions\": [ ");
        List<? extends Production<?, ?>> productions =
                new ArrayList<>(mappedGrammar.getProductions());
        for (int i = 0; i < productions.size(); ++i) {
            productionToString(builder, productions.get(i));
            if (i < productions.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append("]").append(" }");
        return builder.toString();
    }
}

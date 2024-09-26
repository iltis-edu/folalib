package de.tudortmund.cs.iltis.folalib.io.writer.cfg;

import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.io.writer.cfg.preprocessing.NonTerminalsToExcelColumnNames;
import de.tudortmund.cs.iltis.folalib.io.writer.cfg.preprocessing.SNTUniqueNaming;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import de.tudortmund.cs.iltis.utils.io.writer.general.Writer;
import java.util.stream.Collectors;

/**
 * A GrammarWriter which outputs a grammar as a string
 *
 * <p>The format is as one would expect in lectures, e.g.
 *
 * <pre>
 *     S -> a A b b
 *     S -> ε
 *     A -> b S
 *     B -> b
 *     B -> S A S
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
 * <p><b>Important:</b> the startSymbol of the grammar is implicit in the resulting string and as a
 * result the original grammar <b>cannot</b> be reconstructed from the string automatically. If this
 * is important for you, either use another writer like {@link JSONStyleGrammarWriter} or an
 * appropriate preprocessing like {@link
 * de.tudortmund.cs.iltis.folalib.io.writer.cfg.preprocessing.SNTUniqueNaming}.
 */
public class GrammarWriter implements Writer<Grammar<?, ?, ? extends Production<?, ?>>> {

    protected GrammarWriterProperties properties;
    protected SerializableFunction<GrammarSymbol<?, ?>, String> symbolToString;

    protected final SerializableFunction<
                    Grammar<?, ?, ? extends Production<?, ?>>,
                    Grammar<?, ?, ? extends Production<?, ?>>>
            preprocessing;

    /** Constructs a new GrammarWriter which outputs the grammar as is */
    public GrammarWriter() {
        this(GrammarWriterProperties.defaultTextProperties(), null);
    }

    /**
     * Constructs a new GrammarWriter which preprocess the grammar before outputting it
     *
     * @param preprocessing the function to preprocess the grammar beforehand, e.g. {@link
     *     NonTerminalsToExcelColumnNames} or {@link SNTUniqueNaming}
     */
    public GrammarWriter(
            SerializableFunction<
                            Grammar<?, ?, ? extends Production<?, ?>>,
                            Grammar<?, ?, ? extends Production<?, ?>>>
                    preprocessing) {
        this(GrammarWriterProperties.defaultTextProperties(), preprocessing);
    }

    /**
     * Constructs a new GrammarWriter which preprocess the grammar before outputting it
     *
     * @param properties the properties to use while outputting the grammar, e.g. which symbol to
     *     use for epsilon
     * @param preprocessing the function to preprocess the grammar beforehand, e.g. {@link
     *     NonTerminalsToExcelColumnNames} or {@link SNTUniqueNaming}
     */
    public GrammarWriter(
            GrammarWriterProperties properties,
            SerializableFunction<
                            Grammar<?, ?, ? extends Production<?, ?>>,
                            Grammar<?, ?, ? extends Production<?, ?>>>
                    preprocessing) {
        this(properties, null, preprocessing);
    }

    /**
     * Constructs a new GrammarWriter which preprocess the grammar before outputting it
     *
     * @param properties the properties to use while outputting the grammar, e.g. which symbol to
     *     use for epsilon
     * @param symbolToString the function to use to convert a {@link GrammarSymbol} to a String,
     *     useful for escaping special chars
     * @param preprocessing the function to preprocess the grammar beforehand, e.g. {@link
     *     NonTerminalsToExcelColumnNames} or {@link SNTUniqueNaming}
     */
    public GrammarWriter(
            GrammarWriterProperties properties,
            SerializableFunction<GrammarSymbol<?, ?>, String> symbolToString,
            SerializableFunction<
                            Grammar<?, ?, ? extends Production<?, ?>>,
                            Grammar<?, ?, ? extends Production<?, ?>>>
                    preprocessing) {
        this.preprocessing = preprocessing;
        this.symbolToString = symbolToString == null ? Object::toString : symbolToString;
        this.properties = properties;
    }

    @Override
    public String write(Grammar<?, ?, ? extends Production<?, ?>> grammar) {
        Grammar<?, ?, ? extends Production<?, ?>> mappedGrammar =
                preprocessing != null ? preprocessing.apply(grammar) : grammar;
        StringBuilder builder = new StringBuilder();
        mappedGrammar.getProductions().forEach(prod -> productionToString(builder, prod));
        return builder.toString();
    }

    protected void productionToString(StringBuilder builder, Production<?, ?> production) {
        builder.append(properties.getProductionInitiator());
        sententialFormToString(builder, production.getLhs());
        builder.append(properties.getArrow());
        sententialFormToString(builder, production.getRhs());
        builder.append(properties.getProductionTerminator());
    }

    protected void sententialFormToString(StringBuilder builder, SententialForm<?, ?> form) {
        if (form.isEmpty()) {
            builder.append(properties.getEmptyProductionSymbol());
            return;
        }
        builder.append(
                form.stream()
                        .map(gs -> symbolToString.apply(gs))
                        .collect(Collectors.joining(properties.getGrammarSymbolSeparator())));
    }
}

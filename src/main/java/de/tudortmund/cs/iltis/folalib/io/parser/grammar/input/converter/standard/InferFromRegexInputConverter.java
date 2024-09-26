package de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.standard;

import com.google.gwt.regexp.shared.RegExp;
import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.InputConversionException;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.InputToGrammarSymbolConverter;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.io.writer.general.SafeTextIndexedSymbolWriter;
import java.util.Objects;

/** A strategy that infers the alphabets from the two given regular expressions. */
public class InferFromRegexInputConverter implements InputToGrammarSymbolConverter {

    // GWTs RegExp is not serializable for some weird reason, so we simply store the regexes as
    // strings
    String terminalsRegEx;
    String nonTerminalsRegEx;

    public InferFromRegexInputConverter(String terminalsRegEx, String nonTerminalsRegEx) {
        Objects.requireNonNull(terminalsRegEx);
        Objects.requireNonNull(nonTerminalsRegEx);

        try {
            RegExp.compile(terminalsRegEx);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    "The regular expression for the terminals is invalid");
        }
        try {
            RegExp.compile(nonTerminalsRegEx);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    "The regular expression for the non-terminals is invalid");
        }

        this.terminalsRegEx = terminalsRegEx;
        this.nonTerminalsRegEx = nonTerminalsRegEx;
    }

    /**
     * @throws InputConversionException if either both regular expression match the input or neither
     *     regular expression match the input
     */
    @Override
    public GrammarSymbol<IndexedSymbol, IndexedSymbol> convertInput(IndexedSymbol input)
            throws InputConversionException {
        // We need to test both possible forms of an IndexedSymbol converted to a string
        RegExp regexTerminal = RegExp.compile(terminalsRegEx);
        RegExp regexNonTerminal = RegExp.compile(nonTerminalsRegEx);

        boolean possibleTerminal = testBothIndexedSymbolSyntaxOnRegex(regexTerminal, input);
        boolean possibleNonTerminal = testBothIndexedSymbolSyntaxOnRegex(regexNonTerminal, input);

        if (possibleTerminal && possibleNonTerminal)
            throw new InputConversionException(input, "Symbol matches both regular expressions");
        if (possibleTerminal) return new GrammarSymbol.Terminal<>(input);
        if (possibleNonTerminal) return new GrammarSymbol.NonTerminal<>(input);

        // If both regexes don't match
        throw new InputConversionException(input, "Symbol matches neither regular expressions");
    }

    /**
     * @param regex The regular expression that may describe the test subject
     * @param testSubject The IndexedSymbol that may match the regular expression
     * @return True if one (or both) of the two syntax of an IndexedSymbol (either
     *     value^superscript_subscript or value_subscript^superscript) match the regular expression
     */
    private boolean testBothIndexedSymbolSyntaxOnRegex(RegExp regex, IndexedSymbol testSubject) {
        SafeTextIndexedSymbolWriter iSymbolWriter = new SafeTextIndexedSymbolWriter();
        String iSymbol = iSymbolWriter.write(testSubject);
        String iSymbolReversed = iSymbolWriter.writeReversed(testSubject);

        return regex.test(iSymbol) || regex.test(iSymbolReversed);
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private InferFromRegexInputConverter() {}
}

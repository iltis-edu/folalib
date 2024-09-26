package de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard;

import com.google.gwt.regexp.shared.RegExp;
import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.*;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.collections.ListSet;
import de.tudortmund.cs.iltis.utils.io.writer.general.SafeTextIndexedSymbolWriter;
import java.util.Objects;

/**
 * A policy that infers the alphabet from the input. Every symbol read must match the specified
 * regular expression. The regular expression only needs to specify one syntax for an {@link
 * IndexedSymbol} (either value^superscript_subscript or value_subscript^superscript). Iff the
 * inferred alphabet is empty the base alphabet will be used instead. The symbols in the base
 * alphabet do not have to match the specified regular expression.
 */
public class InferFromRegexPolicy implements AlphabetInferencePolicy {

    private String regExpString;
    private Alphabet<IndexedSymbol> baseAlphabet;

    public InferFromRegexPolicy(String regExp, Alphabet<IndexedSymbol> baseAlphabet) {
        Objects.requireNonNull(regExp);
        Objects.requireNonNull(baseAlphabet);

        if (baseAlphabet.isEmpty())
            throw new IllegalArgumentException("The base alphabet cannot be empty");

        try {
            RegExp.compile(regExp);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("The given regular expression is invalid");
        }

        this.regExpString = regExp;
        this.baseAlphabet = baseAlphabet;
    }

    @Override
    public Alphabet<IndexedSymbol> getInferredAlphabet(Alphabet<IndexedSymbol> actuallyUsedSymbols)
            throws InferenceException {
        if (actuallyUsedSymbols.isEmpty()) return baseAlphabet;

        RegExp matchingRegExp = RegExp.compile(regExpString);

        ListSet<IndexedSymbol> notMatchingSymbols = new ListSet<>();
        for (IndexedSymbol current : actuallyUsedSymbols) {

            // Test both possible forms to write an indexed symbol
            SafeTextIndexedSymbolWriter iSymbolWriter = new SafeTextIndexedSymbolWriter();
            String iSymbol = iSymbolWriter.write(current);
            String iSymbolReversed = iSymbolWriter.writeReversed(current);

            if (!matchingRegExp.test(iSymbol) && !matchingRegExp.test(iSymbolReversed)) {
                notMatchingSymbols.add(current);
            }
        }

        if (notMatchingSymbols.isEmpty()) return actuallyUsedSymbols;

        // The alphabet in the error case consists of all actually used symbols in the regex.
        throw new InferenceException(notMatchingSymbols, actuallyUsedSymbols);
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private InferFromRegexPolicy() {}
}

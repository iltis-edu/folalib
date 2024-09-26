package de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard;

import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.*;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/** A policy that simply uses one pre-defined alphabet */
public class GivenAlphabetPolicy implements AlphabetInferencePolicy {

    private Alphabet<IndexedSymbol> alphabet;

    public GivenAlphabetPolicy(Alphabet<IndexedSymbol> alphabet) {
        Objects.requireNonNull(alphabet);

        if (alphabet.isEmpty()) throw new IllegalArgumentException("The alphabet cannot be empty");
        this.alphabet = alphabet;
    }

    @Override
    public Alphabet<IndexedSymbol> getInferredAlphabet(Alphabet<IndexedSymbol> actuallyUsedSymbols)
            throws InferenceException {
        Set<IndexedSymbol> actuallyUsedSymbolsMutable =
                new LinkedHashSet<>(actuallyUsedSymbols.toUnmodifiableSet());
        // If we remove all allowed symbols from the list of symbols that were actually used the
        // remaining symbols
        // are not allowed. The remaining list must be empty to avoid an exception.
        actuallyUsedSymbolsMutable.removeAll(alphabet.toUnmodifiableSet());

        if (actuallyUsedSymbolsMutable.isEmpty()) return alphabet;

        // The alphabet in the error case is the usual inferred alphabet plus all used symbols that
        // are not
        // allowed.
        throw new InferenceException(
                actuallyUsedSymbolsMutable, Alphabets.unionOf(alphabet, actuallyUsedSymbols));
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private GivenAlphabetPolicy() {}
}

package de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard;

import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferencePolicy;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;

/**
 * A policy that infers the alphabet from the input. <b>Please note: It is possible to get an empty
 * alphabet with this policy. Please use {@link InferMinimalNonEmptyAlphabetPolicy} instead whenever
 * possible.</b>
 */
public class InferMinimalAlphabetPolicy implements AlphabetInferencePolicy {

    @Override
    public Alphabet<IndexedSymbol> getInferredAlphabet(
            Alphabet<IndexedSymbol> actuallyUsedSymbols) {
        return actuallyUsedSymbols;
    }
}

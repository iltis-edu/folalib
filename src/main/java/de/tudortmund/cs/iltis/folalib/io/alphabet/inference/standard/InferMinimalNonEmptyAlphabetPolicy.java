package de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard;

import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferencePolicy;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.util.Objects;

/**
 * A policy that infers the alphabet from the input. Iff the inferred alphabet is empty the base
 * alphabet will be used instead.
 */
public class InferMinimalNonEmptyAlphabetPolicy implements AlphabetInferencePolicy {

    private Alphabet<IndexedSymbol> baseAlphabet;

    public InferMinimalNonEmptyAlphabetPolicy(Alphabet<IndexedSymbol> baseAlphabet) {
        Objects.requireNonNull(baseAlphabet);

        if (baseAlphabet.isEmpty())
            throw new IllegalArgumentException("The base alphabet cannot be empty");
        this.baseAlphabet = baseAlphabet;
    }

    @Override
    public Alphabet<IndexedSymbol> getInferredAlphabet(
            Alphabet<IndexedSymbol> actuallyUsedSymbols) {
        if (actuallyUsedSymbols.isEmpty()) return baseAlphabet;
        else return actuallyUsedSymbols;
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private InferMinimalNonEmptyAlphabetPolicy() {}
}

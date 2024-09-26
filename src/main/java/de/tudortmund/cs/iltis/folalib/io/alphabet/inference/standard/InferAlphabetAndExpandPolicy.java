package de.tudortmund.cs.iltis.folalib.io.alphabet.inference.standard;

import de.tudortmund.cs.iltis.folalib.io.alphabet.inference.AlphabetInferencePolicy;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.util.Objects;

/** A policy that infers the alphabet from the input and adds all given symbols additionally. */
public class InferAlphabetAndExpandPolicy implements AlphabetInferencePolicy {

    private Alphabet<IndexedSymbol> additional;

    public InferAlphabetAndExpandPolicy(Alphabet<IndexedSymbol> additional) {
        Objects.requireNonNull(additional);

        if (additional.isEmpty())
            throw new IllegalArgumentException("The additional alphabet cannot be empty");

        this.additional = additional;
    }

    @Override
    public Alphabet<IndexedSymbol> getInferredAlphabet(
            Alphabet<IndexedSymbol> actuallyUsedSymbols) {
        return Alphabets.unionOf(actuallyUsedSymbols, additional);
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private InferAlphabetAndExpandPolicy() {}
}

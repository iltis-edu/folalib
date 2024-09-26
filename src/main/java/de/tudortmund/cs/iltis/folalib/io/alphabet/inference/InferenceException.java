package de.tudortmund.cs.iltis.folalib.io.alphabet.inference;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This exception will be thrown by an {@link AlphabetInferencePolicy} if a symbol was found that is
 * not allowed by the chosen policy. It stores an {@link AlphabetInferenceFaultCollection} which
 * should contain an {@link AlphabetInferenceFault} for every used symbol that is not allowed by the
 * used {@link AlphabetInferencePolicy}. Additionally, it stores an {@link Alphabet} which should be
 * the best fitting alphabet regarding the configured alphabet inference policy and the
 * (erroneously) used symbols. This is usually the regular inferred alphabet plus the not allowed
 * symbols.
 */
public class InferenceException extends Exception {

    private AlphabetInferenceFaultCollection faultCollection;
    private Alphabet<IndexedSymbol> bestFitAlphabet;

    public InferenceException(
            AlphabetInferenceFaultCollection faultCollection,
            Alphabet<IndexedSymbol> bestFitAlphabet) {
        super();

        Objects.requireNonNull(faultCollection);
        Objects.requireNonNull(bestFitAlphabet);

        if (bestFitAlphabet.isEmpty())
            throw new IllegalArgumentException("The error alphabet cannot be empty");

        this.faultCollection = faultCollection;
        this.bestFitAlphabet = bestFitAlphabet;
    }

    /**
     * Automatically converts every symbol in the parameter-list {@code notAllowedSymbols} into a
     * corresponding {@link AlphabetInferenceFault} and adds these in a {@link
     * AlphabetInferenceFaultCollection} to this exception.
     */
    public InferenceException(
            Collection<IndexedSymbol> notAllowedSymbols, Alphabet<IndexedSymbol> bestFitAlphabet) {
        this(
                new AlphabetInferenceFaultCollection(
                        notAllowedSymbols.stream()
                                .map(
                                        symbol ->
                                                new AlphabetInferenceFault(
                                                        AlphabetInferenceFaultReason
                                                                .SYMBOL_NOT_ALLOWED,
                                                        symbol))
                                .collect(Collectors.toList())),
                bestFitAlphabet);
    }

    public AlphabetInferenceFaultCollection getFaultCollection() {
        return faultCollection;
    }

    public Alphabet<IndexedSymbol> getBestFitAlphabet() {
        return bestFitAlphabet;
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private InferenceException() {}
}

package de.tudortmund.cs.iltis.folalib.io.alphabet.inference;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.io.Serializable;

/**
 * An interface for an alphabet inference policy for a parser. An {@link Alphabet} over all symbols
 * that were actually used in the object outputted by the parser can be given to the method {@link
 * AlphabetInferencePolicy#getInferredAlphabet(Alphabet)}. The method will analyse this {@link
 * Alphabet} and return the minimal {@link Alphabet} that can be used and that matches the policy's
 * requirements and configuration.
 *
 * <p>If the given {@link Alphabet} contains one or more symbols which are not allowed the exception
 * {@link InferenceException} will be thrown. The exception will contain an {@link
 * AlphabetInferenceFaultCollection} which contains an {@link AlphabetInferenceFault} for every used
 * symbol that is not allowed. {@link AlphabetInferenceFaultReason#SYMBOL_NOT_ALLOWED} is used as
 * reason for these faults.
 */
public interface AlphabetInferencePolicy extends Serializable {

    Alphabet<IndexedSymbol> getInferredAlphabet(Alphabet<IndexedSymbol> actuallyUsedSymbols)
            throws InferenceException;
}

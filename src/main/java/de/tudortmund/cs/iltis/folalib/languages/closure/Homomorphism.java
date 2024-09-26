package de.tudortmund.cs.iltis.folalib.languages.closure;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Language;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.stream.Collectors;

/**
 * Represents the abstract concept of applying a homomorphism to a language.
 *
 * <p>In this case each symbol of the language is mapped to a word of (possibly different) symbols,
 * which results in another language.
 *
 * <p>Example: L = {a, aa, abc}, Alphabet = {a, b, c} f(a) = bb f(b) = ε f(c) = c ==> resulting
 * language = {f(a), f(a)f(a), f(a)f(b)f(c)} = {bb, bbbb, bbεc}
 *
 * @param <L> the language to which the homomorphism is applied
 * @param <S> the type of symbols originally in the language
 * @param <T> the type of individual symbols of the words, where the words are the type of symbols
 *     of the language after applying the homomorphism
 */
public class Homomorphism<S extends Serializable, T extends Serializable, L extends Language<S>>
        implements Serializable {

    private SerializableFunction<S, Word<T>> homomorphism;
    private L language;

    /* For serialization */
    @SuppressWarnings("unused")
    private Homomorphism() {}

    public Homomorphism(L language, SerializableFunction<S, Word<T>> homomorphism) {
        this.language = language;
        this.homomorphism = homomorphism;
    }

    public L getLanguage() {
        return language;
    }

    public SerializableFunction<S, Word<T>> getHomomorphism() {
        return homomorphism;
    }

    public Alphabet<T> getTargetAlphabet() {
        return new Alphabet<>(
                language.getAlphabet().stream()
                        .map(s -> homomorphism.apply(s))
                        .flatMap(Word::stream)
                        .collect(Collectors.toList()));
    }
}

package de.tudortmund.cs.iltis.folalib.languages.closure;

import de.tudortmund.cs.iltis.folalib.languages.Language;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the abstract concept of applying an inverse homomorphism to a language.
 *
 * <p>In this case, each symbol of a specified domain is mapped to a word of symbols over a
 * codomain. Then, the resulting language consists of all words over the domain, s.t. the
 * concatenation of the mapped, individual symbols is in the given language.
 *
 * <p>Example: L = {a, aa, abc, bcc}, Alphabet = {1, 2, 3, 4} f(1) = a f(2) = abc f(3) = bb f(4) =
 * bc ==> resulting language = {f(1) = a, f(1)f(1) = aa, f(2) = f(1)f(4) = abc, f(?) = bcc} = {1,
 * 11, 2, 14}
 *
 * @param <L> the language to which the homomorphism is applied
 * @param <S> the type of symbols originally in the language
 * @param <T> the type of individual symbols of the words, where the words are the type of symbols
 *     of the language after applying the homomorphism
 */
public class InverseHomomorphism<
                S extends Serializable, T extends Serializable, L extends Language<T>>
        implements Serializable {

    private SerializableFunction<S, Word<T>> homomorphism;
    private Set<S> domain;
    private L language;

    /* For serialization */
    @SuppressWarnings("unused")
    private InverseHomomorphism() {}

    public InverseHomomorphism(
            SerializableFunction<S, Word<T>> homomorphism, Set<S> domain, L language) {
        this.homomorphism = homomorphism;
        this.domain = domain;
        this.language = language;
        if (!wellDefinedFunction(
                homomorphism, domain, language.getAlphabet().toUnmodifiableSet())) {
            throw new IllegalArgumentException(
                    "The given homomorphism is not well defined w.r.t. the specified domain and language.");
        }
    }

    private boolean wellDefinedFunction(
            SerializableFunction<S, Word<T>> f, Set<S> domain, Set<T> codomain) {
        return domain.stream().map(f).flatMap(Word::stream).allMatch(codomain::contains);
    }

    public L getLanguage() {
        return language;
    }

    public SerializableFunction<S, Word<T>> getHomomorphism() {
        return homomorphism;
    }

    public Set<S> getDomain() {
        return new HashSet<>(domain);
    }
}

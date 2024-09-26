package de.tudortmund.cs.iltis.folalib.languages;

import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class Alphabets {

    /**
     * Creates a new Alphabet from the individual characters of the given string
     *
     * @param symbols the characters which constitute the alphabet
     * @return a new Alphabet of all symbols in the given string
     */
    public static Alphabet<Character> characterAlphabet(String symbols) {
        return new Alphabet<>(symbols.chars().mapToObj(c -> (char) c).collect(Collectors.toSet()));
    }

    /**
     * Creates a new Alphabet from the individual characters of the given string as {@code
     * IndexedSymbol}s.
     *
     * <p>Each character is interpreted as an {@code IndexedSymbol} of length 1 with no sub- or
     * superscript.
     *
     * @param symbols the characters which constitute the alphabet
     * @return a new Alphabet of all symbols in the given string
     */
    public static Alphabet<IndexedSymbol> indexedSymbolAlphabet(String symbols) {
        return new Alphabet<>(
                symbols.chars()
                        .mapToObj(
                                c ->
                                        new IndexedSymbol(
                                                "" + (char) c)) // Character.toString(c) is not
                        // available with GWT
                        .collect(Collectors.toSet()));
    }

    @SafeVarargs
    public static <S extends Serializable> Alphabet<S> unionOf(Alphabet<S>... alphabets) {
        return unionOf(Arrays.asList(alphabets));
    }

    public static <S extends Serializable> Alphabet<S> unionOf(
            Collection<? extends Alphabet<S>> alphabets) {
        LinkedHashSet<S> alphabet = new LinkedHashSet<>();
        for (Alphabet<S> a : alphabets) alphabet.addAll(a.toUnmodifiableSet());
        return new Alphabet<>(alphabet);
    }

    @SafeVarargs
    public static <S extends Serializable> Alphabet<S> intersectionOf(Alphabet<S>... alphabets) {
        return intersectionOf(Arrays.asList(alphabets));
    }

    public static <S extends Serializable> Alphabet<S> intersectionOf(
            Collection<? extends Alphabet<S>> alphabets) {
        LinkedHashSet<S> alphabet = new LinkedHashSet<>();
        if (alphabets.isEmpty()) {
            return new Alphabet<>(alphabet);
        }
        Alphabet<S> first = alphabets.stream().findFirst().get();
        for (S s : first) {
            boolean include = true;
            for (Alphabet<S> as : alphabets) include = include && as.contains(s);
            if (include) alphabet.add(s);
        }
        return new Alphabet<>(alphabet);
    }
}

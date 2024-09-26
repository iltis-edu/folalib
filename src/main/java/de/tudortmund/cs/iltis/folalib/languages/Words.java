package de.tudortmund.cs.iltis.folalib.languages;

import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Words {

    public static Word<Character> characterWord(String symbols) {
        return new Word<>(symbols.chars().mapToObj(c -> (char) c).collect(Collectors.toList()));
    }

    public static Word<IndexedSymbol> indexedSymbolWord(String symbols) {
        return new Word<>(
                symbols.chars()
                        .mapToObj(
                                c ->
                                        new IndexedSymbol(
                                                "" + (char) c)) // Character.toString(c) is not
                        // available with GWT
                        .collect(Collectors.toList()));
    }

    @SafeVarargs
    public static <S extends Serializable> Word<S> concat(Word<S>... alphabets) {
        return concat(Arrays.asList(alphabets));
    }

    public static <S extends Serializable> Word<S> concat(Collection<? extends Word<S>> words) {
        ArrayList<S> word = new ArrayList<>();
        for (Word<S> w : words) word.addAll(w.toUnmodifiableList());
        return new Word<>(word);
    }

    public static <S extends Serializable> Word<S> replicate(Word<S> word, int times) {
        ArrayList<S> result = new ArrayList<>();
        for (int i = 0; i < times; ++i) result.addAll(word.toUnmodifiableList());
        return new Word<>(result);
    }
}

package de.tudortmund.cs.iltis.folalib.languages;

import de.tudortmund.cs.iltis.folalib.io.reader.regex.RegularExpressionReader;
import de.tudortmund.cs.iltis.folalib.io.reader.regex.RegularExpressionReaderProperties;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.util.Collections;

public class RegularLanguages {

    private static final Alphabet<IndexedSymbol> alphabet =
            new Alphabet<>(new IndexedSymbol("a"), new IndexedSymbol("b"), new IndexedSymbol("c"));
    private static final RegularExpressionReader reader =
            new RegularExpressionReader(RegularExpressionReaderProperties.createDefault(alphabet));

    public static RegularLanguage<IndexedSymbol> emptyLanguage() {
        return new RegularLanguage<>(reader.read("∅"));
    }

    public static RegularLanguage<IndexedSymbol> emptyWord() {
        return new RegularLanguage<>(reader.read("ε"));
    }

    public static RegularLanguage<IndexedSymbol> evenLength() {
        return new RegularLanguage<>(reader.read("(aa + ab + ac + ba + bb + bc + ca + cb + cc)*"));
    }

    public static RegularLanguage<IndexedSymbol> oddLength() {
        return new RegularLanguage<>(
                reader.read("(a + b + c) (aa + ab + ac + ba + bb + bc + ca + cb + cc)*"));
    }

    public static RegularLanguage<IndexedSymbol> max(int max) {
        return between(0, max);
    }

    public static RegularLanguage<IndexedSymbol> min(int min) {
        String str = min == 0 ? "ε" : String.join("", Collections.nCopies(min, "(a + b + c)"));
        return new RegularLanguage<>(reader.read(str + "(a + b + c)*"));
    }

    public static RegularLanguage<IndexedSymbol> between(int min, int max) {
        String[] strs = new String[max - min + 1];
        for (int i = 0; min + i <= max; ++i) {
            strs[i] =
                    min + i == 0
                            ? "ε"
                            : String.join("", Collections.nCopies(min + i, "(a + b + c)"));
        }
        String str = String.join(" + ", strs);
        return new RegularLanguage<>(reader.read(str));
    }

    public static RegularLanguage<IndexedSymbol> exact(int length) {
        String str =
                length == 0 ? "ε" : String.join("", Collections.nCopies(length, "(a + b + c)"));
        return new RegularLanguage<>(reader.read(str));
    }

    public static RegularLanguage<IndexedSymbol> words(String... words) {
        String[] strs = new String[words.length];
        for (int i = 0; i < words.length; ++i) {
            strs[i] = words[i].isEmpty() ? "ε" : words[i];
        }
        String str = String.join(" + ", strs);
        return new RegularLanguage<>(reader.read(str));
    }

    public static RegularLanguage<IndexedSymbol> infix(String infix) {
        return new RegularLanguage<>(reader.read("(a+b+c)*" + infix + "(a+b+c)*"));
    }

    public static RegularLanguage<IndexedSymbol> prefix(String prefix) {
        return new RegularLanguage<>(reader.read(prefix + "(a+b+c)*"));
    }

    public static RegularLanguage<IndexedSymbol> suffix(String suffix) {
        return new RegularLanguage<>(reader.read("(a+b+c)*" + suffix));
    }

    public static RegularLanguage<IndexedSymbol> atMostTwoAs() {
        return new RegularLanguage<>(reader.read("(b+c)* (a+ε) (b+c)* (a+ε) (b+c)*"));
    }

    public static RegularLanguage<IndexedSymbol> noAs() {
        return new RegularLanguage<>(reader.read("(b+c)*"));
    }

    public static RegularLanguage<IndexedSymbol> evenNumberOfAs() {
        return new RegularLanguage<>(reader.read("(b+c)* + ((b+c)* a (b+c)* a (b+c)*)*"));
    }

    public static RegularLanguage<IndexedSymbol> firstEqualsLast() {
        return new RegularLanguage<>(
                reader.read("(a (a+b+c)* a) + (b (a+b+c)* b) + (c (a+b+c)* c)"));
    }

    public static RegularLanguage<IndexedSymbol> allWords() {
        return new RegularLanguage<>(reader.read("(a+b+c)*"));
    }
}

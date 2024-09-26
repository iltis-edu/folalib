package de.tudortmund.cs.iltis.folalib.io.writer.word;

import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.io.writer.general.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Writer class for Word<S> */
public class WordWriter<S extends Serializable> implements Writer<Word<S>> {

    private Writer<S> writer;
    private String delimiter;

    public WordWriter(Writer<S> writer) {
        this.writer = writer;
        this.delimiter = "";
    }

    public WordWriter(Writer<S> writer, String delimiter) {
        this.writer = writer;
        this.delimiter = delimiter;
    }

    /**
     * Returns a wordwriter of the type latexIndexedSymbolWordWriter
     *
     * @return writer
     */
    public static WordWriter<IndexedSymbol> latexIndexedSymbolWordWriter() {
        return new WordWriter<>(new LatexIndexedSymbolWriter());
    }

    /**
     * Returns a wordwriter of the type textIndexedSymbolWordWriter
     *
     * @return writer
     */
    public static WordWriter<IndexedSymbol> textIndexedSymbolWordWriter() {
        return new WordWriter<>(new TextIndexedSymbolWriter());
    }

    /**
     * Returns a wordwriter of the type safeTextIndexedSymbolWordWriter
     *
     * @return writer
     */
    public static WordWriter<IndexedSymbol> safeTextIndexedSymbolWordWriter() {
        return new WordWriter<>(new SafeTextIndexedSymbolWriter());
    }

    /**
     * Returns a wordwriter of the type htmlIndexedSymbolWordWriter
     *
     * @return writer
     */
    public static WordWriter<IndexedSymbol> htmlIndexedSymbolWordWriter() {
        return new WordWriter<>(new HTMLIndexedSymbolWriter());
    }

    /**
     * Writes the word with the selected WordWriter
     *
     * @param word
     * @return wordString
     */
    @Override
    public String write(Word<S> word) {

        List<String> characterList = new ArrayList();
        word.stream().forEach(c -> characterList.add(writer.write(c)));
        return String.join(delimiter, characterList);
    }
}

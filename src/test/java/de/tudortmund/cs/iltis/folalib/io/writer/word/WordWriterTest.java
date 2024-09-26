package de.tudortmund.cs.iltis.folalib.io.writer.word;

import static org.junit.Assert.assertEquals;

import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.io.writer.general.LatexIndexedSymbolWriter;
import org.junit.Test;

/** Test class for WordWriter */
public class WordWriterTest {
    @Test
    public void testLatexWordWriter() {
        Word<IndexedSymbol> indexedSymbolWord = new Word<>(new IndexedSymbol("a_12"));
        String indexedSymbolWordString =
                WordWriter.latexIndexedSymbolWordWriter().write(indexedSymbolWord);

        assertEquals(indexedSymbolWordString, "a_{12}");
    }

    @Test
    public void testTextWordWriter() {
        Word<IndexedSymbol> indexedSymbolWord = new Word<>(new IndexedSymbol("a_1"));
        String indexedSymbolWordString =
                WordWriter.textIndexedSymbolWordWriter().write(indexedSymbolWord);

        assertEquals(indexedSymbolWordString, "a₁");
    }

    @Test
    public void testSafeTextWordWriter() {
        Word<IndexedSymbol> indexedSymbolWord = new Word<>(new IndexedSymbol("a_1"));
        String indexedSymbolWordString =
                WordWriter.safeTextIndexedSymbolWordWriter().write(indexedSymbolWord);

        assertEquals(indexedSymbolWordString, "a_1");
    }

    @Test
    public void testHTMLWordWriter() {
        Word<IndexedSymbol> indexedSymbolWord =
                new Word<>(
                        new IndexedSymbol("a"),
                        new IndexedSymbol("<sub>"),
                        new IndexedSymbol("1"),
                        new IndexedSymbol("</sub>"));
        String indexedSymbolWordString =
                WordWriter.htmlIndexedSymbolWordWriter().write(indexedSymbolWord);

        assertEquals(indexedSymbolWordString, "a<sub>1</sub>");
    }

    @Test
    public void testWordWithDelimiter() {
        Word<IndexedSymbol> indexedSymbolWord =
                new Word<>(new IndexedSymbol("a"), new IndexedSymbol("b"), new IndexedSymbol("a"));
        WordWriter<IndexedSymbol> wordWriter =
                new WordWriter<>(new LatexIndexedSymbolWriter(), ",");
        String indexedSymbolWordString = wordWriter.write(indexedSymbolWord);

        assertEquals(indexedSymbolWordString, "a,b,a");
    }
}

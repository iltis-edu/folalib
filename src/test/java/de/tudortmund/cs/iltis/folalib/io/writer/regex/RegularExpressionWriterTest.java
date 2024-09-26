package de.tudortmund.cs.iltis.folalib.io.writer.regex;

import static org.junit.Assert.assertEquals;

import de.tudortmund.cs.iltis.folalib.expressions.regular.*;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import org.junit.Test;

public class RegularExpressionWriterTest {

    IndexedSymbol a = new IndexedSymbol("a");
    IndexedSymbol b = new IndexedSymbol("b");
    IndexedSymbol c = new IndexedSymbol("c");

    private final Alphabet<IndexedSymbol> alphabet = new Alphabet<>(a, b, c);

    @Test
    public void testWriter() {
        RegularExpressionWriter<IndexedSymbol> writer =
                new RegularExpressionWriter<>(
                        RegularExpressionWriterProperties.defaultTextProperties());

        String output =
                writer.write(
                        new Concatenation<>(
                                new Symbol<>(alphabet, a),
                                new Symbol<>(alphabet, b).optional().repetition(4, 7).star().plus(),
                                Range.from(alphabet, a, c),
                                new Alternative<>(
                                        new Symbol<>(alphabet, b),
                                        new EmptyWord<>(alphabet),
                                        new EmptyLanguage<>(alphabet))));

        assertEquals("a(((b?)^{4,7})^*)^+[a-c](b+ε+∅)", output);
    }
}

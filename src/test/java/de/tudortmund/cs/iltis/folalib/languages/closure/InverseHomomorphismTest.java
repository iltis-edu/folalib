package de.tudortmund.cs.iltis.folalib.languages.closure;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.expressions.regular.Concatenation;
import de.tudortmund.cs.iltis.folalib.expressions.regular.Symbol;
import de.tudortmund.cs.iltis.folalib.languages.*;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import org.junit.Test;

public class InverseHomomorphismTest {

    private final IndexedSymbol a = new IndexedSymbol("a");
    private final IndexedSymbol b = new IndexedSymbol("b");
    private final IndexedSymbol c = new IndexedSymbol("c");

    private final Alphabet<IndexedSymbol> alphabetAB = new Alphabet<>(a, b);

    private final Symbol<IndexedSymbol> symbolA = new Symbol<>(alphabetAB, a);
    private final Symbol<IndexedSymbol> symbolB = new Symbol<>(alphabetAB, b);

    @Test
    public void testWellDefinednessCheck() {
        try {
            SerializableFunction<Integer, Word<IndexedSymbol>> f = i -> new Word<>(a, b, c);
            Alphabet<Integer> domain = new Alphabet<>(1, 2, 3);
            RegularLanguage<IndexedSymbol> lang =
                    new RegularLanguage<>(new Concatenation<>(symbolA, symbolB).star());
            // constructor must throw because f over {1, 2, 3} maps to {a, b, c} but c is not in
            // alphabet of lang
            InverseHomomorphism<Integer, IndexedSymbol, RegularLanguage<IndexedSymbol>> invHom =
                    new InverseHomomorphism<>(f, domain.toUnmodifiableSet(), lang);
            fail();
        } catch (IllegalArgumentException excp) {
            assertEquals(
                    "The given homomorphism is not well defined w.r.t. the specified domain and language.",
                    excp.getMessage());
        }
    }
}

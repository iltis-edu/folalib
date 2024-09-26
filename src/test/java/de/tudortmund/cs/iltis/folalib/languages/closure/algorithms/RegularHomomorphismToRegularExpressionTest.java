package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import static de.tudortmund.cs.iltis.folalib.languages.RegularLanguages.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.folalib.languages.closure.Homomorphism;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RegularHomomorphismToRegularExpressionTest {

    private final RegularLanguage<IndexedSymbol> lhs;
    private final SerializableFunction<IndexedSymbol, Word<IndexedSymbol>> homomorphism;
    private final RegularLanguage<IndexedSymbol> expected;

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(
                new Object[][] {
                    {
                        emptyLanguage(), /* under homomorphism */
                        identity(), /* results in */
                        emptyLanguage()
                    },
                    {
                        emptyLanguage(), /* under homomorphism */
                        swap(new IndexedSymbol("a"), new IndexedSymbol("b")), /* results in */
                        emptyLanguage()
                    },
                    {
                        prefix("aaa"), /* under homomorphism */
                        swap(new IndexedSymbol("a"), new IndexedSymbol("b")), /* results in */
                        prefix("bbb")
                    },
                    {
                        infix("abc"), /* under homomorphism */
                        swap(new IndexedSymbol("c"), new IndexedSymbol("a")), /* results in */
                        infix("cba")
                    },
                    {
                        allWords(), /* under homomorphism */
                        drop(new IndexedSymbol("a")), /* results in */
                        noAs()
                    },
                });
    }

    public RegularHomomorphismToRegularExpressionTest(
            RegularLanguage<IndexedSymbol> lhs,
            SerializableFunction<IndexedSymbol, Word<IndexedSymbol>> homomorphism,
            RegularLanguage<IndexedSymbol> expected) {
        this.lhs = lhs;
        this.homomorphism = homomorphism;
        this.expected = expected;
    }

    @Test
    public void testRegularHomomorphism() {
        Homomorphism<IndexedSymbol, IndexedSymbol, RegularLanguage<IndexedSymbol>> homo =
                new Homomorphism<>(lhs, this.homomorphism);
        RegularExpression<IndexedSymbol> result =
                new RegularHomomorphismToRegularExpression<IndexedSymbol, IndexedSymbol>()
                        .apply(homo);
        assertTrue(new RegularLanguage<>(result).isEqualTo(expected));
    }

    private static SerializableFunction<IndexedSymbol, Word<IndexedSymbol>> identity() {
        return Word::new;
    }

    private static SerializableFunction<IndexedSymbol, Word<IndexedSymbol>> swap(
            IndexedSymbol x, IndexedSymbol y) {
        return c -> {
            if (c.equals(x)) {
                return new Word<>(y);
            } else if (c.equals(y)) {
                return new Word<>(x);
            } else {
                return new Word<>(c);
            }
        };
    }

    private static SerializableFunction<IndexedSymbol, Word<IndexedSymbol>> drop(
            IndexedSymbol... cs) {
        return c -> {
            if (Arrays.asList(cs).contains(c)) {
                return new Word<>();
            } else {
                return new Word<>(c);
            }
        };
    }

    private static SerializableFunction<IndexedSymbol, Word<IndexedSymbol>> replicate(int n) {
        return c -> new Word<>(Collections.nCopies(n, c));
    }
}

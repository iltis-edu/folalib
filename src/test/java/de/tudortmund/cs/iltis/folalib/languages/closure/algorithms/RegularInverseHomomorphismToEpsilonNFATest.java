package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import static de.tudortmund.cs.iltis.folalib.languages.RegularLanguages.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.io.reader.alphabet.AlphabetReader;
import de.tudortmund.cs.iltis.folalib.languages.*;
import de.tudortmund.cs.iltis.folalib.languages.closure.InverseHomomorphism;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import de.tudortmund.cs.iltis.utils.io.parser.symbol.FiniteSetSymbolSplittingPolicy;
import java.io.Serializable;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RegularInverseHomomorphismToEpsilonNFATest {

    private final RegularLanguage<IndexedSymbol> lhs;
    private final SerializableFunction<IndexedSymbol, Word<IndexedSymbol>> inverseHomomorphism;
    private final Alphabet<IndexedSymbol> domain;
    private final RegularLanguage<IndexedSymbol> expected;

    private static final AlphabetReader reader =
            new AlphabetReader(
                    new FiniteSetSymbolSplittingPolicy(
                            Set.of(
                                    new IndexedSymbol("a"),
                                    new IndexedSymbol("b"),
                                    new IndexedSymbol("c"))));

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(
                new Object[][] {
                    {
                        emptyLanguage(), /* under reverse homomorphism */
                        identity(), /* over domain */
                        reader.read("abc"), /* results in */
                        emptyLanguage()
                    },
                    {
                        evenLength(), /* under reverse homomorphism */
                        identity(), /* over domain */
                        reader.read("abc"), /* results in */
                        evenLength()
                    },
                    {
                        atMostTwoAs(), /* under reverse homomorphism */
                        identity(), /* over domain */
                        reader.read("abc"), /* results in */
                        atMostTwoAs()
                    },
                    {
                        infix("abbc"), /* under reverse homomorphism */
                        identity(), /* over domain */
                        reader.read("abc"), /* results in */
                        infix("abbc")
                    },
                    {
                        exact(3), /* under reverse homomorphism */
                        identity(), /* over domain */
                        reader.read("abc"), /* results in */
                        exact(3)
                    },
                    {
                        prefix("abc"), /* under reverse homomorphism */
                        swap(new IndexedSymbol("b"), new IndexedSymbol("c")), /* over domain */
                        reader.read("abc"), /* results in */
                        prefix("acb")
                    },
                    {
                        words("aa", "bb", "cc"), /* under reverse homomorphism */
                        replicate(2), /* over domain */
                        reader.read("abc"), /* results in */
                        exact(1)
                    },
                    {
                        words("", "a", "aa"), /* under reverse homomorphism */
                        drop(new IndexedSymbol("b"), new IndexedSymbol("c")), /* over domain */
                        reader.read("abc"), /* results in */
                        atMostTwoAs()
                    }
                });
    }

    public RegularInverseHomomorphismToEpsilonNFATest(
            RegularLanguage<IndexedSymbol> lhs,
            SerializableFunction<IndexedSymbol, Word<IndexedSymbol>> inverseHomomorphism,
            Alphabet<IndexedSymbol> domain,
            RegularLanguage<IndexedSymbol> expected) {
        this.lhs = lhs;
        this.inverseHomomorphism = inverseHomomorphism;
        this.domain = domain;
        this.expected = expected;
    }

    @Test
    public void testRegularInverseHomomorphism() {
        InverseHomomorphism<IndexedSymbol, IndexedSymbol, RegularLanguage<IndexedSymbol>>
                inverseHomo =
                        new InverseHomomorphism<>(
                                inverseHomomorphism, domain.toUnmodifiableSet(), lhs);
        NFA<? extends Serializable, IndexedSymbol> result =
                new RegularInverseHomomorphismToEpsilonNFA<IndexedSymbol, IndexedSymbol>()
                        .apply(inverseHomo);
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

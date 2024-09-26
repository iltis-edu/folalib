package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import static de.tudortmund.cs.iltis.folalib.languages.RegularLanguages.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.io.reader.regex.RegularExpressionReaderProperties;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.SymmetricDifference;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RegularSymmetricDifferenceToDFATest {

    private final RegularLanguage<IndexedSymbol> lhs;
    private final RegularLanguage<IndexedSymbol> rhs;
    private final RegularLanguage<IndexedSymbol> expected;

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(
                new Object[][] {
                    {
                        emptyLanguage(), /* symmetric difference with */
                        emptyLanguage(), /* results in */
                        emptyLanguage()
                    },
                    {
                        evenLength(), /* symmetric difference with */
                        evenLength(), /* results in */
                        emptyLanguage()
                    },
                    {
                        evenLength(), /* symmetric difference with */
                        oddLength(), /* results in */
                        allWords()
                    },
                    {
                        oddLength(), /* symmetric difference with */
                        evenLength(), /* results in */
                        allWords()
                    },
                    {
                        words("a", "b", "c", "cb"), /* symmetric difference with */
                        words(
                                "a", "aa", "ab", "ba", "bb", "ac", "ca", "cc", "bc",
                                "cb"), /* results in */
                        words("b", "c", "aa", "ab", "ba", "bb", "ac", "ca", "cc", "bc")
                    },
                    {
                        languageFromRegex("a+aa*b"), /* symmetric difference with */
                        languageFromRegex("b+aa*b"), /* results in */
                        languageFromRegex("a+b")
                    },
                    {
                        languageFromRegex("a+aa*b"), /* symmetric difference with */
                        languageFromRegex("a+aa*b"), /* results in */
                        emptyLanguage()
                    }
                });
    }

    public RegularSymmetricDifferenceToDFATest(
            RegularLanguage<IndexedSymbol> lhs,
            RegularLanguage<IndexedSymbol> rhs,
            RegularLanguage<IndexedSymbol> expected) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.expected = expected;
    }

    @Test
    public void testRegularSymmetricDifference() {
        SymmetricDifference<RegularLanguage<IndexedSymbol>, RegularLanguage<IndexedSymbol>> symDif =
                new SymmetricDifference<>(lhs, rhs);
        NFA<? extends Serializable, IndexedSymbol> result =
                new RegularSymmetricDifferenceToDFA<IndexedSymbol>().apply(symDif);
        assertTrue(new RegularLanguage<>(result).isEqualTo(expected));
    }

    private static RegularLanguage<IndexedSymbol> languageFromRegex(String string) {
        Alphabet<IndexedSymbol> alphabet =
                new Alphabet<>(
                        new IndexedSymbol("a"), new IndexedSymbol("b"), new IndexedSymbol("c"));

        return new RegularLanguage<>(
                RegularExpression.fromString(
                        string, RegularExpressionReaderProperties.createDefault(alphabet)));
    }
}

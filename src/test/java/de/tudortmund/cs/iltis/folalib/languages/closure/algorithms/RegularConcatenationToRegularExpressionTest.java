package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import static de.tudortmund.cs.iltis.folalib.languages.RegularLanguages.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Concatenation;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RegularConcatenationToRegularExpressionTest {

    private final RegularLanguage<Character> lhs;
    private final RegularLanguage<Character> rhs;
    private final RegularLanguage<Character> expected;

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(
                new Object[][] {
                    {
                        emptyLanguage(), /* concatenated with */
                        evenNumberOfAs(), /* results in */
                        emptyLanguage()
                    },
                    {
                        evenLength(), /* concatenated with */
                        emptyWord(), /* results in */
                        evenLength()
                    },
                    {exact(1), /* concatenated with */ evenLength(), /* results in */ oddLength()},
                    {min(2), /* concatenated with */ min(4), /* results in */ min(6)},
                    {max(2), /* concatenated with */ max(4), /* results in */ max(6)},
                    {
                        words("a", "b", "c"), /* concatenated with */
                        words("a", "b", "c"), /* results in */
                        exact(2)
                    },
                    {
                        words("abc"), /* concatenated with */
                        allWords(), /* results in */
                        prefix("abc")
                    },
                    {
                        allWords(), /* concatenated with */
                        words("cc"), /* results in */
                        suffix("cc")
                    },
                });
    }

    public RegularConcatenationToRegularExpressionTest(
            RegularLanguage<Character> lhs,
            RegularLanguage<Character> rhs,
            RegularLanguage<Character> expected) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.expected = expected;
    }

    @Test
    public void testRegularConcatenation() {
        Concatenation<RegularLanguage<Character>, RegularLanguage<Character>> concatenation =
                new Concatenation<>(lhs, rhs);
        RegularExpression<Character> result =
                new RegularConcatenationToRegularExpression<Character>().apply(concatenation);
        assertTrue(new RegularLanguage<>(result).isEqualTo(expected));
    }
}

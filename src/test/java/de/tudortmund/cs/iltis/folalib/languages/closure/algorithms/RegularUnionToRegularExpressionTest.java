package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import static de.tudortmund.cs.iltis.folalib.languages.RegularLanguages.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Union;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RegularUnionToRegularExpressionTest {

    private final RegularLanguage<Character> lhs;
    private final RegularLanguage<Character> rhs;
    private final RegularLanguage<Character> expected;

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(
                new Object[][] {
                    {
                        emptyLanguage(), /* union with */
                        evenNumberOfAs(), /* results in */
                        evenNumberOfAs()
                    },
                    {evenLength(), /* union with */ oddLength(), /* results in */ allWords()},
                    {emptyWord(), /* union with */ evenLength(), /* results in */ evenLength()},
                    {min(2), /* union with */ min(4), /* results in */ min(2)},
                    {max(2), /* union with */ max(4), /* results in */ max(4)},
                    {
                        words("a", "b", "c"), /* union with */
                        words(
                                "aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb",
                                "cc"), /* results in */
                        between(1, 2)
                    }
                });
    }

    public RegularUnionToRegularExpressionTest(
            RegularLanguage<Character> lhs,
            RegularLanguage<Character> rhs,
            RegularLanguage<Character> expected) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.expected = expected;
    }

    @Test
    public void testRegularUnion() {
        Union<RegularLanguage<Character>, RegularLanguage<Character>> union = new Union<>(lhs, rhs);
        RegularExpression<Character> result =
                new RegularUnionToRegularExpression<Character>().apply(union);
        assertTrue(new RegularLanguage<>(result).isEqualTo(expected));
    }
}

package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import static de.tudortmund.cs.iltis.folalib.languages.RegularLanguages.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.KleeneStar;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RegularKleeneStarToRegularExpressionTest {

    private final RegularLanguage<Character> lhs;
    private final RegularLanguage<Character> expected;

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(
                new Object[][] {
                    {
                        /* kleene star of */
                        emptyLanguage(), /* results in */ emptyWord()
                    },
                    {
                        /* kleene star of */
                        emptyWord(), /* results in */ emptyWord()
                    },
                    {
                        /* kleene star of */
                        evenLength(), /* results in */ evenLength()
                    },
                    {
                        /* kleene star of */
                        oddLength(), /* results in */ allWords()
                    },
                    {
                        /* kleene star of */
                        noAs(), /* results in */ noAs()
                    },
                });
    }

    public RegularKleeneStarToRegularExpressionTest(
            RegularLanguage<Character> lhs, RegularLanguage<Character> expected) {
        this.lhs = lhs;
        this.expected = expected;
    }

    @Test
    public void testRegularKleeneStar() {
        KleeneStar<RegularLanguage<Character>> star = new KleeneStar<>(lhs);
        RegularExpression<Character> result =
                new RegularKleeneStarToRegularExpression<Character>().apply(star);
        assertTrue(new RegularLanguage<>(result).isEqualTo(expected));
    }
}

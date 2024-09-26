package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import static de.tudortmund.cs.iltis.folalib.languages.RegularLanguages.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.KleenePlus;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RegularKleenePlusToRegularExpressionTest {

    private final RegularLanguage<Character> lhs;
    private final RegularLanguage<Character> expected;

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(
                new Object[][] {
                    {
                        /* kleene plus of */
                        emptyLanguage(), /* results in */ emptyLanguage()
                    },
                    {
                        /* kleene plus of */
                        emptyWord(), /* results in */ emptyWord()
                    },
                    {
                        /* kleene plus of */
                        words("", "a", "b", "c"), /* results in */ allWords()
                    },
                    {
                        /* kleene plus of */
                        words(
                                "", "aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb",
                                "cc"), /* results in */
                        evenLength()
                    },
                    {
                        /* kleene plus of */
                        noAs(), /* results in */ noAs()
                    },
                });
    }

    public RegularKleenePlusToRegularExpressionTest(
            RegularLanguage<Character> lhs, RegularLanguage<Character> expected) {
        this.lhs = lhs;
        this.expected = expected;
    }

    @Test
    public void testRegularKleenePlus() {
        KleenePlus<RegularLanguage<Character>> plus = new KleenePlus<>(lhs);
        RegularExpression<Character> result =
                new RegularKleenePlusToRegularExpression<Character>().apply(plus);
        assertTrue(new RegularLanguage<>(result).isEqualTo(expected));
    }
}

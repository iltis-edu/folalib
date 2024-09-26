package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import static de.tudortmund.cs.iltis.folalib.languages.RegularLanguages.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.KleeneStar;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RegularKleeneStarToEpsilonNFATest {

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

    public RegularKleeneStarToEpsilonNFATest(
            RegularLanguage<Character> lhs, RegularLanguage<Character> expected) {
        this.lhs = lhs;
        this.expected = expected;
    }

    @Test
    public void testRegularKleeneStar() {
        KleeneStar<RegularLanguage<Character>> star = new KleeneStar<>(lhs);
        NFA<? extends Serializable, Character> result =
                new RegularKleeneStarToEpsilonNFA<Character>().apply(star);
        assertTrue(new RegularLanguage<>(result).isEqualTo(expected));
    }
}

package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import static de.tudortmund.cs.iltis.folalib.languages.RegularLanguages.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Reversal;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RegularReversalToEpsilonNFATest {

    private final RegularLanguage<Character> lhs;
    private final RegularLanguage<Character> expected;

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(
                new Object[][] {
                    {
                        /* reversing */
                        emptyLanguage(), /* results in */ emptyLanguage()
                    },
                    {
                        /* reversing */
                        evenLength(), /* results in */ evenLength()
                    },
                    {
                        /* reversing */
                        prefix("bbb"), /* results in */ suffix("bbb")
                    },
                    {
                        /* reversing */
                        suffix("bbb"), /* results in */ prefix("bbb")
                    },
                    {
                        /* reversing */
                        atMostTwoAs(), /* results in */ atMostTwoAs()
                    },
                    {
                        /* reversing */
                        firstEqualsLast(), /* results in */ firstEqualsLast()
                    },
                });
    }

    public RegularReversalToEpsilonNFATest(
            RegularLanguage<Character> lhs, RegularLanguage<Character> expected) {
        this.lhs = lhs;
        this.expected = expected;
    }

    @Test
    public void testRegularReversal() {
        Reversal<RegularLanguage<Character>> reversal = new Reversal<>(lhs);
        NFA<? extends Serializable, Character> result =
                new RegularReversalToEpsilonNFA<Character>().apply(reversal);
        assertTrue(new RegularLanguage<>(result).isEqualTo(expected));
    }
}

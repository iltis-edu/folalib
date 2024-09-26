package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import static de.tudortmund.cs.iltis.folalib.languages.RegularLanguages.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Difference;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RegularDifferenceToEpsilonNFATest {

    private final RegularLanguage<Character> lhs;
    private final RegularLanguage<Character> rhs;
    private final RegularLanguage<Character> expected;

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(
                new Object[][] {
                    {
                        emptyLanguage(), /* subtracting */
                        evenNumberOfAs(), /* results in */
                        emptyLanguage()
                    },
                    {
                        firstEqualsLast(), /* subtracting */
                        emptyLanguage(), /* results in */
                        firstEqualsLast()
                    },
                    {evenLength(), /* subtracting */ oddLength(), /* results in */ evenLength()},
                    {allWords(), /* subtracting */ evenLength(), /* results in */ oddLength()},
                    {max(4), /* subtracting */ max(2), /* results in */ between(3, 4)},
                    {max(4), /* subtracting */ min(2), /* results in */ max(1)},
                    {
                        words("a", "b", "c"), /* subtracting */
                        words("a", "aa"), /* results in */
                        words("b", "c")
                    }
                });
    }

    public RegularDifferenceToEpsilonNFATest(
            RegularLanguage<Character> lhs,
            RegularLanguage<Character> rhs,
            RegularLanguage<Character> expected) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.expected = expected;
    }

    @Test
    public void testRegularDifference() {
        Difference<RegularLanguage<Character>, RegularLanguage<Character>> difference =
                new Difference<>(lhs, rhs);
        NFA<? extends Serializable, Character> result =
                new RegularDifferenceToEpsilonNFA<Character>().apply(difference);
        assertTrue(new RegularLanguage<>(result).isEqualTo(expected));
    }
}

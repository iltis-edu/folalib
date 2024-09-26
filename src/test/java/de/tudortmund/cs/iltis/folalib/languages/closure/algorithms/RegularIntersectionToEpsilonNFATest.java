package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import static de.tudortmund.cs.iltis.folalib.languages.RegularLanguages.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Intersection;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RegularIntersectionToEpsilonNFATest {

    private final RegularLanguage<Character> lhs;
    private final RegularLanguage<Character> rhs;
    private final RegularLanguage<Character> expected;

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(
                new Object[][] {
                    {
                        emptyLanguage(), /* intersected with */
                        evenNumberOfAs(), /* results in */
                        emptyLanguage()
                    },
                    {
                        evenLength(), /* intersected with */
                        oddLength(), /* results in */
                        emptyLanguage()
                    },
                    {
                        emptyWord(), /* intersected with */
                        evenLength(), /* results in */
                        emptyWord()
                    },
                    {min(2), /* intersected with */ max(4), /* results in */ between(2, 4)},
                    {
                        oddLength(), /* intersected with */
                        words("a", "aa", "aaa"), /* results in */
                        words("a", "aaa")
                    },
                    {
                        evenNumberOfAs(), /* intersected with */
                        max(1), /* results in */
                        words("", "b", "c")
                    },
                    {
                        allWords(), /* intersected with */
                        firstEqualsLast(), /* result in */
                        firstEqualsLast()
                    }
                });
    }

    public RegularIntersectionToEpsilonNFATest(
            RegularLanguage<Character> lhs,
            RegularLanguage<Character> rhs,
            RegularLanguage<Character> expected) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.expected = expected;
    }

    @Test
    public void testRegularIntersection() {
        Intersection<RegularLanguage<Character>, RegularLanguage<Character>> intersection =
                new Intersection<>(lhs, rhs);
        NFA<? extends Serializable, Character> result =
                new RegularIntersectionToEpsilonNFA<Character>().apply(intersection);
        assertTrue(new RegularLanguage<>(result).isEqualTo(expected));
    }
}

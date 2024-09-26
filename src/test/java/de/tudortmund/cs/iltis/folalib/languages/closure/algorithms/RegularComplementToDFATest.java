package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import static de.tudortmund.cs.iltis.folalib.languages.RegularLanguages.*;
import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Complement;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RegularComplementToDFATest {

    private final RegularLanguage<Character> lhs;
    private final RegularLanguage<Character> expected;

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(
                new Object[][] {
                    {
                        /* complementing */
                        emptyLanguage(), /* results in */ allWords()
                    },
                    {
                        /* complementing */
                        allWords(), /* results in */ emptyLanguage()
                    },
                    {
                        /* complementing */
                        evenLength(), /* results in */ oddLength()
                    },
                    {
                        /* complementing */
                        min(3), /* results in */ max(2)
                    },
                    {
                        /* complementing */
                        words("", "a", "b", "c"), /* results in */ min(2)
                    },
                    {
                        /* complemeting */
                        emptyWord(), /* results in */ min(1)
                    }
                });
    }

    public RegularComplementToDFATest(
            RegularLanguage<Character> lhs, RegularLanguage<Character> expected) {
        this.lhs = lhs;
        this.expected = expected;
    }

    @Test
    public void testRegularComplement() {
        Complement<RegularLanguage<Character>> complement = new Complement<>(lhs);
        NFA<? extends Serializable, Character> result =
                new RegularComplementToDFA<Character>().apply(complement);
        assertTrue(new RegularLanguage<>(result).isEqualTo(expected));
    }
}

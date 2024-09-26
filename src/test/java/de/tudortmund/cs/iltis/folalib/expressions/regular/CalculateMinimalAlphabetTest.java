package de.tudortmund.cs.iltis.folalib.expressions.regular;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import org.junit.Test;

public class CalculateMinimalAlphabetTest {

    @Test
    public void test() {
        Range<Character> range = Range.from(Alphabets.characterAlphabet("abcd"), 'a', 'c');

        RegularExpression<Character> regex =
                new Symbol<>(new Alphabet<>('a', 'e'), 'a')
                        .star()
                        .concat(range)
                        .concat(
                                new Repetition<>(new Symbol<>(new Alphabet<>('f'), 'f'), 3, 5)
                                        .or(
                                                new EmptyWord<>(new Alphabet<>('a', 'x', 'y', 'z'))
                                                        .or(new EmptyWord<>())));

        assertEquals(
                regex.withAlphabet(Alphabets.characterAlphabet("abcf")),
                regex.withMinimalAlphabet()); // d, e, x, y, z do not occur
    }
}

package de.tudortmund.cs.iltis.folalib.expressions.regular;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFATestHelper;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import org.junit.Test;

public class RegularExpressionTest extends NFATestHelper {
    private final Symbol<Character> reA;
    private final Symbol<Character> reB;
    private final RegularExpression<Character> epsilon;
    private final RegularExpression<Character> empty;
    private final RegularExpression<Character> concatAB;
    private final RegularExpression<Character> alternAB;
    private final RegularExpression<Character> kleeneA;
    private final RegularExpression<Character> rangeABC;

    public RegularExpressionTest() {
        Alphabet<Character> alphabet = Alphabets.characterAlphabet("abc");
        this.reA = new Symbol<>(alphabet, 'a');
        this.reB = new Symbol<>(alphabet, 'b');
        this.epsilon = new EmptyWord<>(alphabet);
        this.empty = new EmptyLanguage<>(alphabet);
        this.concatAB = reA.concat(reB);
        this.alternAB = reA.or(reB);
        this.kleeneA = reA.star();
        this.rangeABC = Range.from(alphabet, reA.getSymbol(), reB.getSymbol());
    }

    @Test
    public void equalsTest() {
        assertNotEqual(reA, reB);
        assertNotEqual(reA, epsilon);
        assertNotEqual(reA, empty);
        assertNotEqual(reA, concatAB);
        assertNotEqual(reA, alternAB);
        assertNotEqual(reA, kleeneA);
        assertNotEqual(reA, rangeABC);

        assertNotEqual(epsilon, empty);
        assertNotEqual(epsilon, reB);
        assertEquals(epsilon, epsilon);
        assertNotEqual(epsilon, empty);
        assertNotEqual(epsilon, concatAB);
        assertNotEqual(epsilon, alternAB);
        assertNotEqual(epsilon, kleeneA);
        assertNotEqual(epsilon, rangeABC);

        assertEquals(epsilon, epsilon);
        assertNotEqual(epsilon, empty);
        assertNotEqual(epsilon, reB);
        assertNotEqual(epsilon, empty);
        assertNotEqual(epsilon, concatAB);
        assertNotEqual(epsilon, alternAB);
        assertNotEqual(epsilon, kleeneA);

        assertEquals(empty, empty);
        assertNotEqual(empty, reB);
        assertNotEqual(empty, epsilon);
        assertEquals(empty, empty);
        assertNotEqual(empty, concatAB);
        assertNotEqual(empty, alternAB);
        assertNotEqual(empty, kleeneA);
        assertNotEqual(empty, rangeABC);

        assertNotEqual(concatAB, empty);
        assertNotEqual(concatAB, reB);
        assertNotEqual(concatAB, epsilon);
        assertNotEqual(concatAB, empty);
        assertEquals(concatAB, concatAB);
        assertNotEqual(concatAB, alternAB);
        assertNotEqual(concatAB, kleeneA);
        assertNotEqual(concatAB, rangeABC);

        assertNotEqual(alternAB, empty);
        assertNotEqual(alternAB, reB);
        assertNotEqual(alternAB, epsilon);
        assertNotEqual(alternAB, empty);
        assertNotEqual(alternAB, concatAB);
        assertEquals(alternAB, alternAB);
        assertNotEqual(alternAB, kleeneA);
        assertNotEqual(alternAB, rangeABC);

        assertNotEqual(kleeneA, empty);
        assertNotEqual(kleeneA, reB);
        assertNotEqual(kleeneA, epsilon);
        assertNotEqual(kleeneA, empty);
        assertNotEqual(kleeneA, concatAB);
        assertNotEquals(kleeneA, alternAB);
        assertEquals(kleeneA, kleeneA);
        assertNotEqual(kleeneA, rangeABC);

        assertNotEqual(rangeABC, empty);
        assertNotEqual(rangeABC, reB);
        assertNotEqual(rangeABC, epsilon);
        assertNotEqual(rangeABC, empty);
        assertNotEqual(rangeABC, concatAB);
        assertNotEquals(rangeABC, alternAB);
        assertNotEqual(rangeABC, kleeneA);
        assertEquals(rangeABC, rangeABC);
    }

    @Test
    public void testStrictRegexConstructionViaConstructors() {

        try {
            RegularExpression<Character> symbol =
                    new Symbol<>(Alphabets.characterAlphabet("bc"), 'a');
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Alphabet of RegularExpression.Symbol must contain symbol.", e.getMessage());
        }

        try {
            RegularExpression<Character> a_over_ab =
                    new Symbol<>(Alphabets.characterAlphabet("ab"), 'a');
            RegularExpression<Character> b_over_bc =
                    new Symbol<>(Alphabets.characterAlphabet("bc"), 'b');
            RegularExpression<Character> a_then_b = new Concatenation<>(a_over_ab, b_over_bc);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Alphabet of all children must be identical to be inferred.", e.getMessage());
        }

        try {
            RegularExpression<Character> emptyConcatenation = new Concatenation<>();
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "At least one child required to infer alphabet from subexpressions.",
                    e.getMessage());
        }

        try {
            RegularExpression<Character> a_over_ab =
                    new Symbol<>(Alphabets.characterAlphabet("ab"), 'a');
            RegularExpression<Character> negativeBound = new Repetition<>(a_over_ab, -3);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Lower bound of Repetition must not be negative.", e.getMessage());
        }

        try {
            RegularExpression<Character> a_over_ab =
                    new Symbol<>(Alphabets.characterAlphabet("ab"), 'a');
            RegularExpression<Character> negativeBound = new Repetition<>(a_over_ab, 5, 2);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Lower bound of Repetition must not be greater than upper bound.",
                    e.getMessage());
        }
    }

    @Test
    public void testConvenienceRegexConstructionViaConvenienceMethods() {
        Alphabet<Character> a = new Alphabet<>('a');
        Alphabet<Character> b = new Alphabet<>('b');
        Alphabet<Character> c = new Alphabet<>('c');
        Alphabet<Character> ab = Alphabets.characterAlphabet("ab");
        Alphabet<Character> bc = Alphabets.characterAlphabet("bc");
        Alphabet<Character> ca = Alphabets.characterAlphabet("ca");
        Alphabet<Character> abc = Alphabets.characterAlphabet("abc");
        RegularExpression<Character> a_over_a = new Symbol<>(a, 'a');
        RegularExpression<Character> b_over_b = new Symbol<>(b, 'b');
        RegularExpression<Character> b_over_bc = new Symbol<>(bc, 'b');
        RegularExpression<Character> c_over_c = new Symbol<>(c, 'c');

        assertEquals(ab, a_over_a.concat(b_over_b).getAlphabet());
        assertEquals(a, a_over_a.or(a_over_a).getAlphabet());
        assertEquals(bc, b_over_bc.star().getAlphabet());
        assertEquals(c, c_over_c.optional().getAlphabet());
        assertEquals(abc, a_over_a.or(b_over_b, c_over_c).getAlphabet());
        assertEquals(ab, a_over_a.concat(new EmptyWord<>(ab)).getAlphabet());
    }

    @Test
    public void testWithAlphabet() {

        RegularExpression<Character> s1 = new Symbol<>(new Alphabet<>('a'), 'a');
        assertEquals(
                Alphabets.characterAlphabet("abc"),
                s1.withAlphabet(Alphabets.characterAlphabet("abc")).getAlphabet());

        try {
            RegularExpression<Character> s = new Symbol<>(new Alphabet<>('a'), 'a');
            s.withAlphabet(Alphabets.characterAlphabet("bcd"));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Alphabet of RegularExpression.Symbol must contain symbol.", e.getMessage());
        }

        try {
            Alphabet<Character> alphabet = Alphabets.characterAlphabet("abc");
            RegularExpression<Character> s =
                    new Symbol<>(alphabet, 'a')
                            .star()
                            .concat(new EmptyWord<>(alphabet).or(new Symbol<>(alphabet, 'c')));
            s.withAlphabet(Alphabets.characterAlphabet("ab"));
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Alphabet of RegularExpression.Symbol must contain symbol.", e.getMessage());
        }
    }
}

package de.tudortmund.cs.iltis.folalib.expressions.regular;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import org.junit.Test;

public class RegularExpressionSimplifierTest {

    private final Alphabet<Character> alphabetA = Alphabets.characterAlphabet("a");
    private final Alphabet<Character> alphabetABC = Alphabets.characterAlphabet("abc");
    private final Alphabet<Character> alphabetBC = Alphabets.characterAlphabet("bc");

    @Test
    public void testSimplifyAlternativeWithEmptyLanguage() {
        RegularExpression<Character> regex =
                new Alternative<>(new Symbol<>(alphabetABC, 'a'), new EmptyLanguage<>(alphabetABC));
        RegularExpression<Character> simplified = RegularExpressionSimplifier.simplify(regex);
        assertEquals(new Symbol<>(alphabetABC, 'a'), simplified);
    }

    @Test
    public void testSimplifyAlternativeWithSingleChild() {
        RegularExpression<Character> regex = new Alternative<>(new Symbol<>(alphabetA, 'a'));
        RegularExpression<Character> simplified = RegularExpressionSimplifier.simplify(regex);
        assertEquals(new Symbol<>(alphabetA, 'a'), simplified);
    }

    @Test
    public void testSimplifyAlternativeWithNestedAlternative() {
        RegularExpression<Character> regex =
                new Alternative<>(
                        new Symbol<>(alphabetABC, 'a'),
                        new Alternative<>(
                                new Symbol<>(alphabetABC, 'b'), new Symbol<>(alphabetABC, 'c')));
        RegularExpression<Character> simplified = RegularExpressionSimplifier.simplify(regex);
        assertEquals(
                new Alternative<>(
                        new Symbol<>(alphabetABC, 'a'),
                        new Symbol<>(alphabetABC, 'b'),
                        new Symbol<>(alphabetABC, 'c')),
                simplified);
    }

    @Test
    public void testSimplifyConcatenationWithEmptyLanguage() {
        RegularExpression<Character> regex =
                new Concatenation<>(new EmptyLanguage<>(alphabetA), new Symbol<>(alphabetA, 'a'));
        RegularExpression<Character> simplified = RegularExpressionSimplifier.simplify(regex);
        assertEquals(new EmptyLanguage<>(alphabetA), simplified);
    }

    @Test
    public void testSimplifyConcatenationWithSingleChild() {
        RegularExpression<Character> regex = new Concatenation<>(new Symbol<>(alphabetABC, 'a'));
        RegularExpression<Character> simplified = RegularExpressionSimplifier.simplify(regex);
        assertEquals(new Symbol<>(alphabetABC, 'a'), simplified);
    }

    @Test
    public void testSimplifyConcatenationOfNestedConcatenation() {
        RegularExpression<Character> regex =
                new Concatenation<>(
                        new Concatenation<>(
                                new Symbol<>(alphabetABC, 'a'), new Symbol<>(alphabetABC, 'b')),
                        new Symbol<>(alphabetABC, 'c'));
        RegularExpression<Character> simplified = RegularExpressionSimplifier.simplify(regex);
        assertEquals(
                new Concatenation<>(
                        new Symbol<>(alphabetABC, 'a'),
                        new Symbol<>(alphabetABC, 'b'),
                        new Symbol<>(alphabetABC, 'c')),
                simplified);
    }

    @Test
    public void testSimplifyConcatenationWithEmptyWord() {
        RegularExpression<Character> regex =
                new Concatenation<>(new EmptyWord<>(alphabetABC), new Symbol<>(alphabetABC, 'a'));
        RegularExpression<Character> simplified = RegularExpressionSimplifier.simplify(regex);
        assertEquals(new Symbol<>(alphabetABC, 'a'), simplified);
    }

    @Test
    public void testSimplifyKleeneStarOfEmptyWord() {
        RegularExpression<Character> regex = new KleeneStar<>(new EmptyWord<>(alphabetA));
        RegularExpression<Character> simplified = RegularExpressionSimplifier.simplify(regex);
        assertEquals(new EmptyWord<>(alphabetA), simplified);
    }

    @Test
    public void testSimplifyKleeneStarOfEmptyLanguage() {
        RegularExpression<Character> regex = new KleeneStar<>(new EmptyLanguage<>(alphabetBC));
        RegularExpression<Character> simplified = RegularExpressionSimplifier.simplify(regex);
        assertEquals(new EmptyWord<>(alphabetBC), simplified);
    }

    @Test
    public void testSimplifyKleenePlusOfEmptyWord() {
        RegularExpression<Character> regex = new KleenePlus<>(new EmptyWord<>(alphabetABC));
        RegularExpression<Character> simplified = RegularExpressionSimplifier.simplify(regex);
        assertEquals(new EmptyWord<>(alphabetABC), simplified);
    }

    @Test
    public void testSimplifyKleenePlusOfEmptyLanguage() {
        RegularExpression<Character> regex = new KleenePlus<>(new EmptyLanguage<>(alphabetBC));
        RegularExpression<Character> simplified = RegularExpressionSimplifier.simplify(regex);
        assertEquals(new EmptyWord<>(alphabetBC), simplified);
    }

    @Test
    public void testRange() {
        RegularExpression<Character> regex =
                new Concatenation<>(
                        Range.from(alphabetABC, 'a', 'c'),
                        new EmptyWord<>(alphabetABC),
                        Range.from(alphabetABC, 'a', 'b'));
        RegularExpression<Character> simplified = RegularExpressionSimplifier.simplify(regex);
        assertEquals(
                Range.from(alphabetABC, 'a', 'c').concat(Range.from(alphabetABC, 'a', 'b')),
                simplified);
    }
}

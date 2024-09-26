package de.tudortmund.cs.iltis.folalib.expressions.regular;

import static org.junit.Assert.assertEquals;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import org.junit.Test;

public class ToStandardRegexTransformTest {

    private final Symbol<Character> reA;
    private final Symbol<Character> reB;
    private final RegularExpression<Character> epsilon;
    private final RegularExpression<Character> empty;
    private final RegularExpression<Character> concatAB;
    private final RegularExpression<Character> alternAB;
    private final RegularExpression<Character> kleeneA;

    Alphabet<Character> alphabet = Alphabets.characterAlphabet("abc");

    public ToStandardRegexTransformTest() {
        this.reA = new Symbol<>(alphabet, 'a');
        this.reB = new Symbol<>(alphabet, 'b');
        this.epsilon = new EmptyWord<>(alphabet);
        this.empty = new EmptyLanguage<>(alphabet);
        this.concatAB = reA.concat(reB);
        this.alternAB = reA.or(reB);
        this.kleeneA = reA.star();
    }

    @Test
    public void toStandardTransformationTest() {
        assertEquals(reA, ToStandardRegexTransform.toStandard(reA));
        assertEquals(epsilon, ToStandardRegexTransform.toStandard(epsilon));
        assertEquals(empty, ToStandardRegexTransform.toStandard(empty));
        assertEquals(concatAB, ToStandardRegexTransform.toStandard(concatAB));
        assertEquals(alternAB, ToStandardRegexTransform.toStandard(alternAB));
        assertEquals(kleeneA, ToStandardRegexTransform.toStandard(kleeneA));

        assertEquals(reA.or(epsilon), ToStandardRegexTransform.toStandard(reA.optional()));
        assertEquals(reA.concat(reA.star()), ToStandardRegexTransform.toStandard(reA.plus()));
        assertEquals(reA.concat(reA), ToStandardRegexTransform.toStandard(reA.repetition(2)));
        assertEquals(reA.concat(reA, reA), ToStandardRegexTransform.toStandard(reA.repetition(3)));

        assertEquals(
                reA.concat(
                        reA,
                        reA,
                        reA.or(new EmptyWord<>(alphabet)),
                        reA.or(new EmptyWord<>(alphabet))),
                ToStandardRegexTransform.toStandard(reA.repetition(3, 5)));

        assertEquals(
                reA.or(new EmptyWord<>(alphabet))
                        .or(new EmptyWord<>(alphabet))
                        .concat(
                                reA.or(new EmptyWord<>(alphabet))
                                        .or(new EmptyWord<>(alphabet))
                                        .star())
                        .concat(
                                new Concatenation<>(
                                        reA,
                                        reA,
                                        reA,
                                        reA.or(new EmptyWord<>(alphabet)),
                                        reA.or(new EmptyWord<>(alphabet))))
                        .or(reA.star().concat(new EmptyWord<>(alphabet))),
                ToStandardRegexTransform.toStandard(
                        reA.optional()
                                .optional()
                                .plus()
                                .concat(reA.repetition(3, 5))
                                .or(reA.star().concat(new EmptyWord<>(alphabet)))));

        Alphabet<IndexedSymbol> localAlphabet =
                new Alphabet<>(
                        new IndexedSymbol("a"),
                        new IndexedSymbol("b"),
                        new IndexedSymbol("c"),
                        new IndexedSymbol("d"));

        Symbol<IndexedSymbol> a = new Symbol<>(localAlphabet, new IndexedSymbol("a"));
        Symbol<IndexedSymbol> b = new Symbol<>(localAlphabet, new IndexedSymbol("b"));
        Symbol<IndexedSymbol> c = new Symbol<>(localAlphabet, new IndexedSymbol("c"));
        Symbol<IndexedSymbol> d = new Symbol<>(localAlphabet, new IndexedSymbol("d"));

        assertEquals(
                ToStandardRegexTransform.toStandard(
                        Range.from(localAlphabet, a.getSymbol(), c.getSymbol())),
                new Alternative<>(a, b, c));

        // No IndexedSymbols are included in this range for the given alphabet. This should return
        // an epsilon
        assertEquals(
                ToStandardRegexTransform.toStandard(
                        Range.from(localAlphabet, d.getSymbol(), c.getSymbol())),
                new EmptyLanguage<>(localAlphabet));
    }

    @Test
    public void testRepetitionEdgeCases() {
        assertEquals(
                new Concatenation<>(
                        new EmptyWord<>(alphabet),
                        reA,
                        new Alternative<>(reA, new EmptyWord<>(alphabet))),
                ToStandardRegexTransform.toStandard(
                        new Concatenation<>(
                                reA.repetition(0), reA.repetition(1), reA.repetition(0, 1))));
    }

    @Test
    public void testRangeEdgeCases() {
        assertEquals(
                new Concatenation<>(reA, new EmptyLanguage<>(alphabet)),
                ToStandardRegexTransform.toStandard(
                        new Concatenation<>(
                                Range.from(alphabet, reA.getSymbol(), reA.getSymbol()),
                                Range.from(alphabet, reB.getSymbol(), reA.getSymbol()))));
    }
}

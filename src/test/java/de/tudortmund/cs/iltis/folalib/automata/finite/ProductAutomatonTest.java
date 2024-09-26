package de.tudortmund.cs.iltis.folalib.automata.finite;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.StateSupplier;
import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.expressions.regular.Symbol;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.folalib.util.BinaryFunctions;
import de.tudortmund.cs.iltis.utils.collections.SerializablePair;
import java.io.Serializable;
import java.util.LinkedHashSet;
import org.junit.Test;

public class ProductAutomatonTest {
    @Test
    public void testProductAutomatonAndComplement() {
        Alphabet<Integer> alphabet = new Alphabet<>(0, 1);
        NFA<Integer, Integer> mod3 =
                new NFABuilder<Integer, Integer>(alphabet)
                        .withInitial(0)
                        .withStates(1, 2)
                        .withAccepting(0)
                        .withTransition(0, 0, 0)
                        .withTransition(1, 0, 1)
                        .withTransition(2, 0, 2)
                        .withTransition(0, 1, 1)
                        .withTransition(1, 1, 2)
                        .withTransition(2, 1, 0)
                        .build()
                        .unwrap();

        RegularExpression<Integer> zeroStar = new Symbol<>(alphabet, 0).star();
        NFA<? extends Serializable, Integer> mod2 =
                new RegularLanguage<>(
                                zeroStar.concat(
                                                new Symbol<>(alphabet, 1)
                                                        .concat(zeroStar)
                                                        .concat(new Symbol<>(alphabet, 1))
                                                        .star())
                                        .concat(zeroStar))
                        .getNFA();

        NFA<? extends Serializable, Integer> mod6 =
                mod3.productWithoutDFATransform(mod2, BinaryFunctions.AND);
        NFA<Integer, Integer> notMod3 =
                mod3.complementWithoutDFATransform(-1)
                        .get(); // Dummy discardState because mod3 ist already total

        for (int i = 0; i < 1024 /*8192*/; ++i) {
            Word<Integer> word = new Word<>(toBitArray(i));
            assertEquals(
                    new NFAExecutor<>(mod6, word).run(),
                    new NFAExecutor<>(mod2, word).run() && new NFAExecutor<>(mod3, word).run());
            assertNotEquals(
                    new NFAExecutor<>(notMod3, word).run(), new NFAExecutor<>(mod3, word).run());
        }
    }

    @Test
    public void testProductAutomatonAndComplementWithMultipleInitialStates() {
        Alphabet<Integer> alphabet = new Alphabet<>(0, 1);
        NFA<Integer, Integer> mod3 =
                new NFABuilder<Integer, Integer>(alphabet)
                        .withInitial(0, 2)
                        .withStates(1)
                        .withAccepting(0)
                        .withTransition(0, 0, 0)
                        .withTransition(1, 0, 1)
                        .withTransition(2, 0, 2)
                        .withTransition(0, 1, 1)
                        .withTransition(1, 1, 2)
                        .withTransition(2, 1, 0)
                        .build()
                        .unwrap();

        RegularExpression<Integer> zeroStar = new Symbol<>(alphabet, 0).star();
        NFA<? extends Serializable, Integer> mod2 =
                new RegularLanguage<>(
                                zeroStar.concat(
                                                new Symbol<>(alphabet, 1)
                                                        .concat(zeroStar)
                                                        .concat(new Symbol<>(alphabet, 1))
                                                        .star())
                                        .concat(zeroStar))
                        .getNFA();

        // Map the states to integers to get rid of the '?'
        StateSupplier<Integer> supplier = StateSupplier.integerStateSupplier();
        NFA<Integer, Integer> mod2Mapped = mod2.mapStates(t -> supplier.get());

        // The method automatically converts mod3 into a DFA to successfully perform product()
        NFA<SerializablePair<LinkedHashSet<Integer>, LinkedHashSet<Integer>>, Integer> mod6 =
                mod3.product(mod2Mapped, BinaryFunctions.AND);

        for (int i = 0; i < 1024 /*8192*/; ++i) {
            Word<Integer> word = new Word<>(toBitArray(i));

            assertEquals(
                    new NFAExecutor<>(mod6, word).run(),
                    new NFAExecutor<>(mod2, word).run() && new NFAExecutor<>(mod3, word).run());
        }
    }

    @Test
    public void testEmptyProductAutomaton() {
        NFA<Integer, Integer> mod3 =
                new NFABuilder<Integer, Integer>(new Alphabet<>(0, 1))
                        .withInitial(0)
                        .withStates(1, 2)
                        .withAccepting(0)
                        .withTransition(0, 0, 0)
                        .withTransition(1, 0, 1)
                        .withTransition(2, 0, 2)
                        .withTransition(0, 1, 1)
                        .withTransition(1, 1, 2)
                        .withTransition(2, 1, 0)
                        .build()
                        .unwrap();

        NFABuilder<Integer, Integer> builder =
                new NFABuilder<Integer, Integer>(new Alphabet<>(0, 1))
                        .withInitial(0)
                        .withStates(1, 2, 4, 5)
                        .withAccepting(0, 3);

        for (int i = 0; i < 6; ++i) {
            builder.withTransition(i, 0, i).withTransition(i, 1, (i + 1) % 6);
        }

        NFA<Integer, Integer> mod3Complicated = builder.buildAndReset().unwrap();

        // Equivalence test via symmetric difference (Bollig GTI)
        assertTrue(mod3.product(mod3Complicated, BinaryFunctions.XOR).isEmpty());

        // Equivalence test via subset test via intersection with complement (Schwentick GTI)
        assertTrue(
                mod3.product(
                                mod3Complicated.complementWithoutDFATransform(-1).get(),
                                BinaryFunctions.AND)
                        .isEmpty());
        assertTrue(
                mod3.complementWithoutDFATransform(-1)
                        .get()
                        .product(mod3Complicated, BinaryFunctions.AND)
                        .isEmpty());
    }

    private Integer[] toBitArray(int i) {
        if (i == 0) return new Integer[0];
        if (i == 1) return new Integer[] {1};

        int highest = (int) Math.floor(Math.log(i) / Math.log(2));
        Integer[] arr = new Integer[highest + 1];

        for (int j = highest; j >= 0; --j) {
            int power = (int) Math.pow(2, j);
            if (power >= i) {
                arr[j] = 1;
                i -= power;
            } else {
                arr[j] = 0;
            }
        }

        return arr;
    }
}

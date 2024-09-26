package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.utils.test.AdvancedTest;
import java.io.Serializable;
import java.util.Collection;

public class Utils extends AdvancedTest {

    protected <T extends Serializable, S extends Serializable, K extends Serializable>
            void assertAccepts(PDA<T, S, K> pda, Word<S> word, int maxSteps) {
        try {
            PDAStepper<T, S, K> stepper = new PDAStepper<>(pda, word);
            assertTrue(stepper.run(maxSteps));
        } catch (Exception e) {
            fail();
        }
    }

    protected <T extends Serializable, S extends Serializable, K extends Serializable>
            void assertRejects(PDA<T, S, K> pda, Word<S> word, int maxSteps) {
        try {
            PDAStepper<T, S, K> stepper = new PDAStepper<>(pda, word);
            assertFalse(stepper.run(maxSteps));
        } catch (Exception e) {
            fail();
        }
    }

    protected <
                    T extends Serializable,
                    U extends Serializable,
                    S extends Serializable,
                    K extends Serializable,
                    J extends Serializable>
            void assertEquivalent(
                    PDA<T, S, K> pda1,
                    PDA<U, S, J> pda2,
                    Iterable<? extends Word<S>> words,
                    int maxSteps) {
        try {
            for (Word<S> word : words) {
                PDAStepper<T, S, K> stepper1 = new PDAStepper<>(pda1, word);
                PDAStepper<U, S, J> stepper2 = new PDAStepper<>(pda2, word);
                if (stepper1.run(maxSteps) != stepper2.run(maxSteps)) {
                    fail();
                }
            }
        } catch (Exception e) {
            fail();
        }
    }

    protected <T> void assertContains(Collection<T> collection, T element) {
        assertTrue(collection.contains(element));
    }

    protected <I, G> MaybeGenerated<I, G> in(I i) {
        return new MaybeGenerated.Input<>(i);
    }

    protected <I, G> MaybeGenerated<I, G> gen(G g) {
        return new MaybeGenerated.Generated<>(g);
    }
}

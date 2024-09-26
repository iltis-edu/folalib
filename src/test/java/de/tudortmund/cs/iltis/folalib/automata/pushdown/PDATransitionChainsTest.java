package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.transform.ConstrainedSupplier;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import java.util.List;
import org.junit.Test;

public class PDATransitionChainsTest {

    private final String q0 = "q0";
    private final String q1 = "q1";

    @Test
    public void testSingletonTransitionChain() {
        PDATransition<String, Character, Integer> transition =
                new PDATransition<>(q1, 'a', PDAStackSymbol.exactly(2), new PDAStackWord<>(1, 0));

        ConstrainedSupplier<String> stateSupplier = ConstrainedSupplier.constrainedStringSupplier();
        PDATransitionChains<String, Character, Integer> chains =
                new PDATransitionChains<>(q0, transition, new Alphabet<>(0, 1, 2), stateSupplier);

        assertEquals(1, chains.getNumberOfChains());

        PDATransitionChains.PDATransitionChain<String, Character, Integer> chain =
                chains.getChainAt(0);

        String state0 = chain.get(0).first();
        PDATransition<String, Character, Integer> trans0 = chain.get(0).second();

        assertEquals(1, chain.size());

        assertEquals(q0, state0);
        assertEquals(q1, trans0.getState());
        assertEquals(transition.getInputSymbol(), trans0.getInputSymbol());
        assertEquals(2, trans0.getNewTopOfStack().size());
    }

    @Test
    public void testTransitionChain() {
        PDATransition<String, Character, Integer> transition =
                new PDATransition<>(
                        q1, 'a', PDAStackSymbol.exactly(3), new PDAStackWord<>(1, 0, 2, 0, 1, 3));

        ConstrainedSupplier<String> stateSupplier = ConstrainedSupplier.constrainedStringSupplier();
        PDATransitionChains<String, Character, Integer> chains =
                new PDATransitionChains<>(
                        q0, transition, new Alphabet<>(0, 1, 2, 3), stateSupplier);

        assertEquals(1, chains.getNumberOfChains());

        PDATransitionChains.PDATransitionChain<String, Character, Integer> chain =
                chains.getChainAt(0);

        String state0 = chain.get(0).first();
        String state1 = chain.get(1).first();
        String state2 = chain.get(2).first();
        String state3 = chain.get(3).first();
        String state4 = chain.get(4).first();

        PDATransition<String, Character, Integer> trans0 = chain.get(0).second();
        PDATransition<String, Character, Integer> trans1 = chain.get(1).second();
        PDATransition<String, Character, Integer> trans2 = chain.get(2).second();
        PDATransition<String, Character, Integer> trans3 = chain.get(3).second();
        PDATransition<String, Character, Integer> trans4 = chain.get(4).second();

        assertEquals(5, chain.size());

        assertEquals(q0, state0);
        assertEquals(transition.getInputSymbol(), trans0.getInputSymbol());
        assertFalse(trans0.getStackSymbol().isVariable());
        assertEquals(state1, trans0.getState());
        assertEquals(new PDAStackWord<>(1, 3), trans0.getNewTopOfStack());

        assertTrue(trans1.isEpsilon());
        assertFalse(trans1.getStackSymbol().isVariable());
        assertEquals(state2, trans1.getState());
        assertEquals(new PDAStackWord<>(0, 1), trans1.getNewTopOfStack());

        assertTrue(trans2.isEpsilon());
        assertFalse(trans2.getStackSymbol().isVariable());
        assertEquals(state3, trans2.getState());
        assertEquals(new PDAStackWord<>(2, 0), trans2.getNewTopOfStack());

        assertTrue(trans3.isEpsilon());
        assertFalse(trans3.getStackSymbol().isVariable());
        assertEquals(state4, trans3.getState());
        assertEquals(new PDAStackWord<>(0, 2), trans3.getNewTopOfStack());

        assertTrue(trans4.isEpsilon());
        assertFalse(trans1.getStackSymbol().isVariable());
        assertEquals(new PDAStackWord<>(1, 0), trans4.getNewTopOfStack());
        assertEquals(q1, trans4.getState());
    }

    @Test
    public void testTransitionChainEmptyStack() {
        PDATransition<String, Character, Integer> transition =
                new PDATransition<>(q1, 'a', PDAStackSymbol.exactly(2), new PDAStackWord<>());

        ConstrainedSupplier<String> stateSupplier = ConstrainedSupplier.constrainedStringSupplier();
        PDATransitionChains<String, Character, Integer> chains =
                new PDATransitionChains<>(q0, transition, new Alphabet<>(), stateSupplier);

        assertEquals(1, chains.getNumberOfChains());

        PDATransitionChains.PDATransitionChain<String, Character, Integer> chain =
                chains.getChainAt(0);
        assertEquals(1, chain.size());

        String state0 = chain.get(0).first();
        PDATransition<String, Character, Integer> trans0 = chain.get(0).second();

        assertEquals(q0, state0);
        assertEquals(q1, trans0.getState());
        assertEquals(transition.getInputSymbol(), trans0.getInputSymbol());
        assertEquals(transition.getStackSymbol(), trans0.getStackSymbol());
        assertFalse(transition.getStackSymbol().isVariable());
        assertTrue(trans0.getNewTopOfStack().isEmpty());
    }

    @Test
    public void testTransitionChainWithWildcard() {
        Integer wildcard = null;
        PDATransition<String, Character, Integer> transition =
                new PDATransition<>(
                        q1,
                        'a',
                        PDAStackSymbol.anyOf(1, 3),
                        new PDAStackWord<>(wildcard, 2, wildcard, 1, 1));

        ConstrainedSupplier<String> stateSupplier = ConstrainedSupplier.constrainedStringSupplier();
        PDATransitionChains<String, Character, Integer> chains =
                new PDATransitionChains<>(
                        q0, transition, new Alphabet<>(0, 1, 2, 3), stateSupplier);

        assertEquals(
                2, chains.getNumberOfChains()); // one chain for each symbol in stack alphabet which
        // matches .anyOf(1, 3)

        for (Integer symbol : List.of(1, 3)) {
            assertTrue(
                    chains.getChains().stream()
                            .anyMatch(
                                    chain ->
                                            chain.get(0)
                                                    .second()
                                                    .getStackSymbol()
                                                    .matches(symbol)));
        }

        // each chain has 4 steps and each step is concrete (i.e. the stack symbol is *not* a
        // wildcard)
        for (PDATransitionChains.PDATransitionChain<String, Character, Integer> chain : chains) {
            assertEquals(4, chain.size());
            for (Pair<String, PDATransition<String, Character, Integer>> step : chain) {
                assertFalse(step.second().getStackSymbol().isVariable());
                assertFalse(step.second().containsWildcard());
            }
        }
    }
}

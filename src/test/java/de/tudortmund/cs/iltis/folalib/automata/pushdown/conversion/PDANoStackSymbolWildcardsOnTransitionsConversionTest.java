package de.tudortmund.cs.iltis.folalib.automata.pushdown.conversion;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.*;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import java.util.Set;
import org.junit.Test;

public class PDANoStackSymbolWildcardsOnTransitionsConversionTest extends Utils {

    private final Alphabet<Character> inputAlphabet = Alphabets.characterAlphabet("abc");
    private final Alphabet<Integer> stackAlphabet = new Alphabet<>(0, 1, 2);
    private final String q0 = "q0";
    private final Integer wildcard = null;
    private final PDA<String, Character, Integer> pda =
            new PDABuilder<String, Character, Integer>(inputAlphabet)
                    .withStates(q0)
                    .withStackSymbols(stackAlphabet)
                    .withInitialStackSymbol(0)
                    .withInitial(q0)
                    .withAccepting(q0)
                    .withAcceptanceStrategy(PDAAcceptanceStrategy.ACCEPTING_STATES)

                    // anyOf should be expanded to two distinct transitions, one for 0 and 1 each
                    .withTransition(q0, 'a', PDAStackSymbol.anyOf(0, 1), q0, new PDAStackWord<>())

                    // transitions without wildcards should remain unaltered
                    .withEpsilonTransition(q0, 1, q0, new PDAStackWord<>(1, 1))

                    // wildcard should be expanded once for each element in the stack alphabet
                    // (here: 0, 1 and 2)
                    .withTransition(
                            q0,
                            'c',
                            PDAStackSymbol.wildcard(),
                            q0,
                            new PDAStackWord<>(wildcard, wildcard, wildcard))
                    .build()
                    .unwrap();

    @Test
    public void testConversion() {
        PDAConversion<String, String, Character, Integer, Integer> conversion =
                new PDANoStackSymbolWildcardsOnTransitionsConversion<>();
        PDA<String, Character, Integer> convertedPDA = conversion.apply(pda);

        Set<PDATransition<String, Character, Integer>> transitions =
                convertedPDA.getTransitions().in(q0);

        assertEquals(PDAAcceptanceStrategy.ACCEPTING_STATES, convertedPDA.getAcceptanceStrategy());

        assertEquals(1, convertedPDA.getStates().size());
        assertContains(convertedPDA.getStates(), q0);
        assertEquals(
                q0,
                convertedPDA.getInitialStates().stream()
                        .findFirst()
                        .get()); // Original automaton only contains one initial state and the
        // conversion does not change that, so this is fine.
        assertEquals(1, convertedPDA.getAcceptingStates().size());
        assertContains(convertedPDA.getAcceptingStates(), q0);

        assertEquals((Integer) 0, pda.getInitialStackSymbol());
        assertEquals(stackAlphabet, convertedPDA.getStackAlphabet());

        assertEquals(6, transitions.size());

        // first transition expanded
        PDATransition<String, Character, Integer> trans0 =
                new PDATransition<>(q0, 'a', PDAStackSymbol.exactly(0), new PDAStackWord<>());
        PDATransition<String, Character, Integer> trans1 =
                new PDATransition<>(q0, 'a', PDAStackSymbol.exactly(1), new PDAStackWord<>());

        // second transition unaltered
        PDATransition<String, Character, Integer> trans2 =
                new PDATransition<>(q0, PDAStackSymbol.exactly(1), new PDAStackWord<>(1, 1));

        // third transition expanded + wildcard correctly substituted in newTopOfStack
        PDATransition<String, Character, Integer> trans3 =
                new PDATransition<>(
                        q0, 'c', PDAStackSymbol.exactly(0), new PDAStackWord<>(0, 0, 0));
        PDATransition<String, Character, Integer> trans4 =
                new PDATransition<>(
                        q0, 'c', PDAStackSymbol.exactly(1), new PDAStackWord<>(1, 1, 1));
        PDATransition<String, Character, Integer> trans5 =
                new PDATransition<>(
                        q0, 'c', PDAStackSymbol.exactly(2), new PDAStackWord<>(2, 2, 2));

        assertContains(transitions, trans0);
        assertContains(transitions, trans1);
        assertContains(transitions, trans2);
        assertContains(transitions, trans3);
        assertContains(transitions, trans4);
        assertContains(transitions, trans5);
    }
}

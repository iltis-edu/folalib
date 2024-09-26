package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.fault.PDAConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.fault.PDAConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.fault.PDASyntaxFault;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.fault.PDATransitionFault;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.util.List;
import org.junit.Test;

public class PDABuilderTest extends Utils {

    private final Alphabet<Character> inputAlphabet = Alphabets.characterAlphabet("abc");
    private final Alphabet<Integer> stackAlphabet = new Alphabet<>(-2, -1, 0, 1, 2);

    private final PDABuilder<String, Character, Integer> builder =
            new PDABuilder<String, Character, Integer>(inputAlphabet)
                    .withStackSymbols(stackAlphabet);

    private final String q0 = "q0";
    private final String q1 = "q1";
    private final String q2 = "q2";
    private final String q3 = "q3";

    private final PDATransition<String, Character, Integer> unknownInputSymbol =
            new PDATransition<>(q1, 'z', PDAStackSymbol.wildcard(), new PDAStackWord<>());
    private final PDATransition<String, Character, Integer> unknownOrigin =
            new PDATransition<>(q2, 'b', PDAStackSymbol.anyOf(0, 1), new PDAStackWord<>());
    private final PDATransition<String, Character, Integer> unknownDestination =
            new PDATransition<>(q3, 'b', PDAStackSymbol.wildcard(), new PDAStackWord<>());
    private final PDATransition<String, Character, Integer> unknownStackSymbol =
            new PDATransition<>(q1, PDAStackSymbol.exactly(5), new PDAStackWord<>());
    private final PDATransition<String, Character, Integer> unknownStackSymbolAnyOf =
            new PDATransition<>(q1, PDAStackSymbol.anyOf(0, 1, 5), new PDAStackWord<>());
    private final PDATransition<String, Character, Integer> invalidNewTopOfStack =
            new PDATransition<>(q2, PDAStackSymbol.exactly(0), new PDAStackWord<>(5));

    @Test
    public void testNoFaults() {
        builder.reset();
        boolean isValid =
                builder.withStates(q0, q1)
                        .withStackSymbols(stackAlphabet)
                        .withInitial(q0, q2)
                        .withInitialStackSymbol(0)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.ACCEPTING_STATES)
                        .withTransition(
                                q0,
                                'a',
                                PDAStackSymbol.anyOf(0, -1, -2),
                                q0,
                                new PDAStackWord<>(-1))
                        .withTransition(
                                q0, 'a', PDAStackSymbol.wildcard(), q0, new PDAStackWord<>(-2))
                        .withTransition(
                                q0, 'c', PDAStackSymbol.exactly(-1), q2, new PDAStackWord<>())
                        .withTransition(q0, 'b', -2, q0, new PDAStackWord<>())
                        .withEpsilonTransition(q0, 0, q1, new PDAStackWord<>())
                        .build()
                        .match(ok -> true, err -> false);
        assertTrue(isValid);
    }

    @Test
    public void testAllFaults() {
        Alphabet<Integer> stackAlphabet =
                new Alphabet<>(0, 1, 2); // `null` is not allowed in stack alphabet
        PDABuilder<String, Character, Integer> builder = new PDABuilder<>(inputAlphabet);
        builder.withStackSymbols(stackAlphabet);
        PDAConstructionFaultCollection faultCollection =
                builder.withStates(q0, q1, q2)
                        // missing initial state
                        // missing initial stack symbol
                        // missing acceptance strategy
                        .withTransition(q0, unknownInputSymbol)
                        .withTransition(q3, unknownOrigin)
                        .withTransition(q2, unknownDestination)
                        .withTransition(q1, unknownStackSymbol)
                        .withTransition(q2, unknownStackSymbolAnyOf)
                        .withTransition(q0, invalidNewTopOfStack)
                        .build()
                        .unwrapErr();
        List<Fault<PDAConstructionFaultReason>> faults = faultCollection.getFaults();
        assertEquals(9, faults.size());
        assertContains(faults, PDASyntaxFault.missingInitialState());
        assertContains(faults, PDASyntaxFault.missingInitialStackSymbol());
        assertContains(faults, PDASyntaxFault.missingAcceptanceStrategy());
        assertContains(faults, PDATransitionFault.unknownInputSymbol(q0, unknownInputSymbol));
        assertContains(faults, PDATransitionFault.unknownOrigin(q3, unknownOrigin));
        assertContains(faults, PDATransitionFault.unknownDestination(q2, unknownDestination));
        assertContains(faults, PDATransitionFault.unknownStackSymbol(q1, unknownStackSymbol));
        assertContains(faults, PDATransitionFault.unknownStackSymbol(q2, unknownStackSymbolAnyOf));
        assertContains(faults, PDATransitionFault.invalidNewTopOfStack(q0, invalidNewTopOfStack));
    }

    @Test
    public void testDuplicateFaults() {
        builder.reset();
        PDAConstructionFaultCollection faultCollection =
                builder.withStates(q0, q1)
                        .withStackSymbols(stackAlphabet)
                        .withInitial(q0)
                        .withInitialStackSymbol(0)
                        // missing acceptance strategy
                        .withTransition(
                                q2,
                                unknownInputSymbol) // unknown origin q2 + invalid input symbol 'z'
                        .withTransition(
                                q2,
                                invalidNewTopOfStack) // unknown origin q2 + unknown destination q2
                        // + invalid push 5
                        .withTransition(
                                q3,
                                unknownDestination) // unknown origin q3 + unknown destination q3
                        .build()
                        .unwrapErr();
        List<Fault<PDAConstructionFaultReason>> faults = faultCollection.getFaults();
        assertEquals(8, faults.size());
        assertContains(faults, PDASyntaxFault.missingAcceptanceStrategy());
        assertContains(faults, PDATransitionFault.unknownOrigin(q2, unknownInputSymbol));
        assertContains(faults, PDATransitionFault.unknownInputSymbol(q2, unknownInputSymbol));
        assertContains(faults, PDATransitionFault.unknownOrigin(q2, invalidNewTopOfStack));
        assertContains(faults, PDATransitionFault.unknownDestination(q2, invalidNewTopOfStack));
        assertContains(faults, PDATransitionFault.invalidNewTopOfStack(q2, invalidNewTopOfStack));
        assertContains(faults, PDATransitionFault.unknownOrigin(q3, unknownDestination));
        assertContains(faults, PDATransitionFault.unknownDestination(q3, unknownDestination));
    }

    @Test
    public void testMissingInitialState() {
        builder.reset();
        PDAConstructionFaultCollection faultCollection =
                builder.withStates(q0, q1)
                        .withStackSymbols(stackAlphabet)
                        .withAccepting(q2)
                        .withInitialStackSymbol(0)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK)
                        .withEpsilonTransition(
                                q0, PDAStackSymbol.wildcard(), q1, new PDAStackWord<>(2))
                        .build()
                        .unwrapErr();
        assertEquals(1, faultCollection.getFaults().size());
        assertTrue(
                faultCollection.containsAnyFault(PDAConstructionFaultReason.MISSING_INITIAL_STATE));
    }

    @Test
    public void testMissingInitialStackSymbol() {
        builder.reset();
        PDAConstructionFaultCollection faultCollection =
                builder.withInitial(q0)
                        .withStackSymbols(stackAlphabet)
                        .withAccepting(q2)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK)
                        .withTransition(
                                q0, 'a', PDAStackSymbol.anyOf(0, 1, 2), q2, new PDAStackWord<>())
                        .build()
                        .unwrapErr();
        assertEquals(1, faultCollection.getFaults().size());
        assertTrue(
                faultCollection.containsAnyFault(
                        PDAConstructionFaultReason.MISSING_INITIAL_STACK_SYMBOL));
    }

    @Test
    public void testMissingAcceptanceStrategy() {
        builder.reset();
        PDAConstructionFaultCollection faultCollection =
                builder.withInitial(q1)
                        .withStates(q0, q1, q2)
                        .withInitialStackSymbol(0)
                        .withTransition(
                                q2, 'c', PDAStackSymbol.wildcard(), q2, new PDAStackWord<>())
                        .build()
                        .unwrapErr();
        assertEquals(1, faultCollection.getFaults().size());
        assertTrue(
                faultCollection.containsAnyFault(
                        PDAConstructionFaultReason.MISSING_ACCEPTANCE_STRATEGY));
    }

    @Test
    public void testTransitionUnknownInputSymbol() {
        List<Fault<PDAConstructionFaultReason>> faults =
                faultsFromBuildingPDAWithFaultyTransition(q2, unknownInputSymbol);
        assertEquals(1, faults.size());
        assertContains(faults, PDATransitionFault.unknownInputSymbol(q2, unknownInputSymbol));
    }

    @Test
    public void testTransitionUnknownOrigin() {
        List<Fault<PDAConstructionFaultReason>> faults =
                faultsFromBuildingPDAWithFaultyTransition(q3, unknownOrigin);
        assertEquals(1, faults.size());
        assertContains(faults, PDATransitionFault.unknownOrigin(q3, unknownOrigin));
    }

    @Test
    public void testTransitionUnknownDestination() {
        List<Fault<PDAConstructionFaultReason>> faults =
                faultsFromBuildingPDAWithFaultyTransition(q2, unknownDestination);
        assertEquals(1, faults.size());
        assertContains(faults, PDATransitionFault.unknownDestination(q2, unknownDestination));
    }

    @Test
    public void testTransitionUnknownStackSymbol() {
        List<Fault<PDAConstructionFaultReason>> faults =
                faultsFromBuildingPDAWithFaultyTransition(q2, unknownStackSymbol);
        assertEquals(1, faults.size());
        assertContains(faults, PDATransitionFault.unknownStackSymbol(q2, unknownStackSymbol));
    }

    @Test
    public void testTransitionUnknownStackSymbolAnyOf() {
        List<Fault<PDAConstructionFaultReason>> faults =
                faultsFromBuildingPDAWithFaultyTransition(q2, unknownStackSymbolAnyOf);
        assertEquals(1, faults.size());
        assertContains(faults, PDATransitionFault.unknownStackSymbol(q2, unknownStackSymbolAnyOf));
    }

    @Test
    public void testTransitionInvalidStackPush() {
        List<Fault<PDAConstructionFaultReason>> faults =
                faultsFromBuildingPDAWithFaultyTransition(q2, invalidNewTopOfStack);
        assertEquals(1, faults.size());
        assertContains(faults, PDATransitionFault.invalidNewTopOfStack(q2, invalidNewTopOfStack));
    }

    // Helper method to get the faults which result from constructing an almost valid PDA with
    // exactly one faulty transition
    private List<Fault<PDAConstructionFaultReason>> faultsFromBuildingPDAWithFaultyTransition(
            String from, PDATransition<String, Character, Integer> transition) {
        builder.reset();
        PDAConstructionFaultCollection faultCollection =
                builder.withInitial(q1)
                        .withStackSymbols(stackAlphabet)
                        .withStates(q0, q1, q2)
                        .withInitialStackSymbol(-1)
                        .withAccepting(q1)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.ACCEPTING_STATES)
                        .withTransition(from, transition)
                        .build()
                        .unwrapErr();
        return faultCollection.getFaults();
    }
}

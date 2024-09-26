package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultCollection;
import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultReason;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.fault.PDADeterminacyFault;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import java.util.List;
import org.junit.Test;

public class PDADeterminacyCheckTest extends Utils {

    private final Alphabet<Character> inputAlphabet = Alphabets.characterAlphabet("abc");
    private final Alphabet<Integer> stackAlphabet = new Alphabet<>(0, 1, 2);
    private final String q0 = "q0";
    private final String q1 = "q1";
    private final String q2 = "q2";
    private final PDABuilder<String, Character, Integer> builder =
            new PDABuilder<String, Character, Integer>(inputAlphabet)
                    .withStackSymbols(stackAlphabet);

    @Test
    public void testIsDeterministic() {
        builder.reset();
        PDA<String, Character, Integer> deterministicPDA =
                builder.withStackSymbols(stackAlphabet)
                        .withStates(q0, q1, q2)
                        .withInitial(q0)
                        .withInitialStackSymbol(0)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.ACCEPTING_STATES)
                        .withTransition(
                                q0, 'a', PDAStackSymbol.exactly(0), q0, new PDAStackWord<>(0))
                        .withTransition(
                                q0, 'b', PDAStackSymbol.anyOf(0, 1), q1, new PDAStackWord<>(1))
                        .withTransition(
                                q0, 'c', PDAStackSymbol.exactly(0), q2, new PDAStackWord<>(2))
                        .withTransition(
                                q1, 'a', PDAStackSymbol.exactly(1), q2, new PDAStackWord<>())
                        .withEpsilonTransition(
                                q1, PDAStackSymbol.exactly(0), q0, new PDAStackWord<>())
                        .withTransition(
                                q2, 'a', PDAStackSymbol.exactly(0), q0, new PDAStackWord<>(2))
                        .withEpsilonTransition(
                                q2, PDAStackSymbol.exactly(1), q2, new PDAStackWord<>(2))
                        .build()
                        .unwrap();

        DeterminacyFaultCollection<
                        String,
                        PDADeterminacyFault<String, Character, Integer, DeterminacyFaultReason>>
                faultCollection =
                        new PDADeterminacyCheck<String, Character, Integer>()
                                .checkDeterminacy(deterministicPDA);

        assertFalse(faultCollection.containsAnyFault());
    }

    @Test
    public void testIsDeterministicSubsumingTransitions() {
        builder.reset();
        PDA<String, Character, Integer> deterministicPDA =
                builder.withStackSymbols(stackAlphabet)
                        .withStates(q0, q1, q2)
                        .withInitial(q0)
                        .withInitialStackSymbol(0)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK)
                        .withTransition(
                                q0, 'a', PDAStackSymbol.exactly(0), q0, new PDAStackWord<>(0))
                        .withTransition(
                                q0, 'b', PDAStackSymbol.anyOf(0, 1), q1, new PDAStackWord<>(1))
                        .withTransition(
                                q0, 'c', PDAStackSymbol.exactly(0), q2, new PDAStackWord<>(2))
                        .withTransition(
                                q1, 'a', PDAStackSymbol.exactly(1), q2, new PDAStackWord<>())
                        .withEpsilonTransition(
                                q1, PDAStackSymbol.exactly(0), q0, new PDAStackWord<>())

                        // in q2 upon reading 'a' with a topOfStack == 0 two possible transitions
                        // exist, however
                        // this does *not* contradict determinacy, because the second transitions
                        // subsumes the first one
                        // in other words, either one leads to the same subsequent configuration
                        .withTransition(
                                q2, 'a', PDAStackSymbol.exactly(0), q0, new PDAStackWord<>(2))
                        .withTransition(
                                q2, 'a', PDAStackSymbol.anyOf(0, 2), q0, new PDAStackWord<>(2))
                        .withEpsilonTransition(
                                q2, PDAStackSymbol.exactly(1), q2, new PDAStackWord<>(2))
                        .build()
                        .unwrap();

        DeterminacyFaultCollection<
                        String,
                        PDADeterminacyFault<String, Character, Integer, DeterminacyFaultReason>>
                faultCollection =
                        new PDADeterminacyCheck<String, Character, Integer>()
                                .checkDeterminacy(deterministicPDA);

        assertFalse(faultCollection.containsAnyFault());
    }

    @Test
    public void testIsDeterministicEquivalentTransitions() {
        builder.reset();
        Integer wildcard = null;
        PDA<String, Character, Integer> deterministicPDA =
                builder.withStackSymbols(stackAlphabet)
                        .withStates(q0, q1)
                        .withInitial(q0)
                        .withInitialStackSymbol(0)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.ACCEPTING_STATES)
                        .withAccepting(q1)
                        // both transitions lead to the exact same configuration but the word they
                        // write to the stack is
                        // not identical per se but effectively identical, because the `wildcard`s
                        // will be replaced with
                        // `0`s in this case
                        .withTransition(
                                q0, 'a', PDAStackSymbol.exactly(0), q1, new PDAStackWord<>(0, 0))
                        .withTransition(
                                q0,
                                'a',
                                PDAStackSymbol.anyOf(0),
                                q1,
                                new PDAStackWord<>(wildcard, wildcard))
                        .build()
                        .unwrap();

        DeterminacyFaultCollection<
                        String,
                        PDADeterminacyFault<String, Character, Integer, DeterminacyFaultReason>>
                faultCollection =
                        new PDADeterminacyCheck<String, Character, Integer>()
                                .checkDeterminacy(deterministicPDA);

        assertFalse(faultCollection.containsAnyFault());
    }

    @Test
    public void testIsNotDeterministic() {
        builder.reset();
        PDA<String, Character, Integer> nonDeterministicPDA =
                builder.withStackSymbols(stackAlphabet)
                        .withStates(q0, q1, q2)
                        .withInitial(q0, q1)
                        .withInitialStackSymbol(0)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK)
                        .withTransition(
                                q0, 'a', PDAStackSymbol.exactly(0), q0, new PDAStackWord<>(0))

                        // in q0 upon reading b and topOfStack == 0 the next state can either be q1
                        // or q2
                        .withTransition(
                                q0, 'b', PDAStackSymbol.anyOf(0, 1), q1, new PDAStackWord<>(1))
                        .withTransition(
                                q0, 'b', PDAStackSymbol.exactly(0), q2, new PDAStackWord<>(2))

                        // in state q1 either a or nothing can be read when topOfStack == 0
                        .withTransition(
                                q1, 'a', PDAStackSymbol.exactly(0), q2, new PDAStackWord<>())
                        .withEpsilonTransition(
                                q1, PDAStackSymbol.exactly(0), q0, new PDAStackWord<>())

                        // for topOfStack = 0 either 2 or 1 can replace the topmost stack symbol can
                        // happen
                        .withTransition(
                                q2, 'a', PDAStackSymbol.exactly(0), q0, new PDAStackWord<>(2))
                        .withTransition(
                                q2, 'a', PDAStackSymbol.wildcard(), q0, new PDAStackWord<>(1))
                        .build()
                        .unwrap();

        PDADeterminacyFault<String, Character, Integer, DeterminacyFaultReason> fault0 =
                new PDADeterminacyFault<>(
                        q0, 'b', 0, DeterminacyFaultReason.AMBIGUOUS_TRANSITION, 2);
        PDADeterminacyFault<String, Character, Integer, DeterminacyFaultReason> fault1 =
                new PDADeterminacyFault<>(
                        q1, 'a', 0, DeterminacyFaultReason.AMBIGUOUS_TRANSITION, 2);
        PDADeterminacyFault<String, Character, Integer, DeterminacyFaultReason> fault2 =
                new PDADeterminacyFault<>(
                        q2, 'a', 0, DeterminacyFaultReason.AMBIGUOUS_TRANSITION, 2);
        PDADeterminacyFault<String, Character, Integer, DeterminacyFaultReason> fault3 =
                new PDADeterminacyFault<>(
                        null, null, null, DeterminacyFaultReason.MULTIPLE_INITIAL_STATES);

        DeterminacyFaultCollection<
                        String,
                        PDADeterminacyFault<String, Character, Integer, DeterminacyFaultReason>>
                faultCollection =
                        new PDADeterminacyCheck<String, Character, Integer>()
                                .checkDeterminacy(nonDeterministicPDA);
        List<PDADeterminacyFault<String, Character, Integer, DeterminacyFaultReason>> faults =
                faultCollection.getDeterminismFaults();

        assertTrue(faultCollection.containsAnyFault());
        assertEquals(4, faults.size());
        assertContains(faults, fault0);
        assertContains(faults, fault1);
        assertContains(faults, fault2);
        assertContains(faults, fault3);
    }
}

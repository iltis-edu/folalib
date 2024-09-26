package de.tudortmund.cs.iltis.folalib.automata.finite;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultCollection;
import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultReason;
import de.tudortmund.cs.iltis.folalib.automata.finite.fault.*;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.folalib.languages.Words;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import org.junit.Test;

public class NFATest extends NFATestHelper {
    private Alphabet<Character> SigmaABC;
    private NFA<String, Character> evenAs;
    private NFA<String, Character> epsilon;
    private NFA<String, Character> a;
    private NFA<String, Character> multipleInitial;

    public NFATest() {
        SigmaABC = Alphabets.characterAlphabet("abc");

        String q0 = "0";
        String q1 = "1";
        String q2 = "2";

        evenAs =
                new NFABuilder<String, Character>(SigmaABC)
                        .withInitial(q0)
                        .withAccepting(q0)
                        .withStates(q1)
                        .withTransition(q0, 'a', q1)
                        .withTransition(q0, 'b', q0)
                        .withTransition(q1, 'a', q0)
                        .withTransition(q1, 'b', q1)
                        .build()
                        .unwrap();

        epsilon =
                new NFABuilder<String, Character>(SigmaABC)
                        .withInitial(q0)
                        .withAccepting(q1)
                        .withEpsilonTransition(q0, q1)
                        .withEpsilonTransition(
                                q1, q0) // create and eps-loop to show we terminate now
                        .build()
                        .unwrap();

        a =
                new NFABuilder<String, Character>(SigmaABC)
                        .withInitial(q0)
                        .withAccepting(q1)
                        .withTransition(q0, 'a', q1)
                        .build()
                        .unwrap();

        multipleInitial =
                new NFABuilder<String, Character>(SigmaABC)
                        .withInitial(q0, q1)
                        .withAccepting(q1, q2)
                        .withTransition(q0, 'a', q1)
                        .withTransition(q0, 'b', q2)
                        .withTransition(q1, 'a', q0)
                        .withTransition(q2, 'a', q1)
                        .withEpsilonTransition(q1, q2)
                        .build()
                        .unwrap();
    }

    @Test
    public void simpleTest() {
        assertAccepts(evenAs, Words.characterWord(""));
        assertAccepts(evenAs, Words.characterWord("aa"));
        assertAccepts(evenAs, Words.characterWord("baabb"));
        assertAccepts(evenAs, Words.characterWord("bbb"));

        assertRejects(evenAs, Words.characterWord("a"));
        assertRejects(evenAs, Words.characterWord("bba"));
        assertRejects(evenAs, Words.characterWord("ba"));
        assertRejects(evenAs, Words.characterWord("aaab"));
        assertRejects(evenAs, Words.characterWord("aabaaa"));

        assertAccepts(epsilon, Words.characterWord(""));
        assertRejects(epsilon, Words.characterWord("a"));
        assertRejects(epsilon, Words.characterWord("ab"));

        assertRejects(a, Words.characterWord(""));
        assertAccepts(a, Words.characterWord("a"));
        assertRejects(a, Words.characterWord("b"));
        assertRejects(a, Words.characterWord("ab"));

        assertAccepts(multipleInitial, Words.characterWord(""));
        assertAccepts(multipleInitial, Words.characterWord("aa"));
        assertRejects(multipleInitial, Words.characterWord("bab"));

        assertFalse(multipleInitial.isTotal());
        NFA<MaybeGenerated<String, Integer>, Character> totalMultipleInitial =
                multipleInitial.totalify();
        assertTrue(totalMultipleInitial.isTotal());
    }

    @Test
    public void testDeterminacyDetection() {
        NFABuilder<Integer, Character> builder =
                new NFABuilder<Integer, Character>(SigmaABC).withInitial(0).withAccepting(1);

        // NFA without transitions is deterministic
        assertTrue(builder.build().unwrap().isDeterministic());

        builder.withTransition(0, 'a', 0).withTransition(0, 'b', 1).withTransition(1, 'a', 1);

        // NFA without conflicting transitions is deterministic
        assertTrue(builder.build().unwrap().isDeterministic());

        builder.withEpsilonTransition(0, 1);

        // NFA with an epsilon transition is non-deterministic
        assertFalse(builder.build().unwrap().isDeterministic());

        builder.withEpsilonTransition(0, 0);

        // NFA with an two epsilon transition is non-deterministic
        assertFalse(builder.build().unwrap().isDeterministic());

        builder.clearTransitions().withTransition(0, 'a', 0).withTransition(0, 'a', 1);

        // NFA with conflicting transitions is non-deterministic
        assertFalse(builder.build().unwrap().isDeterministic());

        builder =
                new NFABuilder<Integer, Character>(Alphabets.characterAlphabet("a"))
                        .withInitial(0)
                        .withAccepting(1)
                        .withTransition(0, 'a', 1);

        // 'a'-transition from 1 missing, automaton not total
        assertFalse(builder.build().unwrap().isTotal());

        builder.withTransition(1, 'a', 0);

        // now it's total
        assertTrue(builder.build().unwrap().isTotal());

        // For automaton with multiple initial states
        DeterminacyFaultCollection<
                        String, NFADeterminacyFault<String, Character, DeterminacyFaultReason>>
                faults = multipleInitial.checkDeterminacy();
        assertTrue(faults.hasDeterminismFaults());
        assertFalse(multipleInitial.isDeterministic());
        assertTrue(faults.containsAnyFault(DeterminacyFaultReason.MULTIPLE_INITIAL_STATES));
    }

    @Test
    public void testReachability() {
        NFABuilder<Integer, Character> builder =
                new NFABuilder<Integer, Character>(SigmaABC)
                        .withInitial(0)
                        .withStates(1)
                        .withAccepting(2)
                        .withTransition(0, 'a', 1);

        assertEquals(
                builder.build().unwrap().getReachableStates(),
                new LinkedHashSet<>(Arrays.asList(0, 1)));
        assertTrue(builder.build().unwrap().isEmpty());

        builder.withTransition(1, 'a', 2);

        assertEquals(
                builder.build().unwrap().getReachableStates(),
                new LinkedHashSet<>(Arrays.asList(0, 1, 2)));
        assertFalse(builder.build().unwrap().isEmpty());
    }

    @Test
    public void testBuilderErrors() {
        NFATransition<Integer, Character> faulting = new NFATransition<>('d', 2);

        NFABuilder<Integer, Character> builder =
                new DFABuilder<Integer, Character>(SigmaABC)
                        .ensureTotality()
                        .withStates(0, 1) // no initial state declared
                        .withTransition(0, 'a', 0)
                        .withTransition(0, 'b', 0) // missing 'c' transition
                        .withTransition(0, 'a', 1) // non-deterministic transition!
                        .withTransition(1, 'a', 0)
                        .withTransition(1, 'b', 0)
                        .withTransition(1, 'c', 0)
                        .withTransition(2, faulting); // unknown origin, symbol and destination!

        List<Fault<NFAConstructionFaultReason>> faults = builder.build().unwrapErr().getFaults();

        assertTrue(faults.contains(new SyntaxFault()));
        assertTrue(
                faults.contains(
                        NFADeterminacyFault.missingTransition(0, 'c').asConstructionFault()));
        assertTrue(
                faults.contains(
                        NFADeterminacyFault.ambiguousTransition(0, 'a', Arrays.asList(0, 1))
                                .asConstructionFault()));
        assertTrue(
                faults.contains(
                        new TransitionFault<>(
                                NFAConstructionFaultReason.TRANSITION_UNKNOWN_ORIGIN,
                                2,
                                faulting)));
        assertTrue(
                faults.contains(
                        new TransitionFault<>(
                                NFAConstructionFaultReason.TRANSITION_UNKNOWN_DESTINATION,
                                2,
                                faulting)));
        assertTrue(
                faults.contains(
                        new TransitionFault<>(
                                NFAConstructionFaultReason.TRANSITION_UNKNOWN_SYMBOL,
                                2,
                                faulting)));
    }

    @Test
    public void testRequireNotNull() {
        NFABuilder<Serializable, Character> builder =
                new NFABuilder<>(Alphabets.characterAlphabet("ab"))
                        .withInitial()
                        .withInitial("q1", "q2");

        try {
            builder.withInitial(null, "q1");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }

    @Test
    public void testDFABuilderMultipleInitialStates() {
        DFABuilder<String, Character> dfaBuilder =
                new DFABuilder<>(Alphabets.characterAlphabet("01"));
        dfaBuilder
                .withStates("q0", "q1", "q2")
                .withInitial("q0", "q1")
                .withAccepting("q2")
                .withTransition("q0", '0', "q2");

        NFAConstructionFaultCollection faults = dfaBuilder.build().unwrapErr();

        assertEquals(1, faults.getFaults().size());
        assertEquals(
                1,
                faults.getFaults().stream()
                        .filter(
                                fault ->
                                        fault.getReason()
                                                == NFAConstructionFaultReason
                                                        .MULTIPLE_INITIAL_STATES)
                        .count());
    }
}

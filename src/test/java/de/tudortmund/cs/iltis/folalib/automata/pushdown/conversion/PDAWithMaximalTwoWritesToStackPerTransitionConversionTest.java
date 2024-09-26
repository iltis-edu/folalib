package de.tudortmund.cs.iltis.folalib.automata.pushdown.conversion;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.*;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.Test;

public class PDAWithMaximalTwoWritesToStackPerTransitionConversionTest extends Utils {

    private final Alphabet<Character> inputAlphabet = Alphabets.characterAlphabet("abc");
    private final Alphabet<Integer> stackAlphabet = new Alphabet<>(0, 1, 2);
    private final String q0 = "q0";
    private final String q1 = "q1";
    private final Integer wildcard = null;
    private final PDA<String, Character, Integer> pda =
            new PDABuilder<String, Character, Integer>(inputAlphabet)
                    .withStates(q0, q1)
                    .withStackSymbols(stackAlphabet)
                    .withInitialStackSymbol(0)
                    .withInitial(q0)
                    .withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK)

                    // newTopOfStack is too long, this should be split up into a chain from q0 to q0
                    .withTransition(
                            q0, 'a', PDAStackSymbol.exactly(2), q0, new PDAStackWord<>(0, 0, 1, 1))

                    // newTopOfStack is too long but there is a wildcard -> we expect two chains
                    // from q0 to q1
                    .withTransition(
                            q0,
                            'b',
                            PDAStackSymbol.anyOf(0, 1),
                            q1,
                            new PDAStackWord<>(2, wildcard, 2))

                    // this transition is okay and should remain unaltered
                    .withEpsilonTransition(
                            q1, PDAStackSymbol.wildcard(), q0, new PDAStackWord<>(wildcard))
                    .build()
                    .unwrap();

    @Test
    public void testConversion() {
        PDAConversion<String, MaybeGenerated<String, String>, Character, Integer, Integer>
                conversion = new PDAWithMaximalTwoWritesToStackPerTransitionConversion<>();
        PDA<MaybeGenerated<String, String>, Character, Integer> convertedPDA =
                conversion.apply(pda);

        assertEquals(PDAAcceptanceStrategy.EMPTY_STACK, convertedPDA.getAcceptanceStrategy());

        // 2 original states +
        // 1 chain q0 -> tmp0 -> tmp1 -> q0 (with 2 additional states) +
        // 2 chains q0 -> tmp2 -> q1 and q0 -> tmp3 -> q1 (with 1 additional state each)
        // = 2 + 1*2 + 2*1 = 6
        assertEquals(6, convertedPDA.getStates().size());
        assertContains(convertedPDA.getStates(), in(q0));
        assertContains(convertedPDA.getStates(), in(q1));
        assertContains(
                convertedPDA.getStates(),
                gen(
                        "0")); /* this is what our algorithms "generates" (highly coupled code, unfortunately) */
        assertContains(convertedPDA.getStates(), gen("1"));
        assertContains(convertedPDA.getStates(), gen("2"));
        assertContains(convertedPDA.getStates(), gen("3"));
        assertEquals(
                in(q0),
                convertedPDA.getInitialStates().stream()
                        .findFirst()
                        .get()); // Original automaton only contains one initial state and the
        // conversion does not change that, so this is fine.
        assertEquals(0, convertedPDA.getAcceptingStates().size());

        assertEquals((Integer) 0, pda.getInitialStackSymbol());
        assertEquals(stackAlphabet, convertedPDA.getStackAlphabet());

        Set<
                        Pair<
                                MaybeGenerated<String, String>,
                                PDATransition<MaybeGenerated<String, String>, Character, Integer>>>
                allTransitions = allTransitions(convertedPDA);
        assertEquals(8, allTransitions.size());

        // first transition: q0 --a,2:0011--> q0 becomes q0 --a,2:11--> tmp0 --eps,1:01--> tmp1
        // --eps,0:00--> q0
        // second transition: q0 --b,{0, 1}:2*2--> q1 becomes q0 --b,0:02--> tmp2 --eps,0:20--> q1
        // and q0 --b,1:12--> tmp3 --eps,1:21--> q1
        // third transition: q1 --eps,*:*--> q0 remains unchanged

        // Find tmp0, tmp1, tmp2 and tmp3 generically
        MaybeGenerated<String, String> tmp0 =
                getWhere(
                        allTransitions,
                        pair ->
                                pair.first().equals(in(q0))
                                        && pair.second().getInputSymbol().equals('a'),
                        pair -> pair.second().getState());
        MaybeGenerated<String, String> tmp1 =
                getWhere(
                        allTransitions,
                        pair -> pair.first().equals(tmp0),
                        pair -> pair.second().getState());
        MaybeGenerated<String, String> tmp2 =
                getWhere(
                        allTransitions,
                        pair ->
                                pair.first().equals(in(q0))
                                        && pair.second().getInputSymbol().equals('b')
                                        && pair.second().getStackSymbol().matches(0),
                        pair -> pair.second().getState());
        MaybeGenerated<String, String> tmp3 =
                getWhere(
                        allTransitions,
                        pair ->
                                pair.first().equals(in(q0))
                                        && pair.second().getInputSymbol().equals('b')
                                        && pair.second().getStackSymbol().matches(1),
                        pair -> pair.second().getState());

        // q0 --a,2:11--> tmp0
        assertContains(
                allTransitions,
                new Pair<>(
                        in(q0),
                        new PDATransition<>(
                                tmp0, 'a', PDAStackSymbol.exactly(2), new PDAStackWord<>(1, 1))));
        // tmp0 --eps,1:01--> tmp1
        assertContains(
                allTransitions,
                new Pair<>(
                        tmp0,
                        new PDATransition<>(
                                tmp1, PDAStackSymbol.exactly(1), new PDAStackWord<>(0, 1))));
        // tmp1 --eps,0:00--> q0
        assertContains(
                allTransitions,
                new Pair<>(
                        tmp1,
                        new PDATransition<>(
                                in(q0), PDAStackSymbol.exactly(0), new PDAStackWord<>(0, 0))));

        // q0 --b,0:02--> tmp2
        assertContains(
                allTransitions,
                new Pair<>(
                        in(q0),
                        new PDATransition<>(
                                tmp2, 'b', PDAStackSymbol.exactly(0), new PDAStackWord<>(0, 2))));
        // tmp2 --eps,0:20--> q1
        assertContains(
                allTransitions,
                new Pair<>(
                        tmp2,
                        new PDATransition<>(
                                in(q1), PDAStackSymbol.exactly(0), new PDAStackWord<>(2, 0))));

        // q0 --b,1:12--> tmp3
        assertContains(
                allTransitions,
                new Pair<>(
                        in(q0),
                        new PDATransition<>(
                                tmp3, 'b', PDAStackSymbol.exactly(1), new PDAStackWord<>(1, 2))));
        // tmp3 --eps,1:21--> q1
        assertContains(
                allTransitions,
                new Pair<>(
                        tmp3,
                        new PDATransition<>(
                                in(q1), PDAStackSymbol.exactly(1), new PDAStackWord<>(2, 1))));

        // q1 --eps,*:*--> q0 remains unaltered
        assertContains(
                allTransitions,
                new Pair<>(
                        in(q1),
                        new PDATransition<>(
                                in(q0), PDAStackSymbol.wildcard(), new PDAStackWord<>(wildcard))));
    }

    private <T, K> K getWhere(
            Collection<T> collection, Predicate<T> predicate, SerializableFunction<T, K> getter) {
        return getter.apply(collection.stream().filter(predicate).findFirst().get());
    }

    private <T extends Serializable, S extends Serializable, K extends Serializable>
            Set<Pair<T, PDATransition<T, S, K>>> allTransitions(PDA<T, S, K> pda) {
        Set<Pair<T, PDATransition<T, S, K>>> transitions = new LinkedHashSet<>();
        for (Map.Entry<T, Set<PDATransition<T, S, K>>> entry :
                pda.getTransitions().getTransitions().entrySet()) {
            T state = entry.getKey();
            for (PDATransition<T, S, K> transition : entry.getValue()) {
                transitions.add(new Pair<>(state, transition));
            }
        }
        return transitions;
    }
}

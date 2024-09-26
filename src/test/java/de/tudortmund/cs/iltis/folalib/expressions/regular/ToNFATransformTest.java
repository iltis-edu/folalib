package de.tudortmund.cs.iltis.folalib.expressions.regular;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.StateSupplier;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.automata.finite.fault.NFAConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.util.Result;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ToNFATransformTest {

    private final Alphabet<Character> alphabet = new Alphabet<>('a', 'b', 'c');

    @Test
    public void testInspectEmptyLanguage() {
        ToNFATransform<Integer, Character> transform =
                new ToNFATransform<>(StateSupplier.integerStateSupplier());
        EmptyLanguage<Character> emptyLanguage = new EmptyLanguage<>(alphabet);
        NFABuilder<Integer, Character> builder = transform.inspectEmptyLanguage(emptyLanguage);
        Result<NFA<Integer, Character>, NFAConstructionFaultCollection> result = builder.build();
        assertTrue(result.match(nfa -> true, errorCollection -> false));
        NFA<Integer, Character> nfa = builder.build().unwrap();
        assertEquals(alphabet, nfa.getAlphabet());
    }

    @Test
    public void testInspectConcatenationIfChildHasNoAcceptingState() {
        // The special case that a builder has no accepting state can occur when the language is
        // empty
        NFABuilder<Integer, Character> builder =
                new NFABuilder<Integer, Character>(new Alphabet<>()).withInitial(0);
        List<NFABuilder<Integer, Character>> childrenOutput = new ArrayList<>();
        childrenOutput.add(builder);

        Concatenation<Character> concatenation = new Concatenation<>(new EmptyLanguage<>(alphabet));
        ToNFATransform<Integer, Character> transform =
                new ToNFATransform<>(StateSupplier.integerStateSupplier());
        NFABuilder<Integer, Character> outputBuilder =
                transform.inspectConcatenation(concatenation, childrenOutput);

        NFA<Integer, Character> result = outputBuilder.build().unwrap();
        assertTrue(
                result.isEmpty()); // if any language in a concatenation is empty, the result must
        // also be empty
        assertEquals(alphabet, result.getAlphabet());
    }
}

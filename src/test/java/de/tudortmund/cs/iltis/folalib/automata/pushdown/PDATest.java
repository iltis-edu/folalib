package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import java.util.LinkedList;
import org.junit.Test;

public class PDATest {

    private final Alphabet<Character> inputAlphabet = Alphabets.characterAlphabet("abc");
    private final Alphabet<Integer> stackAlphabet = new Alphabet<>(0, 1, 2);
    private final String q0 = "q0";
    private final String q1 = "q1";
    private final String q2 = "q2";
    private final PDABuilder<String, Character, Integer> builder =
            new PDABuilder<String, Character, Integer>(inputAlphabet)
                    .withStackSymbols(stackAlphabet);

    @Test
    public void testIsAcceptingConfigurationEmptyStack() {
        builder.reset();
        PDA<String, Character, Integer> pda =
                builder.withStates(q0, q1)
                        .withInitial(q0, q2)
                        .withAccepting(q1)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK)
                        .withInitialStackSymbol(0)
                        .build()
                        .unwrap();
        PDAConfiguration<String, Character, Integer> initialConfiguration =
                new PDAConfiguration<>(
                        q0,
                        new Word<>(),
                        pda.getInitialStack()); // initial stack is not empty => configuration
        // is not accepting
        PDAConfiguration<String, Character, Integer> initialConfiguration2 =
                new PDAConfiguration<>(
                        q2,
                        new Word<>(),
                        pda.getInitialStack()); // initial stack is not empty => configuration
        // is not accepting
        PDAConfiguration<String, Character, Integer> config0 =
                new PDAConfiguration<>(
                        q0,
                        new Word<>(),
                        new LinkedList<>()); // stack and word are both empty => configuration is
        // accepting
        PDAConfiguration<String, Character, Integer> config1 =
                new PDAConfiguration<>(
                        q1,
                        new Word<>('a'), // word is not read completely => configuration is not
                        // accepting
                        new LinkedList<>());
        assertFalse(pda.isAcceptingConfiguration(initialConfiguration));
        assertFalse(pda.isAcceptingConfiguration(initialConfiguration2));
        assertTrue(pda.isAcceptingConfiguration(config0));
        assertFalse(pda.isAcceptingConfiguration(config1));
    }

    @Test
    public void testIsAcceptingConfigurationAcceptingStates() {
        builder.reset();
        PDA<String, Character, Integer> pda =
                builder.withStates(q0, q1)
                        .withInitial(q0)
                        .withAccepting(q1)
                        .withAcceptanceStrategy(PDAAcceptanceStrategy.ACCEPTING_STATES)
                        .withInitialStackSymbol(0)
                        .build()
                        .unwrap();
        PDAConfiguration<String, Character, Integer> initialConfiguration =
                new PDAConfiguration<>(
                        q0, // initial state is not accepting => configuration is not accepting
                        new Word<>(),
                        pda.getInitialStack());
        PDAConfiguration<String, Character, Integer> initialConfiguration2 =
                new PDAConfiguration<>(
                        q2, // initial state is not accepting => configuration is not accepting
                        new Word<>(),
                        pda.getInitialStack());
        PDAConfiguration<String, Character, Integer> config0 =
                new PDAConfiguration<>(
                        q1, // state is accepting => configuration is accepting
                        new Word<>(),
                        pda.getInitialStack()); // non-empty state is irrelevant for acceptance
        PDAConfiguration<String, Character, Integer> config1 =
                new PDAConfiguration<>(
                        q1,
                        new Word<>('a'), // word is not read completely => configuration is not
                        // accepting
                        new LinkedList<>());
        assertFalse(pda.isAcceptingConfiguration(initialConfiguration));
        assertFalse(pda.isAcceptingConfiguration(initialConfiguration2));
        assertTrue(pda.isAcceptingConfiguration(config0));
        assertFalse(pda.isAcceptingConfiguration(config1));
    }
}

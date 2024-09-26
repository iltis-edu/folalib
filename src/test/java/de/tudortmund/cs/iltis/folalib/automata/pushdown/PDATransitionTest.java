package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.folalib.languages.Words;
import java.util.Deque;
import java.util.LinkedList;
import org.junit.Test;

public class PDATransitionTest {

    private final String q0 = "q0";
    private final String q1 = "q1";
    private final Word<Character> word = Words.characterWord("cabbcba");
    private final Word<Character> emptyWord = Words.characterWord("");
    private final Deque<Integer> stack;
    private final Deque<Integer> emptyStack = new LinkedList<>();

    public PDATransitionTest() {
        stack = new LinkedList<>();
        stack.push(1);
        stack.push(2);
    }

    @Test
    public void testApplicableTransitions() {
        PDATransition<String, Character, Integer> trans0 =
                new PDATransition<>(q0, 'c', PDAStackSymbol.exactly(2), new PDAStackWord<>());
        PDATransition<String, Character, Integer> trans1 =
                new PDATransition<String, Character, Integer>(
                        q0, 'c', PDAStackSymbol.wildcard(), new PDAStackWord<>());
        PDATransition<String, Character, Integer> trans2 =
                new PDATransition<>(q0, 'c', PDAStackSymbol.anyOf(0, 1, 2), new PDAStackWord<>());
        PDATransition<String, Character, Integer> trans3 =
                new PDATransition<String, Character, Integer>(
                        q0,
                        // epsilon transition
                        PDAStackSymbol.wildcard(),
                        new PDAStackWord<>());
        PDAConfiguration<String, Character, Integer> config =
                new PDAConfiguration<>(q0, word, stack);
        PDAConfiguration<String, Character, Integer> config1 =
                new PDAConfiguration<>(q0, emptyWord, stack);
        assertTrue(trans0.isApplicable(config));
        assertTrue(trans1.isApplicable(config));
        assertTrue(trans2.isApplicable(config));
        assertTrue(trans3.isApplicable(config));
        assertTrue(trans3.isApplicable(config1));
    }

    @Test
    public void testNonApplicableTransitionWrongInputSymbol() {
        PDATransition<String, Character, Integer> trans =
                new PDATransition<>(
                        q0,
                        'b', // wrong input symbol
                        PDAStackSymbol.exactly(2),
                        new PDAStackWord<>());
        PDAConfiguration<String, Character, Integer> config =
                new PDAConfiguration<>(q0, word, stack);
        PDAConfiguration<String, Character, Integer> config1 =
                new PDAConfiguration<>(q0, emptyWord, stack);
        assertFalse(trans.isApplicable(config));
        assertFalse(trans.isApplicable(config1));
    }

    @Test
    public void testNonApplicableTransitionWrongTopOfStack() {
        PDATransition<String, Character, Integer> trans =
                new PDATransition<>(
                        q0,
                        'c',
                        PDAStackSymbol.anyOf(0, 1), // wrong topmost stack symbol
                        new PDAStackWord<>());
        PDAConfiguration<String, Character, Integer> config =
                new PDAConfiguration<>(q0, word, stack);
        assertFalse(trans.isApplicable(config));
    }

    @Test
    public void testNonApplicableTransitionEmptyStack() {
        PDATransition<String, Character, Integer> trans =
                new PDATransition<String, Character, Integer>(
                        q0,
                        'c',
                        PDAStackSymbol.wildcard(),
                        new PDAStackWord<>()); // cannot pop when stack is empty
        PDAConfiguration<String, Character, Integer> config =
                new PDAConfiguration<>(q0, word, emptyStack);
        assertFalse(trans.isApplicable(config));
    }

    @Test
    public void testFireSimpleTransition() {
        PDATransition<String, Character, Integer> trans =
                new PDATransition<>(q1, 'c', PDAStackSymbol.exactly(2), new PDAStackWord<>(2));
        PDAConfiguration<String, Character, Integer> config =
                new PDAConfiguration<>(q0, word, stack);
        PDAConfiguration<String, Character, Integer> nextConfig = trans.fire(config);
        PDAConfiguration<String, Character, Integer> expectedConfig =
                new PDAConfiguration<>(q1, word, stack, 1);
        assertEquals(expectedConfig, nextConfig);
    }

    @Test
    public void testFireEpsilonTransition() {
        PDATransition<String, Character, Integer> trans =
                new PDATransition<>(
                        q0,
                        // epsilon transition
                        PDAStackSymbol.anyOf(0, 1, 2),
                        new PDAStackWord<>(0));
        PDAConfiguration<String, Character, Integer> config =
                new PDAConfiguration<>(q0, word, stack);
        PDAConfiguration<String, Character, Integer> nextConfig = trans.fire(config);
        Deque<Integer> newStack = new LinkedList<>(stack);
        newStack.pop();
        newStack.push(0);
        PDAConfiguration<String, Character, Integer> expectedConfig =
                new PDAConfiguration<>(q0, word, newStack);
        assertEquals(expectedConfig, nextConfig);
    }

    @Test
    public void testFireMultiplePushes() {
        PDATransition<String, Character, Integer> trans =
                new PDATransition<>(
                        q1,
                        // epsilon transition
                        PDAStackSymbol.anyOf(0, 1, 2),
                        new PDAStackWord<>(4, -2));
        PDAConfiguration<String, Character, Integer> config =
                new PDAConfiguration<>(q1, word, stack);
        PDAConfiguration<String, Character, Integer> nextConfig = trans.fire(config);
        Deque<Integer> newStack = new LinkedList<>(stack);
        newStack.pop();
        newStack.push(-2);
        newStack.push(4);
        PDAConfiguration<String, Character, Integer> expectedConfig =
                new PDAConfiguration<>(q1, word, newStack);
        assertEquals(expectedConfig, nextConfig);
    }

    @Test
    public void testFireWildcard() {
        Integer wildcard = null;
        PDATransition<String, Character, Integer> trans =
                new PDATransition<>(
                        q1,
                        'c',
                        PDAStackSymbol.wildcard(),
                        new PDAStackWord<>(wildcard, 1, wildcard));
        PDAConfiguration<String, Character, Integer> config =
                new PDAConfiguration<>(q1, word, stack);
        PDAConfiguration<String, Character, Integer> nextConfig = trans.fire(config);
        Deque<Integer> newStack = new LinkedList<>(stack);
        newStack.push(1);
        newStack.push(2); // top of stack is 2 => tau becomes 2 as well
        PDAConfiguration<String, Character, Integer> expectedConfig =
                new PDAConfiguration<>(q1, word, newStack, 1);
        assertEquals(expectedConfig, nextConfig);
    }

    @Test
    public void testFirePop() {
        PDATransition<String, Character, Integer> trans =
                new PDATransition<String, Character, Integer>(
                        q1, 'c', PDAStackSymbol.wildcard(), new PDAStackWord<>());
        PDAConfiguration<String, Character, Integer> config =
                new PDAConfiguration<>(q0, word, stack);
        PDAConfiguration<String, Character, Integer> nextConfig = trans.fire(config);
        Deque<Integer> newStack = new LinkedList<>(stack);
        newStack.pop();
        PDAConfiguration<String, Character, Integer> expectedConfig =
                new PDAConfiguration<>(q1, word, newStack, 1);
        assertEquals(expectedConfig, nextConfig);
    }
}

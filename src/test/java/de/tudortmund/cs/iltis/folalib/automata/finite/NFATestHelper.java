package de.tudortmund.cs.iltis.folalib.automata.finite;

import static junit.framework.TestCase.*;

import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.utils.test.AdvancedTest;

public class NFATestHelper extends AdvancedTest {
    protected void assertAccepts(NFA<String, Character> automaton, Word<Character> word) {
        try {
            NFAExecutor<String, Character> exec = new NFAExecutor<>(automaton, word);
            assertTrue(exec.run());
        } catch (Exception e) {
            fail();
        }
    }

    protected void assertRejects(NFA<String, Character> automaton, Word<Character> word) {
        try {
            NFAExecutor<String, Character> exec = new NFAExecutor<>(automaton, word);
            assertFalse(exec.run());
        } catch (Exception e) {
            fail();
        }
    }
}

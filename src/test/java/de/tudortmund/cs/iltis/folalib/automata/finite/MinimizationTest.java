package de.tudortmund.cs.iltis.folalib.automata.finite;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.finite.conversion.DFAMinimizationConversion;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.io.Serializable;
import org.junit.Test;

/** Test class for the NFAMinimization */
public class MinimizationTest<V extends Serializable, E extends Serializable>
        extends DFAMinimizationConversion {

    @Test
    public void testMinimizationUnreachableStates() {
        IndexedSymbol firstSymbol = new IndexedSymbol("a");
        IndexedSymbol secondSymbol = new IndexedSymbol("b");
        NFA<Integer, IndexedSymbol> toMinimize =
                new NFABuilder<Integer, IndexedSymbol>(new Alphabet<>(firstSymbol, secondSymbol))
                        .withInitial(1)
                        .withStates(1, 2, 3, 4, 5, 6, 7, 8)
                        .withAccepting(2, 3)
                        .withTransition(1, firstSymbol, 1)
                        .withTransition(1, firstSymbol, 4)
                        .withTransition(1, secondSymbol, 2)
                        .withTransition(2, firstSymbol, 3)
                        .withTransition(2, secondSymbol, 6)
                        .withTransition(3, firstSymbol, 3)
                        .withTransition(3, secondSymbol, 6)
                        .withTransition(4, firstSymbol, 5)
                        .withTransition(4, secondSymbol, 2)
                        .withTransition(5, firstSymbol, 4)
                        .withTransition(5, secondSymbol, 2)
                        .withTransition(6, firstSymbol, 6)
                        .withTransition(6, secondSymbol, 6)
                        .withTransition(7, firstSymbol, 7)
                        .withTransition(7, secondSymbol, 8)
                        .withTransition(8, firstSymbol, 8)
                        .withTransition(8, secondSymbol, 7)
                        .build()
                        .unwrap();
        RegularLanguage<IndexedSymbol> toMinimizeLanguage = new RegularLanguage<>(toMinimize);

        NFA<Integer, IndexedSymbol> minimizedDFA = convert(toMinimizeLanguage.getDFA());
        RegularLanguage<IndexedSymbol> minimizedDFALanguage = new RegularLanguage<>(minimizedDFA);

        assertEquals(1, minimizedDFA.getInitialStates().size());
        assertEquals(3, minimizedDFA.getReachableStates().size());
        assertEquals(1, minimizedDFA.getAcceptingStates().size());
        assertTrue(minimizedDFALanguage.isEqualTo(toMinimizeLanguage));
        assertFalse(minimizedDFA.getReachableStates().contains(7));
        assertFalse(minimizedDFA.getReachableStates().contains(8));
    }

    @Test
    public void testNfaConversion() {
        IndexedSymbol firstSymbol = new IndexedSymbol("a");
        IndexedSymbol secondSymbol = new IndexedSymbol("b");
        NFA<Integer, IndexedSymbol> toMinimize =
                new NFABuilder<Integer, IndexedSymbol>(new Alphabet<>(firstSymbol, secondSymbol))
                        .withInitial(1)
                        .withStates(1, 2, 3, 4, 5, 6)
                        .withAccepting(2, 3)
                        .withTransition(1, firstSymbol, 1)
                        .withTransition(1, firstSymbol, 4)
                        .withTransition(1, secondSymbol, 2)
                        .withTransition(2, firstSymbol, 3)
                        .withTransition(2, secondSymbol, 6)
                        .withTransition(3, firstSymbol, 3)
                        .withTransition(3, secondSymbol, 6)
                        .withTransition(4, firstSymbol, 5)
                        .withTransition(4, secondSymbol, 2)
                        .withTransition(5, firstSymbol, 4)
                        .withTransition(5, secondSymbol, 2)
                        .withTransition(6, firstSymbol, 6)
                        .withTransition(6, secondSymbol, 6)
                        .build()
                        .unwrap();
        RegularLanguage<IndexedSymbol> toMinimizeLanguage = new RegularLanguage<>(toMinimize);
        NFA<Integer, IndexedSymbol> minimizedDFA = convert(toMinimize.determinize());
        RegularLanguage<IndexedSymbol> minimizedDFALanguage = new RegularLanguage<>(minimizedDFA);
        assertEquals(1, minimizedDFA.getInitialStates().size());
        assertEquals(3, minimizedDFA.getReachableStates().size());
        assertEquals(1, minimizedDFA.getAcceptingStates().size());
        assertTrue(minimizedDFALanguage.isEqualTo(toMinimizeLanguage));
        assertFalse(minimizedDFA.getReachableStates().contains(7));
        assertFalse(minimizedDFA.getReachableStates().contains(8));
    }
}

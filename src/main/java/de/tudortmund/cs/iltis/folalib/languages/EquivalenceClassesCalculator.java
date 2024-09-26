package de.tudortmund.cs.iltis.folalib.languages;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.automata.finite.conversion.DFAMinimizationConversion;
import de.tudortmund.cs.iltis.utils.collections.ListSet;
import java.io.Serializable;
import java.util.Set;

/** Computes the equivalence classes of a regular language and returns them as regular languages */
public class EquivalenceClassesCalculator<S extends Serializable, T extends Serializable>
        extends DFAMinimizationConversion {

    /**
     * Computes the equivalence classes of a regular language
     *
     * @return equivalence classes as regular languages
     */
    public Set<RegularLanguage<T>> calculate(RegularLanguage<T> language) {
        return getEquivalenceClasses(minimizeDFA(language));
    }

    /**
     * Minimizes the DFA of the given language
     *
     * @return minimized DFA of the language
     */
    private NFA<S, T> minimizeDFA(RegularLanguage<T> language) {
        return convert(language.getDFA());
    }

    /**
     * Creates a dfa for each state of the minimized dfa and sets the respective state as the only
     * accepting state
     *
     * @return set of the equivalence classes of the language
     */
    private Set<RegularLanguage<T>> getEquivalenceClasses(NFA<S, T> minimizedDFA) {
        Set<RegularLanguage<T>> equivalenceClasses = new ListSet<>();
        for (S state : minimizedDFA.getReachableStates()) {
            NFABuilder<S, T> singleNFABuilder =
                    new NFABuilder<>(minimizedDFA).overrideAccepting(state);
            NFA<S, T> equivalenceClassDFA = singleNFABuilder.build().unwrap();
            equivalenceClasses.add(new RegularLanguage<>(equivalenceClassDFA));
        }
        return equivalenceClasses;
    }
}

package de.tudortmund.cs.iltis.folalib.automata;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Interface for dynamic sources of automata states.
 *
 * <p>Classes implementing should realize a virtually infinite, yet non-repeating, sequence of
 * potential automata states
 *
 * @param <State> The type of states generated
 */
public interface StateSupplier<State extends Serializable> extends Supplier<State>, Serializable {
    /**
     * Generates a state which is ensured to be fresh (that is, unused) in the given automaton
     *
     * @param in The automaton for which to generate a fresh state
     * @return The fresh state
     */
    default State getUnused(Automaton<State, ?, ?, ?> in) {
        State state;

        do {
            state = get();
        } while (in.getStates().contains(state));

        return state;
    }

    /**
     * Factory method for a {@link StateSupplier} that generates states which are consecutive
     * integers, starting at 0
     *
     * @return A {@link StateSupplier}
     */
    static StateSupplier<Integer> integerStateSupplier() {
        return new StateSupplier<Integer>() {
            private int stateCount = 0;

            @Override
            public Integer get() {
                return stateCount++;
            }
        };
    }
}

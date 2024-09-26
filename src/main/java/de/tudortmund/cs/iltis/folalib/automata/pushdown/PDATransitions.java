package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import de.tudortmund.cs.iltis.folalib.automata.Transitions;
import java.io.Serializable;

/**
 * A collection of all transitions in a PDA
 *
 * @param <T> The type of states
 * @param <S> The type of input symbols
 * @param <K> The type of the stack symbols
 */
public class PDATransitions<T extends Serializable, S extends Serializable, K extends Serializable>
        extends Transitions<T, PDATransition<T, S, K>> {

    public PDATransitions(Transitions<T, PDATransition<T, S, K>> toClone) {
        super(toClone);
    }

    /* For serialization */
    public PDATransitions() {}
}

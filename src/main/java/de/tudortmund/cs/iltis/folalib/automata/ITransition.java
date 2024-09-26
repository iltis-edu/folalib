package de.tudortmund.cs.iltis.folalib.automata;

import java.io.Serializable;

/**
 * Interface specifying the API of classes realizing a transition of some automaton
 *
 * <p>Transitions in general always lead to exactly one state and can potentially be epsilon.
 *
 * @param <State>
 * @param <Alph>
 * @param <Config>
 */
public interface ITransition<
                State extends Serializable,
                Alph extends Serializable,
                Config extends Configuration<State, Alph>>
        extends Serializable {
    /**
     * Checks if this transition can fire for the given configuration (for example, whether the next
     * input symbol matches the symbol on which this transition triggers)<br>
     * Note: this method should <strong>not</strong> check the source state. The assumption is that
     * the caller of this method has already checked that the automaton (which this transition is a
     * part of) has this transition as an outgoing transition on the state stored in {@code config}.
     * This allows the same transition to be used from multiple states to save memory for larger
     * automata with identical transitions from multiple sources.
     *
     * @param config The current configuration
     * @return {@code true} iff this {@link ITransition} can fire.
     */
    boolean isApplicable(Config config);

    /**
     * Fires this transition, transforming the given transition into its successor configuration
     * under this transition.
     *
     * <p>The given transition is not modified.
     *
     * <p>Assumes that {@link ITransition#isApplicable(Configuration)} returned true for {@code
     * config}.
     *
     * @param config The current configuration
     * @return The successor configuration
     */
    Config fire(Config config);

    /**
     * Whether this transition is an epsilon transition
     *
     * @return {@code true} iff this {@link ITransition} is epsilon.
     */
    boolean isEpsilon();

    /**
     * Gets the state this transition leads to
     *
     * @return The state this transition leads to
     */
    State getState();
}

package de.tudortmund.cs.iltis.folalib.automata.finite.conversion;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import java.io.Serializable;

/**
 * Transforms the {@link EpsilonNFA} into an epsilon-NFA with only one initial state. This is
 * achieved by creating a new initial state, which will be the only initial state. From this state,
 * an epsilon transition to every original initial state will be created. The new epsilon-NFA
 * accepts the same language, but contains only one initial state.
 *
 * @param <State> The type of the states of the epsilon-NFA
 * @param <Sym> The type of the input symbols of the epsilon-NFA
 */
public class EpsilonNFAOnlyOneInitialStateConversion<
                State extends Serializable, Sym extends Serializable>
        extends NFAConversion<State, MaybeGenerated<State, String>, Sym> {

    @Override
    protected NFA<MaybeGenerated<State, String>, Sym> convert(NFA<State, Sym> epsilonNFA) {
        // Generate one new initial state. This will become the only initial state later on.
        final String initialStateLabel = "newInitialState";
        MaybeGenerated<State, String> initialState =
                new MaybeGenerated.Generated<>(initialStateLabel);

        // Map all other states to the MaybeGenerated type
        NFA<MaybeGenerated<State, String>, Sym> mappedNfa =
                epsilonNFA.mapStates(MaybeGenerated.Input::new);

        NFABuilder<MaybeGenerated<State, String>, Sym> builder = new NFABuilder<>(mappedNfa);
        builder.overrideInitial(initialState);

        // Add an epsilon transition from the new initial state to all original initial states.
        for (MaybeGenerated<State, String> start : mappedNfa.getInitialStates())
            builder.withEpsilonTransition(initialState, start);

        return builder.build().unwrap();
    }

    @Override
    protected NFA<MaybeGenerated<State, String>, Sym> identity(NFA<State, Sym> epsilonNFA) {
        return epsilonNFA.mapStates(MaybeGenerated.Input::new);
    }

    @Override
    protected boolean isRedundant(NFA<State, Sym> epsilonNFA) {
        return epsilonNFA.getInitialStates().size() == 1;
    }
}

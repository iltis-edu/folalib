package de.tudortmund.cs.iltis.folalib.automata.pushdown.conversion;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDA;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDABuilder;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDAStackSymbol;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDAStackWord;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import java.io.Serializable;

/**
 * Transforms the PDA into a PDA with only one initial state. This is achieved by creating a new
 * initial state, which will be the only initial state. From this state, an epsilon transition to
 * every original initial state will be created. The new PDA accepts the same language, but contains
 * only one initial state.
 *
 * @param <T> The type of the states of the PDA
 * @param <S> The type of the input symbols of the PDA
 * @param <K> The type of the stack alphabet of the PDA
 */
public class PDAMultipleInitialStatesToOnlyOneInitialStateConversion<
                T extends Serializable, S extends Serializable, K extends Serializable>
        extends PDAConversion<T, MaybeGenerated<T, String>, S, K, K> {

    @Override
    protected PDA<MaybeGenerated<T, String>, S, K> convert(PDA<T, S, K> pda) {
        // Generate one new initial state. This will become the only initial state later on.
        final String initialStateLabel = "newInitialState";
        MaybeGenerated<T, String> initialState = new MaybeGenerated.Generated<>(initialStateLabel);

        // Map all other states to the MaybeGenerated type
        PDA<MaybeGenerated<T, String>, S, K> mappedPda = pda.mapStates(MaybeGenerated.Input::new);

        PDABuilder<MaybeGenerated<T, String>, S, K> builder = new PDABuilder<>(mappedPda);
        builder.overrideInitial(initialState);

        // Add an epsilon transition from the new initial state to all original initial states.
        K wildcard = null;
        for (MaybeGenerated<T, String> start : mappedPda.getInitialStates())
            builder.withEpsilonTransition(
                    initialState, PDAStackSymbol.wildcard(), start, new PDAStackWord<>(wildcard));

        return builder.build().unwrap();
    }

    @Override
    protected PDA<MaybeGenerated<T, String>, S, K> identity(PDA<T, S, K> pda) {
        return pda.mapStates(MaybeGenerated.Input::new);
    }

    @Override
    protected boolean isRedundant(PDA<T, S, K> pda) {
        return (pda.getInitialStates().size() < 2);
    }
}

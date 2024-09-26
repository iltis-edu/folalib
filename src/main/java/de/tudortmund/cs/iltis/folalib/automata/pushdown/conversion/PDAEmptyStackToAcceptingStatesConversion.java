package de.tudortmund.cs.iltis.folalib.automata.pushdown.conversion;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDA;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDAAcceptanceStrategy;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDABuilder;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDAStackWord;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGeneratedDeepFold;
import de.tudortmund.cs.iltis.folalib.util.CachedSerializableFunction;
import java.io.Serializable;

/**
 * Convert the given PDA to another PDA, such that they decide the same language but the new one
 * accepts depending on the current state. This method additionally converts the PDA into a PDA
 * which only has one initial state before the actual conversion.
 *
 * <p>If the given PDA already accept by accepting states, an identity transformation is applied
 *
 * @param <T> The type of the states of the PDA
 * @param <S> The type of the input symbols of the PDA
 * @param <K> The type of stack alphabet symbol of the PDA
 */
public class PDAEmptyStackToAcceptingStatesConversion<
                T extends Serializable, S extends Serializable, K extends Serializable>
        extends PDAConversion<T, MaybeGenerated<T, String>, S, K, MaybeGenerated<K, String>> {

    public static final String initialStateLabel = "initialState";
    public static final String acceptingStateLabel = "acceptingState";
    public static final String newInitialStackSymbolLabel = "newInitialStackSymbol";

    @Override
    protected PDA<MaybeGenerated<T, String>, S, MaybeGenerated<K, String>> identity(
            PDA<T, S, K> pda) {
        return pda.<MaybeGenerated<T, String>>mapStates(MaybeGenerated.Input::new)
                .mapStackAlphabet(MaybeGenerated.Input::new);
    }

    /* The algorithm is taken from GTI lectures, SS 18, slide 346 */
    protected PDA<MaybeGenerated<T, String>, S, MaybeGenerated<K, String>> convert(
            PDA<T, S, K> pda) {

        PDA<MaybeGenerated<T, String>, S, K> oneInitialStatePda =
                new PDAMultipleInitialStatesToOnlyOneInitialStateConversion<T, S, K>().apply(pda);

        MaybeGenerated<MaybeGenerated<T, String>, String> initialState =
                new MaybeGenerated.Generated<>(initialStateLabel);
        MaybeGenerated<MaybeGenerated<T, String>, String> acceptingState =
                new MaybeGenerated.Generated<>(acceptingStateLabel);

        MaybeGenerated<K, String> newStackSymbol =
                new MaybeGenerated.Generated<>(newInitialStackSymbolLabel);

        CachedSerializableFunction<
                        MaybeGenerated<T, String>,
                        MaybeGenerated<MaybeGenerated<T, String>, String>>
                stateMapping = new CachedSerializableFunction<>(MaybeGenerated.Input::new);
        CachedSerializableFunction<K, MaybeGenerated<K, String>> stackSymbolMapping =
                new CachedSerializableFunction<>(MaybeGenerated.Input::new);

        PDA<MaybeGenerated<MaybeGenerated<T, String>, String>, S, K> mappedPda =
                oneInitialStatePda.mapStates(stateMapping);

        PDABuilder<MaybeGenerated<MaybeGenerated<T, String>, String>, S, MaybeGenerated<K, String>>
                builder = new PDABuilder<>(mappedPda.mapStackAlphabet(stackSymbolMapping));

        builder.overrideInitial(initialState);
        builder.withAccepting(acceptingState);
        builder.withStackSymbols(newStackSymbol);
        builder.withEpsilonTransition(
                initialState,
                newStackSymbol,
                mappedPda.getInitialStates().stream()
                        .findFirst()
                        .get(), // Generated PDA should only contain one initial state, so this is
                // fine.
                new PDAStackWord<>(
                        stackSymbolMapping.apply(mappedPda.getInitialStackSymbol()),
                        newStackSymbol));

        for (MaybeGenerated<MaybeGenerated<T, String>, String> state : mappedPda.getStates()) {
            builder.withEpsilonTransition(
                    state, newStackSymbol, acceptingState, new PDAStackWord<>(newStackSymbol));
        }

        builder.withAcceptanceStrategy(PDAAcceptanceStrategy.ACCEPTING_STATES);
        builder.withInitialStackSymbol(newStackSymbol);
        PDA<MaybeGenerated<MaybeGenerated<T, String>, String>, S, MaybeGenerated<K, String>>
                pdaNesting = builder.build().unwrap();

        // Unfold the MaybeGenerated nesting
        return pdaNesting.mapStates(
                (state) -> MaybeGeneratedDeepFold.deepFold(state, this::helper));
    }

    // Helper method for MaybeGenerated unfold
    private MaybeGenerated<T, String> helper(String path, Object result) {
        if (path.matches(
                "0+")) { // path of only zero's indicates result was found at In{In{In{... x ...}}}
            return new MaybeGenerated.Input<>((T) result);
        } else if (path.equals("1")) { // 1 == Gen{x}, i.e. we generated this ourselves
            return new MaybeGenerated.Generated<>(
                    "emptyStackToAcceptingStatesConversion_" + result.toString());
        } else if (path.equals(
                "01")) { // 01 == In{Gen{x}}, i.e. we got this as input but it was generated from
            // "one initial state conversion"
            return new MaybeGenerated.Generated<>("oneInitialStateConversion_" + result.toString());
        } else {
            throw new RuntimeException("Unreachable");
        }
    }

    @Override
    protected boolean isRedundant(PDA<T, S, K> pda) {
        return pda.getAcceptanceStrategy() == PDAAcceptanceStrategy.ACCEPTING_STATES;
    }
}

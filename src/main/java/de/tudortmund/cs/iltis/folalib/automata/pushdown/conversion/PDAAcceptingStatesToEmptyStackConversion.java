package de.tudortmund.cs.iltis.folalib.automata.pushdown.conversion;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.*;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGeneratedDeepFold;
import de.tudortmund.cs.iltis.folalib.util.CachedSerializableFunction;
import java.io.Serializable;

/**
 * Convert the given PDA to another PDA, such that they decide the same language but the new one
 * accepts based on whether the stack is empty. This method additionally converts the PDA into a PDA
 * which only has one initial state before the actual conversion.
 *
 * <p>If the given PDA already accepts by empty stack, an identity transformation is applied.
 *
 * @param <T> The type of the states of the PDA
 * @param <S> The type of the input symbols of the PDA
 * @param <K> The type of stack alphabet symbol of the PDA
 */
public class PDAAcceptingStatesToEmptyStackConversion<
                T extends Serializable, S extends Serializable, K extends Serializable>
        extends PDAConversion<T, MaybeGenerated<T, String>, S, K, MaybeGenerated<K, String>> {

    public static final String initialStateLabel = "initialState";
    public static final String clearStackStateLabel = "clearStackState";
    public static final String newInitialStackSymbolLabel = "newInitialStackSymbol";

    @Override
    protected PDA<MaybeGenerated<T, String>, S, MaybeGenerated<K, String>> identity(
            PDA<T, S, K> pda) {
        return pda.<MaybeGenerated<T, String>>mapStates(MaybeGenerated.Input::new)
                .mapStackAlphabet(MaybeGenerated.Input::new);
    }

    /* The algorithm is taken from GTI lectures, SS18, slide 348 */
    protected PDA<MaybeGenerated<T, String>, S, MaybeGenerated<K, String>> convert(
            PDA<T, S, K> pda) {

        PDA<MaybeGenerated<T, String>, S, K> oneInitialStateNoStackSymbolWildcardsPda =
                new PDAMultipleInitialStatesToOnlyOneInitialStateConversion<T, S, K>()
                        .andThen(new PDANoStackSymbolWildcardsOnTransitionsConversion<>())
                        .apply(pda);

        MaybeGenerated<MaybeGenerated<T, String>, String> initialState =
                new MaybeGenerated.Generated<>(initialStateLabel);
        MaybeGenerated<MaybeGenerated<T, String>, String> clearStackState =
                new MaybeGenerated.Generated<>(clearStackStateLabel);

        MaybeGenerated<K, String> newStackSymbol =
                new MaybeGenerated.Generated<>(newInitialStackSymbolLabel);

        CachedSerializableFunction<
                        MaybeGenerated<T, String>,
                        MaybeGenerated<MaybeGenerated<T, String>, String>>
                stateMapping = new CachedSerializableFunction<>(MaybeGenerated.Input::new);
        CachedSerializableFunction<K, MaybeGenerated<K, String>> stackSymbolMapping =
                new CachedSerializableFunction<>(MaybeGenerated.Input::new);

        PDA<MaybeGenerated<MaybeGenerated<T, String>, String>, S, K> mappedPda =
                oneInitialStateNoStackSymbolWildcardsPda.mapStates(stateMapping);

        PDABuilder<MaybeGenerated<MaybeGenerated<T, String>, String>, S, MaybeGenerated<K, String>>
                builder = new PDABuilder<>(mappedPda.mapStackAlphabet(stackSymbolMapping));

        builder.overrideInitial(initialState);
        builder.withStates(clearStackState);
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

        for (MaybeGenerated<MaybeGenerated<T, String>, String> state :
                mappedPda.getAcceptingStates()) {
            builder.withEpsilonTransition(
                    state, PDAStackSymbol.wildcard(), clearStackState, new PDAStackWord<>());
        }
        builder.withEpsilonTransition(
                clearStackState, PDAStackSymbol.wildcard(), clearStackState, new PDAStackWord<>());

        builder.withAcceptanceStrategy(PDAAcceptanceStrategy.EMPTY_STACK);
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
                    "acceptingStatesToEmptyStackConversion_" + result.toString());
        } else if (path.equals(
                "01")) { // 01 == In{Gen{x}}, i.e. we got this as input but it was generated from
            // "one initial state conversion"
            return new MaybeGenerated.Generated<>("oneInitialStateConversion_" + result.toString());
        } else {
            throw new RuntimeException("Unreachable");
        }
    }

    protected boolean isRedundant(PDA<T, S, K> pda) {
        return pda.getAcceptanceStrategy() == PDAAcceptanceStrategy.EMPTY_STACK;
    }
}

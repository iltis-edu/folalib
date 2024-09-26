package de.tudortmund.cs.iltis.folalib.automata.pushdown.conversion;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.*;
import de.tudortmund.cs.iltis.folalib.transform.ConstrainedSupplier;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Convert a PDA to another PDA such that they decide the same language but the new one replaces the
 * top of the stack with at most 2 symbols, i.e. effectively add <= 1 symbol per transition
 *
 * @param <T> The type of the states of the PDA
 * @param <S> The type of the input symbols of the PDA
 * @param <K> The type of the stack alphabet of the PDA
 */
public class PDAWithMaximalTwoWritesToStackPerTransitionConversion<
                T extends Serializable, S extends Serializable, K extends Serializable>
        extends PDAConversion<T, MaybeGenerated<T, String>, S, K, K> {

    /* For serialization */
    @SuppressWarnings("unused")
    public PDAWithMaximalTwoWritesToStackPerTransitionConversion() {}

    @Override
    protected PDA<MaybeGenerated<T, String>, S, K> identity(PDA<T, S, K> pda) {
        return pda.mapStates(MaybeGenerated.Input::new);
    }

    /* The approach is simple: we expand each invalid transition by a chain of epsilon transition with artificial states
    in between */
    protected PDA<MaybeGenerated<T, String>, S, K> convert(PDA<T, S, K> pda) {
        PDA<MaybeGenerated<T, String>, S, K> mappedPDA = pda.mapStates(MaybeGenerated.Input::new);
        PDABuilder<MaybeGenerated<T, String>, S, K> builder = new PDABuilder<>(mappedPDA);

        ConstrainedSupplier<MaybeGenerated<T, String>> stateSupplier = getStateSupplier();
        stateSupplier.constrain(mappedPDA.getStates());

        for (MaybeGenerated<T, String> state : mappedPDA.getStates()) {
            builder.removeTransitions(state);
            for (PDATransition<MaybeGenerated<T, String>, S, K> transition :
                    mappedPDA.getTransitions().in(state)) {
                PDATransitionChains<MaybeGenerated<T, String>, S, K> chains =
                        new PDATransitionChains<>(
                                state, transition, pda.getStackAlphabet(), stateSupplier);
                for (PDATransitionChains.PDATransitionChain<MaybeGenerated<T, String>, S, K> chain :
                        chains) {
                    for (Pair<
                                    MaybeGenerated<T, String>,
                                    PDATransition<MaybeGenerated<T, String>, S, K>>
                            step : chain) {
                        builder.withStates(step.first());
                        builder.withTransition(step.first(), step.second());
                    }
                }
            }
        }
        return builder.build().unwrap();
    }

    private ConstrainedSupplier<MaybeGenerated<T, String>> getStateSupplier() {
        return new ConstrainedSupplier<MaybeGenerated<T, String>>() {
            private final ConstrainedSupplier<String> counter =
                    ConstrainedSupplier.constrainedStringSupplier();
            private final Set<MaybeGenerated<T, String>> blocked = new LinkedHashSet<>();

            @Override
            public void constrain(MaybeGenerated<T, String> value) {
                blocked.add(value);
            }

            @Override
            public MaybeGenerated<T, String> get() {
                MaybeGenerated<T, String> next;
                do {
                    next = new MaybeGenerated.Generated<>(counter.get());
                } while (blocked.contains(next));
                return next;
            }
        };
    }

    @Override
    protected boolean isRedundant(PDA<T, S, K> pda) {
        return pda.getTransitions().forall((s, trans) -> trans.getNewTopOfStack().size() <= 2);
    }
}

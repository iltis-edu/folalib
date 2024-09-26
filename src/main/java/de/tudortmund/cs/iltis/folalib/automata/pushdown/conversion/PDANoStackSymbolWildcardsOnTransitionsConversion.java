package de.tudortmund.cs.iltis.folalib.automata.pushdown.conversion;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDA;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDABuilder;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDATransition;
import java.io.Serializable;

/**
 * Convert the given PDA such that they both decide the same language but the new one has no
 * transitions with wildcards on it
 *
 * @param <T> The type of the states of the PDA
 * @param <S> The type of the input symbols of the PDA
 * @param <K> The type of the stack alphabet of the PDA
 */
public class PDANoStackSymbolWildcardsOnTransitionsConversion<
                T extends Serializable, S extends Serializable, K extends Serializable>
        extends PDAConversion<T, T, S, K, K> {

    @Override
    protected PDA<T, S, K> identity(PDA<T, S, K> pda) {
        return new PDABuilder<>(pda).build().unwrap();
    }

    /* The approach is simple: we replace each transition with a wildcard by a set of individual transitions which
     * altogether are equivalent to the original transition with a wildcard
     */
    protected PDA<T, S, K> convert(PDA<T, S, K> pda) {
        PDABuilder<T, S, K> builder = new PDABuilder<>(pda);
        builder.clearTransitions();
        for (T state : pda.getStates()) {
            for (PDATransition<T, S, K> transition : pda.getTransitions().in(state)) {
                for (PDATransition<T, S, K> concreteTransition :
                        transition.substituteWildcards(pda.getStackAlphabet())) {
                    builder.withTransition(state, concreteTransition);
                }
            }
        }
        return builder.build().unwrap();
    }

    @Override
    protected boolean isRedundant(PDA<T, S, K> pda) {
        return pda.getTransitions()
                .forall(
                        (s, trans) ->
                                !trans.containsWildcard() && !trans.getStackSymbol().isVariable());
    }
}

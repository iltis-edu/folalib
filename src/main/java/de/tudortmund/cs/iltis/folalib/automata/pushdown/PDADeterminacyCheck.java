package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultCollection;
import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultReason;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.fault.PDADeterminacyFault;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An algorithm to check whether a given PDA is deterministic
 *
 * @param <T> The type of the states of the PDA
 * @param <S> The type of the input symbols of the PDA
 * @param <K> The type of the stack symbols of the PDA
 */
public class PDADeterminacyCheck<
        T extends Serializable, S extends Serializable, K extends Serializable> {
    private PDA<T, S, K> pda;
    private List<PDADeterminacyFault<T, S, K, DeterminacyFaultReason>> faults;

    private void setup(PDA<T, S, K> pda) {
        this.pda = pda;
        faults = new ArrayList<>();
    }

    /**
     * Check whether a PDA is deterministic return a collection of all reasons why it is not.
     *
     * @param pda The PDA to check whether it is deterministic
     * @return a collection of reasons why the given PDA is not deterministic
     */
    public DeterminacyFaultCollection<T, PDADeterminacyFault<T, S, K, DeterminacyFaultReason>>
            checkDeterminacy(PDA<T, S, K> pda) {
        setup(pda);

        if (pda.getInitialStates().size() > 1) {
            faults.add(
                    new PDADeterminacyFault<>(
                            null, null, null, DeterminacyFaultReason.MULTIPLE_INITIAL_STATES));
        }

        for (T state : pda.getStates()) {
            Set<PDATransition<T, S, K>> outgoingTransitions = pda.getTransitions().in(state);
            checkDeterminacy(state, outgoingTransitions);
        }
        return new DeterminacyFaultCollection<>(faults);
    }

    private void checkDeterminacy(T state, Set<PDATransition<T, S, K>> transitions) {
        for (S symbol : pda.getAlphabet()) {
            for (K stackSymbol : pda.getStackAlphabet()) {
                int possibleNextConfigurations =
                        countPossibleNextConfigurations(transitions, symbol, stackSymbol);
                if (possibleNextConfigurations > 1) {
                    faults.add(
                            new PDADeterminacyFault<>(
                                    state,
                                    symbol,
                                    stackSymbol,
                                    DeterminacyFaultReason.AMBIGUOUS_TRANSITION,
                                    possibleNextConfigurations));
                }
            }
        }
    }

    private int countPossibleNextConfigurations(
            Set<PDATransition<T, S, K>> transitions, S symbol, K stackSymbol) {
        return transitions.stream()
                .filter(trans -> isApplicable(trans, symbol, stackSymbol))
                .map(
                        trans ->
                                new Pair<>(
                                        trans.getState(),
                                        trans.getNewTopOfStack().substituteWildcards(stackSymbol)))
                .collect(Collectors.toSet())
                .size();
    }

    private boolean isApplicable(PDATransition<T, S, K> transition, S symbol, K stackSymbol) {
        return (transition.isEpsilon() || transition.getInputSymbol().equals(symbol))
                && transition.getStackSymbol().matches(stackSymbol);
    }
}

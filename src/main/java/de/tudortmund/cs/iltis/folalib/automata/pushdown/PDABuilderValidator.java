package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.fault.PDAConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.fault.PDAConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.fault.PDASyntaxFault;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.fault.PDATransitionFault;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;
import java.util.*;

public class PDABuilderValidator<
                T extends Serializable, S extends Serializable, K extends Serializable>
        implements Serializable {

    private LinkedHashSet<T> states;
    private LinkedHashSet<T> initialStates;
    private K initialTopOfStack;
    private PDAAcceptanceStrategy acceptanceStrategy;
    private LinkedHashSet<S> inputAlphabet;
    private LinkedHashSet<K> stackAlphabet;
    private PDATransitions<T, S, K> transitions;

    private final List<Fault<PDAConstructionFaultReason>> faults = new ArrayList<>();

    /**
     * Validates whether this {@param builder} instance, as currently configured, would produce a
     * valid {@link PDA}. Please note that this validation only checks the syntax, not the
     * semantics. It is <b>not</b> a construction fault if no accepting states exist.
     *
     * @return A set of faults indicating why the current builder state does not represent a valid
     *     PDA, or an empty set in case of a valid construction
     */
    public PDAConstructionFaultCollection validate(PDABuilder<T, S, K> builder) {
        faults.clear(); // Clear the construction faults previously found in case the same builder
        // gets validated again and was changed beforehand.
        setup(builder);
        collectFaults();
        return new PDAConstructionFaultCollection(faults);
    }

    private void setup(PDABuilder<T, S, K> builder) {
        states = builder.states;
        initialStates = builder.initialStates;
        initialTopOfStack = builder.initialTopOfStack;
        acceptanceStrategy = builder.acceptanceStrategy;
        inputAlphabet = builder.inputAlphabet;
        stackAlphabet = builder.stackAlphabet;
        transitions = builder.transitions;
    }

    private void collectFaults() {
        validateSyntax();
        validateTransitions();
    }

    private void validateSyntax() {
        if (initialStates.isEmpty()) {
            faults.add(PDASyntaxFault.missingInitialState());
        }
        if (initialTopOfStack == null) {
            faults.add(PDASyntaxFault.missingInitialStackSymbol());
        }
        if (acceptanceStrategy == null) {
            faults.add(PDASyntaxFault.missingAcceptanceStrategy());
        }
    }

    private void validateTransitions() {
        for (Map.Entry<T, Set<PDATransition<T, S, K>>> entry :
                transitions.getTransitions().entrySet()) {
            for (PDATransition<T, S, K> transition : entry.getValue()) {
                T state = entry.getKey();
                validateOrigin(state, transition);
                validateDestination(state, transition);
                validateInputSymbol(state, transition);
                validateStackSymbol(state, transition);
                validateNewTopOfStack(state, transition);
            }
        }
    }

    private void validateOrigin(T state, PDATransition<T, S, K> transition) {
        if (!states.contains(state)) {
            faults.add(PDATransitionFault.unknownOrigin(state, transition));
        }
    }

    private void validateDestination(T state, PDATransition<T, S, K> transition) {
        if (!states.contains(transition.getState())) {
            faults.add(PDATransitionFault.unknownDestination(state, transition));
        }
    }

    private void validateInputSymbol(T state, PDATransition<T, S, K> transition) {
        if (!transition.isEpsilon() && !inputAlphabet.contains(transition.getInputSymbol())) {
            faults.add(PDATransitionFault.unknownInputSymbol(state, transition));
        }
    }

    private void validateStackSymbol(T state, PDATransition<T, S, K> transition) {
        if (!transition.getStackSymbol().compatibleWithAlphabet(stackAlphabet)) {
            faults.add(PDATransitionFault.unknownStackSymbol(state, transition));
        }
    }

    private void validateNewTopOfStack(T state, PDATransition<T, S, K> transition) {
        for (K symbol : transition.getNewTopOfStack()) {
            if (symbol != null && !stackAlphabet.contains(symbol)) {
                faults.add(PDATransitionFault.invalidNewTopOfStack(state, transition));
            }
        }
    }
}

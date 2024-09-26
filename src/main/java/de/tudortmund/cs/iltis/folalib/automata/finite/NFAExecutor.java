package de.tudortmund.cs.iltis.folalib.automata.finite;

import de.tudortmund.cs.iltis.folalib.automata.Configuration;
import de.tudortmund.cs.iltis.folalib.automata.Executor;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The NFAExecutor is a specialization of the basic Executor. It offers a constructor for an
 * automaton together with some word, which defines the start configuration.
 */
public class NFAExecutor<T extends Serializable, S extends Serializable>
        extends Executor<NFA<T, S>, T, S, Configuration<T, S>, NFATransition<T, S>> {
    public NFAExecutor(NFA<T, S> automaton, Word<S> word) {
        super(automaton, automaton.getAllStartConfigurations(word));
    }

    // For GWT serialization
    @SuppressWarnings("unused")
    private NFAExecutor() {}

    private NFAExecutor(NFA<T, S> automaton, Set<Configuration<T, S>> configurations) {
        super(automaton, configurations);
    }

    @Override
    public Executor<NFA<T, S>, T, S, Configuration<T, S>, NFATransition<T, S>> newExecutor(
            NFA<T, S> automaton, Set<Configuration<T, S>> configurations) {
        return new NFAExecutor<>(automaton, configurations);
    }

    @Override
    public boolean hasAccepted() {
        return configurations.stream().anyMatch(automaton::isAcceptingConfiguration);
    }

    @Override
    protected Set<Configuration<T, S>> computeEpsilonClosure(Configuration<T, S> configuration) {
        return automaton.epsilonClosureOf(configuration.getState()).stream()
                .map(
                        state ->
                                new Configuration<>(
                                        state,
                                        configuration.getWord(),
                                        configuration.getPosition()))
                .collect(Collectors.toSet());
    }

    @Override
    public NFAExecutor<T, S> nextStep() {
        return (NFAExecutor<T, S>) super.nextStep();
    }
}

package de.tudortmund.cs.iltis.folalib.automata;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An executor belongs to a specific automaton and manages a set of configurations (intended to
 * represent the configurations that a nondeterministic automaton could have possibly reached so
 * far). This basic executor has to be specialized by implementing an acceptance condition.
 */
public abstract class Executor<
                A extends Automaton<T, S, Config, Trans>,
                T extends Serializable,
                S extends Serializable,
                Config extends Configuration<T, S>,
                Trans extends ITransition<T, S, Config>>
        implements Serializable {
    protected A automaton;
    protected Set<Config> configurations;

    // For GWT serialization
    protected Executor() {}

    protected Executor(A automaton, Set<Config> configurations) {
        this.automaton = automaton;
        this.configurations = new LinkedHashSet<>(configurations);
    }

    protected Executor<A, T, S, Config, Trans> newExecutor(A automaton, Config configuration) {
        return this.newExecutor(automaton, Collections.singleton(configuration));
    }

    protected abstract Executor<A, T, S, Config, Trans> newExecutor(
            A automaton, Set<Config> configurations);

    public A getAutomaton() {
        return this.automaton;
    }

    public Set<Config> getConfigurations() {
        return Collections.unmodifiableSet(configurations);
    }

    public Set<T> getStates() {
        return configurations.stream().map(Configuration::getState).collect(Collectors.toSet());
    }

    public boolean isHalted() {
        return configurations.stream().allMatch(automaton::isHaltingConfiguration);
    }

    public abstract boolean hasAccepted();

    public Executor<A, T, S, Config, Trans> nextStep() {
        Set<Config> newConfigurations = new LinkedHashSet<>();

        // Compute the set of successor configurations, then build the epsilon closure of that set

        for (Config configuration : this.configurations) {
            if (!configuration.hasSymbol()) {
                // The entire epsilon closure of `configuration` also has no more symbols, so they
                // will also trigger this if-branch (meaning we do not have to recalculate the
                // eps-closure of `configuration`)
                newConfigurations.add(configuration);
            } else {
                for (ITransition<T, S, Config> trans :
                        this.automaton.getApplicableTransitions(configuration)) {
                    if (!trans.isEpsilon())
                        newConfigurations.addAll(computeEpsilonClosure(trans.fire(configuration)));
                }
            }
        }

        if (this.isHalted() || this.configurations.equals(newConfigurations)) return this;

        return this.newExecutor(this.automaton, newConfigurations);
    }

    public boolean run() {
        // We intentionally do *not* use `nextStep` because of its poor performance (searching
        // epsilon closure, defensive copies, etc.)
        Set<Config> allPreviouslyReached = new LinkedHashSet<>(configurations);
        Set<Config> newlyReached = new LinkedHashSet<>();
        Set<Config> currentFront = new LinkedHashSet<>(configurations);

        // For NFAs, this loop will always terminate (because we will reach a "fixed point" of
        // configurations)
        // For other automate models, this does not necessarily terminate.
        while (!currentFront.isEmpty()) {
            if (currentFront.stream().anyMatch(automaton::isAcceptingConfiguration)) return true;

            for (Config config : currentFront) {
                for (Trans trans : automaton.getApplicableTransitions(config)) {
                    Config c = trans.fire(config);
                    if (!allPreviouslyReached.contains(c) && !currentFront.contains(c))
                        newlyReached.add(c);
                }
            }

            if (newlyReached.isEmpty()) {
                // no new configurations found but currentFront is also non-accepting (initial check
                // of this loop)
                return false;
            }

            allPreviouslyReached.addAll(currentFront);
            currentFront = newlyReached;
            newlyReached = new LinkedHashSet<>();
        }

        return false;
    }

    @Override
    public String toString() {
        return this.configurations.toString();
    }

    protected Set<Config> computeEpsilonClosure(Config configuration) {
        // Set of all configurations that can be reached using only epsilon transitions
        Set<Config> closure = new LinkedHashSet<>();

        // We compute the closure using breath-first-search on the transition graph.
        Queue<Config> toProcess = new LinkedList<>();
        toProcess.offer(configuration);

        while (!toProcess.isEmpty()) {
            Config current = toProcess.poll();

            for (ITransition<T, S, Config> trans :
                    this.automaton.getApplicableTransitions(current)) {
                if (trans.isEpsilon()) {
                    Config successor = trans.fire(current);

                    if (!toProcess.contains(successor)
                            && !closure.contains(successor)
                            && !successor.equals(current)) toProcess.offer(successor);
                }
            }

            // Based on how this method is used in `nextStep()`, it is desirable to include the
            // initial state in the eps closure
            closure.add(current);
        }
        return closure;
    }
}

package de.tudortmund.cs.iltis.folalib.automata;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A stepper which allows to step through the execution of an automaton.
 *
 * <p>In the context of this class, one step corresponds to "advancing by one transition". This is
 * an important distinction with regard to the {@link Executor}: the executor computes the epsilon
 * closure of each state. Thus, `n` steps correspond to reading `n` input symbols. In this class,
 * `n` steps corresponds to `n` transitions used which is not necessarily equivalent. The reason for
 * this is that the epsilon closure may be infinite for some automata. In these cases we would still
 * like to "step" through the execution.
 *
 * @param <T> The type of states of the automaton
 * @param <S> The type of input symbols of the automaton
 * @param <Config> The type of configurations of the automaton
 * @param <Trans> The type of transitions of the automaton
 * @param <Aut> The type of the automaton itself
 */
public class AutomatonStepper<
                T extends Serializable,
                S extends Serializable,
                Config extends Configuration<T, S>,
                Trans extends ITransition<T, S, Config>,
                Aut extends Automaton<T, S, Config, Trans>>
        implements Serializable {

    protected Aut automaton;
    protected Set<Config> configurations;

    public AutomatonStepper(Aut automaton, Config configuration) {
        this(automaton, Collections.singleton(configuration));
    }

    public AutomatonStepper(Aut automaton, Set<Config> configurations) {
        this.automaton = automaton;
        this.configurations = new LinkedHashSet<>(configurations);
    }

    /* For serialization */
    @SuppressWarnings("unused")
    public AutomatonStepper() {}

    /**
     * Get the set of current configurations of this stepper
     *
     * @return the set of current configurations
     */
    public Set<Config> getConfigurations() {
        return Collections.unmodifiableSet(configurations);
    }

    /**
     * Advance the stepper by a single step
     *
     * <p>One step in this case refers to "advancing by using one transition" and not "reading one
     * input symbol". See also: {@link AutomatonStepper}
     *
     * @return a new stepper with all possible follow-up configurations
     */
    public AutomatonStepper<T, S, Config, Trans, Aut> step() {
        Set<Config> followingConfigurations =
                configurations.stream()
                        .flatMap(
                                config ->
                                        automaton.getApplicableTransitions(config).stream()
                                                .map(trans -> trans.fire(config)))
                        .collect(Collectors.toSet());
        return new AutomatonStepper<>(automaton, followingConfigurations);
    }

    /**
     * Advance the stepper by n steps
     *
     * @param n the number of steps to perform
     * @return a new stepper with all possible configurations
     */
    public AutomatonStepper<T, S, Config, Trans, Aut> step(int n) {
        AutomatonStepper<T, S, Config, Trans, Aut> stepper =
                new AutomatonStepper<>(automaton, configurations);
        for (int i = 0; i < n; ++i) {
            stepper = stepper.step();
        }
        return stepper;
    }

    /**
     * Step through the execution of the automaton until it has accepted or otherwise `maxSteps`
     * have been taken
     *
     * @param maxSteps the maximal number of steps to take
     * @return {@code true} iff the automaton accepts in `maxSteps` or less, {@code false} otherwise
     */
    public boolean run(int maxSteps) {
        AutomatonStepper<T, S, Config, Trans, Aut> stepper =
                new AutomatonStepper<>(automaton, configurations);
        for (int i = 0; i <= maxSteps; ++i) {
            if (stepper.hasAccepted()) {
                return true;
            }
            stepper = stepper.step();
        }
        return false;
    }

    /**
     * Test whether any of the current configurations is accepting
     *
     * @return {@code true} iff any of the current configurations is accepting
     */
    public boolean hasAccepted() {
        return configurations.stream().anyMatch(automaton::isAcceptingConfiguration);
    }
}

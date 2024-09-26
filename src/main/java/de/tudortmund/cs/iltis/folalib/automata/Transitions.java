package de.tudortmund.cs.iltis.folalib.automata;

import de.tudortmund.cs.iltis.utils.collections.DefaultMap;
import de.tudortmund.cs.iltis.utils.function.SerializableBiPredicate;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.*;

public class Transitions<State extends Serializable, Trans extends Serializable>
        implements Serializable {

    protected DefaultMap<State, Set<Trans>> transitionsByState =
            new DefaultMap<>(new SetSupplier<>());

    public static class SetSupplier<State, Trans>
            implements SerializableFunction<State, Set<Trans>> {
        @Override
        public Set<Trans> apply(State state) {
            return new LinkedHashSet<>();
        }
    }

    public Transitions() {}

    protected Transitions(Transitions<State, Trans> toClone) {
        for (Map.Entry<State, Set<Trans>> entry : toClone.transitionsByState.entrySet())
            transitionsByState.put(entry.getKey(), new LinkedHashSet<>(entry.getValue()));
    }

    public DefaultMap<State, Set<Trans>> getTransitions() {
        return transitionsByState;
    }

    public void addTransition(State from, Trans trans) {
        transitionsByState.get(from).add(trans);
    }

    public Set<Trans> in(State state) {
        return transitionsByState.get(state);
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();

        for (Map.Entry<State, Set<Trans>> entry : transitionsByState.entrySet()) {
            String textFromState = entry.getKey().toString();

            for (Trans transition : entry.getValue())
                text.append(textFromState).append(" ").append(transition.toString()).append("\n");
        }

        return text.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transitions<?, ?> that = (Transitions<?, ?>) o;

        // TODO: ask about why DefaultMap doesnt have an .equals implementation
        return new HashMap<>(transitionsByState).equals(new HashMap<>(that.transitionsByState));
    }

    @Override
    public int hashCode() {
        return Objects.hash(transitionsByState);
    }

    public boolean forall(SerializableBiPredicate<State, Trans> predicate) {
        for (Map.Entry<State, Set<Trans>> entry : transitionsByState.entrySet()) {
            for (Trans trans : entry.getValue()) {
                if (!predicate.test(entry.getKey(), trans)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean exists(SerializableBiPredicate<State, Trans> predicate) {
        for (Map.Entry<State, Set<Trans>> entry : transitionsByState.entrySet()) {
            for (Trans trans : entry.getValue()) {
                if (predicate.test(entry.getKey(), trans)) {
                    return true;
                }
            }
        }
        return false;
    }
}

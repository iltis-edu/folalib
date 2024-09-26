package de.tudortmund.cs.iltis.folalib.languages;

import de.tudortmund.cs.iltis.utils.collections.immutable.ImmutableSet;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Alphabet<S extends Serializable> extends ImmutableSet<S> {

    /* For serialization */
    @SuppressWarnings("unused")
    private Alphabet() {}

    @SafeVarargs
    public Alphabet(S... symbols) {
        super(symbols);
    }

    public Alphabet(Collection<? extends S> collection) {
        super(collection);
    }

    @Override
    public Alphabet<S> unionWith(ImmutableSet<? extends S> other) {
        return shareConstructInto(Alphabet::new, super.unionWith(other));
    }

    @Override
    public Alphabet<S> intersectionWith(ImmutableSet<? extends S> other) {
        return shareConstructInto(Alphabet::new, super.intersectionWith(other));
    }

    @Override
    public <T extends Serializable> Alphabet<T> map(Function<? super S, T> f) {
        return shareConstructInto(Alphabet::new, super.map(f));
    }

    @Override
    public Alphabet<S> filter(Predicate<? super S> p) {
        return shareConstructInto(Alphabet::new, super.filter(p));
    }
}

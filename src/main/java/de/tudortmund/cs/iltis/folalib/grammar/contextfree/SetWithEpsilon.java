package de.tudortmund.cs.iltis.folalib.grammar.contextfree;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;

public class SetWithEpsilon<T extends Serializable> extends HashSet<T> {
    private boolean containsEpsilon;

    /* package-private */ void setContainsEpsilon(boolean containsEpsilon) {
        this.containsEpsilon = containsEpsilon;
    }

    public boolean containsEpsilon() {
        return containsEpsilon;
    }

    public boolean addAllTerminals(SetWithEpsilon<? extends T> c) {
        return super.addAll(c);
    }

    public boolean addAll(SetWithEpsilon<? extends T> c) {
        boolean modified = super.addAll(c);

        if (c.containsEpsilon && !containsEpsilon) {
            setContainsEpsilon(true);
            modified = true;
        }

        return modified;
    }

    // for completeness sake...

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && !containsEpsilon;
    }

    @Override
    public void clear() {
        setContainsEpsilon(false);
        super.clear();
    }

    public boolean removeAll(SetWithEpsilon<?> c) {
        boolean modified = super.removeAll(c);

        if (c.containsEpsilon()) {
            setContainsEpsilon(false);
            modified = true;
        }

        return modified;
    }

    public boolean containsAll(SetWithEpsilon<?> c) {
        return super.containsAll(c) && (!c.containsEpsilon || containsEpsilon);
    }

    public boolean retainAll(SetWithEpsilon<?> c) {
        boolean modified = super.retainAll(c);

        if (!c.containsEpsilon && containsEpsilon) {
            setContainsEpsilon(false);
            modified = true;
        }

        return modified;
    }

    @Override
    public int size() {
        return super.size() + (containsEpsilon ? 1 : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SetWithEpsilon<?> that = (SetWithEpsilon<?>) o;
        return containsEpsilon == that.containsEpsilon;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), containsEpsilon);
    }
}

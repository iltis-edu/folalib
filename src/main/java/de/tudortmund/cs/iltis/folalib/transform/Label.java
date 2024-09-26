package de.tudortmund.cs.iltis.folalib.transform;

import java.io.Serializable;

public abstract class Label<For extends Serializable> implements Serializable {
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        return other.getClass().equals(getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

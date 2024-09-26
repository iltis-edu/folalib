package de.tudortmund.cs.iltis.folalib.languages;

import java.io.Serializable;

public interface Language<S extends Serializable> extends Serializable {
    boolean contains(Word<S> word);

    Alphabet<S> getAlphabet();
}

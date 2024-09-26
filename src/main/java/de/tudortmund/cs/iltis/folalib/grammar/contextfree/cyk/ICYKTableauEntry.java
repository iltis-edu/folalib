package de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk;

import java.io.Serializable;
import java.util.Optional;

public interface ICYKTableauEntry<N extends Serializable> extends Serializable {
    N getNonTerminal();

    Optional<Integer> getSplittingPoint();
}

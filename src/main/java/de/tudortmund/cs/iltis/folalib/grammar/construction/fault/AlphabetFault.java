package de.tudortmund.cs.iltis.folalib.grammar.construction.fault;

import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;

public class AlphabetFault<T extends Serializable> extends Fault<GrammarConstructionFaultReason> {
    private T sharedSymbol;

    public AlphabetFault(T sharedSymbol) {
        super(GrammarConstructionFaultReason.ALPHABETS_NOT_DISJOINT);

        this.sharedSymbol = sharedSymbol;
    }

    /* For serialization */
    @SuppressWarnings("unused")
    private AlphabetFault() {}

    public T getSharedSymbol() {
        return sharedSymbol;
    }

    @Override
    protected Object clone() {
        return new AlphabetFault<>(sharedSymbol);
    }
}

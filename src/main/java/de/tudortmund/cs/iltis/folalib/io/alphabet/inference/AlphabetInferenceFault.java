package de.tudortmund.cs.iltis.folalib.io.alphabet.inference;

import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.util.Objects;

/**
 * A class to encapsulate an {@link AlphabetInferenceFaultReason} with its source-symbol as {@link
 * IndexedSymbol}. Multiple faults can be collected in an {@link AlphabetInferenceFaultCollection}.
 */
public class AlphabetInferenceFault extends Fault<AlphabetInferenceFaultReason> {

    private IndexedSymbol symbol;

    public AlphabetInferenceFault(AlphabetInferenceFaultReason reason, IndexedSymbol symbol) {
        super(reason);
        Objects.requireNonNull(symbol);
        this.symbol = symbol;
    }

    public IndexedSymbol getSymbol() {
        return symbol;
    }

    @Override
    protected Object clone() {
        return new AlphabetInferenceFault(getReason(), symbol);
    }

    @Override
    public String toString() {
        return "AlphabetInferenceFault [reason = " + getReason() + ", symbol = " + symbol + "]";
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private AlphabetInferenceFault() {}
}

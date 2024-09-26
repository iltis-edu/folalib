package de.tudortmund.cs.iltis.folalib.grammar.construction.fault;

import de.tudortmund.cs.iltis.utils.collections.Fault;

public class NoStartSymbolFault extends Fault<GrammarConstructionFaultReason> {
    public NoStartSymbolFault() {
        super(GrammarConstructionFaultReason.MISSING_START_SYMBOL);
    }

    @Override
    protected Object clone() {
        return new NoStartSymbolFault();
    }
}

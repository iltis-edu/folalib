package de.tudortmund.cs.iltis.folalib.automata.finite.fault;

import de.tudortmund.cs.iltis.utils.collections.Fault;

public class SyntaxFault extends Fault<NFAConstructionFaultReason> {
    public SyntaxFault() {
        super(NFAConstructionFaultReason.MISSING_INITIAL_STATE);
    }

    @Override
    protected SyntaxFault clone() {
        return new SyntaxFault();
    }
}

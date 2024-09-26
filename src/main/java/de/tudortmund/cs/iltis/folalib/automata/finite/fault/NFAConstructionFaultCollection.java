package de.tudortmund.cs.iltis.folalib.automata.finite.fault;

import de.tudortmund.cs.iltis.utils.collections.Fault;
import de.tudortmund.cs.iltis.utils.collections.FaultCollection;
import java.util.List;

public class NFAConstructionFaultCollection
        extends FaultCollection<NFAConstructionFaultReason, Fault<NFAConstructionFaultReason>> {
    public NFAConstructionFaultCollection(List<Fault<NFAConstructionFaultReason>> faults) {
        super(faults);
    }

    public NFAConstructionFaultCollection() {
        super();
    }

    @Override
    public FaultCollection<NFAConstructionFaultReason, Fault<NFAConstructionFaultReason>> clone() {
        return new NFAConstructionFaultCollection(getFaults());
    }
}

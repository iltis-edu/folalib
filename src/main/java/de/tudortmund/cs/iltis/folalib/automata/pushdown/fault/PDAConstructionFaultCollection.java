package de.tudortmund.cs.iltis.folalib.automata.pushdown.fault;

import de.tudortmund.cs.iltis.utils.collections.Fault;
import de.tudortmund.cs.iltis.utils.collections.FaultCollection;
import java.util.List;

/** A collection of faults and reasons why a PDA could not be constructed */
public class PDAConstructionFaultCollection
        extends FaultCollection<PDAConstructionFaultReason, Fault<PDAConstructionFaultReason>> {

    public PDAConstructionFaultCollection(List<Fault<PDAConstructionFaultReason>> faults) {
        super(faults);
    }

    /* For Serialization */
    @SuppressWarnings("unused")
    public PDAConstructionFaultCollection() {}

    @Override
    public FaultCollection<PDAConstructionFaultReason, Fault<PDAConstructionFaultReason>> clone() {
        return new PDAConstructionFaultCollection(getFaults());
    }
}

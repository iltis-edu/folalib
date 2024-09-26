package de.tudortmund.cs.iltis.folalib.grammar.construction.fault;

import de.tudortmund.cs.iltis.utils.collections.Fault;
import de.tudortmund.cs.iltis.utils.collections.FaultCollection;
import java.util.List;

public class GrammarConstructionFaultCollection
        extends FaultCollection<
                GrammarConstructionFaultReason, Fault<GrammarConstructionFaultReason>> {
    public GrammarConstructionFaultCollection(List<Fault<GrammarConstructionFaultReason>> faults) {
        super(faults);
    }

    @Override
    public FaultCollection<GrammarConstructionFaultReason, Fault<GrammarConstructionFaultReason>>
            clone() {
        return new GrammarConstructionFaultCollection(getFaults());
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private GrammarConstructionFaultCollection() {}
}

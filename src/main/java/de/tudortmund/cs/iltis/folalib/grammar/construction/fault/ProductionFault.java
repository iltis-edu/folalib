package de.tudortmund.cs.iltis.folalib.grammar.construction.fault;

import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;

public class ProductionFault<Prod extends Serializable>
        extends Fault<GrammarConstructionFaultReason> {
    private Prod faultingProduction;

    public ProductionFault(GrammarConstructionFaultReason reason, Prod faultingProduction) {
        super(reason);

        this.faultingProduction = faultingProduction;
    }

    /* For serialization */
    @SuppressWarnings("unused")
    private ProductionFault() {}

    public Prod getFaultingProduction() {
        return faultingProduction;
    }

    @Override
    protected Object clone() {
        return new ProductionFault<>(getReason(), faultingProduction);
    }
}

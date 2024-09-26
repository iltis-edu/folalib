package de.tudortmund.cs.iltis.folalib.io.alphabet.inference;

import de.tudortmund.cs.iltis.utils.collections.FaultCollection;
import java.util.List;

/**
 * A fault collection for faults occurring during inferring an alphabet. It does not contain any
 * additional attributes.
 */
public class AlphabetInferenceFaultCollection
        extends FaultCollection<AlphabetInferenceFaultReason, AlphabetInferenceFault> {

    /** Creates an empty fault collection. */
    public AlphabetInferenceFaultCollection() {
        super();
    }

    /** Creates a fault collection, already containing the given faults. */
    public AlphabetInferenceFaultCollection(List<AlphabetInferenceFault> faults) {
        super(faults);
    }

    @Override
    public AlphabetInferenceFaultCollection withFault(AlphabetInferenceFault fault) {
        return (AlphabetInferenceFaultCollection) super.withFault(fault);
    }

    @Override
    public AlphabetInferenceFaultCollection withFaults(
            FaultCollection<
                            ? extends AlphabetInferenceFaultReason,
                            ? extends AlphabetInferenceFault>
                    faults) {
        return (AlphabetInferenceFaultCollection) super.withFaults(faults);
    }

    @Override
    public String toString() {
        return "AlphabetInferenceFaultCollection [faults = " + getFaults() + "]";
    }

    @Override
    public FaultCollection<AlphabetInferenceFaultReason, AlphabetInferenceFault> clone() {
        // cloning is done in constructor
        return new AlphabetInferenceFaultCollection(faults);
    }
}

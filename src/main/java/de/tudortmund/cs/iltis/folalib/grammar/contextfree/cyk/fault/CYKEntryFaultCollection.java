package de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk.fault;

import de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk.ICYKTableauEntry;
import de.tudortmund.cs.iltis.utils.collections.FaultCollection;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class CYKEntryFaultCollection<
                T extends Serializable, N extends Serializable, Entry extends ICYKTableauEntry<N>>
        extends FaultCollection<CYKEntryFaultReason, CYKEntryFault<T, N, Entry>> {
    public CYKEntryFaultCollection(List<CYKEntryFault<T, N, Entry>> faults) {
        super(faults);
    }

    @Override
    public FaultCollection<CYKEntryFaultReason, CYKEntryFault<T, N, Entry>> clone() {
        return null;
    }

    public List<CYKEntryFault<T, N, Entry>> getMissingFaults() {
        return getFaults().stream()
                .filter(
                        f ->
                                f.getReason() == CYKEntryFaultReason.MISSING_ENTRY
                                        || f.getReason()
                                                == CYKEntryFaultReason.MISSING_ENTRY_AFTEREFFECT
                                        || f.getReason() == CYKEntryFaultReason.MISSING_ENTRY_GHOST)
                .collect(Collectors.toList());
    }

    public List<CYKEntryFault<T, N, Entry>> getAbundantFaults() {
        return getFaults().stream()
                .filter(
                        f ->
                                f.getReason() == CYKEntryFaultReason.ABUNDANT_ENTRY
                                        || f.getReason()
                                                == CYKEntryFaultReason.ABUNDANT_ENTRY_AFTEREFFECT
                                        || f.getReason()
                                                == CYKEntryFaultReason.ABUNDANT_ENTRY_GHOST)
                .collect(Collectors.toList());
    }

    public List<CYKEntryFault<T, N, Entry>> getMissingFaultsFor(N nonTerminal) {
        return getFaults().stream()
                .filter(
                        f ->
                                f.getReason() == CYKEntryFaultReason.MISSING_ENTRY
                                        || f.getReason()
                                                == CYKEntryFaultReason.MISSING_ENTRY_AFTEREFFECT
                                        || f.getReason() == CYKEntryFaultReason.MISSING_ENTRY_GHOST)
                .filter(f -> f.getNonTerminal().equals(nonTerminal))
                .collect(Collectors.toList());
    }

    public List<CYKEntryFault<T, N, Entry>> getAbundantFaultsFor(N nonTerminal) {
        return getFaults().stream()
                .filter(
                        f ->
                                f.getReason() == CYKEntryFaultReason.ABUNDANT_ENTRY
                                        || f.getReason()
                                                == CYKEntryFaultReason.ABUNDANT_ENTRY_AFTEREFFECT
                                        || f.getReason()
                                                == CYKEntryFaultReason.ABUNDANT_ENTRY_GHOST)
                .filter(f -> f.getNonTerminal().equals(nonTerminal))
                .collect(Collectors.toList());
    }

    public List<CYKEntryFault<T, N, Entry>> getFaultsForNonTerminal(N nonTerminal) {
        return getFaults().stream()
                .filter(f -> f.getNonTerminal().equals(nonTerminal))
                .collect(Collectors.toList());
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private CYKEntryFaultCollection() {}
}

package de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk.fault;

import static de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk.fault.CYKEntryFaultReason.*;

import de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk.ICYKTableauEntry;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk.InternalCYKTableauEntry;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import java.io.Serializable;
import java.util.*;

public class CYKEntryFault<
                T extends Serializable,
                N extends Serializable,
                Entry extends ICYKTableauEntry<N> & Serializable>
        extends Fault<CYKEntryFaultReason> {
    private HashMap<Pair<Integer, Integer>, List<CYKEntryFault<T, N, Entry>>> afterEffectOf;
    private Entry wrongEntry;
    private InternalCYKTableauEntry<T, N> correctEntry;

    public CYKEntryFault(Entry wrongEntry, InternalCYKTableauEntry<T, N> correctEntry) {
        super(WRONG_K_VALUE);

        this.wrongEntry = wrongEntry;
        this.correctEntry = correctEntry;
        this.afterEffectOf = new HashMap<>();
    }

    public CYKEntryFault(Entry abundantEntry, boolean isGhost) {
        super(isGhost ? ABUNDANT_ENTRY_GHOST : ABUNDANT_ENTRY);

        this.wrongEntry = abundantEntry;
        this.correctEntry = null;
        this.afterEffectOf = new HashMap<>();
    }

    public CYKEntryFault(
            Entry abundantEntry,
            HashMap<Pair<Integer, Integer>, List<CYKEntryFault<T, N, Entry>>> afterEffectOf) {
        super(ABUNDANT_ENTRY_AFTEREFFECT);

        this.wrongEntry = abundantEntry;
        this.correctEntry = null;
        this.afterEffectOf = afterEffectOf;
    }

    public CYKEntryFault(InternalCYKTableauEntry<T, N> correctEntry, boolean isGhost) {
        super(isGhost ? MISSING_ENTRY_GHOST : MISSING_ENTRY);

        this.wrongEntry = null;
        this.correctEntry = correctEntry;
        this.afterEffectOf = new HashMap<>();
    }

    public CYKEntryFault(
            InternalCYKTableauEntry<T, N> correctEntry,
            HashMap<Pair<Integer, Integer>, List<CYKEntryFault<T, N, Entry>>> afterEffectOf) {
        super(MISSING_ENTRY_AFTEREFFECT);

        this.wrongEntry = null;
        this.correctEntry = correctEntry;
        this.afterEffectOf = afterEffectOf;
    }

    /**
     * Gets the non-terminal associated with this fault.
     *
     * @return The non-terminal
     */
    public N getNonTerminal() {
        if (wrongEntry == null) return correctEntry.getNonTerminal();
        return wrongEntry.getNonTerminal();
    }

    public Optional<Entry> getWrongEntry() {
        return Optional.ofNullable(wrongEntry);
    }

    public Optional<InternalCYKTableauEntry<T, N>> getCorrectEntry() {
        return Optional.ofNullable(correctEntry);
    }

    /**
     * Computes how many algorithm steps this error is removed from its actual cause. Returns 0 for
     * non-aftereffect faults.
     *
     * <p>If there are multiple causes, the minimal distance is returned.
     *
     * @return The amount of algorithm step this fault is removed from the student's initial mistake
     */
    public int distanceToError() {
        int distance = 0;

        for (List<CYKEntryFault<T, N, Entry>> causes : afterEffectOf.values()) {
            for (CYKEntryFault<T, N, Entry> cause : causes)
                distance = Math.min(distance, cause.distanceToError());
        }

        return distance;
    }

    public boolean isGhost() {
        switch (getReason()) {
            case MISSING_ENTRY:
            case MISSING_ENTRY_AFTEREFFECT:
            case ABUNDANT_ENTRY:
            case ABUNDANT_ENTRY_AFTEREFFECT:
            case WRONG_K_VALUE:
                return false;
            case MISSING_ENTRY_GHOST:
            case ABUNDANT_ENTRY_GHOST:
                return true;
            default: // the weakness of java's static analysis capabilities knows no bounds
                throw new RuntimeException(
                        "Java is incapable of detecting exhaustive switch statements");
        }
    }

    public Map<Pair<Integer, Integer>, List<CYKEntryFault<T, N, Entry>>> afterEffectOf() {
        return Collections.unmodifiableMap(afterEffectOf);
    }

    public boolean isAfterEffect() {
        return !afterEffectOf.isEmpty();
    }

    @Override
    protected Object clone() {
        switch (getReason()) {
            case MISSING_ENTRY:
                return new CYKEntryFault<>(correctEntry, false);
            case MISSING_ENTRY_AFTEREFFECT:
                return new CYKEntryFault<>(correctEntry, afterEffectOf);
            case MISSING_ENTRY_GHOST:
                return new CYKEntryFault<>(correctEntry, true);
            case ABUNDANT_ENTRY:
                return new CYKEntryFault<>(wrongEntry, false);
            case ABUNDANT_ENTRY_AFTEREFFECT:
                return new CYKEntryFault<>(wrongEntry, afterEffectOf);
            case ABUNDANT_ENTRY_GHOST:
                return new CYKEntryFault<>(wrongEntry, true);
            case WRONG_K_VALUE:
                return new CYKEntryFault<>(wrongEntry, correctEntry);
            default:
                throw new RuntimeException(
                        "Java is incapable of detecting exhaustive switch statements");
        }
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private CYKEntryFault() {}
}

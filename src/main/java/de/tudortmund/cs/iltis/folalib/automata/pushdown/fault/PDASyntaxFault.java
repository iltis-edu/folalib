package de.tudortmund.cs.iltis.folalib.automata.pushdown.fault;

import de.tudortmund.cs.iltis.utils.collections.Fault;

/**
 * A syntactic fault why a PDA could not be constructed, e.g. because of a missing initial state
 *
 * <p>The intended usage is to call one of the public static convenience methods to create new
 * PDASyntaxFault objects, e.g. {@code PDASyntaxFault.missingInitialState()}.
 */
public class PDASyntaxFault extends Fault<PDAConstructionFaultReason> {

    private PDASyntaxFault(PDAConstructionFaultReason reason) {
        super(reason);
    }

    /* For Serialization */
    @SuppressWarnings("unused")
    private PDASyntaxFault() {}

    /**
     * Create a new PDASyntaxFault object indicating that the PDA could not be constructed because
     * no initial state has been specified
     *
     * @return a new syntax fault
     */
    public static PDASyntaxFault missingInitialState() {
        return new PDASyntaxFault(PDAConstructionFaultReason.MISSING_INITIAL_STATE);
    }

    /**
     * Create a new PDASyntaxFault object indicating that the PDA could not be constructed because
     * no initial stack symbol has been specified
     *
     * @return a new syntax fault
     */
    public static PDASyntaxFault missingInitialStackSymbol() {
        return new PDASyntaxFault(PDAConstructionFaultReason.MISSING_INITIAL_STACK_SYMBOL);
    }

    /**
     * Create a new PDASyntaxFault object indicating that the PDA could not be constructed because
     * the acceptance strategy is not known
     *
     * @return a new syntax fault
     */
    public static PDASyntaxFault missingAcceptanceStrategy() {
        return new PDASyntaxFault(PDAConstructionFaultReason.MISSING_ACCEPTANCE_STRATEGY);
    }

    @Override
    protected Object clone() {
        return new PDASyntaxFault(getReason());
    }
}

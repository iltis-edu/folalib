package de.tudortmund.cs.iltis.folalib.automata.finite;

import de.tudortmund.cs.iltis.folalib.automata.Automaton;
import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultCollection;
import de.tudortmund.cs.iltis.folalib.automata.DeterminacyFaultReason;
import de.tudortmund.cs.iltis.folalib.automata.finite.fault.NFAConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.automata.finite.fault.NFAConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.automata.finite.fault.NFADeterminacyFault;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.util.Result;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;
import java.util.List;

/**
 * {@link NFABuilder} which only produces deterministic NFAs
 *
 * <p>Whether a totality check is desired can be configured via {@link DFABuilder#ensureTotality()}
 *
 * <p>The constructed automaton will already have its `determinacy` field initialized, and {@link
 * Automaton#isDeterministic()} will return {@code true}.
 *
 * @param <State>
 * @param <Symbol>
 */
public class DFABuilder<State extends Serializable, Symbol extends Serializable>
        extends NFABuilder<State, Symbol> {
    private boolean ensureTotality;

    /** {@inheritDoc} */
    public DFABuilder(Alphabet<Symbol> alphabet) {
        super(alphabet);
    }

    // For GWT serialization
    private DFABuilder() {}

    /**
     * Enables totality validation, meaning this {@link DFABuilder} will ensure the produced DFA is
     * total.
     *
     * <p>Must be called as the first method in the construction process.
     *
     * @return {@code this} for method chaining
     */
    public NFABuilder<State, Symbol> ensureTotality() {
        this.ensureTotality = true;
        return this;
    }

    // HACK: really ugly solution for caching the determinacy faults
    private DeterminacyFaultCollection<
                    State, NFADeterminacyFault<State, Symbol, DeterminacyFaultReason>>
            determinismFaults;

    /** {@inheritDoc} */
    @Override
    public NFAConstructionFaultCollection validate() {
        determinismFaults = transitions.checkDeterminacy(states, initialStates, alphabet);

        if (!determinismFaults.hasDeterminismFaults()
                && (!determinismFaults.hasTotalityFaults() || !ensureTotality))
            return super.validate();

        List<Fault<NFAConstructionFaultReason>> faults = super.validate().getFaults();

        for (NFADeterminacyFault<State, Symbol, DeterminacyFaultReason> fault :
                determinismFaults.getFaults()) {
            if (!(fault.getReason() == DeterminacyFaultReason.MISSING_TRANSITION
                    && !ensureTotality)) // Don't add missing transition-faults if totality shall
                // not be ensured
                faults.add(fault.asConstructionFault());
        }

        return new NFAConstructionFaultCollection(faults);
    }

    /** {@inheritDoc} */
    @Override
    public Result<NFA<State, Symbol>, NFAConstructionFaultCollection> build() {
        return super.build()
                .map(
                        nfa -> {
                            nfa.determinacy = determinismFaults;
                            return nfa;
                        });
    }

    /** {@inheritDoc} */
    @Override
    public Result<NFA<State, Symbol>, NFAConstructionFaultCollection> buildAndReset() {
        return super.buildAndReset()
                .map(
                        nfa -> {
                            nfa.determinacy = determinismFaults;
                            return nfa;
                        });
    }
}

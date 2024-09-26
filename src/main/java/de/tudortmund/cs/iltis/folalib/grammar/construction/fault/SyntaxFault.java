package de.tudortmund.cs.iltis.folalib.grammar.construction.fault;

import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.io.Serializable;

public class SyntaxFault<T extends Serializable> extends Fault<GrammarConstructionFaultReason> {

    private T symbol;

    public SyntaxFault(GrammarConstructionFaultReason reason, T symbol) {
        super(reason);
        this.symbol = symbol;
    }

    /* For serialization */
    @SuppressWarnings("unused")
    public SyntaxFault() {}

    @Override
    protected Object clone() {
        return new SyntaxFault<>(getReason(), symbol);
    }

    public static <T extends Serializable> SyntaxFault<T> unknownTerminalSymbol(T t) {
        return new SyntaxFault<>(GrammarConstructionFaultReason.UNKNOWN_TERMINAL, t);
    }

    public static <N extends Serializable> SyntaxFault<N> unknownNonTerminalSymbol(N n) {
        return new SyntaxFault<>(GrammarConstructionFaultReason.UNKNOWN_NONTERMINAL, n);
    }
}

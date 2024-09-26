package de.tudortmund.cs.iltis.folalib.grammar.construction.builder;

import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class IntegratedSententialFormBuilder<
        T extends Serializable, N extends Serializable, Builder> {
    private final Consumer<SententialForm<T, N>> whenDone;
    private final Builder integratedInto;

    @SuppressWarnings("FieldMayBeFinal")
    private List<GrammarSymbol<T, N>> symbols;

    public IntegratedSententialFormBuilder(
            Consumer<SententialForm<T, N>> whenDone, Builder integratedInto) {
        this.whenDone = whenDone;
        this.integratedInto = integratedInto;

        this.symbols = new ArrayList<>();
    }

    @SafeVarargs
    public final IntegratedSententialFormBuilder<T, N, Builder> t(T... terminals) {
        for (T terminal : terminals) symbols.add(new GrammarSymbol.Terminal<>(terminal));
        return this;
    }

    @SafeVarargs
    public final IntegratedSententialFormBuilder<T, N, Builder> nt(N... terminals) {
        for (N nonTerminal : terminals) symbols.add(new GrammarSymbol.NonTerminal<>(nonTerminal));
        return this;
    }

    public Builder finish() {
        whenDone.accept(new SententialForm<>(symbols));
        return integratedInto;
    }
}

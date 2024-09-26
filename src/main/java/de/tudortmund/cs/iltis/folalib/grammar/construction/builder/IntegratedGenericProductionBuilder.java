package de.tudortmund.cs.iltis.folalib.grammar.construction.builder;

import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import java.io.Serializable;
import java.util.function.Consumer;

public class IntegratedGenericProductionBuilder<
        T extends Serializable, N extends Serializable, Builder> {
    private SententialForm<T, N> lhs;
    private SententialForm<T, N> rhs;

    private Builder integratedInto;
    private Consumer<Production<T, N>> whenDone;

    public IntegratedGenericProductionBuilder(
            Builder integratedInto, Consumer<Production<T, N>> whenDone) {
        this.integratedInto = integratedInto;
        this.whenDone = whenDone;
    }

    public IntegratedSententialFormBuilder<T, N, IntegratedGenericProductionBuilder<T, N, Builder>>
            lhs() {
        return new IntegratedSententialFormBuilder<>(lhs -> this.lhs = lhs, this);
    }

    public IntegratedSententialFormBuilder<T, N, IntegratedGenericProductionBuilder<T, N, Builder>>
            rhs() {
        return new IntegratedSententialFormBuilder<>(rhs -> this.rhs = rhs, this);
    }

    public Builder finish() {
        whenDone.accept(
                new Production<>(
                        lhs == null ? new SententialForm<>() : lhs,
                        rhs == null ? new SententialForm<>() : rhs));
        return integratedInto;
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private IntegratedGenericProductionBuilder() {}
}

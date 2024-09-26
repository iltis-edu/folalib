package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import de.tudortmund.cs.iltis.folalib.automata.AutomatonStepper;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import java.io.Serializable;

public class PDAStepper<T extends Serializable, S extends Serializable, K extends Serializable>
        extends AutomatonStepper<
                T, S, PDAConfiguration<T, S, K>, PDATransition<T, S, K>, PDA<T, S, K>> {

    public PDAStepper(PDA<T, S, K> pda, Word<S> word) {
        super(pda, pda.getAllStartConfigurations(word));
    }

    /* For serialization */
    @SuppressWarnings("unused")
    public PDAStepper() {}
}

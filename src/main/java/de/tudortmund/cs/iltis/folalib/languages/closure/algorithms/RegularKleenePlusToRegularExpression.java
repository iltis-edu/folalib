package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.KleenePlus;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute a RegularExpression for all words in language^+.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularKleenePlusToRegularExpression<S extends Serializable>
        implements SerializableFunction<KleenePlus<RegularLanguage<S>>, RegularExpression<S>> {

    @Override
    public final RegularExpression<S> apply(KleenePlus<RegularLanguage<S>> plus) {
        return plus.getLanguage().getRegularExpression().plus();
    }
}

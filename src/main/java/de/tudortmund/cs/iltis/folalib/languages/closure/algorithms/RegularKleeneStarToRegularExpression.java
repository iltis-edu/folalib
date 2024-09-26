package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.KleeneStar;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute a RegularExpression for all words in language^*.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularKleeneStarToRegularExpression<S extends Serializable>
        implements SerializableFunction<KleeneStar<RegularLanguage<S>>, RegularExpression<S>> {

    @Override
    public final RegularExpression<S> apply(KleeneStar<RegularLanguage<S>> star) {
        return star.getLanguage().getRegularExpression().star();
    }
}

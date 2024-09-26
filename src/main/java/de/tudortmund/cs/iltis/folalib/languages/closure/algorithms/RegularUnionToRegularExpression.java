package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Union;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute a RegularExpression for the union of two regular languages.
 *
 * @param <S> the type of symbols of the languages
 */
public class RegularUnionToRegularExpression<S extends Serializable>
        implements SerializableFunction<
                Union<RegularLanguage<S>, RegularLanguage<S>>, RegularExpression<S>> {

    @Override
    public final RegularExpression<S> apply(Union<RegularLanguage<S>, RegularLanguage<S>> union) {
        RegularExpression<S> regex1 = union.getFirstLanguage().getRegularExpression();
        RegularExpression<S> regex2 = union.getSecondLanguage().getRegularExpression();
        return regex1.or(regex2);
    }
}
